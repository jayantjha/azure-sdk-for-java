package com.azure.ai.projects.usage.agent;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.AgentsClient;
import com.azure.ai.projects.models.*;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class SampleAgentFunctionsStreaming {

    @Test
    void functionsStreamingExample() {
        AgentsClient agentsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .subscriptionId(Configuration.getGlobalConfiguration().get("SUBSCRIPTIONID", "subscriptionid"))
            .resourceGroupName(Configuration.getGlobalConfiguration().get("RESOURCEGROUPNAME", "resourcegroupname"))
            .projectName(Configuration.getGlobalConfiguration().get("PROJECTNAME", "projectname"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildAgentsClient();

        Supplier<String> getUserFavoriteCity = () -> "Seattle, WA";
        var getUserFavoriteCityTool = new FunctionToolDefinition(
            new FunctionDefinition(
                "getUserFavoriteCity",
                BinaryData.fromObject(
                    new Object()
                ))
        );

        Function<String, String> getCityNickname = location -> {
            return "The Emerald City";
        };

        var getCityNicknameTool = new FunctionToolDefinition(
            new FunctionDefinition(
                "getCityNickname",
                BinaryData.fromObject(
                    Map.of(
                        "type", "object",
                        "properties", Map.of(
                            "location",
                            Map.of(
                                "type", "string",
                                "description", "The city and state, e.g. San Francisco, CA")
                        ),
                        "required", new String[] {"location"}))
            )
        );

        Function<RequiredToolCall, ToolOutput> getResolvedToolOutput = toolCall -> {
            if (toolCall instanceof RequiredFunctionToolCall) {
                var functionToolCall = (RequiredFunctionToolCall)toolCall;
                if (functionToolCall.getFunction().getName().equals("getUserFavoriteCity"))
                    return new ToolOutput().setToolCallId(functionToolCall.getId())
                        .setOutput(getUserFavoriteCity.get());

                try {
                    JsonMapper jsonMapper = new JsonMapper();
                    JsonNode rootNode = jsonMapper.readTree(functionToolCall.getFunction().getArguments());
                    if (functionToolCall.getFunction().getName().equals("getCityNickname")) {

                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }
            return null;
        };

        var agentName = "functions_streaming_example";
        var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You are a weather bot. Use the provided functions to help answer questions. "
                + "Customize your responses to the user's preferences as much as possible and use friendly "
                + "nicknames for cities whenever possible.")
            .setTools(List.of(getUserFavoriteCityTool, getCityNicknameTool));
        Agent agent = agentsClient.createAgent(createAgentOptions);

        var thread = agentsClient.createThread();
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What's the weather like in my favorite city?");










        // ---------------------- usual code --------------------

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
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
}
