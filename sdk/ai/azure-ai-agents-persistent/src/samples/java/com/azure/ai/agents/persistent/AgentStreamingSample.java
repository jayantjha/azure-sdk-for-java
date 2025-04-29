// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.CodeInterpreterToolDefinition;
import com.azure.ai.agents.persistent.models.CreateAgentOptions;
import com.azure.ai.agents.persistent.models.CreateRunOptions;
import com.azure.ai.agents.persistent.models.MessageDeltaImageFileContent;
import com.azure.ai.agents.persistent.models.MessageDeltaTextContent;
import com.azure.ai.agents.persistent.models.MessageRole;
import com.azure.ai.agents.persistent.models.PersistentAgent;
import com.azure.ai.agents.persistent.models.PersistentAgentStreamEvent;
import com.azure.ai.agents.persistent.models.PersistentAgentThread;
import com.azure.ai.agents.persistent.models.ThreadMessage;
import com.azure.ai.agents.persistent.models.streaming.StreamMessageUpdate;
import com.azure.ai.agents.persistent.models.streaming.StreamUpdate;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import reactor.core.publisher.Flux;
import java.util.Arrays;

public final class AgentStreamingSample {

    public static void main(String[] args) {
        PersistentAgentsClient agentsClient
            = new PersistentAgentsClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildClient();

        String agentName = "agent_streaming_example";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You politely help with math questions. Use the code interpreter tool when asked to visualize numbers.")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));
        PersistentAgent agent = agentsClient.createAgent(createAgentOptions);

        PersistentAgentThread thread = agentsClient.createThread();
        ThreadMessage createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "Hi, Assistant! Draw a graph for a line with a slope of 4 and y-intercept of 9.");

        CreateRunOptions createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");

        try {
            Flux<StreamUpdate> streamingUpdates = agentsClient.createRunStreaming(createRunOptions);

            streamingUpdates.doOnNext(
                streamUpdate -> {
                    if (streamUpdate.getKind() == PersistentAgentStreamEvent.THREAD_RUN_CREATED) {
                        System.out.println("----- Run started! -----");
                    } else if (streamUpdate instanceof StreamMessageUpdate) {
                        StreamMessageUpdate messageUpdate = (StreamMessageUpdate) streamUpdate;
                        messageUpdate.getMessage().getDelta().getContent().stream().forEach(delta -> {
                            if (delta instanceof MessageDeltaImageFileContent) {
                                MessageDeltaImageFileContent imgContent = (MessageDeltaImageFileContent) delta;
                                System.out.println("Image fileId: " + imgContent.getImageFile().getFileId());
                            } else if (delta instanceof MessageDeltaTextContent) {
                                MessageDeltaTextContent textContent = (MessageDeltaTextContent) delta;
                                System.out.print(textContent.getText().getValue());
                            }
                        });
                    }
                }
            ).blockLast();

            System.out.println();
        } catch (Exception ex) {
            throw ex;
        } finally {
            //cleanup
            agentsClient.deleteThread(thread.getId());
            agentsClient.deleteAgent(agent.getId());
        }
    }
}
