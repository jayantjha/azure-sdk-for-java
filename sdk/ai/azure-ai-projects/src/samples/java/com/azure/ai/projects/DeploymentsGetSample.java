package com.azure.ai.projects;

import com.azure.ai.projects.models.Deployment;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import java.util.Map;

public class DeploymentsGetSample {

    public static void main(String[] args) {
        DeploymentsClient deploymentsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildDeploymentsClient();

        // BEGIN:com.azure.ai.projects.DeploymentsGetSample

        String bingDeploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "");
        Deployment deployment = deploymentsClient.get(bingDeploymentName);

        System.out.printf("Deployment name: %s%n", deployment.getName());
        System.out.printf("Deployment type: %s%n", deployment.getType().getValue());

        // END:com.azure.ai.projects.DeploymentsGetSample
    }
}
