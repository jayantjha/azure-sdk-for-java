package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.AgentDeletionStatus;
import com.azure.ai.agents.persistent.models.CreateAgentOptions;
import com.azure.ai.agents.persistent.models.PersistentAgent;
import com.azure.ai.agents.persistent.models.UpdateAgentOptions;
import com.azure.core.http.HttpClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static com.azure.ai.agents.persistent.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdministrationClientTest extends ClientTestBase {

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testAdministrationClientOperations(HttpClient httpClient) {
        PersistentAgentsAdministrationClient agentsClient = getClientBuilder(httpClient).buildClient();

        assertNotNull(agentsClient, "PersistentAgentsAdministrationClient should not be null");

        String agentName = "TestAgentOperationsAgent";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You are a helpful agent");

        PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
        // Validate the agent creation
        assertAgent(agent);

        List<PersistentAgent> agentList = agentsClient.listAgents().getData();

        // Validate the agent listing
        assertNotNull(agentList, "Agent list should not be null");
        assertTrue(agentList.size() > 0, "Agent list should not be empty");

        PersistentAgent retrievedAgent = agentsClient.getAgent(agent.getId());

        // Validate the agent retrieval
        assertAgent(retrievedAgent);
        assertTrue(retrievedAgent.getId() == agent.getId(), "Retrieved agent ID should match created agent ID");

        UpdateAgentOptions updateAgentOptions = new UpdateAgentOptions(agent.getId())
            .setInstructions("Updated instructions for the agent");
        PersistentAgent updatedAgent = agentsClient.updateAgent(updateAgentOptions);

        // Validate the agent update
        assertAgent(updatedAgent);
        assertTrue(updatedAgent.getInstructions().equals("Updated instructions for the agent"), "Updated agent instructions should match");
        assertTrue(updatedAgent.getId() == agent.getId(), "Updated agent ID should match created agent ID");

        AgentDeletionStatus deletionStatus = agentsClient.deleteAgent(agent.getId());

        // Validate the agent deletion
        assertNotNull(deletionStatus, "Deletion status should not be null");
        assertTrue(deletionStatus.isDeleted(), "Agent should be deleted");
    }
}
