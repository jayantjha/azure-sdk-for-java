// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.implementation.models.GetConnectionResponse;
import com.azure.ai.projects.implementation.models.GetWorkspaceResponse;
import com.azure.ai.projects.implementation.models.ListConnectionsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionsClientTest extends AIProjectClientTestBase {

    @BeforeEach
    void setUp() {
        this.beforeTest();
    }

    @Test
    void getWorkspace() {
        GetWorkspaceResponse workspace = connectionsClient.getWorkspace();
        assertNotNull(workspace);
        assertNotNull(workspace.getId());
    }

    @Test
    void listConnections() {
        ListConnectionsResponse connections = connectionsClient.listConnections();
        assertTrue(connections.getValue().size() > 0);
    }

    @Test
    void getConnection() {
        GetConnectionResponse connection = connectionsClient.getConnection("jayant-hub-2aqa-connection-AISearch");
        assertNotNull(connection);
        assertNotNull(connection.getId());
    }
}
