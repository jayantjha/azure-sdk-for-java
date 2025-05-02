// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.BingCustomSearchConfiguration;
import com.azure.ai.agents.persistent.models.BingCustomSearchConfigurationList;
import com.azure.ai.agents.persistent.models.BingGroundingSearchConfiguration;
import com.azure.ai.agents.persistent.models.BingGroundingSearchConfigurationList;
import com.azure.ai.agents.persistent.models.BingGroundingToolDefinition;
import com.azure.ai.agents.persistent.models.CreateAgentOptions;
import com.azure.ai.agents.persistent.models.CreateRunOptions;
import com.azure.ai.agents.persistent.models.MessageContent;
import com.azure.ai.agents.persistent.models.MessageImageFileContent;
import com.azure.ai.agents.persistent.models.MessageRole;
import com.azure.ai.agents.persistent.models.MessageTextContent;
import com.azure.ai.agents.persistent.models.OpenAIPageableListOfThreadMessage;
import com.azure.ai.agents.persistent.models.PersistentAgent;
import com.azure.ai.agents.persistent.models.PersistentAgentThread;
import com.azure.ai.agents.persistent.models.RunStatus;
import com.azure.ai.agents.persistent.models.ThreadMessage;
import com.azure.ai.agents.persistent.models.ThreadRun;
import com.azure.ai.agents.persistent.models.ToolConnection;
import com.azure.ai.agents.persistent.models.ToolConnectionList;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import java.util.Arrays;

public class AgentBingGroundingSample {

    public static void main(String[] args) {

        PersistentAgentsAdministrationClientBuilder clientBuilder = new PersistentAgentsAdministrationClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build());
        PersistentAgentsAdministrationClient agentsClient = clientBuilder.buildClient();
        ThreadsClient threadsClient = clientBuilder.buildThreadsClient();
        MessagesClient messagesClient = clientBuilder.buildMessagesClient();
        RunsClient runsClient = clientBuilder.buildRunsClient();

        String bingConnectionId = Configuration.getGlobalConfiguration().get("BING_CONNECTION_ID", "");
        ToolConnectionList toolConnectionList = new ToolConnectionList()
            .setConnectionList(Arrays.asList(new ToolConnection(bingConnectionId)));

        BingGroundingSearchConfiguration searchConfiguration = new BingGroundingSearchConfiguration(bingConnectionId);
        BingGroundingSearchConfigurationList searchConfigurationList = new BingGroundingSearchConfigurationList(Arrays.asList(searchConfiguration));

        BingGroundingToolDefinition bingGroundingTool = new BingGroundingToolDefinition(searchConfigurationList);

        String agentName = "bing_grounding_example";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-35-turbo")
            .setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(bingGroundingTool));
        PersistentAgent agent = agentsClient.createAgent(createAgentOptions);

        PersistentAgentThread thread = threadsClient.createThread();
        ThreadMessage createdMessage = messagesClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "How does wikipedia explain Euler's Identity?");

        //run agent
        CreateRunOptions createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        ThreadRun threadRun = runsClient.createRun(createRunOptions);

        try {
            do {
                Thread.sleep(500);
                threadRun = runsClient.getRun(thread.getId(), threadRun.getId());
            }
            while (
                threadRun.getStatus() == RunStatus.QUEUED
                    || threadRun.getStatus() == RunStatus.IN_PROGRESS
                    || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

            if (threadRun.getStatus() == RunStatus.FAILED) {
                System.out.println(threadRun.getLastError().getMessage());
            }

            OpenAIPageableListOfThreadMessage runMessages = messagesClient.listMessages(thread.getId());
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
            threadsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }
}
