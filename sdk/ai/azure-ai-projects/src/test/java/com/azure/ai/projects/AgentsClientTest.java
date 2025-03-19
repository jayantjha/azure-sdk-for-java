package com.azure.ai.projects;

import com.azure.ai.projects.implementation.models.*;
import com.azure.ai.projects.models.*;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.rest.RequestOptions;
import com.azure.core.test.TestMode;
import com.azure.core.test.TestProxyTestBase;
import com.azure.core.test.utils.MockTokenCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class AgentsClientTest extends TestProxyTestBase{

    protected InternalAgentsClient agentsClient;

    @BeforeEach
    void setup() {
        createAgentsClient();
    }

    private void createAgentsClient() {
        AIProjectClientBuilder internalAgentsClientbuilder
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .subscriptionId(Configuration.getGlobalConfiguration().get("SUBSCRIPTIONID", "subscriptionid"))
            .resourceGroupName(Configuration.getGlobalConfiguration().get("RESOURCEGROUPNAME", "resourcegroupname"))
            .projectName(Configuration.getGlobalConfiguration().get("PROJECTNAME", "projectname"))
            .httpClient(getHttpClientOrUsePlayback(getHttpClients().findFirst().orElse(null)))
            .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC));
        if (getTestMode() == TestMode.PLAYBACK) {
            internalAgentsClientbuilder.credential(new MockTokenCredential());
        } else if (getTestMode() == TestMode.RECORD) {
            internalAgentsClientbuilder.addPolicy(interceptorManager.getRecordPolicy())
                .credential(new DefaultAzureCredentialBuilder().build());
        } else if (getTestMode() == TestMode.LIVE) {
            internalAgentsClientbuilder.credential(new DefaultAzureCredentialBuilder().build());
        }
        agentsClient = internalAgentsClientbuilder.buildInternalAgentsClient();
    }

    @Test
    void createBasicAgent() {
        var agentName = "my_code_interpreter_agent_basic";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a helpful agent")
                .setTools(List.of(new CodeInterpreterToolDefinition()));
            agent = agentsClient.createAgent(createAgentOptions);
        }
        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "I need to solve the equation `3x + 11 = 14`. Can you help me?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    void createAgentWithAdditionalMessages() {
        var agentName = "my_code_interpreter_agent_with_additional_messages";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a personal electronics tutor. Write and run code to answer questions.")
                .setTools(List.of(new CodeInterpreterToolDefinition()));
            agent = agentsClient.createAgent(createAgentOptions);
        }
        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What is the impedance formula?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalMessages(List.of(new ThreadMessageOptions(
                MessageRole.AGENT, "E=mc^2"
            ), new ThreadMessageOptions(
                MessageRole.USER, "What is the impedance formula?"
            )));
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    void createAgentWithCustomFunction() {
        Supplier<String> getUserFavoriteCity = () -> "Seattle, WA";
        var getUserFavoriteCityTool = new FunctionToolDefinition(
            new FunctionDefinition(
                "getUserFavoriteCity",
                BinaryData.fromObject(
                    new Object()
                ))
        );

        Function<String, String> getCityNickname = location -> {
            return "The Emerald City";
        };

        var getCityNicknameTool = new FunctionToolDefinition(
            new FunctionDefinition(
                "getCityNickname",
                BinaryData.fromObject(
                    Map.of(
                        "type", "object",
                        "properties", Map.of(
                            "location",
                            Map.of(
                                "type", "string",
                                "description", "The city and state, e.g. San Francisco, CA")
                        ),
                        "required", new String[] {"location"}))
            )
        );

        Function<RequiredToolCall, ToolOutput> getResolvedToolOutput = toolCall -> {
            if (toolCall instanceof RequiredFunctionToolCall) {
                var functionToolCall = (RequiredFunctionToolCall)toolCall;
                if (functionToolCall.getFunction().getName().equals("getUserFavoriteCity"))
                    return new ToolOutput().setToolCallId(functionToolCall.getId())
                        .setOutput(getUserFavoriteCity.get());

                try {
                    JsonMapper jsonMapper = new JsonMapper();
                    JsonNode rootNode = jsonMapper.readTree(functionToolCall.getFunction().getArguments());
                    if (functionToolCall.getFunction().getName().equals("getCityNickname")) {

                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }
            return null;
        };

        var agentName = "my_custom_function_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a weather bot. Use the provided functions to help answer questions. "
                    + "Customize your responses to the user's preferences as much as possible and use friendly "
                    + "nicknames for cities whenever possible.")
                .setTools(List.of(getUserFavoriteCityTool, getCityNicknameTool));
            agent = agentsClient.createAgent(createAgentOptions);
        }
        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What's my favorite city?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
                if (threadRun.getStatus() == RunStatus.REQUIRES_ACTION
                    && threadRun.getRequiredAction() instanceof SubmitToolOutputsAction) {
                    var submitToolsOutputAction = (SubmitToolOutputsAction)(threadRun.getRequiredAction());
                    var toolOutputs = new ArrayList<ToolOutput>();
                    for (RequiredToolCall toolCall : submitToolsOutputAction.getSubmitToolOutputs().getToolCalls()) {
                        toolOutputs.add(getResolvedToolOutput.apply(toolCall));
                    }
                    threadRun = agentsClient.submitToolOutputsToRun(thread.getId(), threadRun.getId(), toolOutputs);
                }
            }
            while (threadRun.getStatus() == RunStatus.QUEUED || threadRun.getStatus() == RunStatus.IN_PROGRESS);
            var runMessages = agentsClient.listMessages(thread.getId());
            assertTrue(runMessages.getData().stream().count() > 1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createAgentWithBingGrounding() {
        ToolConnectionList toolConnectionList = new ToolConnectionList()
            .setConnectionList(List.of(new ToolConnection("/subscriptions/696debc0-8b66-4d84-87b1-39f43917d76c/resourceGroups/rg-jayant/providers/Microsoft.MachineLearningServices/workspaces/jayant-sdk-project-eus/connections/jayantagentbing")));
        BingGroundingToolDefinition bingGroundingTool = new BingGroundingToolDefinition(toolConnectionList);
        var agentName = "my_bing_grounding_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-35-turbo")
                .setName(agentName)
                .setInstructions("You are a helpful agent")
                .setTools(List.of(bingGroundingTool));
            agent = agentsClient.createAgent(createAgentOptions);
        }
        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "How does wikipedia explain Euler's Identity?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    void createAgentWithFileSearch() throws IOException, InterruptedException {
        OpenAIFile uploadedAgentFile = agentsClient.uploadFile(
            new FileDetails(
                BinaryData.fromString("The word `apple` uses the code 442345, while the word `banana` uses the code 673457."))
                .setFilename("sample_file_for_upload.txt"),
            FilePurpose.AGENTS);

        VectorStore vectorStore = agentsClient.createVectorStore(
            List.of(uploadedAgentFile.getId()),
            "my_vector_store",
            null, null, null, null);

        do {
            Thread.sleep(500);
            vectorStore = agentsClient.getVectorStore(vectorStore.getId());
        }
        while (vectorStore.getStatus() == VectorStoreStatus.IN_PROGRESS);

        FileSearchToolResource fileSearchToolResource = new FileSearchToolResource()
            .setVectorStoreIds(List.of(vectorStore.getId()));

        var agentName = "my_file_search_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a helpful agent that can help fetch data from files you know about.")
                .setTools(List.of(new FileSearchToolDefinition()))
                .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));
            agent = agentsClient.createAgent(createAgentOptions);
        }

        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "Can you give me the documented codes for 'banana' and 'orange'?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    void createAgentWithBatchFileSearch() throws IOException, InterruptedException, URISyntaxException {
        var productFile = getFile("product_info_1.md");

        VectorStore vectorStore = agentsClient.createVectorStore(
            null,"my_vector_store",
            null, null, null, null);

        OpenAIFile uploadedAgentFile = agentsClient.uploadFile(
            new FileDetails(
                BinaryData.fromFile(productFile))
                .setFilename("sample_product_info.md"),
            FilePurpose.AGENTS);

        VectorStoreFileBatch vectorStoreFileBatch = agentsClient.createVectorStoreFileBatch(
            vectorStore.getId(), List.of(uploadedAgentFile.getId()), null, null);

        FileSearchToolResource fileSearchToolResource = new FileSearchToolResource()
            .setVectorStoreIds(List.of(vectorStore.getId()));

        var agentName = "my_batch_file_search_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a helpful agent.")
                .setTools(List.of(new FileSearchToolDefinition()))
                .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));
            agent = agentsClient.createAgent(createAgentOptions);
        }

        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What feature does Smart Eyewear offer?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    void createAISearchAgent() {
        String aiSearchConnectionId = "subscriptions/696debc0-8b66-4d84-87b1-39f43917d76c/resourceGroups/rg-jayant/providers/Microsoft.MachineLearningServices/workspaces/jayant-sdk-project-eus/connections/testjayant";

        ToolResources toolResources = new ToolResources()
            .setAzureAISearch(new AzureAISearchResource()
                .setIndexList(List.of(new IndexResource(aiSearchConnectionId, "sample_index"))));

        var agentName = "my_ai_search_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a helpful agent")
                .setTools(List.of(new AzureAISearchToolDefinition()))
                .setToolResources(toolResources);
            agent = agentsClient.createAgent(createAgentOptions);
        }
        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "Tell me anything!");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    void createCodeInterpreterFileAttachmentAgent() throws FileNotFoundException, URISyntaxException {
        var htmlFile = getFile("sample_test.html");

        var agentName = "my_code_interpreter_file_attachment_agent";
        var ciTool = new CodeInterpreterToolDefinition();
        var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
            .setName(agentName)
            .setInstructions("You are a helpful agent")
            .setTools(List.of(ciTool));
        Agent agent = agentsClient.createAgent(createAgentOptions);

        OpenAIFile uploadedFile = agentsClient.uploadFile(
            new FileDetails(BinaryData.fromFile(htmlFile))
                .setFilename("sample_test.html"),
            FilePurpose.AGENTS);

        MessageAttachment messageAttachment = new MessageAttachment(
            List.of(BinaryData.fromObject(ciTool))
        ).setFileId(uploadedFile.getId());

        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What does the attachment say?",
            List.of(messageAttachment),
            null
        );
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    void createAgentWithEnterpriseFileSearch() {
        var storageBlobUri = Configuration.getGlobalConfiguration().get("STORAGE_BLOB_URI", "");
        VectorStoreDataSource vectorStoreDataSource = new VectorStoreDataSource(
            storageBlobUri, VectorStoreDataSourceAssetType.URI_ASSET);
        VectorStore vs = agentsClient.createVectorStore(
            null, "sample_vector_store",
            new VectorStoreConfiguration(List.of(vectorStoreDataSource)),
            null, null, null
        );

        FileSearchToolResource fileSearchToolResource = new FileSearchToolResource()
            .setVectorStoreIds(List.of(vs.getId()));

        var agentName = "my_enterprise_file_search_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a helpful agent.")
                .setTools(List.of(new FileSearchToolDefinition()))
                .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));
            agent = agentsClient.createAgent(createAgentOptions);
        }

        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What feature does Smart Eyewear offer?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);

        agentsClient.deleteVectorStore(vs.getId());
    }

    @Test
    void createAgentWithOpenApi() throws FileNotFoundException, URISyntaxException {
        var filePath = getFile("weather_openapi.json");
        OpenApiAnonymousAuthDetails oaiAuth = new OpenApiAnonymousAuthDetails();
        OpenApiToolDefinition openApiTool = new OpenApiToolDefinition(
            new OpenApiFunctionDefinition(
                "get_weather",
                BinaryData.fromFile(filePath),
                oaiAuth
            ).setDescription("Retrieve weather information for a location")
        );
        var agentName = "my_openapi_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o")
                .setName(agentName)
                .setInstructions("You are a helpful agent")
                .setTools(List.of(openApiTool));
            agent = agentsClient.createAgent(createAgentOptions);
        }
        var thread = agentsClient.createThread();

        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What's the weather in seattle?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    @Disabled
    void createAgentWithAzureFunction() {
        var storageQueueUri = Configuration.getGlobalConfiguration().get("STORAGE_QUEUE_URI", "");

        FunctionDefinition fnDef = new FunctionDefinition(
            "jayantagentazfn",
            BinaryData.fromObject(
                Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "location",
                        Map.of("type", "string", "description", "The location to look up")
                    ),
                    "required", new String[] {"location"}
                )
            )
        );
        AzureFunctionDefinition azureFnDef = new AzureFunctionDefinition(
            fnDef,
            new AzureFunctionBinding(new AzureFunctionStorageQueue(storageQueueUri,"agent-input")),
            new AzureFunctionBinding(new AzureFunctionStorageQueue(storageQueueUri,"agent-output"))
        );
        AzureFunctionToolDefinition azureFnTool = new AzureFunctionToolDefinition(azureFnDef);

        var agentName = "my_azure_fn_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            RequestOptions requestOptions = new RequestOptions()
                .setHeader(HttpHeaderName.fromString("x-ms-enable-preview"), "true");
            CreateAgentRequest createAgentRequestObj = new CreateAgentRequest("gpt-4")
                .setName(agentName)
                .setInstructions("You are a helpful agent. Use the provided function any time "
                    + "you are asked with the weather of any location")
                .setTools(List.of(azureFnTool));
            BinaryData createAgentRequest = BinaryData.fromObject(createAgentRequestObj);
            agent = agentsClient.createAgentWithResponse(createAgentRequest, requestOptions)
                .getValue().toObject(Agent.class);
        }

        var thread = agentsClient.createThread();
        assertNotNull(thread);
        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "What is the weather in Seattle, WA?");
        assertNotNull(createdMessage);

        //run agent
        var createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
            .setAdditionalInstructions("");
        var threadRun = agentsClient.createRun(createRunOptions);
        assertNotNull(threadRun);

        checkMessagesFromRun(threadRun, thread);
    }

    @Test
    @Disabled
    void createStreamingAgent() {
        var agentName = "my_code_interpreter_agent";
        Agent agent = null;
        var agentOptional = agentsClient.listAgents().getData().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst();
        if (agentOptional.isPresent())
            agent = agentOptional.get();
        else {
            var createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
                .setName(agentName)
                .setInstructions("You are a helpful agent")
                .setTools(List.of(new CodeInterpreterToolDefinition()));
            agent = agentsClient.createAgent(createAgentOptions);
        }
        var thread = agentsClient.createThread();
        assertNotNull(thread);

        var createdMessage = agentsClient.createMessage(
            thread.getId(),
            MessageRole.USER,
            "Hi, Assistant! Draw a graph for a line with a slope of 4 and y-intercept of 9.");
        assertNotNull(createdMessage);
        fail("Incomplete");
    }

    @Test
    void listAndDeleteAllAgents() {
        agentsClient.listAgents().getData().stream()
            .forEach(agent -> agentsClient.deleteAgent(agent.getId()));
    }

    private Path getFile(String fileName) throws FileNotFoundException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new FileNotFoundException("File not found");
        }
        File file = new File(resource.toURI());
        return file.toPath();
    }

    private void checkMessagesFromRun(ThreadRun threadRun, AgentThread thread) {
        try {
            do {
                Thread.sleep(500);
                threadRun = agentsClient.getRun(thread.getId(), threadRun.getId());
            }
            while (threadRun.getStatus() == RunStatus.QUEUED || threadRun.getStatus() == RunStatus.IN_PROGRESS);
            var runMessages = agentsClient.listMessages(thread.getId());
            assertTrue(runMessages.getData().stream().count() > 1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
