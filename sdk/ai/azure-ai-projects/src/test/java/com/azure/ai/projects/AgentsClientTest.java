package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.models.CodeInterpreterToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.OpenAIPageableListOfAgent;
import com.azure.ai.projects.models.ToolDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AgentsClientTest extends AIProjectClientTestBase {

    @BeforeEach
    void setup() {
        this.beforeTest();
    }

    @Test
    void createAgent() {
        var agentName = "TestAgent_" + UUID.randomUUID();
        var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(List.of(new CodeInterpreterToolDefinition()));
        var agent = this.agentsClient.createAgent(createAgentOptions);
        var agents = this.agentsClient.listAgents();
        var created = agents.getData().stream()
            .anyMatch(a -> a.getName().equals(agentName));
        assertTrue(created);
    }

    @Test
    void createThread() {
        var thread = this.agentsClient.createThread();
        assertNotNull(thread);
    }
}
