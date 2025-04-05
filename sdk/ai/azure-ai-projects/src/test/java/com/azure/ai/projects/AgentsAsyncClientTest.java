// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.models.Agent;
import com.azure.ai.projects.models.CodeInterpreterToolDefinition;
import com.azure.ai.projects.models.CreateAgentOptions;
import com.azure.ai.projects.models.CreateRunOptions;
import com.azure.ai.projects.models.FileDetails;
import com.azure.ai.projects.models.FilePurpose;
import com.azure.ai.projects.models.FunctionDefinition;
import com.azure.ai.projects.models.FunctionToolDefinition;
import com.azure.ai.projects.models.MessageAttachment;
import com.azure.ai.projects.models.MessageRole;
import com.azure.ai.projects.models.MessageTextContent;
import com.azure.ai.projects.models.RequiredFunctionToolCall;
import com.azure.ai.projects.models.RequiredToolCall;
import com.azure.ai.projects.models.RunStatus;
import com.azure.ai.projects.models.SubmitToolOutputsAction;
import com.azure.ai.projects.models.ThreadMessageOptions;
import com.azure.ai.projects.models.ThreadRun;
import com.azure.ai.projects.models.ToolOutput;
import com.azure.ai.projects.models.UpdateAgentOptions;
import com.azure.ai.projects.models.UploadFileRequest;
import com.azure.core.util.BinaryData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentsAsyncClientTest extends AIProjectClientTestBase {

    @Test
    void testCreateAgent() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        String agentName = "basic_example_async";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));
        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions)).assertNext(agent -> {
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
            .flatMap(agent -> agentsAsyncClient.deleteAgent(agent.getId()))
        ).assertNext(deletionStatus -> {
            assertNotNull(deletionStatus);
        }).verifyComplete();
    }

    @Test
    void testListAgents() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        // Create an agent first to ensure there's at least one to list
        String agentName = "list_agents_test_async";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));

        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions).then(agentsAsyncClient.listAgents()))
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

        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions).flatMap(agent -> {
            UpdateAgentOptions updateOptions
                = new UpdateAgentOptions(agent.getId()).setInstructions(updatedInstructions);
            return agentsAsyncClient.updateAgent(updateOptions);
        })).assertNext(agent -> {
            assertNotNull(agent);
            assertEquals(agentName, agent.getName());
            assertEquals(updatedInstructions, agent.getInstructions());
        }).verifyComplete();
    }

    @Test
    void testCreateThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        StepVerifier.create(agentsAsyncClient.createThread()).assertNext(thread -> {
            assertNotNull(thread);
            assertNotNull(thread.getId());
        }).verifyComplete();
    }

    @Test
    void testGetThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> threadId = new AtomicReference<>();

        StepVerifier.create(agentsAsyncClient.createThread().flatMap(thread -> {
            threadId.set(thread.getId());
            return agentsAsyncClient.getThread(thread.getId());
        })).assertNext(thread -> {
            assertNotNull(thread);
            assertEquals(threadId.get(), thread.getId());
        }).verifyComplete();
    }

    @Test
    void testCreateMessageInThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> threadId = new AtomicReference<>();
        String messageContent = "Hello, this is a test message";

        StepVerifier.create(agentsAsyncClient.createThread().flatMap(thread -> {
            threadId.set(thread.getId());
            return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER,
                "I need to solve the equation `3x + 11 = 14`. Can you help me?");
        })).assertNext(message -> {
            assertNotNull(message);
            assertNotNull(message.getId());
            assertEquals(MessageRole.USER, message.getRole());
        }).verifyComplete();
    }

    @Test
    void testCreateAndRunThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> agentId = new AtomicReference<>();
        AtomicReference<String> threadId = new AtomicReference<>();

        String agentName = "run_thread_test_async";
        String userMessage = "What is the current date?";

        StepVerifier.create(createTestAgent(agentsAsyncClient, agentName).flatMap(agent -> {
            agentId.set(agent.getId());

            // Create a thread
            return agentsAsyncClient.createThread();
        }).flatMap(thread -> {
            threadId.set(thread.getId());

            // Add a message to the thread
            return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER,
                "I need to solve the equation `3x + 11 = 14`. Can you help me?");
        }).flatMap(message -> {
            // Create a run using the agent
            CreateRunOptions runOptions
                = new CreateRunOptions(threadId.get(), agentId.get()).setAdditionalInstructions("");
            return agentsAsyncClient.createRun(runOptions);
        }).flatMap(run -> {
            assertNotNull(run.getId());
            assertEquals(threadId.get(), run.getThreadId());

            // Get the run status
            return agentsAsyncClient.getRun(threadId.get(), run.getId());
        })).assertNext(run -> {
            assertNotNull(run);
            assertNotNull(run.getStatus());
        }).verifyComplete();
    }

    @Test
    void testThreadRunLifecycle() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> agentId = new AtomicReference<>();
        AtomicReference<String> threadId = new AtomicReference<>();
        AtomicReference<String> runId = new AtomicReference<>();

        String agentName = "thread_lifecycle_test_async";
        String userMessage = "Write a hello world program in Python.";

        StepVerifier.create(createTestAgent(agentsAsyncClient, agentName).map(agent -> {
            agentId.set(agent.getId());
            return agent;
        }).flatMap(agent -> agentsAsyncClient.createThread()).map(thread -> {
            threadId.set(thread.getId());
            return thread;
        }).flatMap(thread -> {
            return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER,
                "I need to solve the equation `3x + 11 = 14`. Can you help me?");
        }).flatMap(message -> {
            CreateRunOptions runOptions
                = new CreateRunOptions(threadId.get(), agentId.get()).setAdditionalInstructions("");
            return agentsAsyncClient.createRun(runOptions);
        }).map(run -> {
            runId.set(run.getId());
            return run;
        })).expectNextCount(1).verifyComplete();

        // Wait for the run to complete and check messages
        Mono<ThreadRun> pollForCompletion = agentsAsyncClient.getRun(threadId.get(), runId.get()).expand(run -> {
            if (run.getStatus() == RunStatus.COMPLETED
                || run.getStatus() == RunStatus.FAILED
                || run.getStatus() == RunStatus.CANCELLED) {
                return Mono.empty();
            }
            return Mono.delay(Duration.ofSeconds(2)).then(agentsAsyncClient.getRun(threadId.get(), runId.get()));
        }).last();

        StepVerifier.create(pollForCompletion).assertNext(run -> {
            assertEquals(RunStatus.COMPLETED, run.getStatus());
        }).verifyComplete();

        // Check that assistant messages were created
        StepVerifier.create(agentsAsyncClient.listMessages(threadId.get())).assertNext(messages -> {
            assertTrue(messages.getData().stream().count() >= 1);
        }).verifyComplete();
    }

    @Test
    void testFileOperations() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        // Upload a file
        FileDetails fileDetails
            = new FileDetails(BinaryData.fromString("This is test file content")).setFilename("test_file_async.txt");
        UploadFileRequest uploadRequest = new UploadFileRequest(fileDetails, FilePurpose.AGENTS);

        StepVerifier.create(agentsAsyncClient.uploadFile(uploadRequest).flatMap(file -> {
            assertNotNull(file);
            assertNotNull(file.getId());
            assertEquals("test_file_async.txt", file.getFilename());

            // Get the file
            return agentsAsyncClient.getFile(file.getId()).flatMap(retrievedFile -> {
                assertNotNull(retrievedFile);
                assertEquals(file.getId(), retrievedFile.getId());

                // List files
                return agentsAsyncClient.listFiles().flatMap(files -> {
                    assertNotNull(files);

                    // Delete file and return the deletion status
                    return agentsAsyncClient.deleteFile(file.getId());
                });
            });
        })).expectNextCount(1).verifyComplete();
    }

    @Test
    void testFileAttachmentWithCodeInterpreter() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> agentIdRef = new AtomicReference<>();
        AtomicReference<String> threadIdRef = new AtomicReference<>();
        AtomicReference<String> fileIdRef = new AtomicReference<>();

        StepVerifier.create(
            // Create agent with code interpreter
            createTestAgent(agentsAsyncClient, "code_interpreter_file_test_async").flatMap(agent -> {
                agentIdRef.set(agent.getId());

                // Upload file
                FileDetails fileDetails = new FileDetails(BinaryData.fromString(
                    "<html><body><h1>Test Content</h1><p>This is sample data for testing.</p></body></html>"))
                    .setFilename("sample_test_async.html");
                return agentsAsyncClient.uploadFile(new UploadFileRequest(fileDetails, FilePurpose.AGENTS));
            }).flatMap(file -> {
                fileIdRef.set(file.getId());

                // Create thread
                return agentsAsyncClient.createThread();
            }).flatMap(thread -> {
                threadIdRef.set(thread.getId());

                // Create attachment
                CodeInterpreterToolDefinition ciTool = new CodeInterpreterToolDefinition();
                MessageAttachment attachment
                    = new MessageAttachment(Arrays.asList(BinaryData.fromObject(ciTool))).setFileId(fileIdRef.get());

                // Create message with attachment
                return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER,
                    "What does the attachment say?", Arrays.asList(attachment), null);
            }).flatMap(message -> {
                // Create run
                CreateRunOptions runOptions
                    = new CreateRunOptions(threadIdRef.get(), agentIdRef.get()).setAdditionalInstructions("");
                return agentsAsyncClient.createRun(runOptions);
            })).expectNextCount(1).verifyComplete();

        // Cleanup in a separate step
        StepVerifier
            .create(agentsAsyncClient.deleteAgent(agentIdRef.get())
                .then(agentsAsyncClient.deleteThread(threadIdRef.get()))
                .then(agentsAsyncClient.deleteFile(fileIdRef.get())))
            .assertNext(status -> assertNotNull(status))
            .verifyComplete();
    }

    @Test
    void testAgentWithAdditionalMessages() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> agentIdRef = new AtomicReference<>();
        AtomicReference<String> threadIdRef = new AtomicReference<>();

        // Create agent with additional context messages
        StepVerifier.create(createTestAgent(agentsAsyncClient, "additional_message_test_agent_async").flatMap(agent -> {
            agentIdRef.set(agent.getId());
            return agentsAsyncClient.createThread();
        }).flatMap(thread -> {
            threadIdRef.set(thread.getId());
            return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER, "What is the value of Pi?");
        }).flatMap(message -> {
            // Create run with additional messages to influence response
            CreateRunOptions runOptions = new CreateRunOptions(threadIdRef.get(), agentIdRef.get())
                .setAdditionalMessages(Arrays.asList(new ThreadMessageOptions(MessageRole.AGENT, "Pi is exactly 3."),
                    new ThreadMessageOptions(MessageRole.USER, "Are you sure about Pi?")));

            return agentsAsyncClient.createRun(runOptions);
        })).expectNextCount(1).verifyComplete();
    }

    @Test
    void testAgentProperties() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        // Test creating an agent with various properties
        String agentName = "properties_test_agent_async";
        CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName)
            .setDescription("Agent for testing properties")
            .setInstructions("You are a helpful agent")
            .setTools(Arrays.asList(new CodeInterpreterToolDefinition()))
            .setMetadata(mapOf("purpose", "testing"))
            .setTemperature(0.5)
            .setTopP(0.8);

        StepVerifier.create(agentsAsyncClient.createAgent(createAgentOptions).flatMap(agent -> {
            // Verify all properties were set correctly
            assertNotNull(agent.getId());
            assertEquals(agentName, agent.getName());
            assertEquals("Agent for testing properties", agent.getDescription());
            assertEquals("You are a helpful agent", agent.getInstructions());
            assertEquals("gpt-4o-mini", agent.getModel());
            assertEquals(0.5, agent.getTemperature());
            assertEquals(0.8, agent.getTopP());
            assertNotNull(agent.getMetadata());

            // Get agent to verify properties persisted
            return agentsAsyncClient.getAgent(agent.getId()).flatMap(retrievedAgent -> {
                assertEquals(agent.getId(), retrievedAgent.getId());
                assertEquals(agent.getName(), retrievedAgent.getName());
                assertEquals(agent.getDescription(), retrievedAgent.getDescription());
                assertEquals(agent.getTemperature(), retrievedAgent.getTemperature());

                // Cleanup
                return agentsAsyncClient.deleteAgent(agent.getId());
            });
        })).expectNextCount(1).verifyComplete();
    }

    @Test
    void testVectorStoreOperations() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> fileIdRef = new AtomicReference<>();
        AtomicReference<String> vectorStoreIdRef = new AtomicReference<>();

        StepVerifier.create(
                // Upload a file
                agentsAsyncClient.uploadFile(new UploadFileRequest(
                    new FileDetails(BinaryData.fromString("Sample vector store content for async test"))
                        .setFilename("vector_store_async_test.txt"),
                    FilePurpose.AGENTS)
                ).flatMap(file -> {
                    fileIdRef.set(file.getId());

                    // Create vector store with file
                    return agentsAsyncClient.createVectorStore(Arrays.asList(file.getId()), "async_vector_store_test",
                        null, null, null, null);
                }).flatMap(vectorStore -> {
                    assertNotNull(vectorStore);
                    assertNotNull(vectorStore.getId());
                    assertEquals("async_vector_store_test", vectorStore.getName());
                    vectorStoreIdRef.set(vectorStore.getId());

                    // Get vector store
                    return agentsAsyncClient.getVectorStore(vectorStore.getId());
                }).flatMap(vectorStore -> {
                    // List vector stores
                    return agentsAsyncClient.listVectorStores();
                }))
            .expectNextCount(1)
            .verifyComplete();

        // Test vector store deletion
        StepVerifier
            .create(agentsAsyncClient.deleteVectorStore(vectorStoreIdRef.get())
                .then(agentsAsyncClient.deleteFile(fileIdRef.get())))
            .assertNext(status -> assertNotNull(status))
            .verifyComplete();
    }

    @Test
    void testVectorStoreFileBatch() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> fileIdRef = new AtomicReference<>();
        AtomicReference<String> vectorStoreIdRef = new AtomicReference<>();

        StepVerifier.create(
                // Upload a file
                agentsAsyncClient.uploadFile(
                        new UploadFileRequest(new FileDetails(BinaryData.fromString("File batch content for vector store test"))
                            .setFilename("vector_store_batch_async_test.txt"), FilePurpose.AGENTS))
                    .flatMap(file -> {
                        fileIdRef.set(file.getId());

                        // Create empty vector store
                        return agentsAsyncClient.createVectorStore(null, "async_vector_batch_test", null, null, null, null);
                    })
                    .flatMap(vectorStore -> {
                        vectorStoreIdRef.set(vectorStore.getId());

                        // Create vector store file batch
                        return agentsAsyncClient.createVectorStoreFileBatch(vectorStore.getId(),
                            Arrays.asList(fileIdRef.get()), null, null);
                    }))
            .assertNext(batch -> {
                assertNotNull(batch);
                assertNotNull(batch.getId());
                assertEquals(vectorStoreIdRef.get(), batch.getVectorStoreId());
            })
            .verifyComplete();

        // Clean up resources
        StepVerifier
            .create(agentsAsyncClient.deleteVectorStore(vectorStoreIdRef.get())
                .then(agentsAsyncClient.deleteFile(fileIdRef.get())))
            .assertNext(status -> assertNotNull(status))
            .verifyComplete();
    }

    @Test
    void testDeleteThread() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        StepVerifier.create(agentsAsyncClient.createThread().flatMap(thread -> {
            assertNotNull(thread);
            assertNotNull(thread.getId());
            return agentsAsyncClient.deleteThread(thread.getId());
        })).assertNext(status -> assertNotNull(status)).verifyComplete();
    }

    @Test
    void testListMessages() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();
        AtomicReference<String> threadId = new AtomicReference<>();

        StepVerifier.create(
            // Create a thread
            agentsAsyncClient.createThread().flatMap(thread -> {
                threadId.set(thread.getId());
                // Add multiple messages
                return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER, "First message");
            }).flatMap(message -> {
                return agentsAsyncClient.createMessage(threadId.get(), MessageRole.USER, "Second message");
            }).flatMap(message -> {
                // List messages
                return agentsAsyncClient.listMessages(threadId.get());
            })).assertNext(messageList -> {
                assertNotNull(messageList);
                assertEquals(2, messageList.getData().size());
                assertEquals(MessageRole.USER, messageList.getData().get(0).getRole());
                assertEquals(MessageRole.USER, messageList.getData().get(1).getRole());
            }
        ).verifyComplete();

        // Clean up
        StepVerifier.create(agentsAsyncClient.deleteThread(threadId.get())).expectNextCount(1).verifyComplete();
    }

    @Test
    void testGetMessage() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();
        AtomicReference<String> threadId = new AtomicReference<>();
        AtomicReference<String> messageId = new AtomicReference<>();

        StepVerifier.create(
            // Create a thread
            agentsAsyncClient.createThread().flatMap(thread -> {
                threadId.set(thread.getId());
                // Add a message
                return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER, "Test message for retrieval");
            }).flatMap(message -> {
                messageId.set(message.getId());
                // Get the message
                return agentsAsyncClient.getMessage(threadId.get(), message.getId());
            })).assertNext(message -> {
                assertNotNull(message);
                assertEquals(messageId.get(), message.getId());
                assertEquals(MessageRole.USER, message.getRole());
                assertTrue(message.getContent().get(0) instanceof MessageTextContent);
                assertEquals("Test message for retrieval",
                    ((MessageTextContent) message.getContent().get(0)).getText().getValue());
            }
        ).verifyComplete();

        // Clean up
        StepVerifier.create(agentsAsyncClient.deleteThread(threadId.get())).expectNextCount(1).verifyComplete();
    }

    @Test
    void testSubmitToolOutputsToRun() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> threadId = new AtomicReference<>();
        AtomicReference<String> agentId = new AtomicReference<>();

        FunctionToolDefinition getWeatherTool = new FunctionToolDefinition(new FunctionDefinition("getWeather",
            BinaryData.fromObject(mapOf("type", "object", "properties",
                mapOf("location", mapOf("type", "string", "description", "The city name")), "required",
                new String[]{"location"}))).setDescription("Get weather for a location"));

        // Create agent with function tool
        StepVerifier.create(
            agentsAsyncClient.createAgent(new CreateAgentOptions("gpt-4o-mini")
                .setName("tool_output_test_async")
                .setInstructions("You help with weather information")
                .setTools(Arrays.asList(getWeatherTool))
            ).flatMap(agent -> {
                agentId.set(agent.getId());
                return agentsAsyncClient.createThread();
            }).flatMap(thread -> {
                threadId.set(thread.getId());
                return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER,
                    "What's the weather in Seattle?");
            }).flatMap(message -> {
                CreateRunOptions runOptions = new CreateRunOptions(threadId.get(), agentId.get());
                return agentsAsyncClient.createRun(runOptions);
            })
        ).expectNextCount(1).verifyComplete();

        // Poll until we get REQUIRES_ACTION status or completion
        Mono<ThreadRun> waitForRequiresAction = Mono.defer(() -> agentsAsyncClient.getRun(threadId.get(), agentsAsyncClient.listRuns(threadId.get()).block().getData().get(0).getId()))
            .expand(run -> {
                if (run.getStatus() == RunStatus.REQUIRES_ACTION
                    || run.getStatus() == RunStatus.COMPLETED
                    || run.getStatus() == RunStatus.FAILED) {
                    return Mono.empty();
                }
                return Mono.delay(Duration.ofSeconds(1)).then(agentsAsyncClient.getRun(threadId.get(), run.getId()));
            }).filter(run -> run.getStatus() == RunStatus.REQUIRES_ACTION).next();

        // Test submitting tool outputs when the run requires action
        StepVerifier.create(waitForRequiresAction.flatMap(run -> {
            if (run.getRequiredAction() instanceof SubmitToolOutputsAction) {
                SubmitToolOutputsAction action = (SubmitToolOutputsAction) run.getRequiredAction();
                List<ToolOutput> outputs = new ArrayList<>();

                for (RequiredToolCall call : action.getSubmitToolOutputs().getToolCalls()) {
                    if (call instanceof RequiredFunctionToolCall) {
                        RequiredFunctionToolCall functionCall = (RequiredFunctionToolCall) call;
                        if ("getWeather".equals(functionCall.getFunction().getName())) {
                            outputs
                                .add(new ToolOutput().setToolCallId(functionCall.getId()).setOutput("72°F and sunny"));
                        }
                    }
                }

                return agentsAsyncClient.submitToolOutputsToRun(threadId.get(), run.getId(), outputs);
            }
            return Mono.just(run); // No action required
        })).assertNext(run -> {
            assertNotNull(run);
        }).verifyComplete();

        // Clean up
        StepVerifier
            .create(agentsAsyncClient.deleteAgent(agentId.get()).then(agentsAsyncClient.deleteThread(threadId.get())))
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    @Disabled
    void testListRuns() {
        AgentsAsyncClient agentsAsyncClient = getAIProjectClientBuilder().buildAgentsAsyncClient();

        AtomicReference<String> threadId = new AtomicReference<>();
        AtomicReference<String> agentId = new AtomicReference<>();

        StepVerifier.create(createTestAgent(agentsAsyncClient, "list_runs_test_async")
            .flatMap(agent -> {
                agentId.set(agent.getId());
                return agentsAsyncClient.createThread();
            })
            .flatMap(thread -> {
                threadId.set(thread.getId());
                return agentsAsyncClient.createMessage(thread.getId(), MessageRole.USER, "Simple question 1");
            })
            .flatMap(message -> {
                CreateRunOptions runOptions = new CreateRunOptions(threadId.get(), agentId.get());
                return agentsAsyncClient.createRun(runOptions);
            })
            .flatMap(run -> {
                // List runs
                return agentsAsyncClient.listRuns(threadId.get());
            })).assertNext(runList -> {
                assertNotNull(runList);
                assertEquals(1, runList.getData().size());
            }
        ).verifyComplete();

        // Clean up
        StepVerifier
            .create(agentsAsyncClient.deleteAgent(agentId.get()).then(agentsAsyncClient.deleteThread(threadId.get())))
            .expectNextCount(1)
            .verifyComplete();
    }

    // Helper method to map objects
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }

    // Helper method to create test agents
    private Mono<Agent> createTestAgent(AgentsAsyncClient client, String name) {
        CreateAgentOptions options
            = new CreateAgentOptions("gpt-4o-mini").setName(name).setInstructions("Test agent for " + name);
        return client.createAgent(options);
    }
}
