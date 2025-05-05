package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.implementation.models.UpdateThreadRequest;
import com.azure.ai.agents.persistent.models.AgentDeletionStatus;
import com.azure.ai.agents.persistent.models.CreateAgentOptions;
import com.azure.ai.agents.persistent.models.PersistentAgent;
import com.azure.ai.agents.persistent.models.PersistentAgentThread;
import com.azure.ai.agents.persistent.models.ThreadDeletionStatus;
import com.azure.ai.agents.persistent.models.ToolResources;
import com.azure.ai.agents.persistent.models.UpdateAgentOptions;
import com.azure.core.http.HttpClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.HashMap;
import java.util.List;

import static com.azure.ai.agents.persistent.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadsClientTest extends ClientTestBase {

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testThreadsClientOperations(HttpClient httpClient) {
        PersistentAgentsAdministrationClientBuilder clientBuilder = getClientBuilder(httpClient);
        PersistentAgentsAdministrationClient agentsClient = clientBuilder.buildClient();
        ThreadsClient threadsClient = clientBuilder.buildThreadsClient();

        assertNotNull(agentsClient, "PersistentAgentsAdministrationClient should not be null");
        assertNotNull(threadsClient, "ThreadsClient should not be null");

        String agentName = "TestThreadOperationsAgent";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You are a helpful agent");

        PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
        assertAgent(agent);

        PersistentAgentThread thread = threadsClient.createThread();
        assertNotNull(thread, "Thread should not be null");
        assertNotNull(thread.getId(), "Thread ID should not be null");

        List<PersistentAgentThread> threadList = threadsClient.listThreads().getData();
        assertNotNull(threadList, "Thread list should not be null");
        assertTrue(threadList.size() > 0, "Thread list should not be empty");

        PersistentAgentThread retrievedThread = threadsClient.getThread(thread.getId());
        assertNotNull(retrievedThread, "Retrieved thread should not be null");
        assertTrue(retrievedThread.getId().equals(thread.getId()), "Retrieved thread ID should match created thread ID");

        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("testKey", "testValue");
        PersistentAgentThread updatedThread = threadsClient.updateThread(thread.getId(), new ToolResources(), metadata);
        assertNotNull(updatedThread, "Updated thread should not be null");
        assertTrue(updatedThread.getMetadata().get("testKey").equals("testValue"), "Updated thread metadata should match");

        ThreadDeletionStatus deletionStatus = threadsClient.deleteThread(thread.getId());
        assertNotNull(deletionStatus, "Deletion status should not be null");
        assertTrue(deletionStatus.isDeleted(), "Thread should be deleted");

        // Clean up agent
        agentsClient.deleteAgent(agent.getId());
    }

}
