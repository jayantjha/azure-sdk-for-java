// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.core.util.Configuration;
import com.azure.ai.projects.implementation.models.GetConnectionResponse;
import com.azure.ai.projects.implementation.models.GetWorkspaceResponse;
import com.azure.ai.projects.implementation.models.ListConnectionsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionsAsyncClientTest extends AIProjectClientTestBase {

    @BeforeEach
    void setUp() {
        this.beforeTest();
    }

    @Test
    void getWorkspace() {
        ConnectionsAsyncClient connectionsAsyncClient = getAIProjectClientBuilder().buildConnectionsAsyncClient();

        StepVerifier.create(connectionsAsyncClient.getWorkspace())
            .assertNext(workspace -> {
                assertNotNull(workspace);
                assertNotNull(workspace.getId());
            })
            .verifyComplete();
    }

    @Test
    void listConnections() {
        ConnectionsAsyncClient connectionsAsyncClient = getAIProjectClientBuilder().buildConnectionsAsyncClient();

        StepVerifier.create(connectionsAsyncClient.listConnections())
            .assertNext(connections -> {
                assertTrue(connections.getValue().size() > 0);
            })
            .verifyComplete();
    }

    @Test
    void getConnection() {
        ConnectionsAsyncClient connectionsAsyncClient = getAIProjectClientBuilder().buildConnectionsAsyncClient();
        String connectionName = Configuration.getGlobalConfiguration().get("AISEARCH_CONNECTION_NAME", "");
        StepVerifier.create(connectionsAsyncClient.getConnection(connectionName))
            .assertNext(connection -> {
                assertNotNull(connection);
                assertNotNull(connection.getId());
            })
            .verifyComplete();
    }
}
