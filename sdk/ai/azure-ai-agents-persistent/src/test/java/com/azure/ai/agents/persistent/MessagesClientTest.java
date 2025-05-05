package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.CreateAgentOptions;
import com.azure.ai.agents.persistent.models.MessageRole;
import com.azure.ai.agents.persistent.models.PersistentAgent;
import com.azure.ai.agents.persistent.models.PersistentAgentThread;
import com.azure.ai.agents.persistent.models.ThreadDeletionStatus;
import com.azure.ai.agents.persistent.models.ThreadMessage;
import com.azure.ai.agents.persistent.models.ToolResources;
import com.azure.core.http.HttpClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.HashMap;
import java.util.List;

import static com.azure.ai.agents.persistent.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessagesClientTest extends ClientTestBase {

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testMessagesClientOperations(HttpClient httpClient) {
        PersistentAgentsAdministrationClientBuilder clientBuilder = getClientBuilder(httpClient);
        PersistentAgentsAdministrationClient agentsClient = clientBuilder.buildClient();
        ThreadsClient threadsClient = clientBuilder.buildThreadsClient();
        MessagesClient messagesClient = clientBuilder.buildMessagesClient();

        assertNotNull(agentsClient, "PersistentAgentsAdministrationClient should not be null");
        assertNotNull(threadsClient, "ThreadsClient should not be null");

        String agentName = "TestThreadOperationsAgent";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You are a helpful agent");

        PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
        assertAgent(agent);

        PersistentAgentThread thread = threadsClient.createThread();

        // Test create message
        ThreadMessage createdMessage = messagesClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What do you know about Microsoft");
        assertNotNull(createdMessage, "Created message should not be null");
        assertNotNull(createdMessage.getId(), "Message ID should not be null");
        assertEquals(MessageRole.USER, createdMessage.getRole(), "Message role should be USER");

        // Test retrieve message
        ThreadMessage retrievedMessage = messagesClient.getMessage(thread.getId(), createdMessage.getId());
        assertNotNull(retrievedMessage, "Retrieved message should not be null");
        assertEquals(createdMessage.getId(), retrievedMessage.getId(), "Retrieved message ID should match created message ID");

        // Test create message with metadata
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("source", "test");
        metadata.put("priority", "high");

        ThreadMessage messageWithMetadata = messagesClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "This is a message with metadata",
            null,
            metadata);
        assertNotNull(messageWithMetadata, "Message with metadata should not be null");
        assertNotNull(messageWithMetadata.getMetadata(), "Message metadata should not be null");
        assertEquals("test", messageWithMetadata.getMetadata().get("source"), "Message metadata should contain source");
        assertEquals("high", messageWithMetadata.getMetadata().get("priority"), "Message metadata should contain priority");

        // Test update message
        HashMap<String, String> updatedMetadata = new HashMap<>();
        updatedMetadata.put("updated", "true");
        updatedMetadata.put("timestamp", String.valueOf(System.currentTimeMillis()));

        ThreadMessage updatedMessage = messagesClient.updateMessage(thread.getId(), createdMessage.getId(), updatedMetadata);
        assertNotNull(updatedMessage, "Updated message should not be null");
        assertNotNull(updatedMessage.getMetadata(), "Updated message metadata should not be null");
        assertEquals("true", updatedMessage.getMetadata().get("updated"), "Updated message metadata should contain updated flag");

        // Test list messages
        var messagesList = messagesClient.listMessages(thread.getId());
        assertNotNull(messagesList, "Messages list should not be null");
        assertNotNull(messagesList.getData(), "Messages data should not be null");
        assertTrue(messagesList.getData().size() >= 2, "Should have at least 2 messages");

        // Test list messages with parameters
        var filteredMessages = messagesClient.listMessages(
            thread.getId(),
            null,    // runId
            10,      // limit
            null,    // order
            null,    // after
            null);   // before
        assertNotNull(filteredMessages, "Filtered messages should not be null");
        assertNotNull(filteredMessages.getData(), "Filtered messages data should not be null");
        assertTrue(filteredMessages.getData().size() <= 10, "Should have at most 10 messages");

        // Clean up
        ThreadDeletionStatus deletionStatus = threadsClient.deleteThread(thread.getId());
        assertNotNull(deletionStatus, "Deletion status should not be null");
        assertTrue(deletionStatus.isDeleted(), "Thread should be deleted");

        // Clean up agent
        agentsClient.deleteAgent(agent.getId());
    }

}
