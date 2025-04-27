package com.azure.ai.projects;

import com.azure.ai.projects.models.AzureAISearchIndex;
import com.azure.ai.projects.models.Index;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import java.util.Map;

public class IndexesCreateOrUpdateSample {

    public static void main(String[] args) {
        IndexesClient indexesClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildIndexesClient();

        // BEGIN:com.azure.ai.projects.IndexesGetSample

        String indexName = Configuration.getGlobalConfiguration().get("INDEX_NAME", "my-index");
        String indexVersion = Configuration.getGlobalConfiguration().get("INDEX_VERSION", "1.0");
        String aiSearchConnectionName = Configuration.getGlobalConfiguration().get("AI_SEARCH_CONNECTION_NAME", "my-ai-search-connection-name");
        String aiSearchIndexName = Configuration.getGlobalConfiguration().get("AI_SEARCH_INDEX_NAME", "my-ai-search-index-name");

        Index index = indexesClient.createOrUpdateVersion(
            indexName,
            indexVersion,
            new AzureAISearchIndex(aiSearchConnectionName, aiSearchIndexName)
        );

        System.out.println("Index created: " + index.getId());

        // END:com.azure.ai.projects.IndexesGetSample
    }
}
