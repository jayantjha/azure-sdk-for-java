// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.CodeInterpreterToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.MessageRole;
import com.azure.ai.projects.models.UpdateAgentOptions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AgentsAsyncClientTest extends AIProjectClientTestBase {

    @Test
    void testCreateAgent() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        String agentName = "basic_example_async";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));
        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions))
            .assertNext(agent -> {
                assertNotNull(agent.getId());
            }).verifyComplete();
    }

    @Test
    void testDeleteAgent() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        String agentName = "delete_agent_test_async";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));

        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions)
                .flatMap(agent -> agentsAsyncClient.deleteAgent(agent.getId())))
            .assertNext(deletionStatus -> {
                assertNotNull(deletionStatus);
            })
            .verifyComplete();
    }


    @Test
    void testListAgents() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        // Create an agent first to ensure there's at least one to list
        String agentName = "list_agents_test_async";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));

        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions)
                .then(agentsAsyncClient.listAgents()))
            .assertNext(agents -> {
                assertNotNull(agents);
            })
            .verifyComplete();
    }


    @Test
    void testUpdateAgent() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        String agentName = "update_agent_test_async";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));

        String updatedInstructions = "You are a very helpful and efficient agent";

        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions)
                .flatMap(agent -> {
                    UpdateAgentOptions updateOptions = new UpdateAgentOptions(agent.getId())
                        .setInstructions(updatedInstructions);
                    return agentsAsyncClient.updateAgent(updateOptions);
                }))
            .assertNext(agent -> {
                assertNotNull(agent);
                assertEquals(agentName, agent.getName());
                assertEquals(updatedInstructions, agent.getInstructions());
            })
            .verifyComplete();
    }

    @Test
    void testCreateThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        StepVerifier.create(agentsAsyncClient.createThread())
            .assertNext(thread -> {
                assertNotNull(thread);
                assertNotNull(thread.getId());
            })
            .verifyComplete();
    }


    @Test
    void testGetThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> threadId = new AtomicReference<>();

        StepVerifier.create(agentsAsyncClient.createThread()
                .flatMap(thread -> {
                    threadId.set(thread.getId());
                    return agentsAsyncClient.getThread(thread.getId());
                }))
            .assertNext(thread -> {
                assertNotNull(thread);
                assertEquals(threadId.get(), thread.getId());
            })
            .verifyComplete();
    }


    @Test
    void testCreateMessageInThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> threadId = new AtomicReference<>();
        String messageContent = "Hello, this is a test message";

        StepVerifier.create(agentsAsyncClient.createThread()
                .flatMap(thread -> {
                    threadId.set(thread.getId());
                    return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER,
                        "I need to solve the equation `3x + 11 = 14`. Can you help me?");
                }))
            .assertNext(message -> {
                assertNotNull(message);
                assertNotNull(message.getId());
                assertEquals(MessageRole.USER, message.getRole());
            })
            .verifyComplete();
    }


//    @Test
//    void testCreateAndRunThread() {
//        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();
//
//        AtomicReference<String> agentId = new AtomicReference<>();
//        AtomicReference<String> threadId = new AtomicReference<>();
//
//        String agentName = "run_thread_test_async";
//        String userMessage = "What is the current date?";
//
//        StepVerifier.create(
//                // Create an agent first
//                createTestAgent(agentsAsyncClient, agentName)
//                    .flatMap(agent -> {
//                        agentId.set(agent.getId());
//
//                        // Create a thread
//                        return agentsAsyncClient.createThread();
//                    })
//                    .flatMap(thread -> {
//                        threadId.set(thread.getId());
//
//                        // Add a message to the thread
//                        CreateMessageOptions messageOptions = new CreateMessageOptions(Role.USER)
//                            .setContent(userMessage);
//                        return agentsAsyncClient.createMessage(thread.getId(), messageOptions);
//                    })
//                    .flatMap(message -> {
//                        // Create a run using the agent
//                        CreateRunOptions runOptions = new CreateRunOptions(agentId.get());
//                        return agentsAsyncClient.createRun(threadId.get(), runOptions);
//                    })
//                    .flatMap(run -> {
//                        assertNotNull(run.getId());
//                        assertEquals(agentId.get(), run.getAgentId());
//
//                        // Get the run status
//                        return agentsAsyncClient.getRun(threadId.get(), run.getId());
//                    })
//            )
//            .assertNext(run -> {
//                assertNotNull(run);
//                assertNotNull(run.getStatus());
//            })
//            .verifyComplete();
//    }

    //
//    @Test
//    void testThreadRunLifecycle() {
//        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();
//
//        AtomicReference<String> agentId = new AtomicReference<>();
//        AtomicReference<String> threadId = new AtomicReference<>();
//        AtomicReference<String> runId = new AtomicReference<>();
//
//        String agentName = "thread_lifecycle_test_async";
//        String userMessage = "Write a hello world program in Python.";
//
//        StepVerifier.create(
//            // Create an agent with code interpreter
//            createTestAgent(agentsAsyncClient, agentName)
//                .map(agent -> {
//                    agentId.set(agent.getId());
//                    return agent;
//                })
//                // Create a thread
//                .flatMap(agent -> agentsAsyncClient.createThread(new CreateThreadOptions()))
//                .map(thread -> {
//                    threadId.set(thread.getId());
//                    return thread;
//                })
//                // Add a message to the thread
//                .flatMap(thread -> {
//                    CreateMessageOptions messageOptions = new CreateMessageOptions(Role.USER)
//                        .setContent(userMessage);
//                    return agentsAsyncClient.createMessage(thread.getId(), messageOptions);
//                })
//                // Create a run
//                .flatMap(message -> {
//                    CreateRunOptions runOptions = new CreateRunOptions(agentId.get());
//                    return agentsAsyncClient.createRun(threadId.get(), runOptions);
//                })
//                .map(run -> {
//                    runId.set(run.getId());
//                    return run;
//                })
//            )
//            .expectNextCount(1)
//            .verifyComplete();
//
//        // Wait for the run to complete and check messages
//        Mono<Run> pollForCompletion = agentsAsyncClient.getRun(threadId.get(), runId.get())
//            .expand(run -> {
//                if (run.getStatus() == RunStatus.COMPLETED ||
//                    run.getStatus() == RunStatus.FAILED ||
//                    run.getStatus() == RunStatus.CANCELLED) {
//                    return Mono.empty();
//                }
//                return Mono.delay(Duration.ofSeconds(2))
//                    .then(agentsAsyncClient.getRun(threadId.get(), runId.get()));
//            })
//            .last();
//
//        StepVerifier.create(pollForCompletion)
//            .assertNext(run -> {
//                assertEquals(RunStatus.COMPLETED, run.getStatus());
//            })
//            .verifyComplete();
//
//        // Check that assistant messages were created
//        StepVerifier.create(agentsAsyncClient.listMessages(threadId.get()).collectList())
//            .assertNext(messages -> {
//                assertTrue(messages.size() >= 2); // At least user message and assistant response
//                boolean hasAssistantMessage = messages.stream()
//                    .anyMatch(msg -> msg.getRole() == Role.ASSISTANT);
//                assertTrue(hasAssistantMessage, "No assistant messages found");
//            })
//            .verifyComplete();
//    }
//
//    @Test
//    void testListRunsForThread() {
//        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();
//
//        AtomicReference<String> agentId = new AtomicReference<>();
//        AtomicReference<String> threadId = new AtomicReference<>();
//
//        String agentName = "list_runs_test_async";
//
//        StepVerifier.create(
//            // Create agent
//            createTestAgent(agentsAsyncClient, agentName)
//                .flatMap(agent -> {
//                    agentId.set(agent.getId());
//                    return agentsAsyncClient.createThread(new CreateThreadOptions());
//                })
//                .flatMap(thread -> {
//                    threadId.set(thread.getId());
//                    CreateMessageOptions messageOptions = new CreateMessageOptions(Role.USER)
//                        .setContent("Test message");
//                    return agentsAsyncClient.createMessage(thread.getId(), messageOptions);
//                })
//                .flatMap(message -> {
//                    CreateRunOptions runOptions = new CreateRunOptions(agentId.get());
//                    return agentsAsyncClient.createRun(threadId.get(), runOptions);
//                })
//                .then(Mono.defer(() -> {
//                    return agentsAsyncClient.listRuns(threadId.get()).collectList();
//                }))
//            )
//            .assertNext(runs -> {
//                assertFalse(runs.isEmpty());
//                assertEquals(1, runs.size());
//                assertEquals(agentId.get(), runs.get(0).getAgentId());
//            })
//            .verifyComplete();
//    }
//
    // Helper method to create test agents
    private Mono<Agent> createTestAgent(AgentsAsyncClient client, String name) {
        CreateAgentOptions options = new CreateAgentOptions("gpt-4o-mini")
            .setName(name)
            .setInstructions("Test agent for " + name);
        return client.createAgent(options);
    }
}
