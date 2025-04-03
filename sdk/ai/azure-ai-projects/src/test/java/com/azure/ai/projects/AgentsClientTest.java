// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.AgentStreamEvent;
import com.azure.ai.projects.models.AgentThread;
import com.azure.ai.projects.models.CodeInterpreterToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.CreateRunOptions;
import com.azure.ai.projects.models.FileDetails;
import com.azure.ai.projects.models.FilePurpose;
import com.azure.ai.projects.models.FileSearchToolResource;
import com.azure.ai.projects.models.FunctionDefinition;
import com.azure.ai.projects.models.FunctionToolDefinition;
import com.azure.ai.projects.models.MessageContent;
import com.azure.ai.projects.models.MessageDeltaImageFileContent;
import com.azure.ai.projects.models.MessageDeltaTextContent;
import com.azure.ai.projects.models.MessageImageFileContent;
import com.azure.ai.projects.models.MessageRole;
import com.azure.ai.projects.models.MessageTextContent;
import com.azure.ai.projects.models.OpenAIFile;
import com.azure.ai.projects.models.OpenAIPageableListOfThreadMessage;
import com.azure.ai.projects.models.RequiredFunctionToolCall;
import com.azure.ai.projects.models.RequiredToolCall;
import com.azure.ai.projects.models.RunStatus;
import com.azure.ai.projects.models.SubmitToolOutputsAction;
import com.azure.ai.projects.models.ThreadMessage;
import com.azure.ai.projects.models.ThreadRun;
import com.azure.ai.projects.models.ToolOutput;
import com.azure.ai.projects.models.UploadFileRequest;
import com.azure.ai.projects.models.VectorStore;
import com.azure.ai.projects.models.VectorStoreConfiguration;
import com.azure.ai.projects.models.VectorStoreDataSource;
import com.azure.ai.projects.models.VectorStoreDataSourceAssetType;
import com.azure.ai.projects.models.VectorStoreStatus;
import com.azure.ai.projects.models.streaming.StreamMessageUpdate;
import com.azure.ai.projects.models.streaming.StreamRequiredAction;
import com.azure.ai.projects.models.streaming.StreamThreadRunCreation;
import com.azure.ai.projects.models.streaming.StreamUpdate;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AgentsClientTest extends AIProjectClientTestBase {

    private Agent ciAgent = null;

    @BeforeEach
    void setup() {
        this.beforeTest();
        this.createCIAgent();
    }

    @Test
    void testCreateAgent() {
        String agentName = "basic_example";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));
        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent.getId());
        agentsClient.deleteAgent(agent.getId());
    }

    @Test
    void testCreateRunAndReadMessages() {
        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(thread.getId(), MessageRole.USER,
            "I need to solve the equation `3x + 11 = 14`. Can you help me?");

        //run agent
        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), ciAgent.getId()).setAdditionalInstructions("");
        ThreadRun threadRun = agentsClient.createRun(createRunOptions);

        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            } while (threadRun.getStatus() == RunStatus.QUEUED
                || threadRun.getStatus() == RunStatus.IN_PROGRESS
                || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            if (threadRun.getStatus() == RunStatus.FAILED) {
                System.out.println(threadRun.getLastError().getMessage());
            }

            OpenAIPageableListOfThreadMessage runMessages = agentsClient.listMessages(thread.getId());
            for (ThreadMessage message : runMessages.getData()) {
                System.out.print(String.format("%1$s - %2$s : ", message.getCreatedAt(), message.getRole()));
                for (MessageContent contentItem : message.getContent()) {
                    if (contentItem instanceof MessageTextContent) {
                        System.out.print((((MessageTextContent) contentItem).getText().getValue()));
                    } else if (contentItem instanceof MessageImageFileContent) {
                        String imageFileId = (((MessageImageFileContent) contentItem).getImageFile().getFileId());
                        System.out.print("Image from ID: " + imageFileId);
                    }
                    System.out.println();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            agentsClient.deleteThread(thread.getId());
        }
    }

    @Test
    void testVectorStore() throws InterruptedException {
        String dataUri = Configuration.getGlobalConfiguration().get("DATA_URI", "");
        VectorStoreDataSource vectorStoreDataSource
            = new VectorStoreDataSource(dataUri, VectorStoreDataSourceAssetType.URI_ASSET);

        VectorStore vectorStoreWithConfig = agentsClient.createVectorStore(null, "sample_vector_store",
            new VectorStoreConfiguration(Arrays.asList(vectorStoreDataSource)), null, null, null);

        OpenAIFile uploadedAgentFile = agentsClient.uploadFile(new UploadFileRequest(new FileDetails(BinaryData
            .fromString("The word `apple` uses the code 442345, while the word `banana` uses the code 673457."))
                .setFilename("sample_file_for_upload.txt"),
            FilePurpose.AGENTS));

        VectorStore vectorStoreWithId = agentsClient.createVectorStore(Arrays.asList(uploadedAgentFile.getId()),
            "my_vector_store", null, null, null, null);

        do {
            Thread.sleep(500);
            vectorStoreWithId = agentsClient.getVectorStore(vectorStoreWithId.getId());
        } while (vectorStoreWithId.getStatus() == VectorStoreStatus.IN_PROGRESS);

        FileSearchToolResource fileSearchToolResource
            = new FileSearchToolResource().setVectorStoreIds(Arrays.asList(vectorStoreWithId.getId()));
    }

    @Test
    void testRunStreaming() {
        // function tool definitions
        FunctionToolDefinition getUserFavoriteCityTool = new FunctionToolDefinition(
            new FunctionDefinition("getUserFavoriteCity", BinaryData.fromObject(new Object()))
                .setDescription("Gets the user's favorite city."));

        FunctionToolDefinition getCityNicknameTool = new FunctionToolDefinition(new FunctionDefinition(
            "getCityNickname",
            BinaryData.fromObject(mapOf("type", "object", "properties",
                mapOf("location", mapOf("type", "string", "description", "The city and state, e.g. San Francisco, CA")),
                "required", new String[] { "location" })))
                    .setDescription("Gets the nickname of a city, e.g. 'LA' for 'Los Angeles, CA'."));

        FunctionToolDefinition getCurrentWeatherAtLocationTool
            = new FunctionToolDefinition(
                new FunctionDefinition("getCurrentWeatherAtLocation",
                    BinaryData.fromObject(mapOf("type", "object", "properties", mapOf("location",
                        mapOf("type", "string", "description", "The city and state, e.g. San Francisco, CA"), "unit",
                        mapOf("type", "string", "description", "temperature unit as c or f", "enum",
                            new String[] { "c", "f" })),
                        "required", new String[] { "location", "unit" })))
                            .setDescription("Gets the current weather at a provided location."));

        // actual functions
        Supplier<String> getUserFavoriteCity = () -> "Seattle, WA";

        Function<String, String> getCityNickname = (location) -> {
            switch (location) {
                case "Seattle, WA":
                    return "The Emerald city";

                default:
                    return "No nickname available";
            }
        };

        BiFunction<String, String, String> getCurrentWeatherAtLocation = (location, unit) -> {
            switch (location) {
                case "Seattle, WA":
                    return unit == "f" ? "70f" : "21c";

                default:
                    return "unknown";
            }
        };

        // function resolver
        Function<RequiredToolCall, ToolOutput> getResolvedToolOutput = toolCall -> {
            if (toolCall instanceof RequiredFunctionToolCall) {
                try {
                    RequiredFunctionToolCall functionToolCall = (RequiredFunctionToolCall) toolCall;
                    String functionName = functionToolCall.getFunction().getName();
                    if ("getUserFavoriteCity".equals(functionName)) {
                        return new ToolOutput().setToolCallId(functionToolCall.getId())
                            .setOutput(getUserFavoriteCity.get());
                    } else if ("getCityNickname".equals(functionName)) {
                        String args = functionToolCall.getFunction().getArguments();

                        JsonNode root = new JsonMapper().readTree(args);
                        String location = String.valueOf(root.get("location").asText());
                        return new ToolOutput().setToolCallId(functionToolCall.getId())
                            .setOutput(getCityNickname.apply(location));

                    } else if ("getCurrentWeatherAtLocation".equals(functionName)) {
                        String args = functionToolCall.getFunction().getArguments();

                        JsonNode root = new JsonMapper().readTree(args);
                        String location = String.valueOf(root.get("location").asText());
                        String unit = String.valueOf(root.get("unit").asText());
                        return new ToolOutput().setToolCallId(functionToolCall.getId())
                            .setOutput(getCurrentWeatherAtLocation.apply(location, unit));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        };

        String agentName = "functions_streaming_example";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a weather bot. Use the provided functions to help answer questions. "
                + "Customize your responses to the user's preferences as much as possible and use friendly "
                + "nicknames for cities whenever possible.")
            .setTools(Arrays.asList(getUserFavoriteCityTool, getCityNicknameTool, getCurrentWeatherAtLocationTool));
        Agent agent = agentsClient.createAgent(createAgentOptions);

        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(thread.getId(), MessageRole.USER,
            "What's the weather like in my favorite city?");

        //run agent
        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), ciAgent.getId()).setAdditionalInstructions("");

        try {
            Flux<StreamUpdate> streamingUpdates = agentsClient.createRunStreaming(createRunOptions);

            streamingUpdates.doOnNext(streamUpdate -> {
                if (streamUpdate.getKind() == AgentStreamEvent.THREAD_RUN_CREATED) {
                    System.out.println("----- Run started! -----");
                } else if (streamUpdate instanceof StreamRequiredAction) {
                    StreamRequiredAction actionUpdate = (StreamRequiredAction) streamUpdate;
                    AtomicReference<ThreadRun> streamRun = new AtomicReference<>(actionUpdate.getMessage());

                    while (streamRun.get().getStatus() == RunStatus.REQUIRES_ACTION) {
                        List<ToolOutput> toolOutputs = new ArrayList<>();

                        SubmitToolOutputsAction submitToolsOutputAction
                            = (SubmitToolOutputsAction) (streamRun.get().getRequiredAction());
                        for (RequiredToolCall toolCall : submitToolsOutputAction.getSubmitToolOutputs()
                            .getToolCalls()) {
                            toolOutputs.add(getResolvedToolOutput.apply(toolCall));
                        }

                        agentsClient
                            .submitToolOutputsToRunStreaming(streamRun.get().getThreadId(), streamRun.get().getId(),
                                toolOutputs)
                            .doOnNext(update -> {
                                if (update instanceof StreamRequiredAction) {
                                    streamRun.set(((StreamRequiredAction) update).getMessage());
                                } else if (update instanceof StreamMessageUpdate) {
                                    StreamMessageUpdate messageUpdate = (StreamMessageUpdate) update;
                                    printStreamUpdate(messageUpdate);
                                } else if (update.getKind() == AgentStreamEvent.THREAD_RUN_COMPLETED) {
                                    streamRun.set(((StreamThreadRunCreation) update).getMessage());
                                }
                            })
                            .blockLast();
                    }
                } else if (streamUpdate instanceof StreamMessageUpdate) {
                    StreamMessageUpdate messageUpdate = (StreamMessageUpdate) streamUpdate;
                    printStreamUpdate(messageUpdate);
                }
            }).blockLast();

            System.out.println();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Agent createCIAgent() {
        String agentName = UUID.randomUUID().toString();
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));
        ciAgent = agentsClient.createAgent(createAgentOptions);
        return ciAgent;
    }

    void printStreamUpdate(StreamMessageUpdate messageUpdate) {
        messageUpdate.getMessage().getDelta().getContent().stream().forEach(delta -> {
            if (delta instanceof MessageDeltaImageFileContent) {
                MessageDeltaImageFileContent imgContent = (MessageDeltaImageFileContent) delta;
                System.out.println("Image fileId: " + imgContent.getImageFile().getFileId());
            } else if (delta instanceof MessageDeltaTextContent) {
                MessageDeltaTextContent textContent = (MessageDeltaTextContent) delta;
                System.out.print(textContent.getText().getValue());
            }
        });
    }

    // Use "Map.of" if available
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
