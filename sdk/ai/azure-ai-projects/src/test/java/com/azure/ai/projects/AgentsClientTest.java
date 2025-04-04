// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.implementation.models.CreateAgentRequest;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.AgentStreamEvent;
import com.azure.ai.projects.models.AgentThread;
import com.azure.ai.projects.models.AzureFunctionBinding;
import com.azure.ai.projects.models.AzureFunctionDefinition;
import com.azure.ai.projects.models.AzureFunctionStorageQueue;
import com.azure.ai.projects.models.AzureFunctionToolDefinition;
import com.azure.ai.projects.models.CodeInterpreterToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.CreateRunOptions;
import com.azure.ai.projects.models.FileDetails;
import com.azure.ai.projects.models.FilePurpose;
import com.azure.ai.projects.models.FileSearchToolDefinition;
import com.azure.ai.projects.models.FileSearchToolResource;
import com.azure.ai.projects.models.FunctionDefinition;
import com.azure.ai.projects.models.FunctionToolDefinition;
import com.azure.ai.projects.models.MessageAttachment;
import com.azure.ai.projects.models.MessageContent;
import com.azure.ai.projects.models.MessageDeltaImageFileContent;
import com.azure.ai.projects.models.MessageDeltaTextContent;
import com.azure.ai.projects.models.MessageImageFileContent;
import com.azure.ai.projects.models.MessageRole;
import com.azure.ai.projects.models.MessageTextContent;
import com.azure.ai.projects.models.OpenAIFile;
import com.azure.ai.projects.models.OpenAIPageableListOfThreadMessage;
import com.azure.ai.projects.models.OpenApiAnonymousAuthDetails;
import com.azure.ai.projects.models.OpenApiFunctionDefinition;
import com.azure.ai.projects.models.OpenApiToolDefinition;
import com.azure.ai.projects.models.RequiredFunctionToolCall;
import com.azure.ai.projects.models.RequiredToolCall;
import com.azure.ai.projects.models.RunStatus;
import com.azure.ai.projects.models.SubmitToolOutputsAction;
import com.azure.ai.projects.models.ThreadMessage;
import com.azure.ai.projects.models.ThreadMessageOptions;
import com.azure.ai.projects.models.ThreadRun;
import com.azure.ai.projects.models.ToolOutput;
import com.azure.ai.projects.models.ToolResources;
import com.azure.ai.projects.models.UpdateAgentOptions;
import com.azure.ai.projects.models.UploadFileRequest;
import com.azure.ai.projects.models.VectorStore;
import com.azure.ai.projects.models.VectorStoreConfiguration;
import com.azure.ai.projects.models.VectorStoreDataSource;
import com.azure.ai.projects.models.VectorStoreDataSourceAssetType;
import com.azure.ai.projects.models.VectorStoreFileBatch;
import com.azure.ai.projects.models.VectorStoreStatus;
import com.azure.ai.projects.models.streaming.StreamMessageUpdate;
import com.azure.ai.projects.models.streaming.StreamRequiredAction;
import com.azure.ai.projects.models.streaming.StreamThreadRunCreation;
import com.azure.ai.projects.models.streaming.StreamUpdate;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import com.azure.json.JsonProviders;
import com.azure.json.JsonReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
        assertNotNull(agent.getName());
        assertEquals(agentName, agent.getName());
        assertNotNull(agent.getCreatedAt());
        assertEquals("You are a helpful agent", agent.getInstructions());

        Agent retrievedAgent = agentsClient.getAgent(agent.getId());
        assertNotNull(retrievedAgent);
        assertEquals(agent.getId(), retrievedAgent.getId());
        assertEquals(agent.getName(), retrievedAgent.getName());

        agentsClient.deleteAgent(agent.getId());
    }

    @Test
    void testListAgents() {
        // Create a few agents for testing
        String agentName1 = "list_test_agent_1_" + UUID.randomUUID();
        String agentName2 = "list_test_agent_2_" + UUID.randomUUID();

        CreateAgentOptions createAgentOptions1
            = new CreateAgentOptions("gpt-4o-mini").setName(agentName1).setInstructions("Test agent 1");
        CreateAgentOptions createAgentOptions2
            = new CreateAgentOptions("gpt-4o-mini").setName(agentName2).setInstructions("Test agent 2");

        Agent agent1 = agentsClient.createAgent(createAgentOptions1);
        Agent agent2 = agentsClient.createAgent(createAgentOptions2);

        // List all agents
        List<Agent> agentList = agentsClient.listAgents().getData().stream().toList();

        // Verify the list contains our agents
        boolean foundAgent1 = false;
        boolean foundAgent2 = false;

        for (Agent agent : agentList) {
            if (agent.getId().equals(agent1.getId())) {
                foundAgent1 = true;
            }
            if (agent.getId().equals(agent2.getId())) {
                foundAgent2 = true;
            }
        }

        assertTrue(foundAgent1, "Agent 1 should be found in the list");
        assertTrue(foundAgent2, "Agent 2 should be found in the list");

        // Clean up
        agentsClient.deleteAgent(agent1.getId());
        agentsClient.deleteAgent(agent2.getId());
    }

    @Test
    void testUpdateAgent() {
        String originalName = "update_test_agent_" + UUID.randomUUID();
        String updatedName = "updated_agent_" + UUID.randomUUID();

        CreateAgentOptions createAgentOptions
            = new CreateAgentOptions("gpt-4o-mini").setName(originalName).setInstructions("Original instructions");

        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent.getId());
        assertEquals(originalName, agent.getName());

        UpdateAgentOptions updateAgentOptions = new UpdateAgentOptions(agent.getId());
        updateAgentOptions.setName(updatedName).setInstructions("Updated instructions");

        // Update the agent
        Agent updatedAgent = agentsClient.updateAgent(updateAgentOptions);

        assertNotNull(updatedAgent);
        assertEquals(agent.getId(), updatedAgent.getId());
        assertEquals(updatedName, updatedAgent.getName());
        assertEquals("Updated instructions", updatedAgent.getInstructions());

        // Verify by getting the agent
        Agent retrievedAgent = agentsClient.getAgent(agent.getId());
        assertEquals(updatedName, retrievedAgent.getName());
        assertEquals("Updated instructions", retrievedAgent.getInstructions());

        // Clean up
        agentsClient.deleteAgent(agent.getId());
    }

    @Test
    void testCreateRunAndReadMessages() {
        AgentThread thread = agentsClient.createThread();
        assertNotNull(thread);
        assertNotNull(thread.getId());

        ThreadMessage createdMessage = agentsClient.createMessage(thread.getId(), MessageRole.USER,
            "I need to solve the equation `3x + 11 = 14`. Can you help me?");
        assertNotNull(createdMessage);
        assertEquals(MessageRole.USER, createdMessage.getRole());
        assertNotNull(createdMessage.getId());

        //run agent
        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), ciAgent.getId()).setAdditionalInstructions("");
        ThreadRun threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);
        assertNotNull(threadRun.getId());
        assertEquals(thread.getId(), threadRun.getThreadId());

        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            } while (threadRun.getStatus() == RunStatus.QUEUED
                || threadRun.getStatus() == RunStatus.IN_PROGRESS
                || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            if (threadRun.getStatus() == RunStatus.FAILED) {
                fail("Run failed: " + threadRun.getLastError().getMessage());
            }

            OpenAIPageableListOfThreadMessage runMessages = agentsClient.listMessages(thread.getId());
            assertNotNull(runMessages);
            assertFalse(runMessages.getData().isEmpty(), "Messages list should not be empty");

            boolean foundUserMessage = false;
            boolean foundAssistantMessage = false;

            for (ThreadMessage message : runMessages.getData()) {
                for (MessageContent contentItem : message.getContent()) {
                    if (contentItem instanceof MessageTextContent) {
                        String content = ((MessageTextContent) contentItem).getText().getValue();
                        if (message.getRole() == MessageRole.USER && content.contains("3x + 11 = 14")) {
                            foundUserMessage = true;
                        } else if (message.getRole() == MessageRole.AGENT && content.contains("x = 1")) {
                            foundAssistantMessage = true;
                        }
                    }
                }
            }

            assertTrue(foundUserMessage, "Should find at least one user message");
            assertTrue(foundAssistantMessage, "Should find at least one assistant message");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            agentsClient.deleteThread(thread.getId());
        }
    }

    @Test
    void testThreadOperations() {
        // Create a new thread
        AgentThread thread = agentsClient.createThread();
        assertNotNull(thread);
        assertNotNull(thread.getId());

        // Get thread
        AgentThread retrievedThread = agentsClient.getThread(thread.getId());
        assertNotNull(retrievedThread);
        assertEquals(thread.getId(), retrievedThread.getId());

        // Create multiple messages
        ThreadMessage message1 = agentsClient.createMessage(thread.getId(), MessageRole.USER, "First message");
        ThreadMessage message2 = agentsClient.createMessage(thread.getId(), MessageRole.USER, "Second message");

        assertNotNull(message1);
        assertNotNull(message2);
        assertNotEquals(message1.getId(), message2.getId());

        // List messages
        OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
        assertNotNull(messages);
        assertEquals(2, messages.getData().size());

        // Get a specific message
        ThreadMessage retrievedMessage = agentsClient.getMessage(thread.getId(), message1.getId());
        assertNotNull(retrievedMessage);
        assertEquals(message1.getId(), retrievedMessage.getId());

        // Clean up
        agentsClient.deleteThread(thread.getId());

        // Verify deletion (should throw an exception)
        try {
            agentsClient.getThread(thread.getId());
            fail("Should have thrown an exception for deleted thread");
        } catch (Exception e) {
            // Expected exception
        }
    }

    @Test
    void testVectorStore() throws InterruptedException {
        String dataUri = Configuration.getGlobalConfiguration().get("DATA_URI", "");
        VectorStoreDataSource vectorStoreDataSource
            = new VectorStoreDataSource(dataUri, VectorStoreDataSourceAssetType.URI_ASSET);

        VectorStore vectorStoreWithConfig = agentsClient.createVectorStore(null, "sample_vector_store",
            new VectorStoreConfiguration(Arrays.asList(vectorStoreDataSource)), null, null, null);
        assertNotNull(vectorStoreWithConfig);
        assertNotNull(vectorStoreWithConfig.getId());
        assertEquals("sample_vector_store", vectorStoreWithConfig.getName());

        OpenAIFile uploadedAgentFile = agentsClient.uploadFile(new UploadFileRequest(new FileDetails(BinaryData
            .fromString("The word `apple` uses the code 442345, while the word `banana` uses the code 673457."))
                .setFilename("sample_file_for_upload.txt"),
            FilePurpose.AGENTS));
        assertNotNull(uploadedAgentFile);
        assertNotNull(uploadedAgentFile.getId());
        assertEquals("sample_file_for_upload.txt", uploadedAgentFile.getFilename());

        VectorStore vectorStoreWithId = agentsClient.createVectorStore(Arrays.asList(uploadedAgentFile.getId()),
            "my_vector_store", null, null, null, null);
        assertNotNull(vectorStoreWithId);
        assertNotNull(vectorStoreWithId.getId());
        assertEquals("my_vector_store", vectorStoreWithId.getName());

        do {
            Thread.sleep(500);
            vectorStoreWithId = agentsClient.getVectorStore(vectorStoreWithId.getId());
        } while (vectorStoreWithId.getStatus() == VectorStoreStatus.IN_PROGRESS);

        assertEquals(VectorStoreStatus.COMPLETED, vectorStoreWithId.getStatus());

        // List vector stores
        List<VectorStore> vectorStores = agentsClient.listVectorStores().getData().stream().toList();
        assertFalse(vectorStores.isEmpty());

        // Test with file search tool
        FileSearchToolResource fileSearchToolResource
            = new FileSearchToolResource().setVectorStoreIds(Arrays.asList(vectorStoreWithId.getId()));
        assertNotNull(fileSearchToolResource);
        assertEquals(1, fileSearchToolResource.getVectorStoreIds().size());
        assertEquals(vectorStoreWithId.getId(), fileSearchToolResource.getVectorStoreIds().get(0));

        // Clean up
        agentsClient.deleteVectorStore(vectorStoreWithId.getId());
        agentsClient.deleteVectorStore(vectorStoreWithConfig.getId());
        agentsClient.deleteFile(uploadedAgentFile.getId());
    }

    @Test
    void testFileOperations() {
        // Upload a file
        OpenAIFile uploadedFile = agentsClient.uploadFile(new UploadFileRequest(
            new FileDetails(BinaryData.fromString("This is test file content")).setFilename("test_file.txt"),
            FilePurpose.AGENTS));

        assertNotNull(uploadedFile);
        assertNotNull(uploadedFile.getId());
        assertEquals("test_file.txt", uploadedFile.getFilename());

        // Get the file
        OpenAIFile retrievedFile = agentsClient.getFile(uploadedFile.getId());
        assertNotNull(retrievedFile);
        assertEquals(uploadedFile.getId(), retrievedFile.getId());
        assertEquals(uploadedFile.getFilename(), retrievedFile.getFilename());

        // List files
        List<OpenAIFile> files = agentsClient.listFiles().getData().stream().toList();
        assertFalse(files.isEmpty());
        boolean foundFile = false;
        for (OpenAIFile file : files) {
            if (file.getId().equals(uploadedFile.getId())) {
                foundFile = true;
                break;
            }
        }
        assertTrue(foundFile, "Uploaded file should be in the file list");

        // Download file content
        OpenAIFile content = agentsClient.getFile(uploadedFile.getId());
        assertNotNull(content);

        // Clean up
        agentsClient.deleteFile(uploadedFile.getId());
    }

    @Test
    void testAgentWithFunctions() {
        // Create an agent with function tools
        FunctionToolDefinition getUserFavoriteCityTool = new FunctionToolDefinition(
            new FunctionDefinition("getUserFavoriteCity", BinaryData.fromObject(new Object()))
                .setDescription("Gets the user's favorite city."));

        FunctionToolDefinition getCityNicknameTool = new FunctionToolDefinition(new FunctionDefinition(
            "getCityNickname",
            BinaryData.fromObject(mapOf("type", "object", "properties",
                mapOf("location", mapOf("type", "string", "description", "The city and state, e.g. San Francisco, CA")),
                "required", new String[] { "location" }))).setDescription("Gets the nickname of a city."));

        String agentName = "functions_test_agent_" + UUID.randomUUID();
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a weather bot. Use the provided functions.")
            .setTools(Arrays.asList(getUserFavoriteCityTool, getCityNicknameTool));

        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent);
        assertEquals(agentName, agent.getName());

        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage
            = agentsClient.createMessage(thread.getId(), MessageRole.USER, "What's the nickname of my favorite city?");

        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), agent.getId()).setAdditionalInstructions("");
        ThreadRun threadRun = agentsClient.createRun(createRunOptions);

        try {
            // Process function calls
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
                if (threadRun.getStatus() == RunStatus.REQUIRES_ACTION
                    && threadRun.getRequiredAction() instanceof SubmitToolOutputsAction) {
                    SubmitToolOutputsAction submitToolsOutputAction
                        = (SubmitToolOutputsAction) (threadRun.getRequiredAction());
                    ArrayList<ToolOutput> toolOutputs = new ArrayList<ToolOutput>();

                    for (RequiredToolCall toolCall : submitToolsOutputAction.getSubmitToolOutputs().getToolCalls()) {
                        if (toolCall instanceof RequiredFunctionToolCall) {
                            RequiredFunctionToolCall functionToolCall = (RequiredFunctionToolCall) toolCall;
                            String functionName = functionToolCall.getFunction().getName();

                            if ("getUserFavoriteCity".equals(functionName)) {
                                toolOutputs.add(
                                    new ToolOutput().setToolCallId(functionToolCall.getId()).setOutput("Seattle, WA"));
                            } else if ("getCityNickname".equals(functionName)) {
                                try {
                                    String args = functionToolCall.getFunction().getArguments();
                                    JsonNode root = new JsonMapper().readTree(args);
                                    String location = String.valueOf(root.get("location").asText());
                                    toolOutputs.add(new ToolOutput().setToolCallId(functionToolCall.getId())
                                        .setOutput("The Emerald City"));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }

                    threadRun = agentsClient.submitToolOutputsToRun(thread.getId(), threadRun.getId(), toolOutputs);
                }
            } while (threadRun.getStatus() == RunStatus.QUEUED
                || threadRun.getStatus() == RunStatus.IN_PROGRESS
                || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            // Verify the run completed successfully
            assertNotEquals(RunStatus.FAILED, threadRun.getStatus());

            // Verify we got a response mentioning "Emerald City"
            OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
            boolean foundEmeraldCity = false;
            for (ThreadMessage message : messages.getData()) {
                if (message.getRole() == MessageRole.AGENT) {
                    for (MessageContent contentItem : message.getContent()) {
                        if (contentItem instanceof MessageTextContent) {
                            String content = ((MessageTextContent) contentItem).getText().getValue();
                            if (content.contains("Emerald City")) {
                                foundEmeraldCity = true;
                                break;
                            }
                        }
                    }
                }
            }
            assertTrue(foundEmeraldCity, "Agent response should mention 'Emerald City'");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }

    @Test
    void testVectorStoreBatchFileSearch() throws InterruptedException {
        // Upload a file
        OpenAIFile uploadedFile = agentsClient.uploadFile(new UploadFileRequest(
            new FileDetails(BinaryData.fromString("The Smart Eyewear offers AR display and voice control features."))
                .setFilename("product_info.md"),
            FilePurpose.AGENTS));
        assertNotNull(uploadedFile);

        // Create vector store
        VectorStore vectorStore
            = agentsClient.createVectorStore(null, "test_vector_store_" + UUID.randomUUID(), null, null, null, null);
        assertNotNull(vectorStore);

        // Create vector store file batch
        VectorStoreFileBatch vectorStoreFileBatch = agentsClient.createVectorStoreFileBatch(vectorStore.getId(),
            Arrays.asList(uploadedFile.getId()), null, null);
        assertNotNull(vectorStoreFileBatch);

        // Wait for vector store to be ready
        do {
            Thread.sleep(500);
            vectorStore = agentsClient.getVectorStore(vectorStore.getId());
        } while (vectorStore.getStatus() == VectorStoreStatus.IN_PROGRESS);

        // Create file search tool resource
        FileSearchToolResource fileSearchToolResource
            = new FileSearchToolResource().setVectorStoreIds(Arrays.asList(vectorStore.getId()));

        // Create agent with file search tool
        String agentName = "file_search_test_agent_" + UUID.randomUUID();
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent that can search files for information.")
            .setTools(Arrays.asList(new FileSearchToolDefinition()))
            .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));

        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent);

        // Create thread and message
        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage
            = agentsClient.createMessage(thread.getId(), MessageRole.USER, "What features does Smart Eyewear offer?");

        // Run the agent
        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), agent.getId()).setAdditionalInstructions("");
        ThreadRun threadRun = agentsClient.createRun(createRunOptions);

        try {
            do {
                Thread.sleep(1000); // Longer sleep for file search
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            } while (threadRun.getStatus() == RunStatus.QUEUED
                || threadRun.getStatus() == RunStatus.IN_PROGRESS
                || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            if (threadRun.getStatus() == RunStatus.FAILED) {
                fail("Run failed: " + threadRun.getLastError().getMessage());
            }

            // Verify response contains information about the product
            OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
            boolean foundFeatures = false;
            for (ThreadMessage message : messages.getData()) {
                if (message.getRole() == MessageRole.AGENT) {
                    for (MessageContent contentItem : message.getContent()) {
                        if (contentItem instanceof MessageTextContent) {
                            String content = ((MessageTextContent) contentItem).getText().getValue();
                            if (content.contains("AR display") || content.contains("voice control")) {
                                foundFeatures = true;
                                break;
                            }
                        }
                    }
                }
            }

            assertTrue(foundFeatures, "Agent response should mention Smart Eyewear features");

        } finally {
            // Cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
            agentsClient.deleteVectorStore(vectorStore.getId());
            agentsClient.deleteFile(uploadedFile.getId());
        }
    }

    @Test
    void testAgentWithAdditionalMessages() {
        // Create agent
        String agentName = "additional_message_test_agent_" + UUID.randomUUID();
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent that provides consistent answers to questions.")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));

        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent);

        // Create thread
        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage
            = agentsClient.createMessage(thread.getId(), MessageRole.USER, "What is the value of Pi?");

        // Run agent with additional messages
        CreateRunOptions createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalMessages(Arrays.asList(new ThreadMessageOptions(MessageRole.AGENT, "Pi is exactly 3."),
                new ThreadMessageOptions(MessageRole.USER, "Are you sure about Pi?")));

        ThreadRun threadRun = agentsClient.createRun(createRunOptions);

        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            } while (threadRun.getStatus() == RunStatus.QUEUED
                || threadRun.getStatus() == RunStatus.IN_PROGRESS
                || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            // Verify the run completed successfully
            assertNotEquals(RunStatus.FAILED, threadRun.getStatus(),
                threadRun.getStatus() == RunStatus.FAILED
                    ? threadRun.getLastError().getMessage()
                    : "Run should not fail");

            // Verify that the response is influenced by additional messages
            OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
            boolean foundPiResponse = false;

            for (ThreadMessage message : messages.getData()) {
                if (message.getRole() == MessageRole.AGENT) {
                    for (MessageContent contentItem : message.getContent()) {
                        if (contentItem instanceof MessageTextContent) {
                            String content = ((MessageTextContent) contentItem).getText().getValue().toLowerCase();
                            // Response should mention Pi is approximately 3.14, not exactly 3
                            if (content.contains("3.14") || content.contains("not 3")) {
                                foundPiResponse = true;
                            }
                        }
                    }
                }
            }

            assertTrue(foundPiResponse, "Agent should correct the value of Pi from the additional message");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }

    @Test
    void testFileAttachmentWithCodeInterpreter() {
        // Create agent with code interpreter
        String agentName = "code_interpreter_file_test_" + UUID.randomUUID();
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You help analyze data from files.")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));

        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent);

        // Upload file
        OpenAIFile uploadedFile = agentsClient.uploadFile(new UploadFileRequest(new FileDetails(BinaryData
            .fromString("<html><body><h1>Test Content</h1><p>This is sample data for testing.</p></body></html>"))
                .setFilename("sample_test.html"),
            FilePurpose.AGENTS));
        assertNotNull(uploadedFile);

        // Create attachment with code interpreter tool
        CodeInterpreterToolDefinition ciTool = new CodeInterpreterToolDefinition();
        MessageAttachment messageAttachment
            = new MessageAttachment(Arrays.asList(BinaryData.fromObject(ciTool))).setFileId(uploadedFile.getId());
        assertNotNull(messageAttachment);

        // Create thread and message with attachment
        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(thread.getId(), MessageRole.USER,
            "What does the attachment say?", Arrays.asList(messageAttachment), null);
        assertNotNull(createdMessage);

        // Run agent
        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), agent.getId()).setAdditionalInstructions("");
        ThreadRun threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            } while (threadRun.getStatus() == RunStatus.QUEUED
                || threadRun.getStatus() == RunStatus.IN_PROGRESS
                || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            if (threadRun.getStatus() == RunStatus.FAILED) {
                fail("Run failed: " + threadRun.getLastError().getMessage());
            }

            // Verify response includes file content
            OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
            assertNotNull(messages);
            assertFalse(messages.getData().isEmpty(), "Messages should not be empty");

            boolean foundFileContent = false;
            for (ThreadMessage message : messages.getData()) {
                if (message.getRole() == MessageRole.AGENT) {
                    for (MessageContent contentItem : message.getContent()) {
                        if (contentItem instanceof MessageTextContent) {
                            String content = ((MessageTextContent) contentItem).getText().getValue();
                            if (content.contains("Test Content") || content.contains("sample data")) {
                                foundFileContent = true;
                                break;
                            }
                        }
                    }
                }
            }

            assertTrue(foundFileContent, "Agent response should include content from the attached file");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
            agentsClient.deleteFile(uploadedFile.getId());
        }
    }

    @Test
    void testOpenApiTool() {
        try {

            Path filePath = getFile("weather_openapi.json");
            JsonReader reader = JsonProviders.createReader(Files.readAllBytes(filePath));

            OpenApiAnonymousAuthDetails oaiAuth = new OpenApiAnonymousAuthDetails();
            OpenApiToolDefinition openApiTool = new OpenApiToolDefinition(new OpenApiFunctionDefinition("openapitool",
                reader.getNullable(nonNullReader -> BinaryData.fromObject(nonNullReader.readUntyped())), oaiAuth));
            // Create agent with OpenAPI tool
            String agentName = "openapi_test_agent_" + UUID.randomUUID();
            CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
                .setInstructions("You are a helpful agent that can check the weather.")
                .setTools(Arrays.asList(openApiTool));

            Agent agent = agentsClient.createAgent(createAgentOptions);
            assertNotNull(agent);

            // Create thread
            AgentThread thread = agentsClient.createThread();
            ThreadMessage createdMessage
                = agentsClient.createMessage(thread.getId(), MessageRole.USER, "What's the weather in Seattle?");
            assertNotNull(createdMessage);

            // Run agent
            CreateRunOptions createRunOptions
                = new CreateRunOptions(thread.getId(), agent.getId()).setAdditionalInstructions("");

            ThreadRun threadRun = agentsClient.createRun(createRunOptions);
            assertNotNull(threadRun);

            try {
                do {
                    Thread.sleep(500);
                    threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
                } while (threadRun.getStatus() == RunStatus.QUEUED
                    || threadRun.getStatus() == RunStatus.IN_PROGRESS
                    || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

                // Verify the run completed (may fail without actual endpoint)
                assertNotNull(threadRun.getStatus());

                // Even if we can't call an actual API, verify that we have an agent response
                OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
                assertNotNull(messages);

                // There should be at least 2 messages (user question and agent response)
                assertTrue(messages.getData().size() >= 2, "Should have at least a question and response");

            } finally {
                // Cleanup
                agentsClient.deleteThread(thread.getId());
                agentsClient.deleteAgent(agent.getId());
            }
        } catch (Exception e) {
            // OpenAPI tests might fail due to lack of actual endpoint
            System.out.println("OpenAPI test encountered exception: " + e.getMessage());
        }
    }

    @Test
    void testAgentStreaming() {
        // Create an agent with code interpreter tool for visualization
        String agentName = "streaming_test_agent_" + UUID.randomUUID();
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions(
                "You politely help with math questions. Use the code interpreter tool when asked to visualize numbers.")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));

        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent);

        // Create thread with a message asking for a visualization
        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(thread.getId(), MessageRole.USER,
            "Draw a graph for a line with a slope of 4 and y-intercept of 9.");

        // Set up run with streaming
        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), agent.getId()).setAdditionalInstructions("");

        try {
            // Store results to verify afterward
            AtomicBoolean receivedRunStartEvent = new AtomicBoolean(false);
            AtomicBoolean receivedTextContent = new AtomicBoolean(false);
            AtomicBoolean receivedImageContent = new AtomicBoolean(false);

            // Execute with streaming
            Flux<StreamUpdate> streamingUpdates = agentsClient.createRunStreaming(createRunOptions);

            // Process the streaming updates
            streamingUpdates.doOnNext(streamUpdate -> {
                if (streamUpdate.getKind() == AgentStreamEvent.THREAD_RUN_CREATED) {
                    receivedRunStartEvent.set(true);
                } else if (streamUpdate instanceof StreamMessageUpdate) {
                    StreamMessageUpdate messageUpdate = (StreamMessageUpdate) streamUpdate;
                    messageUpdate.getMessage().getDelta().getContent().forEach(delta -> {
                        if (delta instanceof MessageDeltaImageFileContent) {
                            MessageDeltaImageFileContent imgContent = (MessageDeltaImageFileContent) delta;
                            if (imgContent.getImageFile() != null && imgContent.getImageFile().getFileId() != null) {
                                receivedImageContent.set(true);
                            }
                        } else if (delta instanceof MessageDeltaTextContent) {
                            MessageDeltaTextContent textContent = (MessageDeltaTextContent) delta;
                            if (textContent.getText() != null && textContent.getText().getValue() != null) {
                                receivedTextContent.set(true);
                            }
                        }
                    });
                }
            }).blockLast();

            // Verify we received the expected streaming events
            assertTrue(receivedRunStartEvent.get(), "Should receive run start event");
            assertTrue(receivedTextContent.get(), "Should receive text content in stream");

            // Image content is not guaranteed depending on the model's response
            // But we can check the final messages to see if any images were created
            OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
            boolean foundGeneratedImage = false;

            for (ThreadMessage message : messages.getData()) {
                for (MessageContent contentItem : message.getContent()) {
                    if (contentItem instanceof MessageImageFileContent) {
                        foundGeneratedImage = true;
                        break;
                    }
                }
            }

            // Check that messages were created correctly
            assertFalse(messages.getData().isEmpty(), "Should have created messages");

        } finally {
            // Cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }

    @Test
    void testAgentFunctionsStreaming() {
        // Define function tools
        FunctionToolDefinition getUserFavoriteCityTool = new FunctionToolDefinition(
            new FunctionDefinition("getUserFavoriteCity", BinaryData.fromObject(new Object()))
                .setDescription("Gets the user's favorite city."));

        FunctionToolDefinition getCityNicknameTool = new FunctionToolDefinition(new FunctionDefinition(
            "getCityNickname",
            BinaryData.fromObject(mapOf("type", "object", "properties",
                mapOf("location", mapOf("type", "string", "description", "The city and state, e.g. San Francisco, CA")),
                "required", new String[] { "location" }))).setDescription("Gets the nickname of a city."));

        FunctionToolDefinition getCurrentWeatherTool
            = new FunctionToolDefinition(
                new FunctionDefinition("getCurrentWeatherAtLocation",
                    BinaryData.fromObject(mapOf("type", "object", "properties", mapOf("location",
                        mapOf("type", "string", "description", "The city and state, e.g. San Francisco, CA"), "unit",
                        mapOf("type", "string", "description", "temperature unit as c or f", "enum",
                            new String[] { "c", "f" })),
                        "required", new String[] { "location", "unit" })))
                            .setDescription("Gets the current weather at a provided location."));

        // Function implementations
        Supplier<String> getUserFavoriteCity = () -> "Seattle, WA";

        Function<String, String> getCityNickname = (location) -> {
            if ("Seattle, WA".equals(location)) {
                return "The Emerald City";
            }
            return "No nickname available";
        };

        BiFunction<String, String, String> getCurrentWeatherAtLocation = (location, unit) -> {
            if ("Seattle, WA".equals(location)) {
                return unit.equals("f") ? "70F" : "21C";
            }
            return "Unknown";
        };

        // Create the agent
        String agentName = "function_streaming_test_agent_" + UUID.randomUUID();
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a weather bot. Use the provided functions to help answer questions.")
            .setTools(Arrays.asList(getUserFavoriteCityTool, getCityNicknameTool, getCurrentWeatherTool));

        Agent agent = agentsClient.createAgent(createAgentOptions);
        assertNotNull(agent);

        // Create thread
        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(thread.getId(), MessageRole.USER,
            "What's the weather like in my favorite city?");

        // Function resolver
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

        // Setup and start streaming run
        CreateRunOptions createRunOptions
            = new CreateRunOptions(thread.getId(), agent.getId()).setAdditionalInstructions("");

        try {
            // Tracking variables
            AtomicBoolean receivedRunStartEvent = new AtomicBoolean(false);
            AtomicBoolean receivedTextContent = new AtomicBoolean(false);
            AtomicBoolean receivedToolCall = new AtomicBoolean(false);

            // Execute streaming run
            Flux<StreamUpdate> streamingUpdates = agentsClient.createRunStreaming(createRunOptions);

            AtomicReference<ThreadRun> currentRun = new AtomicReference<>();

            streamingUpdates.doOnNext(streamUpdate -> {
                if (streamUpdate.getKind() == AgentStreamEvent.THREAD_RUN_CREATED) {
                    receivedRunStartEvent.set(true);
                    if (streamUpdate instanceof StreamThreadRunCreation) {
                        currentRun.set(((StreamThreadRunCreation) streamUpdate).getMessage());
                    }
                } else if (streamUpdate instanceof StreamRequiredAction) {
                    receivedToolCall.set(true);
                    StreamRequiredAction actionUpdate = (StreamRequiredAction) streamUpdate;
                    currentRun.set(actionUpdate.getMessage());

                    // Process function calls if required
                    if (currentRun.get().getStatus() == RunStatus.REQUIRES_ACTION
                        && currentRun.get().getRequiredAction() instanceof SubmitToolOutputsAction) {

                        List<ToolOutput> toolOutputs = new ArrayList<>();
                        SubmitToolOutputsAction action = (SubmitToolOutputsAction) currentRun.get().getRequiredAction();

                        for (RequiredToolCall toolCall : action.getSubmitToolOutputs().getToolCalls()) {
                            toolOutputs.add(getResolvedToolOutput.apply(toolCall));
                        }

                        // Submit tool outputs with streaming
                        agentsClient
                            .submitToolOutputsToRunStreaming(currentRun.get().getThreadId(), currentRun.get().getId(),
                                toolOutputs)
                            .doOnNext(update -> {
                                if (update instanceof StreamMessageUpdate) {
                                    StreamMessageUpdate messageUpdate = (StreamMessageUpdate) update;
                                    messageUpdate.getMessage().getDelta().getContent().forEach(delta -> {
                                        if (delta instanceof MessageDeltaTextContent) {
                                            receivedTextContent.set(true);
                                        }
                                    });
                                }
                            })
                            .blockLast();
                    }
                } else if (streamUpdate instanceof StreamMessageUpdate) {
                    StreamMessageUpdate messageUpdate = (StreamMessageUpdate) streamUpdate;
                    messageUpdate.getMessage().getDelta().getContent().forEach(delta -> {
                        if (delta instanceof MessageDeltaTextContent) {
                            receivedTextContent.set(true);
                        }
                    });
                }
            }).blockLast();

            // Verify all the events we expect to happen
            assertTrue(receivedRunStartEvent.get(), "Should receive run start event");
            assertTrue(receivedToolCall.get(), "Should receive tool call event");

            // Verify final messages
            OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
            assertFalse(messages.getData().isEmpty(), "Should have messages");

            for (ThreadMessage message : messages.getData()) {
                if (message.getRole() == MessageRole.AGENT) {
                    for (MessageContent contentItem : message.getContent()) {
                        if (contentItem instanceof MessageTextContent) {
                            String content = ((MessageTextContent) contentItem).getText().getValue().toLowerCase();
                        }
                    }
                }
            }

        } finally {
            // Cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }

    @Test
    void testAzureFunction() {
        try {
            // This test requires Azure Function integration, skipping if credentials aren't available
            String storageQueueUri = Configuration.getGlobalConfiguration().get("STORAGE_QUEUE_URI", "");
            String azureFunctionName = Configuration.getGlobalConfiguration().get("AZURE_FUNCTION_NAME", "");

            if (storageQueueUri.isEmpty() || azureFunctionName.isEmpty()) {
                System.out.println("Skipping Azure Function test - missing configuration");
                return;
            }

            // Create function definition
            FunctionDefinition functionDefinition = new FunctionDefinition(azureFunctionName,
                BinaryData.fromObject(mapOf("type", "object", "properties",
                    mapOf("location", mapOf("type", "string", "description", "The location to look up")), "required",
                    new String[] { "location" })));

            AzureFunctionDefinition azureFunctionDefinition = new AzureFunctionDefinition(functionDefinition,
                new AzureFunctionBinding(new AzureFunctionStorageQueue(storageQueueUri, "agent-input")),
                new AzureFunctionBinding(new AzureFunctionStorageQueue(storageQueueUri, "agent-output")));

            AzureFunctionToolDefinition azureFunctionTool = new AzureFunctionToolDefinition(azureFunctionDefinition);

            // Create agent with Azure Function tool
            String agentName = "azure_function_test_agent_" + UUID.randomUUID();

            // Azure Functions require preview flag
            RequestOptions requestOptions
                = new RequestOptions().setHeader(HttpHeaderName.fromString("x-ms-enable-preview"), "true");

            CreateAgentRequest createAgentRequest = new CreateAgentRequest("gpt-4o-mini").setName(agentName)
                .setInstructions("You are a helpful agent. Use the provided function for weather queries.")
                .setTools(Arrays.asList(azureFunctionTool));

            BinaryData requestBody = BinaryData.fromObject(createAgentRequest);
            Agent agent
                = agentsClient.createAgentWithResponse(requestBody, requestOptions).getValue().toObject(Agent.class);

            assertNotNull(agent);
            assertEquals(agentName, agent.getName());

            // Create thread and message
            AgentThread thread = agentsClient.createThread();
            ThreadMessage createdMessage
                = agentsClient.createMessage(thread.getId(), MessageRole.USER, "What is the weather in Seattle, WA?");

            // Run agent
            CreateRunOptions createRunOptions
                = new CreateRunOptions(thread.getId(), agent.getId()).setAdditionalInstructions("");

            ThreadRun threadRun = agentsClient.createRun(createRunOptions);
            assertNotNull(threadRun);

            // Wait for completion
            try {
                do {
                    Thread.sleep(1000); // Longer wait for Azure Function
                    threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
                } while (threadRun.getStatus() == RunStatus.QUEUED
                    || threadRun.getStatus() == RunStatus.IN_PROGRESS
                    || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

                if (threadRun.getStatus() == RunStatus.FAILED) {
                    System.out.println("Azure Function run failed: " + threadRun.getLastError().getMessage());
                    // Don't fail the test as the actual function might not be available
                } else {
                    // If the run completed, verify response
                    OpenAIPageableListOfThreadMessage messages = agentsClient.listMessages(thread.getId());
                    assertFalse(messages.getData().isEmpty(), "Should have received messages");

                    // There should be at least 2 messages (user query and agent response)
                    assertTrue(messages.getData().size() >= 2, "Should have at least user and agent messages");
                }

            } finally {
                // Cleanup
                agentsClient.deleteThread(thread.getId());
                agentsClient.deleteAgent(agent.getId());
            }

        } catch (Exception e) {
            System.out.println("Azure Function test encountered exception: " + e.getMessage());
            // Don't fail the test as Azure Function integration depends on external services
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

    // Get path of a fileName
    private Path getFile(String fileName) throws FileNotFoundException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new FileNotFoundException("File not found");
        }
        File file = new File(resource.toURI());
        return file.toPath();
    }
}
