// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.agents.persistent.implementation;

import com.azure.ai.agents.persistent.models.PersistentAgentThread;
import com.azure.ai.agents.persistent.models.ThreadMessageOptions;
import com.azure.ai.agents.persistent.models.ToolResources;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.Configuration;
import com.azure.core.util.Context;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.tracing.Tracer;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadsClientTracer extends ClientTracer {

    private static final ClientLogger LOGGER = new ClientLogger(ThreadsClientTracer.class);

    // Thread-specific constants
    static final String GEN_AI_THREAD_ID_KEY = "gen_ai.thread.id";
    static final String GEN_AI_SYSTEM_VALUE = "az.ai.agents";
    static final String OPERATION_CREATE_THREAD = "create_thread";
    static final String EVENT_NAME_USER_MESSAGE = "gen_ai.user.message";

    /**
     * Creates ThreadsClientTracer.
     *
     * @param endpoint the service endpoint.
     * @param configuration the {@link Configuration} instance to check if message content needs to be captured,
     *     if {@code null} is passed then {@link Configuration#getGlobalConfiguration()} will be used.
     * @param tracer the Tracer instance.
     */
    public ThreadsClientTracer(String endpoint, Configuration configuration, Tracer tracer) {
        super(endpoint, configuration, tracer);
    }

    //<editor-fold desc="Tracing CreateThread">

    /**
     * Traces the synchronous convenience API - create thread operation.
     *
     * @param operation the operation performing the actual create thread call.
     * @param requestOptions The requestOptions parameter for the {@code operation}.
     * @return persistent thread created from the request.
     */
    public PersistentAgentThread traceCreateThreadSync(List<ThreadMessageOptions> messages, ToolResources toolResources,
        Operation<PersistentAgentThread> operation, RequestOptions requestOptions) {

        return this.traceSyncOperation(OPERATION_CREATE_THREAD, operation, requestOptions, (span) -> {
            traceCreateThreadInvocationAttributes(span, messages, toolResources);
        }, this::traceCreateThreadResponseAttributes);
    }

    /**
     * Traces the asynchronous convenience API - create thread operation.
     *
     * @param operation the operation performing the actual create thread call.
     * @param requestOptions The requestOptions parameter for the {@code operation}.
     * @return persistent thread created from the request.
     */
    public Mono<PersistentAgentThread> traceCreateThreadAsync(List<ThreadMessageOptions> messages,
        ToolResources toolResources, Operation<Mono<PersistentAgentThread>> operation, RequestOptions requestOptions) {

        return this.traceAsyncMonoOperation(OPERATION_CREATE_THREAD, operation, requestOptions, (span) -> {
            traceCreateThreadInvocationAttributes(span, messages, toolResources);
        }, this::traceCreateThreadResponseAttributes);
    }

    /**
     * Trace the attributes for a create thread request.
     *
     * @param span The current span context.
     */
    void traceCreateThreadInvocationAttributes(Context span, List<ThreadMessageOptions> messages,
        ToolResources toolResources) {
        // Set common span attributes
        this.traceCommonAttributes(span, GEN_AI_SYSTEM_VALUE, OPERATION_CREATE_THREAD);

        // Process and record messages if content capture is enabled
        if (captureContent && messages != null && !messages.isEmpty()) {
            traceCreateThreadMessages(messages, span);
        }
    }

    /**
     * Trace the user messages included in the thread creation.
     *
     * @param messages The messages to be included in the thread.
     * @param span The current span context.
     */
    void traceCreateThreadMessages(List<ThreadMessageOptions> messages, Context span) {
        if (!captureContent || messages == null || messages.isEmpty()) {
            return;
        }

        for (ThreadMessageOptions message : messages) {
            if (message == null) {
                continue;
            }

            Map<String, Object> eventAttributes = new HashMap<>();
            eventAttributes.put(GEN_AI_SYSTEM_KEY, GEN_AI_SYSTEM_VALUE);

            // Create the content body
            Map<String, Object> contentMap = new HashMap<>();
            if (message.getContent() != null) {
                contentMap.put("content", message.getContent().toString());
            }
            if (message.getRole() != null) {
                contentMap.put("role", message.getRole().toString());
            }

            String eventContent = toJsonString(contentMap);
            if (eventContent != null) {
                eventAttributes.put(GEN_AI_EVENT_CONTENT, eventContent);
                tracer.addEvent(EVENT_NAME_USER_MESSAGE, eventAttributes, null, span);
            }
        }
    }

    /**
     * Record the response attributes from a create thread operation.
     *
     * @param span The current span context.
     * @param thread The persistent thread created.
     */
    void traceCreateThreadResponseAttributes(Context span, PersistentAgentThread thread) {
        if (thread != null && !CoreUtils.isNullOrEmpty(thread.getId())) {
            tracer.setAttribute(GEN_AI_THREAD_ID_KEY, thread.getId(), span);
        }
    }
    //</editor-fold>
}
