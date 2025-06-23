// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.agents.persistent;

import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.Configuration;
import com.azure.core.util.ConfigurationProperty;
import com.azure.core.util.ConfigurationPropertyBuilder;
import com.azure.core.util.Context;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.tracing.SpanKind;
import com.azure.core.util.tracing.StartSpanOptions;
import com.azure.core.util.tracing.Tracer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class PersistentAgentsAdministrationClientTracer {

    private static final ClientLogger LOGGER = new ClientLogger(PersistentAgentsAdministrationClientTracer.class);

    private static final StartSpanOptions START_SPAN_OPTIONS = new StartSpanOptions(SpanKind.CLIENT);
    private static final ConfigurationProperty<Boolean> CAPTURE_MESSAGE_CONTENT
        = ConfigurationPropertyBuilder.ofBoolean("azure.tracing.gen_ai.content_recording_enabled")
        .environmentVariableName("AZURE_TRACING_GEN_AI_CONTENT_RECORDING_ENABLED")
        .systemPropertyName("azure.tracing.gen_ai.content_recording_enabled")
        .shared(true).defaultValue(false).build();

    private static final Configuration GLOBAL_CONFIG = Configuration.getGlobalConfiguration();

    private final String host;
    private final int port;
    private final boolean captureContent;
    private final Tracer tracer;

    public PersistentAgentsAdministrationClientTracer(String endpoint, Configuration configuration, Tracer tracer) {
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


    //<editor-fold desc="Operation contracts">

    //</editor-fold>

    //<editor-fold desc="Static utility methods">

    private static URL parse(String endpoint) {
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

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrows(Throwable e) throws E {
        throw (E) e;
    }

    private static Context parentSpan(RequestOptions requestOptions) {
        return requestOptions.getContext() == null ? Context.NONE : requestOptions.getContext();
    }

    //</editor-fold>
}
