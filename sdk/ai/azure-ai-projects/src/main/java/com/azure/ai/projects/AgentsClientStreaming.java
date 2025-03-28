//// Copyright (c) Microsoft Corporation. All rights reserved.
//// Licensed under the MIT License.
//
//package com.azure.ai.projects;
//
//import com.azure.ai.projects.implementation.models.CreateRunRequest;
//import com.azure.ai.projects.models.*;
//import com.azure.core.annotation.ReturnType;
//import com.azure.core.annotation.ServiceMethod;
//import com.azure.core.http.HttpPipeline;
//import com.azure.core.http.HttpResponse;
//import com.azure.core.util.Context;
//import com.azure.core.util.CoreUtils;
//import com.azure.core.util.logging.ClientLogger;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Objects;
//
///**
// * Streaming functionality for the AgentsClient.
// */
//public class AgentsClientStreaming {
//    private final AgentsClient client;
//    private final ClientLogger logger = new ClientLogger(AgentsClientStreaming.class);
//    private final HttpPipeline pipeline;
//
//    /**
//     * Creates an instance of AgentsClientStreaming.
//     *
//     * @param client The AgentsClient instance to use.
//     */
//    public AgentsClientStreaming(AgentsClient client) {
//        this.client = Objects.requireNonNull(client, "'client' cannot be null");
//        this.pipeline = client.getHttpPipeline();
//    }
//
//    /**
//     * Begins a new streaming ThreadRun that evaluates an AgentThread using a specified Agent.
//     *
//     * @param threadId Identifier of the thread.
//     * @param assistantId The ID of the agent that should run the thread.
//     * @param options The options to configure the run.
//     * @param context Additional context that is passed through the HTTP pipeline during the service call.
//     * @return A StreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public StreamingUpdateCollection createRunStreaming(String threadId, String assistantId,
//            CreateRunOptions options, Context context) {
//        Objects.requireNonNull(threadId, "'threadId' cannot be null");
//        Objects.requireNonNull(assistantId, "'assistantId' cannot be null");
//
//        if (CoreUtils.isNullOrEmpty(threadId)) {
//            throw logger.logExceptionAsError(new IllegalArgumentException("'threadId' cannot be empty"));
//        }
//
//        CreateRunRequest request = new CreateRunRequest()
//            .setAssistantId(assistantId)
//            .setOverrideModelName(options == null ? null : options.getOverrideModelName())
//            .setOverrideInstructions(options == null ? null : options.getOverrideInstructions())
//            .setAdditionalInstructions(options == null ? null : options.getAdditionalInstructions())
//            .setAdditionalMessages(options == null ? null : options.getAdditionalMessages())
//            .setOverrideTools(options == null ? null : options.getOverrideTools())
//            .setStream(true)
//            .setTemperature(options == null ? null : options.getTemperature())
//            .setTopP(options == null ? null : options.getTopP())
//            .setMaxPromptTokens(options == null ? null : options.getMaxPromptTokens())
//            .setMaxCompletionTokens(options == null ? null : options.getMaxCompletionTokens())
//            .setTruncationStrategy(options == null ? null : options.getTruncationStrategy())
//            .setToolChoice(options == null ? null : options.getToolChoice())
//            .setResponseFormat(options == null ? null : options.getResponseFormat())
//            .setParallelToolCalls(options == null ? null : options.isParallelToolCalls())
//            .setMetadata(options == null ? null : options.getMetadata());
//
//        HttpResponse response = client.createRunStreamingInternal(threadId, request, context);
//        return new StreamingUpdateCollection(response);
//    }
//
//    /**
//     * Begins a new streaming ThreadRun that evaluates an AgentThread using a specified Agent.
//     *
//     * @param threadId Identifier of the thread.
//     * @param assistantId The ID of the agent that should run the thread.
//     * @param options The options to configure the run.
//     * @return A StreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public StreamingUpdateCollection createRunStreaming(String threadId, String assistantId, CreateRunOptions options) {
//        return createRunStreaming(threadId, assistantId, options, Context.NONE);
//    }
//
//    /**
//     * Begins a new streaming ThreadRun that evaluates an AgentThread using a specified Agent.
//     *
//     * @param threadId Identifier of the thread.
//     * @param assistantId The ID of the agent that should run the thread.
//     * @return A StreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public StreamingUpdateCollection createRunStreaming(String threadId, String assistantId) {
//        return createRunStreaming(threadId, assistantId, null, Context.NONE);
//    }
//
//    /**
//     * Begins a new streaming ThreadRun that evaluates an AgentThread using a specified Agent asynchronously.
//     *
//     * @param threadId Identifier of the thread.
//     * @param assistantId The ID of the agent that should run the thread.
//     * @param options The options to configure the run.
//     * @param context Additional context that is passed through the HTTP pipeline during the service call.
//     * @return A Mono of AsyncStreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public Mono<StreamingUpdateAsyncCollection> createRunStreamingAsync(String threadId, String assistantId,
//                                                                        CreateRunOptions options, Context context) {
//        Objects.requireNonNull(threadId, "'threadId' cannot be null");
//        Objects.requireNonNull(assistantId, "'assistantId' cannot be null");
//
//        if (CoreUtils.isNullOrEmpty(threadId)) {
//            return Mono.error(new IllegalArgumentException("'threadId' cannot be empty"));
//        }
//
//        CreateRunRequest request = new CreateRunRequest()
//            .setAssistantId(assistantId)
//            .setOverrideModelName(options == null ? null : options.getOverrideModelName())
//            .setOverrideInstructions(options == null ? null : options.getOverrideInstructions())
//            .setAdditionalInstructions(options == null ? null : options.getAdditionalInstructions())
//            .setAdditionalMessages(options == null ? null : options.getAdditionalMessages())
//            .setOverrideTools(options == null ? null : options.getOverrideTools())
//            .setStream(true)
//            .setTemperature(options == null ? null : options.getTemperature())
//            .setTopP(options == null ? null : options.getTopP())
//            .setMaxPromptTokens(options == null ? null : options.getMaxPromptTokens())
//            .setMaxCompletionTokens(options == null ? null : options.getMaxCompletionTokens())
//            .setTruncationStrategy(options == null ? null : options.getTruncationStrategy())
//            .setToolChoice(options == null ? null : options.getToolChoice())
//            .setResponseFormat(options == null ? null : options.getResponseFormat())
//            .setParallelToolCalls(options == null ? null : options.isParallelToolCalls())
//            .setMetadata(options == null ? null : options.getMetadata());
//
//        return client.createRunStreamingInternalAsync(threadId, request, context)
//            .map(StreamingUpdateAsyncCollection::new);
//    }
//
//    /**
//     * Begins a new streaming ThreadRun that evaluates an AgentThread using a specified Agent asynchronously.
//     *
//     * @param threadId Identifier of the thread.
//     * @param assistantId The ID of the agent that should run the thread.
//     * @param options The options to configure the run.
//     * @return A Mono of AsyncStreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public Mono<StreamingUpdateAsyncCollection> createRunStreamingAsync(String threadId, String assistantId,
//                                                                        CreateRunOptions options) {
//        return createRunStreamingAsync(threadId, assistantId, options, Context.NONE);
//    }
//
//    /**
//     * Begins a new streaming ThreadRun that evaluates an AgentThread using a specified Agent asynchronously.
//     *
//     * @param threadId Identifier of the thread.
//     * @param assistantId The ID of the agent that should run the thread.
//     * @return A Mono of AsyncStreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public Mono<StreamingUpdateAsyncCollection> createRunStreamingAsync(String threadId, String assistantId) {
//        return createRunStreamingAsync(threadId, assistantId, null, Context.NONE);
//    }
//
//    /**
//     * Submits outputs from tools as requested by tool calls in a stream.
//     *
//     * @param run The ThreadRun that the tool outputs should be submitted to.
//     * @param toolOutputs A list of tools for which the outputs are being submitted.
//     * @param context Additional context that is passed through the HTTP pipeline during the service call.
//     * @return A StreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public StreamingUpdateCollection submitToolOutputsToStream(ThreadRun run, List<ToolOutput> toolOutputs, Context context) {
//        Objects.requireNonNull(run, "'run' cannot be null");
//        Objects.requireNonNull(toolOutputs, "'toolOutputs' cannot be null");
//
//        SubmitToolOutputsRequest request = new SubmitToolOutputsRequest()
//            .setToolOutputs(toolOutputs)
//            .setStream(true);
//
//        HttpResponse response = client.submitToolOutputsInternal(run.getThreadId(), run.getId(), true, request, context);
//        return new StreamingUpdateCollection(response);
//    }
//
//    /**
//     * Submits outputs from tools as requested by tool calls in a stream.
//     *
//     * @param run The ThreadRun that the tool outputs should be submitted to.
//     * @param toolOutputs A list of tools for which the outputs are being submitted.
//     * @return A StreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public StreamingUpdateCollection submitToolOutputsToStream(ThreadRun run, List<ToolOutput> toolOutputs) {
//        return submitToolOutputsToStream(run, toolOutputs, Context.NONE);
//    }
//
//    /**
//     * Submits outputs from tools as requested by tool calls in a stream asynchronously.
//     *
//     * @param run The ThreadRun that the tool outputs should be submitted to.
//     * @param toolOutputs A list of tools for which the outputs are being submitted.
//     * @param context Additional context that is passed through the HTTP pipeline during the service call.
//     * @return A Mono of AsyncStreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public Mono<StreamingUpdateAsyncCollection> submitToolOutputsToStreamAsync(ThreadRun run, List<ToolOutput> toolOutputs,
//                                                                               Context context) {
//        Objects.requireNonNull(run, "'run' cannot be null");
//        Objects.requireNonNull(toolOutputs, "'toolOutputs' cannot be null");
//
//        SubmitToolOutputsRequest request = new SubmitToolOutputsRequest()
//            .setToolOutputs(toolOutputs)
//            .setStream(true);
//
//        return client.submitToolOutputsInternalAsync(run.getThreadId(), run.getId(), true, request, context)
//            .map(StreamingUpdateAsyncCollection::new);
//    }
//
//    /**
//     * Submits outputs from tools as requested by tool calls in a stream asynchronously.
//     *
//     * @param run The ThreadRun that the tool outputs should be submitted to.
//     * @param toolOutputs A list of tools for which the outputs are being submitted.
//     * @return A Mono of AsyncStreamingUpdateCollection containing streaming updates.
//     * @throws IllegalArgumentException thrown if parameters fail the validation.
//     * @throws NullPointerException thrown if the required parameters are null.
//     */
//    @ServiceMethod(returns = ReturnType.COLLECTION)
//    public Mono<StreamingUpdateAsyncCollection> submitToolOutputsToStreamAsync(ThreadRun run, List<ToolOutput> toolOutputs) {
//        return submitToolOutputsToStreamAsync(run, toolOutputs, Context.NONE);
//    }
//}
