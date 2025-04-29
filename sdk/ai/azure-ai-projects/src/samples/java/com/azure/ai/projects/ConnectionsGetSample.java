// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.projects;

import com.azure.ai.projects.models.Connection;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import java.util.Map;

public class ConnectionsGetSample {

    public static void main(String[] args) {
        ConnectionsClient connectionsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildConnectionsClient();

        // BEGIN:com.azure.ai.projects.ConnectionsGetSample

        String bingConnectionName = Configuration.getGlobalConfiguration().get("BING_CONNECTION_NAME", "");
        Connection connection = connectionsClient.get(bingConnectionName);

        System.out.printf("Connection name: %s%n", connection.getName());
        Map<String, String> metadata = connection.getMetadata();
        if (metadata != null) {
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                System.out.printf("Metadata key: %s, value: %s%n", entry.getKey(), entry.getValue());
            }
        }

        // END:com.azure.ai.projects.ConnectionsGetSample
    }
}
