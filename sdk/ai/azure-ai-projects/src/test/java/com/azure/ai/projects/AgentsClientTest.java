// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentsClientTest extends AIProjectClientTestBase {

    @BeforeEach
    void setup() {
        this.beforeTest();
    }

    @Test
    void listAndDeleteAllAgents() {
        agentsClient.listAgents().getData().stream().forEach(agent -> agentsClient.deleteAgent(agent.getId()));
    }
}
