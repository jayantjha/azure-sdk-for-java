// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.projects.usage.agent;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.AgentsClient;
import com.azure.ai.projects.implementation.models.CreateAgentRequest;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.AgentThread;
import com.azure.ai.projects.models.CodeInterpreterToolDefinition;
import com.azure.ai.projects.models.ConnectedAgentDetails;
import com.azure.ai.projects.models.ConnectedAgentToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.CreateRunOptions;
import com.azure.ai.projects.models.MessageContent;
import com.azure.ai.projects.models.MessageImageFileContent;
import com.azure.ai.projects.models.MessageRole;
import com.azure.ai.projects.models.MessageTextContent;
import com.azure.ai.projects.models.OpenAIPageableListOfThreadMessage;
import com.azure.ai.projects.models.RunStatus;
import com.azure.ai.projects.models.ThreadMessage;
import com.azure.ai.projects.models.ThreadRun;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public final class AgentConnectedAgentSample {

    @Test
    void connectedAgentExample() {
        AgentsClient agentsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .subscriptionId(Configuration.getGlobalConfiguration().get("SUBSCRIPTIONID", "subscriptionid"))
            .resourceGroupName(Configuration.getGlobalConfiguration().get("RESOURCEGROUPNAME", "resourcegroupname"))
            .projectName(Configuration.getGlobalConfiguration().get("PROJECTNAME", "projectname"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildAgentsClient();

        String connectedAgentName = "stock_price_bot";
        CreateAgentOptions connectedAgentCreateOptions = new CreateAgentOptions("gpt-4o")
            .setName(connectedAgentName)
            .setInstructions("Your job is to get the stock price of a company. If you don't know the realtime stock price, return the last known stock price.");
        Agent connectedAgent = agentsClient.createAgent(connectedAgentCreateOptions);

        ConnectedAgentToolDefinition connectedAgentToolDefinition = new ConnectedAgentToolDefinition(
            new ConnectedAgentDetails(connectedAgent.getId(), connectedAgent.getName(), "Gets the stock price of a company"));

        String agentName = "my-assistant";
        CreateAgentRequest createAgentRequest = new CreateAgentRequest("gpt-4o")
            .setName(agentName)
            .setInstructions("You are a helpful assistant, and use the connected agent to get stock prices.")
            .setTools(Arrays.asList(connectedAgentToolDefinition));
        RequestOptions requestOptions = new RequestOptions()
            .setHeader("x-ms-enable-preview", "true");
        Agent agent = agentsClient.createAgentWithResponse(BinaryData.fromObject(createAgentRequest), requestOptions)
            .getValue().toObject(Agent.class);

        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What is the stock price of Microsoft?");

        //run agent
        CreateRunOptions createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        ThreadRun threadRun = agentsClient.createRun(createRunOptions);

        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            }
            while (
                threadRun.getStatus() == RunStatus.QUEUED
                    || threadRun.getStatus() == RunStatus.IN_PROGRESS
                    || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            if (threadRun.getStatus() == RunStatus.FAILED) {
                System.out.println(threadRun.getLastError().getMessage());
            }

            OpenAIPageableListOfThreadMessage runMessages = agentsClient.listMessages(thread.getId());
            for (ThreadMessage message : runMessages.getData()) {
                System.out.print(String.format("%1$s - %2$s : ", message.getCreatedAt(), message.getRole()));
                for (MessageContent contentItem : message.getContent()) {
                    if (contentItem instanceof MessageTextContent) {
                        System.out.print((((MessageTextContent) contentItem).getText().getValue()));
                    } else if (contentItem instanceof MessageImageFileContent) {
                        String imageFileId = (((MessageImageFileContent) contentItem).getImageFile().getFileId());
                        System.out.print("Image from ID: " + imageFileId);
                    }
                    System.out.println();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }
}
