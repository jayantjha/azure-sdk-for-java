package com.azure.ai.projects.usage.agent;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.AgentsClient;
import com.azure.ai.projects.implementation.models.CreateRunRequest;
import com.azure.ai.projects.models.*;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.http.rest.Response;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SampleAgentStreaming {

    @Test
    void agentStreamingExample() {
        AgentsClient agentsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .subscriptionId(Configuration.getGlobalConfiguration().get("SUBSCRIPTIONID", "subscriptionid"))
            .resourceGroupName(Configuration.getGlobalConfiguration().get("RESOURCEGROUPNAME", "resourcegroupname"))
            .projectName(Configuration.getGlobalConfiguration().get("PROJECTNAME", "projectname"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildAgentsClient();

        var agentName = "agent_streaming_example";
        var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You politely help with math questions. Use the code interpreter tool when asked to visualize numbers.")
            .setTools(List.of(new CodeInterpreterToolDefinition()));
        Agent agent = agentsClient.createAgent(createAgentOptions);

        var thread = agentsClient.createThread();
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "Hi, Assistant! Draw a graph for a line with a slope of 4 and y-intercept of 9.");

        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        Flux<BinaryData> streamingUpdates = agentsClient.createRunStreaming(createRunOptions);

//        streamingUpdates
//            .map(binaryData -> {
//                // Deserialize the binary data to a StreamingUpdate object
//                // This assumes you have a similar class structure in Java
//                return JsonSerializer.deserializeStreamingUpdate(binaryData);
//            })
//            .subscribe(streamingUpdate -> {
//                if (streamingUpdate.getUpdateKind() == StreamingUpdateReason.RUN_CREATED) {
//                    System.out.println("--- Run started! ---");
//                } else if (streamingUpdate instanceof MessageContentUpdate) {
//                    MessageContentUpdate contentUpdate = (MessageContentUpdate) streamingUpdate;
//                    System.out.print(contentUpdate.getText());
//                    if (contentUpdate.getImageFileId() != null) {
//                        System.out.println("[Image content file ID: " + contentUpdate.getImageFileId() + "]");
//                    }
//                }
//            });





        // ---------------------- usual code --------------------

        //run agent
        var threadRun = agentsClient.createRun(createRunOptions);

        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            }
            while (
                threadRun.getStatus() == RunStatus.QUEUED
                    || threadRun.getStatus() == RunStatus.IN_PROGRESS
                    || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            if (threadRun.getStatus() == RunStatus.FAILED) {
                System.out.println(threadRun.getLastError().getMessage());
            }

            var runMessages = agentsClient.listMessages(thread.getId());
            for (ThreadMessage message : runMessages.getData())
            {
                System.out.print(String.format("%1$s - %2$s : ", message.getCreatedAt(), message.getRole()));
                for (MessageContent contentItem : message.getContent())
                {
                    if (contentItem instanceof MessageTextContent)
                    {
                        System.out.print((((MessageTextContent) contentItem).getText().getValue()));
                    }
                    else if (contentItem instanceof MessageImageFileContent)
                    {
                        String imageFileId = (((MessageImageFileContent) contentItem).getImageFile().getFileId());
                        System.out.print("Image from ID: " + imageFileId);
                    }
                    System.out.println();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            //cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }

    void createRunStreaming(AgentsClient agentsClient, CreateRunOptions options) {
        RequestOptions requestOptions = new RequestOptions();
        String threadId = options.getThreadId();
        List<RunAdditionalFieldList> include = options.getInclude();
        CreateRunRequest createRunRequestObj
            = new CreateRunRequest(options.getAssistantId()).setModel(options.getModel())
            .setInstructions(options.getInstructions())
            .setAdditionalInstructions(options.getAdditionalInstructions())
            .setAdditionalMessages(options.getAdditionalMessages())
            .setTools(options.getTools())
            .setStream(true)
            .setTemperature(options.getTemperature())
            .setTopP(options.getTopP())
            .setMaxPromptTokens(options.getMaxPromptTokens())
            .setMaxCompletionTokens(options.getMaxCompletionTokens())
            .setTruncationStrategy(options.getTruncationStrategy())
            .setToolChoice(options.getToolChoice())
            .setResponseFormat(options.getResponseFormat())
            .setParallelToolCalls(options.isParallelToolCalls())
            .setMetadata(options.getMetadata());
        BinaryData createRunRequest = BinaryData.fromObject(createRunRequestObj);
        if (include != null) {
            requestOptions.addQueryParam("include[]",
                include.stream()
                    .map(paramItemValue -> Objects.toString(paramItemValue, ""))
                    .collect(Collectors.joining(",")),
                false);
        }
        Response<BinaryData> response = agentsClient.createRunWithResponse(threadId, createRunRequest, requestOptions);

        return null;
    }
}
