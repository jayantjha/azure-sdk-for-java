// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.projects;

import com.azure.ai.agents.persistent.PersistentAgentsAdministrationAsyncClient;
import com.azure.ai.agents.persistent.PersistentAgentsAdministrationClient;
import com.azure.ai.agents.persistent.PersistentAgentsAdministrationClientBuilder;
import com.azure.ai.inference.ChatCompletionsAsyncClient;
import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.EmbeddingsAsyncClient;
import com.azure.ai.inference.EmbeddingsClient;
import com.azure.ai.inference.EmbeddingsClientBuilder;
import com.azure.ai.inference.ImageEmbeddingsAsyncClient;
import com.azure.ai.inference.ImageEmbeddingsClient;
import com.azure.ai.inference.ImageEmbeddingsClientBuilder;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class SampleUtils {

    public static void buildClients() {
        // BEGIN: com.azure.ai.projects.SampleUtils.buildClients
        AIProjectClientBuilder clientBuilder = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build());

        // Connections clients
        ConnectionsClient connectionsClient = clientBuilder.buildConnectionsClient();
        ConnectionsAsyncClient connectionsAsyncClient = clientBuilder.buildConnectionsAsyncClient();

        // Datasets clients
        DatasetsClient datasetsClient = clientBuilder.buildDatasetsClient();
        DatasetsAsyncClient datasetsAsyncClient = clientBuilder.buildDatasetsAsyncClient();

        // Deployments clients
        DeploymentsClient deploymentsClient = clientBuilder.buildDeploymentsClient();
        DeploymentsAsyncClient deploymentsAsyncClient = clientBuilder.buildDeploymentsAsyncClient();

        // Evaluations clients
        EvaluationsClient evaluationsClient = clientBuilder.buildEvaluationsClient();
        EvaluationsAsyncClient evaluationsAsyncClient = clientBuilder.buildEvaluationsAsyncClient();
        
        // Red Teams clients
        RedTeamsClient redTeamsClient = clientBuilder.buildRedTeamsClient();
        RedTeamsAsyncClient redTeamsAsyncClient = clientBuilder.buildRedTeamsAsyncClient();
        
        // Indexes clients
        IndexesClient indexesClient = clientBuilder.buildIndexesClient();
        IndexesAsyncClient indexesAsyncClient = clientBuilder.buildIndexesAsyncClient();
        
        // Telemetry clients
        TelemetryClient telemetryClient = clientBuilder.buildTelemetryClient();
        TelemetryAsyncClient telemetryAsyncClient = clientBuilder.buildTelemetryAsyncClient();

        // Agents clients
        PersistentAgentsAdministrationClientBuilder agentsClientBuilder
            = new PersistentAgentsAdministrationClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build());

        PersistentAgentsAdministrationClient agentsClient = agentsClientBuilder.buildClient();
        PersistentAgentsAdministrationAsyncClient agentsAsyncClient = agentsClientBuilder.buildAsyncClient();

        // Chat completion clients
        ChatCompletionsClientBuilder chatCompletionsClientBuilder
            = new ChatCompletionsClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build());

        ChatCompletionsClient chatCompletionsClient = chatCompletionsClientBuilder.buildClient();
        ChatCompletionsAsyncClient chatCompletionsAsyncClient = chatCompletionsClientBuilder.buildAsyncClient();

        // Image embeddings clients
        ImageEmbeddingsClientBuilder imageEmbeddingsClientBuilder
            = new ImageEmbeddingsClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build());

        ImageEmbeddingsClient imageEmbeddingsClient = imageEmbeddingsClientBuilder.buildClient();
        ImageEmbeddingsAsyncClient imageEmbeddingsAsyncClient = imageEmbeddingsClientBuilder.buildAsyncClient();

        // Embeddings clients
        EmbeddingsClientBuilder embeddingsClientBuilder
            = new EmbeddingsClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build());

        EmbeddingsClient embeddingsClient = embeddingsClientBuilder.buildClient();
        EmbeddingsAsyncClient embeddingsAsyncClient = embeddingsClientBuilder.buildAsyncClient();
        // END: com.azure.ai.projects.SampleUtils.buildClients
    }

    public static Path getPath(String fileName) throws FileNotFoundException, URISyntaxException {
        URL resource = SampleUtils.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new FileNotFoundException("File not found");
        }

        File file = new File(resource.toURI());
        return file.toPath();
    }
}
