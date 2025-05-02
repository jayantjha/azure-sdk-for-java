// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.OpenAIPageableListOfAgentThread;
import com.azure.ai.agents.persistent.models.PersistentAgentThread;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class AgentListThreadSample {

    public static void main(String[] args) {
        PersistentAgentsAdministrationClientBuilder clientBuilder = new PersistentAgentsAdministrationClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build());
        ThreadsClient threadsClient = clientBuilder.buildThreadsClient();;

        OpenAIPageableListOfAgentThread threads = threadsClient.listThreads();
        for (PersistentAgentThread thread : threads.getData()) {
            System.out.printf("Found thread ID: %s%n", thread.getId());
        }
    }
}
