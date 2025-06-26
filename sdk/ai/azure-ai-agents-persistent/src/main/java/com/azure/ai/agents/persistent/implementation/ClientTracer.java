package com.azure.ai.agents.persistent.implementation;

import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.*;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.tracing.SpanKind;
import com.azure.core.util.tracing.StartSpanOptions;
import com.azure.core.util.tracing.Tracer;
import com.azure.json.JsonProviders;
import com.azure.json.JsonWriter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ClientTracer {
    public static final String OTEL_SCHEMA_URL = "https://opentelemetry.io/schemas/1.27.0";

    /**
     * Reference to the operation performing the actual call.
     */
    @FunctionalInterface
    public interface Operation<T> {
        T invoke(RequestOptions requestOptions);
    }

    @FunctionalInterface
    public interface TraceBeforeInvocation {
        void invoke(Context span);
    }

    @FunctionalInterface
    public interface TraceAfterInvocation<T> {
        void invoke(Context span, T result);
    }

    private static final ClientLogger LOGGER = new ClientLogger(ClientTracer.class);

    protected static final StartSpanOptions START_SPAN_OPTIONS = new StartSpanOptions(SpanKind.CLIENT);
    protected static final ConfigurationProperty<Boolean> CAPTURE_MESSAGE_CONTENT
        = ConfigurationPropertyBuilder.ofBoolean("azure.tracing.gen_ai.content_recording_enabled")
            .environmentVariableName("AZURE_TRACING_GEN_AI_CONTENT_RECORDING_ENABLED")
            .systemPropertyName("azure.tracing.gen_ai.content_recording_enabled")
            .shared(true)
            .defaultValue(false)
            .build();

    // OpenTelemetry constants - Based on OpenTelemetryConstants.cs
    protected static final String ERROR_TYPE_KEY = "error.type";
    protected static final String ERROR_MESSAGE_KEY = "error.message";
    protected static final String AZ_NAMESPACE_KEY = "az.namespace";
    protected static final String SERVER_ADDRESS_KEY = "server.address";
    protected static final String SERVER_PORT_KEY = "server.port";

    protected static final String GEN_AI_OPERATION_NAME_KEY = "gen_ai.operation.name";
    protected static final String GEN_AI_SYSTEM_KEY = "gen_ai.system";
    protected static final String GEN_AI_EVENT_CONTENT = "gen_ai.event.content";
    protected static final String EVENT_NAME_SYSTEM_MESSAGE = "gen_ai.system.message";

    protected static final String AZURE_RP_NAMESPACE_VALUE = "Microsoft.CognitiveServices";

    protected static final Configuration GLOBAL_CONFIG = Configuration.getGlobalConfiguration();

    protected final String host;
    protected final int port;
    protected final boolean captureContent;
    protected final Tracer tracer;

    protected Function<Context, Mono<Void>> getAsyncComplete() {
        return ((span) -> {
            tracer.end(null, null, span);
            return Mono.empty();
        });
    }

    protected BiFunction<Context, Throwable, Mono<Void>> getAsyncError() {
        return (span, throwable) -> {
            if (tracer.isRecording(span)) {
                traceErrorAttributes(throwable, span);
            }
            tracer.end(null, throwable, span);
            return Mono.empty();
        };
    }

    protected Function<Context, Mono<Void>> getAsyncCancel() {
        return span -> {
            tracer.end("cancelled", null, span);
            return Mono.empty();
        };
    }

    /**
     * Creates BaseClientTracer.
     *
     * @param endpoint the service endpoint.
     * @param configuration the {@link Configuration} instance to check if message content needs to be captured,
     *     if {@code null} is passed then {@link Configuration#getGlobalConfiguration()} will be used.
     * @param tracer the Tracer instance.
     */
    protected ClientTracer(String endpoint, Configuration configuration, Tracer tracer) {
        final URL url = parse(endpoint);
        if (url != null) {
            this.host = url.getHost();
            this.port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        } else {
            this.host = null;
            this.port = -1;
        }
        this.captureContent = configuration == null
            ? GLOBAL_CONFIG.get(CAPTURE_MESSAGE_CONTENT)
            : configuration.get(CAPTURE_MESSAGE_CONTENT);
        this.tracer = tracer;
    }

    protected void traceCommonAttributes(Context span, String systemName, String operationName) {
        tracer.setAttribute(GEN_AI_SYSTEM_KEY, systemName, span);
        tracer.setAttribute(GEN_AI_OPERATION_NAME_KEY, operationName, span);
        tracer.setAttribute(AZ_NAMESPACE_KEY, AZURE_RP_NAMESPACE_VALUE, span);

        // set server attributes
        if (host != null) {
            tracer.setAttribute(SERVER_ADDRESS_KEY, host, span);
            if (port != -1 && port != 443) {
                tracer.setAttribute(SERVER_PORT_KEY, port, span);
            }
        }
    }

    @SuppressWarnings("try")
    protected <T> T traceSyncOperation(String spanName, Operation<T> operation, RequestOptions requestOptions,
        TraceBeforeInvocation traceBeforeInvocation, TraceAfterInvocation<T> traceAfterInvocation) {
        if (!tracer.isEnabled()) {
            return operation.invoke(requestOptions);
        }
        final Context span = tracer.start(spanName, START_SPAN_OPTIONS, parentSpan(requestOptions));
        if (tracer.isRecording(span)) {
            traceBeforeInvocation.invoke(span);
        }

        try (AutoCloseable ignored = tracer.makeSpanCurrent(span)) {
            final T result = operation.invoke(requestOptions.setContext(span));
            if (tracer.isRecording(span) && result != null) {
                traceAfterInvocation.invoke(span, result);
            }
            tracer.end(null, null, span);
            return result;
        } catch (Exception e) {
            if (tracer.isRecording(span)) {
                traceErrorAttributes(e, span);
            }
            tracer.end(null, e, span);
            sneakyThrows(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Mono<R>, R> T traceAsyncMonoOperation(String spanName, Operation<T> operation,
        RequestOptions requestOptions, TraceBeforeInvocation traceBeforeInvocation,
        TraceAfterInvocation<R> traceAfterInvocation) {
        if (!tracer.isEnabled()) {
            return operation.invoke(requestOptions);
        }

        final Mono<Context> resourceSupplier = Mono.fromSupplier(() -> {
            final Context span = tracer.start(spanName, START_SPAN_OPTIONS, parentSpan(requestOptions));
            if (tracer.isRecording(span)) {
                traceBeforeInvocation.invoke(span);
            }
            return span;
        });

        final Function<Context, Mono<R>> resourceClosure = span -> {
            final RequestOptions rOptions = requestOptions.setContext(span);

            return operation.invoke(rOptions).map(response -> {
                if (tracer.isRecording(span)) {
                    traceAfterInvocation.invoke(span, response);
                }
                return response;
            });
        };

        return (T) Mono.usingWhen(resourceSupplier, resourceClosure, getAsyncComplete(), getAsyncError(),
            getAsyncCancel());
    }

    @SuppressWarnings("unchecked")
    protected <T extends Flux<R>, R> T traceAsyncFluxOperation(String spanName, Operation<T> operation,
        RequestOptions requestOptions, TraceBeforeInvocation traceBeforeInvocation,
        TraceAfterInvocation<R> traceAfterInvocation) {
        if (!tracer.isEnabled()) {
            return operation.invoke(requestOptions);
        }

        final Mono<Context> resourceSupplier = Mono.fromSupplier(() -> {
            final Context span = tracer.start(spanName, START_SPAN_OPTIONS, parentSpan(requestOptions));
            if (tracer.isRecording(span)) {
                traceBeforeInvocation.invoke(span);
            }
            return span;
        });

        final Function<Context, Flux<R>> resourceClosure = span -> {
            final RequestOptions rOptions = requestOptions.setContext(span);

            return operation.invoke(rOptions).map(response -> {
                if (tracer.isRecording(span)) {
                    traceAfterInvocation.invoke(span, response);
                }
                return response;
            });
        };

        return (T) Flux.usingWhen(resourceSupplier, resourceClosure, getAsyncComplete(), getAsyncError(),
            getAsyncCancel());
    }

    protected void setAttributeIfNotNull(String key, Object value, Context span) {
        if (value != null) {
            tracer.setAttribute(key, value.toString(), span);
        }
    }

    protected void setAttributeIfNotNullOrEmpty(String key, CharSequence value, Context span) {
        if (!CoreUtils.isNullOrEmpty(value)) {
            tracer.setAttribute(key, value, span);
        }
    }

    /**
     * Records error attributes on the span.
     *
     * @param e The exception that occurred.
     * @param span The current span context.
     */
    protected void traceErrorAttributes(Throwable e, Context span) {
        if (e != null) {
            tracer.setAttribute(ERROR_TYPE_KEY, e.getClass().getName(), span);
            this.setAttributeIfNotNull(ERROR_MESSAGE_KEY, e.getMessage(), span);
        }
    }

    //<editor-fold desc="Static utility methods">

    /**
     * Serializes an object to JSON string.
     *
     * @param obj The object to serialize.
     * @return A JSON representation of the object.
     */
    protected static String toJsonString(Object obj) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
            JsonWriter writer = JsonProviders.createWriter(stream)) {

            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                writer.writeStartObject();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    writeJsonValue(writer, entry.getKey(), entry.getValue());
                }
                writer.writeEndObject();
            } else {
                writer.writeStartObject();
                writer.writeEndObject();
            }

            writer.flush();
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.verbose("Object serialization error", e);
        }
        return null;
    }

    /**
     * Helper method to write a value to JSON.
     *
     * @param writer The JSON writer.
     * @param key The key name.
     * @param value The value to write.
     * @throws IOException If an I/O error occurs.
     */
    protected static void writeJsonValue(JsonWriter writer, String key, Object value) throws IOException {
        if (value == null) {
            writer.writeNullField(key);
        } else if (value instanceof String) {
            writer.writeStringField(key, (String) value);
        } else if (value instanceof Number) {
            if (value instanceof Integer) {
                writer.writeIntField(key, (Integer) value);
            } else if (value instanceof Long) {
                writer.writeLongField(key, (Long) value);
            } else if (value instanceof Double) {
                writer.writeDoubleField(key, (Double) value);
            } else if (value instanceof Float) {
                writer.writeFloatField(key, (Float) value);
            } else {
                writer.writeNumberField(key, (Number) value);
            }
        } else if (value instanceof Boolean) {
            writer.writeBooleanField(key, (Boolean) value);
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            writer.writeStartObject(key);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                writeJsonValue(writer, entry.getKey(), entry.getValue());
            }
            writer.writeEndObject();
        } else {
            writer.writeStringField(key, value.toString());
        }
    }

    /**
     * Parses an endpoint string into a URL.
     *
     * @param endpoint The endpoint string to parse.
     * @return The parsed URL, or null if invalid.
     */
    protected static URL parse(String endpoint) {
        if (CoreUtils.isNullOrEmpty(endpoint)) {
            return null;
        }
        try {
            final URI uri = new URI(endpoint);
            return uri.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.atWarning().log("service endpoint uri parse error.", e);
        }
        return null;
    }

    /**
     * Utility method for "sneaky throws" pattern.
     *
     * @param e The exception to throw.
     * @param <E> The type of exception.
     * @throws E The exception.
     */
    @SuppressWarnings("unchecked")
    protected static <E extends Throwable> void sneakyThrows(Throwable e) throws E {
        throw (E) e;
    }

    /**
     * Gets the parent span from request options.
     *
     * @param requestOptions The request options.
     * @return The parent span context.
     */
    protected static Context parentSpan(RequestOptions requestOptions) {
        return requestOptions.getContext() == null ? Context.NONE : requestOptions.getContext();
    }
    //</editor-fold>
}
