package com.azure.ai.projects;

import com.azure.ai.projects.models.Connection;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class ConnectionsListSample {

    public static void main(String[] args) {
        ConnectionsClient connectionsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildConnectionsClient();

        // BEGIN:com.azure.ai.projects.ConnectionsListSample

        PagedIterable<Connection> connections = connectionsClient.list();
        for (Connection connection : connections) {
            System.out.printf("Connection name: %s%n", connection.getName());
        }

        // END:com.azure.ai.projects.ConnectionsListSample
    }
}
