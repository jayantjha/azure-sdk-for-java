package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionsClientTest extends AIProjectClientTestBase {

    @BeforeEach
    void setUp() {
        this.beforeTest();
    }

    @Test
    void getWorkspace() {
        var workspace = connectionsClient.getWorkspace();
        assertNotNull(workspace);
    }

    @Test
    void listConnections() {
        var connections = connectionsClient.listConnections();
        assertNotNull(connections);
    }

    @Test
    void getConnection() {
        var connection = connectionsClient.getConnection("jayantagentbing");
        assertNotNull(connection);
    }
}
