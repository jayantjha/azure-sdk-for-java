// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.AgentThread;
import com.azure.ai.projects.models.CodeInterpreterToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.CreateRunOptions;
import com.azure.ai.projects.models.FileDetails;
import com.azure.ai.projects.models.FilePurpose;
import com.azure.ai.projects.models.FileSearchToolResource;
import com.azure.ai.projects.models.MessageContent;
import com.azure.ai.projects.models.MessageDeltaImageFileContent;
import com.azure.ai.projects.models.MessageDeltaTextContent;
import com.azure.ai.projects.models.MessageImageFileContent;
import com.azure.ai.projects.models.MessageRole;
import com.azure.ai.projects.models.MessageTextContent;
import com.azure.ai.projects.models.OpenAIFile;
import com.azure.ai.projects.models.OpenAIPageableListOfThreadMessage;
import com.azure.ai.projects.models.RunStatus;
import com.azure.ai.projects.models.ThreadMessage;
import com.azure.ai.projects.models.ThreadRun;
import com.azure.ai.projects.models.UpdateAgentOptions;
import com.azure.ai.projects.models.UploadFileRequest;
import com.azure.ai.projects.models.VectorStore;
import com.azure.ai.projects.models.VectorStoreConfiguration;
import com.azure.ai.projects.models.VectorStoreDataSource;
import com.azure.ai.projects.models.VectorStoreDataSourceAssetType;
import com.azure.ai.projects.models.VectorStoreStatus;
import com.azure.ai.projects.models.streaming.StreamMessageUpdate;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                System.out.println(threadRun.getLastError().getMessage());
                fail("Run failed: " + threadRun.getLastError().getMessage());
            }

            OpenAIPageableListOfThreadMessage runMessages = agentsClient.listMessages(thread.getId());
            assertNotNull(runMessages);
            assertFalse(runMessages.getData().isEmpty(), "Messages list should not be empty");

            boolean foundUserMessage = false;
            boolean foundAssistantMessage = false;

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

                if (message.getRole() == MessageRole.USER) {
                    foundUserMessage = true;
                } else if (message.getRole() == MessageRole.AGENT) {
                    foundAssistantMessage = true;
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
