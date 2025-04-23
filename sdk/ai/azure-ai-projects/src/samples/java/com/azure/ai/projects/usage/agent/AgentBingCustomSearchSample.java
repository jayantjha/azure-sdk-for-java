// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.projects.usage.agent;

import com.azure.ai.projects.AIProjectClientBuilder;
import com.azure.ai.projects.AgentsClient;
import com.azure.ai.projects.implementation.models.CreateAgentRequest;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.AgentThread;
import com.azure.ai.projects.models.BingCustomSearchToolDefinition;
import com.azure.ai.projects.models.BingGroundingToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.CreateRunOptions;
import com.azure.ai.projects.models.MessageContent;
import com.azure.ai.projects.models.MessageImageFileContent;
import com.azure.ai.projects.models.MessageRole;
import com.azure.ai.projects.models.MessageTextContent;
import com.azure.ai.projects.models.OpenAIPageableListOfThreadMessage;
import com.azure.ai.projects.models.RunStatus;
import com.azure.ai.projects.models.SearchConfiguration;
import com.azure.ai.projects.models.SearchConfigurationList;
import com.azure.ai.projects.models.ThreadMessage;
import com.azure.ai.projects.models.ThreadRun;
import com.azure.ai.projects.models.ToolConnection;
import com.azure.ai.projects.models.ToolConnectionList;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class AgentBingCustomSearchSample {

    @Test
    void bingCustomSearchExample() {
        AgentsClient agentsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .subscriptionId(Configuration.getGlobalConfiguration().get("SUBSCRIPTIONID", "subscriptionid"))
            .resourceGroupName(Configuration.getGlobalConfiguration().get("RESOURCEGROUPNAME", "resourcegroupname"))
            .projectName(Configuration.getGlobalConfiguration().get("PROJECTNAME", "projectname"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildAgentsClient();

        String bingConnectionId = "/subscriptions/696debc0-8b66-4d84-87b1-39f43917d76c/resourceGroups/rg-jayant/providers/Microsoft.MachineLearningServices/workspaces/jayant-project-2aqa/connections/bingcustomsearch1";
        ToolConnectionList toolConnectionList = new ToolConnectionList()
            .setConnectionList(Arrays.asList(new ToolConnection(bingConnectionId)));

        SearchConfiguration searchConfiguration = new SearchConfiguration(bingConnectionId, "bingcustomsearch1");
        SearchConfigurationList searchConfigurationList = new SearchConfigurationList(Arrays.asList(searchConfiguration));

        BingCustomSearchToolDefinition bingCustomSearchToolDefinition = new BingCustomSearchToolDefinition(searchConfigurationList);

        String agentName = "bing_custom_search_example";
        CreateAgentRequest createAgentRequest = new CreateAgentRequest("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(bingCustomSearchToolDefinition));
        RequestOptions requestOptions = new RequestOptions()
            .setHeader("x-ms-enable-preview", "true");
        Agent agent = agentsClient.createAgentWithResponse(BinaryData.fromObject(createAgentRequest), requestOptions)
            .getValue().toObject(Agent.class);

        AgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "How does wikipedia explain Euler's Identity?");

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
