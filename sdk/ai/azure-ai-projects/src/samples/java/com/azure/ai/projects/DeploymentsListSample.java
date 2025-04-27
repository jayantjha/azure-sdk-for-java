package com.azure.ai.projects;

import com.azure.ai.projects.models.Deployment;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

public class DeploymentsListSample {

    public static void main(String[] args) {
        DeploymentsClient deploymentsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildDeploymentsClient();

        // BEGIN:com.azure.ai.projects.DeploymentsListSample

        PagedIterable<Deployment> deployments = deploymentsClient.list();
        for (Deployment deployment : deployments) {
            System.out.printf("Deployment name: %s%n", deployment.getName());
        }

        // END:com.azure.ai.projects.DeploymentsListSample
    }
}
