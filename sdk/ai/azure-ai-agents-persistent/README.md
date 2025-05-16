# Azure AI Persistent Agents client library for Java

Azure AI Persistent Agents client library for Java.

This package contains Microsoft Azure AI Persistent Agents client library.

## Documentation

Various documentation is available to help you get started

- [API reference documentation][docs]
- [Product documentation][product_documentation]

## Getting started

### Prerequisites

- [Java Development Kit (JDK)][jdk] with version 8 or above
- [Azure Subscription][azure_subscription]

### Adding the package to your product

[//]: # ({x-version-update-start;com.azure:azure-ai-agents-persistent;current})
```xml
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-ai-agents-persistent</artifactId>
    <version>1.0.0-beta.1</version>
</dependency>
```
[//]: # ({x-version-update-end})

### Authentication

[Azure Identity][azure_identity] package provides the default implementation for authenticating the client.

## Key concepts

The Azure AI Persistent Agents client library provides several client classes that allow you to interact with the Azure AI Persistent Agents service.

## Examples
The following sections provide several code snippets that show how to use the Azure AI Persistent Agents client library for Java.

### Building service clients
In order to interact with Azure AI Persistent Agents service, you need to create instances of available service client classes listed below:
- [PersistentAgentsAdministrationClient][persistent_agents_administration_client] / [PersistentAgentsAdministrationAsyncClient][persistent_agents_administration_async_client] - Main client for managing agents
- [ThreadsClient][threads_client] / [ThreadsAsyncClient][threads_async_client] - Manage conversation threads
- [MessagesClient][messages_client] / [MessagesAsyncClient][messages_async_client] - Work with messages in threads
- [RunsClient][runs_client] / [RunsAsyncClient][runs_async_client] - Execute and manage thread runs
- [RunStepsClient][run_steps_client] - Retrieve and work with run steps
- [FilesClient][files_client] / [FilesAsyncClient][files_async_client] - Manage files used by agents
- [VectorStoresClient][vector_stores_client] / [VectorStoresAsyncClient][vector_stores_async_client] - Manage vector stores
- [VectorStoreFilesClient][vector_store_files_client] / [VectorStoreFilesAsyncClient][vector_store_files_async_client] - Manage files in vector stores
- [VectorStoreFileBatchesClient][vector_store_file_batches_client] / [VectorStoreFileBatchesAsyncClient][vector_store_file_batches_async_client] - Manage file batches in vector stores

The following snippet shows how to create service client instances:
```java com.azure.ai.agents.persistent.SampleUtils.buildClients
PersistentAgentsAdministrationClientBuilder clientBuilder = new PersistentAgentsAdministrationClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
    .credential(new DefaultAzureCredentialBuilder().build());

// Main administration clients
PersistentAgentsAdministrationClient agentsClient = clientBuilder.buildClient();
PersistentAgentsAdministrationAsyncClient agentsAsyncClient = clientBuilder.buildAsyncClient();

// Thread clients
ThreadsClient threadsClient = clientBuilder.buildThreadsClient();
ThreadsAsyncClient threadsAsyncClient = clientBuilder.buildThreadsAsyncClient();

// Message clients
MessagesClient messagesClient = clientBuilder.buildMessagesClient();
MessagesAsyncClient messagesAsyncClient = clientBuilder.buildMessagesAsyncClient();

// Run clients
RunsClient runsClient = clientBuilder.buildRunsClient();
RunsAsyncClient runsAsyncClient = clientBuilder.buildRunsAsyncClient();
RunStepsClient runStepsClient = clientBuilder.buildRunStepsClient();

// File clients
FilesClient filesClient = clientBuilder.buildFilesClient();
FilesAsyncClient filesAsyncClient = clientBuilder.buildFilesAsyncClient();

// Vector store clients
VectorStoresClient vectorStoresClient = clientBuilder.buildVectorStoresClient();
VectorStoresAsyncClient vectorStoresAsyncClient = clientBuilder.buildVectorStoresAsyncClient();

// Vector store files clients
VectorStoreFilesClient vectorStoreFilesClient = clientBuilder.buildVectorStoreFilesClient();
VectorStoreFilesAsyncClient vectorStoreFilesAsyncClient = clientBuilder.buildVectorStoreFilesAsyncClient();

// Vector store file batches clients
VectorStoreFileBatchesClient vectorStoreFileBatchesClient = clientBuilder.buildVectorStoreFileBatchesClient();
VectorStoreFileBatchesAsyncClient vectorStoreFileBatchesAsyncClient = clientBuilder.buildVectorStoreFileBatchesAsyncClient();
```

### Samples showcasing agents tools and use cases

The following samples demonstrate various tools and use cases for Azure AI Persistent Agents:

#### Basic agent usage
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentBasicSample
String agentName = "basic_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(new CodeInterpreterToolDefinition()));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Basic Sample][agent_basic_sample]
- [Agent Basic Async Sample][agent_basic_async_sample]

#### Agent usage with additional messages
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentAdditionalMessageSample
CreateRunOptions createRunOptions = new CreateRunOptions(thread.getId(), agent.getId())
    .setAdditionalMessages(Arrays.asList(new ThreadMessageOptions(
        MessageRole.AGENT, BinaryData.fromString("E=mc^2")
    ), new ThreadMessageOptions(
        MessageRole.USER, BinaryData.fromString("What is the impedance formula?")
    )));
ThreadRun threadRun = runsClient.createRun(createRunOptions);
```

For a complete example, check the samples below:
- [Agent Additional Message Sample][agent_additional_message_sample]
- [Agent Additional Message Async Sample][agent_additional_message_async_sample]

#### Agent usage with Azure AI Search integration
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentAzureAISearchSample
String aiSearchConnectionId = Configuration.getGlobalConfiguration().get("AI_SEARCH_CONNECTION_ID", "");

ToolResources toolResources = new ToolResources()
    .setAzureAISearch(new AzureAISearchResource()
        .setIndexList(Arrays.asList(new AISearchIndexResource(aiSearchConnectionId, "azureblob-index"))));

String agentName = "ai_search_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(new AzureAISearchToolDefinition()))
    .setToolResources(toolResources);
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Azure AI Search Sample][agent_azure_ai_search_sample]
- [Agent Azure AI Search Async Sample][agent_azure_ai_search_async_sample]

#### Agent usage with Azure Functions integration
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentAzureFunctionSample
String storageQueueUri = Configuration.getGlobalConfiguration().get("STORAGE_QUEUE_URI", "");
String azureFunctionName = Configuration.getGlobalConfiguration().get("AZURE_FUNCTION_NAME", "");

FunctionDefinition fnDef = new FunctionDefinition(
    azureFunctionName,
    BinaryData.fromObject(
        mapOf(
            "type", "object",
            "properties", mapOf(
                "location",
                mapOf("type", "string", "description", "The location to look up")
            ),
            "required", new String[]{"location"}
        )
    )
);
AzureFunctionDefinition azureFnDef = new AzureFunctionDefinition(
    fnDef,
    new AzureFunctionBinding(new AzureFunctionStorageQueue(storageQueueUri, "agent-input")),
    new AzureFunctionBinding(new AzureFunctionStorageQueue(storageQueueUri, "agent-output"))
);
AzureFunctionToolDefinition azureFnTool = new AzureFunctionToolDefinition(azureFnDef);

String agentName = "azure_function_example";
RequestOptions requestOptions = new RequestOptions()
    .setHeader(HttpHeaderName.fromString("x-ms-enable-preview"), "true");
CreateAgentRequest createAgentRequestObj = new CreateAgentRequest("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent. Use the provided function any time "
        + "you are asked with the weather of any location")
    .setTools(Arrays.asList(azureFnTool));
BinaryData createAgentRequest = BinaryData.fromObject(createAgentRequestObj);
PersistentAgent agent = agentsClient.createAgentWithResponse(createAgentRequest, requestOptions)
    .getValue().toObject(PersistentAgent.class);
```

For a complete example, check the samples below:
- [Agent Azure Function Sample][agent_azure_function_sample]
- [Agent Azure Function Async Sample][agent_azure_function_async_sample]

#### Agent usage with Bing Grounding
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentBingGroundingSample
BingGroundingSearchConfiguration searchConfiguration = new BingGroundingSearchConfiguration(bingConnectionId);
BingGroundingSearchConfigurationList searchConfigurationList = new BingGroundingSearchConfigurationList(Arrays.asList(searchConfiguration));

BingGroundingToolDefinition bingGroundingTool = new BingGroundingToolDefinition(searchConfigurationList);

String agentName = "bing_grounding_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-35-turbo")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(bingGroundingTool));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Bing Grounding Sample][agent_bing_grounding_sample]
- [Agent Bing Grounding Async Sample][agent_bing_grounding_async_sample]

#### Agent usage with Code Interpreter and Enterprise File Search
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentCodeInterpreterEnterpriseFileSearchSample
String agentName = "code_interpreter_enterprise_file_search_example";
CodeInterpreterToolDefinition ciTool = new CodeInterpreterToolDefinition();
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(ciTool));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);

String dataUri = Configuration.getGlobalConfiguration().get("DATA_URI", "");
VectorStoreDataSource vectorStoreDataSource = new VectorStoreDataSource(
    dataUri, VectorStoreDataSourceAssetType.URI_ASSET);

MessageAttachment messageAttachment = new MessageAttachment(
    Arrays.asList(BinaryData.fromObject(ciTool))
).setDataSource(vectorStoreDataSource);

PersistentAgentThread thread = threadsClient.createThread();

ThreadMessage createdMessage = messagesClient.createMessage(
    thread.getId(),
    MessageRole.USER,
    "What does the attachment say?",
    Arrays.asList(messageAttachment),
    null
);
```

For a complete example, check the samples below:
- [Agent Code Interpreter Enterprise File Search Sample][agent_code_interpreter_enterprise_file_search_sample]
- [Agent Code Interpreter Enterprise File Search Async Sample][agent_code_interpreter_enterprise_file_search_async_sample]

#### Agent usage with Code Interpreter and File Attachment
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentCodeInterpreterFileAttachmentSample
Path htmlFile = getFile("sample.html");

String agentName = "code_interpreter_file_attachment_example";
CodeInterpreterToolDefinition ciTool = new CodeInterpreterToolDefinition();
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini").setName(agentName).setInstructions("You are a helpful agent").setTools(Arrays.asList(ciTool));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);

FileInfo uploadedFile = filesClient.uploadFile(new UploadFileRequest(
    new FileDetails(BinaryData.fromFile(htmlFile))
    .setFilename("sample.html"), FilePurpose.AGENTS));

MessageAttachment messageAttachment = new MessageAttachment(Arrays.asList(BinaryData.fromObject(ciTool))).setFileId(uploadedFile.getId());

PersistentAgentThread thread = threadsClient.createThread();
ThreadMessage createdMessage = messagesClient.createMessage(
    thread.getId(),
    MessageRole.USER,
    "What does the attachment say?",
    Arrays.asList(messageAttachment),
    null);
```

For a complete example, check the samples below:
- [Agent Code Interpreter File Attachment Sample][agent_code_interpreter_file_attachment_sample]
- [Agent Code Interpreter File Attachment Async Sample][agent_code_interpreter_file_attachment_async_sample]

#### Agent usage with Connected Agents
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentConnectedAgentSample
String connectedAgentName = "stock_price_bot";
CreateAgentOptions connectedAgentCreateOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(connectedAgentName)
    .setInstructions("Your job is to get the stock price of a company. Just return $391.85 EOD 27-Apr-2025");
PersistentAgent connectedAgent = agentsClient.createAgent(connectedAgentCreateOptions);

ConnectedAgentToolDefinition connectedAgentToolDefinition = new ConnectedAgentToolDefinition(
    new ConnectedAgentDetails(connectedAgent.getId(), connectedAgent.getName(), "Gets the stock price of a company"));

String agentName = "my-assistant";
CreateAgentRequest createAgentRequest = new CreateAgentRequest("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful assistant, and use the connected agent to get stock prices.")
    .setTools(Arrays.asList(connectedAgentToolDefinition));
RequestOptions requestOptions = new RequestOptions()
    .setHeader("x-ms-enable-preview", "true");
PersistentAgent agent = agentsClient.createAgentWithResponse(BinaryData.fromObject(createAgentRequest), requestOptions)
    .getValue().toObject(PersistentAgent.class);
```

For a complete example, check the samples below:
- [Agent Connected Agent Sample][agent_connected_agent_sample]
- [Agent Connected Agent Async Sample][agent_connected_agent_async_sample]

#### Agent usage with Enterprise File Search
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentEnterpriseFileSearchSample
String dataUri = Configuration.getGlobalConfiguration().get("DATA_URI", "");
VectorStoreDataSource vectorStoreDataSource = new VectorStoreDataSource(
    "assistant-6FP6sNAo21Z7pVR2ouGoPp", VectorStoreDataSourceAssetType.URI_ASSET);

VectorStore vs = vectorStoresClient.createVectorStore(
    null, "sample_vector_store",
    new VectorStoreConfiguration(Arrays.asList(vectorStoreDataSource)),
    null, null, null
);

FileSearchToolResource fileSearchToolResource = new FileSearchToolResource()
    .setVectorStoreIds(Arrays.asList(vs.getId()));

String agentName = "enterprise_file_search_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(new FileSearchToolDefinition()))
    .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Enterprise File Search Sample][agent_enterprise_file_search_sample]
- [Agent Enterprise File Search Async Sample][agent_enterprise_file_search_async_sample]

#### Agent usage with File Search
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentFileSearchSample
FileInfo uploadedAgentFile = filesClient.uploadFile(
    new UploadFileRequest(
        new FileDetails(
            BinaryData.fromString("The word `apple` uses the code 442345, while the word `banana` uses the code 673457."))
            .setFilename("sample_file_for_upload.txt"),
        FilePurpose.AGENTS));

VectorStore vectorStore = vectorStoresClient.createVectorStore(
    Arrays.asList(uploadedAgentFile.getId()),
    "my_vector_store",
    null, null, null, null);

do {
    Thread.sleep(500);
    vectorStore = vectorStoresClient.getVectorStore(vectorStore.getId());
}
while (vectorStore.getStatus() == VectorStoreStatus.IN_PROGRESS);

FileSearchToolResource fileSearchToolResource = new FileSearchToolResource()
    .setVectorStoreIds(Arrays.asList(vectorStore.getId()));

String agentName = "file_search_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent that can help fetch data from files you know about.")
    .setTools(Arrays.asList(new FileSearchToolDefinition()))
    .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent File Search Sample][agent_file_search_sample]
- [Agent File Search Async Sample][agent_file_search_async_sample]

#### Agent usage with Functions
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentFunctionsSample
Supplier<String> getUserFavoriteCity = () -> "Seattle, WA";
FunctionToolDefinition getUserFavoriteCityTool = new FunctionToolDefinition(
    new FunctionDefinition(
        "getUserFavoriteCity",
        BinaryData.fromObject(
            new Object()
        ))
);

Function<String, String> getCityNickname = location -> {
    return "The Emerald City";
};

FunctionToolDefinition getCityNicknameTool = new FunctionToolDefinition(
    new FunctionDefinition(
        "getCityNickname",
        BinaryData.fromObject(
            mapOf(
                "type", "object",
                "properties", mapOf(
                    "location",
                    mapOf(
                        "type", "string",
                        "description", "The city and state, e.g. San Francisco, CA")
                ),
                "required", new String[]{"location"}))
    ).setDescription("Get the nickname of a city")
);

Function<RequiredToolCall, ToolOutput> getResolvedToolOutput = toolCall -> {
    if (toolCall instanceof RequiredFunctionToolCall) {
        RequiredFunctionToolCall functionToolCall = (RequiredFunctionToolCall) toolCall;
        String functionName = functionToolCall.getFunction().getName();
        if (functionName.equals("getUserFavoriteCity")) {
            return new ToolOutput().setToolCallId(functionToolCall.getId())
                .setOutput(getUserFavoriteCity.get());
        } else if (functionName.equals("getCityNickname")) {
            String arguments = functionToolCall.getFunction().getArguments();
            try {
                JsonNode root = new JsonMapper().readTree(arguments);
                String location = String.valueOf(root.get("location").asText());
                return new ToolOutput().setToolCallId(functionToolCall.getId())
                    .setOutput(getCityNickname.apply(location));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
    return null;
};

String agentName = "functions_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a weather bot. Use the provided functions to help answer questions. "
        + "Customize your responses to the user's preferences as much as possible and use friendly "
        + "nicknames for cities whenever possible.")
    .setTools(Arrays.asList(getUserFavoriteCityTool, getCityNicknameTool));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Functions Sample][agent_functions_sample]
- [Agent Functions Async Sample][agent_functions_async_sample]

#### Agent usage with Image Input via URL
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentImageInputUrlSample
String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

List<MessageInputContentBlock> messageBlock = Arrays.asList(new MessageInputTextBlock("Hello, what is in the image"),
    new MessageInputImageUrlBlock(new MessageImageUrlParam(imageUrl)));

String agentName = "image_input_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent");
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Image Input URL Sample][agent_image_input_url_sample]
- [Agent Image Input URL Async Sample][agent_image_input_url_async_sample]

#### Agent usage with Image Input via Base64
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentImageInputBase64Sample
Path file = getFile("sample_image.jpg");
byte[] imageContent = Files.readAllBytes(file);
String imageBase64 = Base64.getEncoder().encodeToString(imageContent);
String imageUrl = "data:image/png;base64," + imageBase64;

List<MessageInputContentBlock> messageBlock = Arrays.asList(new MessageInputTextBlock("Hello, what is in the image"),
    new MessageInputImageUrlBlock(new MessageImageUrlParam(imageUrl)));

String agentName = "image_input_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent");
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Image Input Base64 Sample][agent_image_input_base64_sample]
- [Agent Image Input Base64 Async Sample][agent_image_input_base64_async_sample]

#### Agent usage with Image Input via File
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentImageInputFileSample
Path file = getFile("sample_image.jpg");

FileInfo uploadedAgentFile = filesClient.uploadFile(
    new UploadFileRequest(
        new FileDetails(
            BinaryData.fromFile(file))
            .setFilename("sample_image.jpg"),
        FilePurpose.AGENTS));

MessageImageFileParam fileParam = new MessageImageFileParam(uploadedAgentFile.getId());

List<MessageInputContentBlock> messageBlock = Arrays.asList(new MessageInputTextBlock("Hello, what is in the image"),
    new MessageInputImageFileBlock(fileParam));

String agentName = "image_input_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o")
    .setName(agentName)
    .setInstructions("You are a helpful agent");
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Image Input File Sample][agent_image_input_file_sample]
- [Agent Image Input File Async Sample][agent_image_input_file_async_sample]

#### Agent usage with OpenAPI
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentOpenApiSample
Path filePath = getFile("weather_openapi.json");
JsonReader reader = JsonProviders.createReader(Files.readAllBytes(filePath));

OpenApiAnonymousAuthDetails oaiAuth = new OpenApiAnonymousAuthDetails();
OpenApiToolDefinition openApiTool = new OpenApiToolDefinition(new OpenApiFunctionDefinition(
    "openapitool",
    reader.getNullable(nonNullReader -> BinaryData.fromObject(nonNullReader.readUntyped())),
    oaiAuth
));

String agentName = "openAPI_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(openApiTool));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent OpenAPI Sample][agent_openapi_sample]
- [Agent OpenAPI Async Sample][agent_openapi_async_sample]

#### Agent usage with Vector Store Batch File Search
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentVectorStoreBatchFileSearchSample
Path productFile = getFile("product_info.md");

VectorStore vectorStore = vectorStoresClient.createVectorStore(
    null, "my_vector_store",
    null, null, null, null);

FileInfo uploadedAgentFile = filesClient.uploadFile(new UploadFileRequest(
    new FileDetails(
        BinaryData.fromFile(productFile))
        .setFilename("sample_product_info.md"),
    FilePurpose.AGENTS));

VectorStoreFileBatch vectorStoreFileBatch = vectorStoreFileBatchesClient.createVectorStoreFileBatch(
    vectorStore.getId(), Arrays.asList(uploadedAgentFile.getId()), null, null);

FileSearchToolResource fileSearchToolResource = new FileSearchToolResource()
    .setVectorStoreIds(Arrays.asList(vectorStore.getId()));

String agentName = "vector_store_batch_file_search_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(new FileSearchToolDefinition()))
    .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Vector Store Batch File Search Sample][agent_vector_store_batch_file_search_sample]
- [Agent Vector Store Batch File Search Async Sample][agent_vector_store_batch_file_search_async_sample]

#### Agent usage with Vector Store Batch Enterprise File Search
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentVectorStoreBatchEnterpriseFileSearchSample
String dataUri = Configuration.getGlobalConfiguration().get("DATA_URI", "");
VectorStoreDataSource vectorStoreDataSource = new VectorStoreDataSource(
    "assistant-6FP6sNAo21Z7pVR2ouGoPp", VectorStoreDataSourceAssetType.ID_ASSET);

VectorStore vs = vectorStoresClient.createVectorStore(
    null, "sample_vector_store",
    new VectorStoreConfiguration(Arrays.asList(vectorStoreDataSource)),
    null, null, null
);

vectorStoreFileBatchesClient.createVectorStoreFileBatch(vs.getId(),
    null, Arrays.asList(vectorStoreDataSource), null);

FileSearchToolResource fileSearchToolResource = new FileSearchToolResource()
    .setVectorStoreIds(Arrays.asList(vs.getId()));

String agentName = "vector_store_batch_enterprise_file_search_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent")
    .setTools(Arrays.asList(new FileSearchToolDefinition()))
    .setToolResources(new ToolResources().setFileSearch(fileSearchToolResource));
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
```

For a complete example, check the samples below:
- [Agent Vector Store Batch Enterprise File Search Sample][agent_vector_store_batch_enterprise_file_search_sample]
- [Agent Vector Store Batch Enterprise File Search Async Sample][agent_vector_store_batch_enterprise_file_search_async_sample]

#### Agent usage with Streaming
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentStreamingSample
Stream<StreamUpdate> streamUpdates = runsClient.createRunStreaming(createRunOptions);

streamUpdates.forEach(streamUpdate -> {
    if (streamUpdate.getKind() == PersistentAgentStreamEvent.THREAD_RUN_CREATED) {
        System.out.println("----- Run started! -----");
    } else if (streamUpdate instanceof StreamMessageUpdate) {
        StreamMessageUpdate messageUpdate = (StreamMessageUpdate) streamUpdate;
        printStreamUpdate(messageUpdate);
    }
});
```

For a complete example, check the samples below:
- [Agent Streaming Sample][agent_streaming_sample]
- [Agent Streaming Async Sample][agent_streaming_async_sample]

#### Agent usage with Functions Streaming
Below is the snippet to understand the usage:
```java com.azure.ai.agents.persistent.AgentFunctionsStreamingSample
Stream<StreamUpdate> streamUpdates = runsClient.createRunStreaming(createRunOptions);

streamUpdates.forEach(
    streamUpdate -> {
        if (streamUpdate.getKind() == PersistentAgentStreamEvent.THREAD_RUN_CREATED) {
            System.out.println("----- Run started! -----");
        } else if (streamUpdate instanceof StreamRequiredAction) {
            StreamRequiredAction actionUpdate = (StreamRequiredAction) streamUpdate;
            AtomicReference<ThreadRun> streamRun = new AtomicReference<>(actionUpdate.getMessage());

            while (streamRun.get().getStatus() == RunStatus.REQUIRES_ACTION) {
                List<ToolOutput> toolOutputs = new ArrayList<>();

                SubmitToolOutputsAction submitToolsOutputAction = (SubmitToolOutputsAction) (streamRun.get().getRequiredAction());
                for (RequiredToolCall toolCall : submitToolsOutputAction.getSubmitToolOutputs().getToolCalls()) {
                    toolOutputs.add(getResolvedToolOutput.apply(toolCall));
                }

                runsClient.submitToolOutputsToRunStreaming(
                    streamRun.get().getThreadId(),
                    streamRun.get().getId(),
                    toolOutputs
                ).forEach(update -> {
                    if (update instanceof StreamRequiredAction) {
                        streamRun.set(((StreamRequiredAction) update).getMessage());
                    } else if (update instanceof StreamMessageUpdate) {
                        StreamMessageUpdate messageUpdate = (StreamMessageUpdate) update;
                        printStreamUpdate(messageUpdate);
                    } else if (update.getKind() == PersistentAgentStreamEvent.THREAD_RUN_COMPLETED) {
                        streamRun.set(((StreamThreadRunCreation) update).getMessage());
                    }
                });
            }
        } else if (streamUpdate instanceof StreamMessageUpdate) {
            StreamMessageUpdate messageUpdate = (StreamMessageUpdate) streamUpdate;
            printStreamUpdate(messageUpdate);
        }
    }
);
```

For a complete example, check the samples below:
- [Agent Functions Streaming Sample][agent_functions_streaming_sample]
- [Agent Functions Streaming Async Sample][agent_functions_streaming_async_sample]

### Other utility snippets
#### Working with thread messages

The following snippet shows how to print the messages in a thread:
```java com.azure.ai.agents.persistent.SampleUtils.printRunMessages
PagedIterable<ThreadMessage> runMessages = messagesClient.listMessages(threadId);
for (ThreadMessage message : runMessages) {
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
```

#### Working with thread messages asynchronously

The following snippet shows how to print the messages in a thread using the async clients:
```java com.azure.ai.agents.persistent.SampleUtils.printRunMessagesAsync
return messagesAsyncClient.listMessages(threadId)
    .doOnNext(message -> {
        System.out.print(String.format("%1$s - %2$s : ", message.getCreatedAt(), message.getRole()));
        message.getContent().forEach(contentItem -> {
            if (contentItem instanceof MessageTextContent) {
                System.out.print((((MessageTextContent) contentItem).getText().getValue()));
            } else if (contentItem instanceof MessageImageFileContent) {
                String imageFileId = (((MessageImageFileContent) contentItem).getImageFile().getFileId());
                System.out.print("Image from ID: " + imageFileId);
            }
            System.out.println();
        });
    })
    .then();
```

#### Handling streaming updates

The following snippet shows how to process and print streaming message updates:
```java com.azure.ai.agents.persistent.SampleUtils.printStreamUpdate
messageUpdate.getMessage().getDelta().getContent().stream().forEach(delta -> {
    if (delta instanceof MessageDeltaImageFileContent) {
        MessageDeltaImageFileContent imgContent = (MessageDeltaImageFileContent) delta;
        System.out.println("Image fileId: " + imgContent.getImageFile().getFileId());
    } else if (delta instanceof MessageDeltaTextContent) {
        MessageDeltaTextContent textContent = (MessageDeltaTextContent) delta;
        System.out.print(textContent.getText().getValue());
    }
});
```

#### Handling streaming updates asynchronously

The following snippet shows how to process and print streaming message updates using the async pattern:
```java com.azure.ai.agents.persistent.SampleUtils.printStreamUpdateAsync
return Mono.fromRunnable(() -> {
    messageUpdate.getMessage().getDelta().getContent().stream().forEach(delta -> {
        if (delta instanceof MessageDeltaImageFileContent) {
            MessageDeltaImageFileContent imgContent = (MessageDeltaImageFileContent) delta;
            System.out.println("Image fileId: " + imgContent.getImageFile().getFileId());
        } else if (delta instanceof MessageDeltaTextContent) {
            MessageDeltaTextContent textContent = (MessageDeltaTextContent) delta;
            System.out.print(textContent.getText().getValue());
        }
    });
});
```

#### Waiting for run completion

The following snippet shows how to wait for a thread run to complete:
```java com.azure.ai.agents.persistent.SampleUtils.waitForRunCompletion
do {
    Thread.sleep(500);
    threadRun = runsClient.getRun(threadId, threadRun.getId());
}
while (
    threadRun.getStatus() == RunStatus.QUEUED
        || threadRun.getStatus() == RunStatus.IN_PROGRESS
        || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

if (threadRun.getStatus() == RunStatus.FAILED) {
    System.out.println(threadRun.getLastError().getMessage());
}
```

#### Waiting for run completion asynchronously

The following snippet shows how to wait for a thread run to complete using the async pattern:
```java com.azure.ai.agents.persistent.SampleUtils.waitForRunCompletionAsync
return Mono.defer(() -> runsAsyncClient.getRun(threadId, threadRun.getId()))
    .flatMap(run -> {
        if (run.getStatus() == RunStatus.QUEUED
            || run.getStatus() == RunStatus.IN_PROGRESS
            || run.getStatus() == RunStatus.REQUIRES_ACTION) {
            return Mono.delay(java.time.Duration.ofMillis(500))
                .then(waitForRunCompletionAsync(threadId, run, runsAsyncClient));
        } else {
            if (run.getStatus() == RunStatus.FAILED && run.getLastError() != null) {
                System.out.println(run.getLastError().getMessage());
            }
            return Mono.just(run);
        }
    });
```

### Service API versions

The client library targets the latest service API version by default.
The service client builder accepts an optional service API version parameter to specify which API version to communicate.

#### Select a service API version

You have the flexibility to explicitly select a supported service API version when initializing a service client via the service client builder.
This ensures that the client can communicate with services using the specified API version.

When selecting an API version, it is important to verify that there are no breaking changes compared to the latest API version.
If there are significant differences, API calls may fail due to incompatibility.

Always ensure that the chosen API version is fully supported and operational for your specific use case and that it aligns with the service's versioning policy.

## Troubleshooting

## Next steps

## Contributing

For details on contributing to this repository, see the [contributing guide](https://github.com/Azure/azure-sdk-for-java/blob/main/CONTRIBUTING.md).

1. Fork it
1. Create your feature branch (`git checkout -b my-new-feature`)
1. Commit your changes (`git commit -am 'Add some feature'`)
1. Push to the branch (`git push origin my-new-feature`)
1. Create new Pull Request

<!-- LINKS -->
[product_documentation]: https://azure.microsoft.com/services/
[docs]: https://azure.github.io/azure-sdk-for-java/
[jdk]: https://learn.microsoft.com/azure/developer/java/fundamentals/
[azure_subscription]: https://azure.microsoft.com/free/
[azure_identity]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/identity/azure-identity
[persistent_agents_administration_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/PersistentAgentsAdministrationClient.java
[persistent_agents_administration_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/PersistentAgentsAdministrationAsyncClient.java
[threads_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/ThreadsClient.java
[threads_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/ThreadsAsyncClient.java
[messages_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/MessagesClient.java
[messages_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/MessagesAsyncClient.java
[runs_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/RunsClient.java
[runs_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/RunsAsyncClient.java
[run_steps_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/RunStepsClient.java
[files_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/FilesClient.java
[files_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/FilesAsyncClient.java
[vector_stores_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/VectorStoresClient.java
[vector_stores_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/VectorStoresAsyncClient.java
[vector_store_files_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/VectorStoreFilesClient.java
[vector_store_files_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/VectorStoreFilesAsyncClient.java
[vector_store_file_batches_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/VectorStoreFileBatchesClient.java
[vector_store_file_batches_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/main/java/com/azure/ai/agents/persistent/VectorStoreFileBatchesAsyncClient.java
[agent_basic_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentBasicSample.java
[agent_basic_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentBasicAsyncSample.java
[agent_additional_message_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentAdditionalMessageSample.java
[agent_additional_message_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentAdditionalMessageAsyncSample.java
[agent_azure_ai_search_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentAzureAISearchSample.java
[agent_azure_ai_search_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentAzureAISearchAsyncSample.java
[agent_azure_function_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentAzureFunctionSample.java
[agent_azure_function_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentAzureFunctionAsyncSample.java
[agent_bing_grounding_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentBingGroundingSample.java
[agent_bing_grounding_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentBingGroundingAsyncSample.java
[agent_code_interpreter_enterprise_file_search_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentCodeInterpreterEnterpriseFileSearchSample.java
[agent_code_interpreter_enterprise_file_search_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentCodeInterpreterEnterpriseFileSearchAsyncSample.java
[agent_code_interpreter_file_attachment_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentCodeInterpreterFileAttachmentSample.java
[agent_code_interpreter_file_attachment_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentCodeInterpreterFileAttachmentAsyncSample.java
[agent_connected_agent_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentConnectedAgentSample.java
[agent_connected_agent_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentConnectedAgentAsyncSample.java
[agent_enterprise_file_search_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentEnterpriseFileSearchSample.java
[agent_enterprise_file_search_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentEnterpriseFileSearchAsyncSample.java
[agent_file_search_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentFileSearchSample.java
[agent_file_search_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentFileSearchAsyncSample.java
[agent_functions_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentFunctionsSample.java
[agent_functions_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentFunctionsAsyncSample.java
[agent_functions_streaming_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentFunctionsStreamingSample.java
[agent_functions_streaming_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentFunctionsStreamingAsyncSample.java
[agent_image_input_url_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentImageInputUrlSample.java
[agent_image_input_url_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentImageInputUrlAsyncSample.java
[agent_image_input_base64_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentImageInputBase64Sample.java
[agent_image_input_base64_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentImageInputBase64AsyncSample.java
[agent_image_input_file_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentImageInputFileSample.java
[agent_image_input_file_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentImageInputFileAsyncSample.java
[agent_openapi_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentOpenApiSample.java
[agent_openapi_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentOpenApiAsyncSample.java
[agent_streaming_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentStreamingSample.java
[agent_streaming_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentStreamingAsyncSample.java
[agent_vector_store_batch_file_search_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentVectorStoreBatchFileSearchSample.java
[agent_vector_store_batch_file_search_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentVectorStoreBatchFileSearchAsyncSample.java
[agent_vector_store_batch_enterprise_file_search_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentVectorStoreBatchEnterpriseFileSearchSample.java
[agent_vector_store_batch_enterprise_file_search_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-agents-persistent/src/samples/java/com/azure/ai/agents/persistent/AgentVectorStoreBatchEnterpriseFileSearchAsyncSample.java


`
