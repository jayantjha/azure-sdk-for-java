# Azure Projects client library for Java

Azure Projects client library for Java.

This package contains Microsoft Azure Projects client library.

## Documentation

Various documentation is available to help you get started

- [API reference documentation][docs]
- [Product documentation][product_documentation]

## Getting started

### Prerequisites

- [Java Development Kit (JDK)][jdk] with version 8 or above
- [Azure Subscription][azure_subscription]

### Adding the package to your product

[//]: # ({x-version-update-start;com.azure:azure-ai-projects;current})
```xml
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-ai-projects</artifactId>
    <version>1.0.0-beta.1</version>
</dependency>
```
[//]: # ({x-version-update-end})

### Authentication

[Azure Identity][azure_identity] package provides the default implementation for authenticating the client.

## Key concepts

The Azure Projects client library provides several client classes that allow you to interact with the Azure Projects service:

## Examples
The following sections provide several code snippets that show how to use the Azure AI Projects client library for Java.

### Building service clients
In order to interact with Azure AI Projects service, you need to create instances of available service client classes listed below:
- [ConnectionsClient][connections_client] / [ConnectionsAsyncClient][connections_async_client] - Manage connections to Azure resources
- [DatasetsClient][datasets_client] / [DatasetsAsyncClient][datasets_async_client] - Work with datasets
- [DeploymentsClient][deployments_client] / [DeploymentsAsyncClient][deployments_async_client] - Manage model deployments
- [EvaluationsClient][evaluations_client] / [EvaluationsAsyncClient][evaluations_async_client] - Perform evaluations
- [IndexesClient][indexes_client] / [IndexesAsyncClient][indexes_async_client] - Work with indexes
- [RedTeamsClient][redteams_client] / [RedTeamsAsyncClient][redteams_async_client] - Manage red team testing
- [TelemetryClient][telemetry_client] / [TelemetryAsyncClient][telemetry_async_client] - Access telemetry data

The following snippet shows how to create service client instances:
```java com.azure.ai.projects.SampleUtils.buildClients
AIProjectClientBuilder clientBuilder = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
    .credential(new DefaultAzureCredentialBuilder().build());

// Connections clients
ConnectionsClient connectionsClient = clientBuilder.buildConnectionsClient();
ConnectionsAsyncClient connectionsAsyncClient = clientBuilder.buildConnectionsAsyncClient();

// Datasets clients
DatasetsClient datasetsClient = clientBuilder.buildDatasetsClient();
DatasetsAsyncClient datasetsAsyncClient = clientBuilder.buildDatasetsAsyncClient();

// Deployments clients
DeploymentsClient deploymentsClient = clientBuilder.buildDeploymentsClient();
DeploymentsAsyncClient deploymentsAsyncClient = clientBuilder.buildDeploymentsAsyncClient();

// Evaluations clients
EvaluationsClient evaluationsClient = clientBuilder.buildEvaluationsClient();
EvaluationsAsyncClient evaluationsAsyncClient = clientBuilder.buildEvaluationsAsyncClient();

// Red Teams clients
RedTeamsClient redTeamsClient = clientBuilder.buildRedTeamsClient();
RedTeamsAsyncClient redTeamsAsyncClient = clientBuilder.buildRedTeamsAsyncClient();

// Indexes clients
IndexesClient indexesClient = clientBuilder.buildIndexesClient();
IndexesAsyncClient indexesAsyncClient = clientBuilder.buildIndexesAsyncClient();

// Telemetry clients
TelemetryClient telemetryClient = clientBuilder.buildTelemetryClient();
TelemetryAsyncClient telemetryAsyncClient = clientBuilder.buildTelemetryAsyncClient();

// Agents clients
PersistentAgentsAdministrationClientBuilder agentsClientBuilder
    = new PersistentAgentsAdministrationClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
    .credential(new DefaultAzureCredentialBuilder().build());

PersistentAgentsAdministrationClient agentsClient = agentsClientBuilder.buildClient();
PersistentAgentsAdministrationAsyncClient agentsAsyncClient = agentsClientBuilder.buildAsyncClient();

// Chat completion clients
ChatCompletionsClientBuilder chatCompletionsClientBuilder
    = new ChatCompletionsClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
    .credential(new DefaultAzureCredentialBuilder().build());

ChatCompletionsClient chatCompletionsClient = chatCompletionsClientBuilder.buildClient();
ChatCompletionsAsyncClient chatCompletionsAsyncClient = chatCompletionsClientBuilder.buildAsyncClient();

// Image embeddings clients
ImageEmbeddingsClientBuilder imageEmbeddingsClientBuilder
    = new ImageEmbeddingsClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
    .credential(new DefaultAzureCredentialBuilder().build());

ImageEmbeddingsClient imageEmbeddingsClient = imageEmbeddingsClientBuilder.buildClient();
ImageEmbeddingsAsyncClient imageEmbeddingsAsyncClient = imageEmbeddingsClientBuilder.buildAsyncClient();

// Embeddings clients
EmbeddingsClientBuilder embeddingsClientBuilder
    = new EmbeddingsClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
    .credential(new DefaultAzureCredentialBuilder().build());

EmbeddingsClient embeddingsClient = embeddingsClientBuilder.buildClient();
EmbeddingsAsyncClient embeddingsAsyncClient = embeddingsClientBuilder.buildAsyncClient();
```

### Working with Connections

#### List connections
```java com.azure.ai.projects.ConnectionsSample.listConnections

PagedIterable<Connection> connections = connectionsClient.listConnections();
for (Connection connection : connections) {
    System.out.printf("Connection name: %s%n", connection.getName());
}

```

#### Get a connection without credentials
```java com.azure.ai.projects.ConnectionsSample.getConnectionWithoutCredentials

String connectionName = Configuration.getGlobalConfiguration().get("TEST_CONNECTION_NAME", "");
Connection connection = connectionsClient.getConnection(connectionName, false);

System.out.printf("Connection name: %s%n", connection.getName());

```

#### Get a connection with credentials
```java com.azure.ai.projects.ConnectionsSample.getConnectionWithCredentials

String connectionName = Configuration.getGlobalConfiguration().get("TEST_CONNECTION_NAME", "");
Connection connection = connectionsClient.getConnection(connectionName, true);

System.out.printf("Connection name: %s%n", connection.getName());
System.out.printf("Connection credentials: %s%n", connection.getCredentials().getType());

```

#### Async pattern for listing connections
```java com.azure.ai.projects.ConnectionsAsyncSample.listConnections

return connectionsAsyncClient.listConnections()
    .doOnNext(connection -> System.out.printf("Connection name: %s%n", connection.getName()));

```

#### Async pattern for getting a connection without credentials
```java com.azure.ai.projects.ConnectionsAsyncSample.getConnectionWithoutCredentials

String connectionName = Configuration.getGlobalConfiguration().get("TEST_CONNECTION_NAME", "");
return connectionsAsyncClient.getConnection(connectionName)
    .doOnNext(connection -> System.out.printf("Connection name: %s%n", connection.getName()));

```

#### Async pattern for getting a connection with credentials
```java com.azure.ai.projects.ConnectionsAsyncSample.getConnectionWithCredentials

String connectionName = Configuration.getGlobalConfiguration().get("TEST_CONNECTION_NAME", "");
return connectionsAsyncClient.getConnectionWithCredentials(connectionName)
    .doOnNext(connection -> {
        System.out.printf("Connection name: %s%n", connection.getName());
        System.out.printf("Connection credentials: %s%n", connection.getCredentials().getType());
    });

```

For a complete example, check the samples below:
- [Connections Sample][connections_sample]
- [Connections Async Sample][connections_async_sample]


### Working with Datasets

#### Creating a dataset with a file
```java com.azure.ai.projects.DatasetsSample.createDatasetWithFile

String datasetName = Configuration.getGlobalConfiguration().get("DATASET_NAME", "my-dataset");
String datasetVersionString = Configuration.getGlobalConfiguration().get("DATASET_VERSION", "1.0");

Path filePath = getPath("product_info.md");

FileDatasetVersion createdDatasetVersion = datasetsClient.createDatasetWithFile(datasetName, datasetVersionString, filePath);

System.out.println("Created dataset version: " + createdDatasetVersion.getId());

```

#### Listing datasets
```java com.azure.ai.projects.DatasetsSample.listDatasets

System.out.println("Listing all datasets (latest versions):");
datasetsClient.listLatestDatasetVersions().forEach(dataset -> {
    System.out.println("\nDataset name: " + dataset.getName());
    System.out.println("Dataset Id: " + dataset.getId());
    System.out.println("Dataset version: " + dataset.getVersion());
    System.out.println("Dataset type: " + dataset.getType());
    if (dataset.getDescription() != null) {
        System.out.println("Description: " + dataset.getDescription());
    }
});

```

#### Getting a dataset
```java com.azure.ai.projects.DatasetsSample.getDataset

String datasetName = Configuration.getGlobalConfiguration().get("DATASET_NAME", "test");
String datasetVersion = Configuration.getGlobalConfiguration().get("DATASET_VERSION", "1");

DatasetVersion dataset = datasetsClient.getDatasetVersion(datasetName, datasetVersion);

System.out.println("Retrieved dataset:");
System.out.println("Name: " + dataset.getName());
System.out.println("Version: " + dataset.getVersion());
System.out.println("Type: " + dataset.getType());
if (dataset.getDataUri() != null) {
    System.out.println("Data URI: " + dataset.getDataUri());
}
if (dataset.getDescription() != null) {
    System.out.println("Description: " + dataset.getDescription());
}

```

#### Creating or updating a dataset
```java com.azure.ai.projects.DatasetsSample.createOrUpdateDataset

String datasetName = Configuration.getGlobalConfiguration().get("DATASET_NAME", "my-dataset");
String datasetVersion = Configuration.getGlobalConfiguration().get("DATASET_VERSION", "1.0");
String dataUri = Configuration.getGlobalConfiguration().get("DATA_URI", "https://example.com/data.txt");

// Create a new FileDatasetVersion with provided dataUri
FileDatasetVersion fileDataset = new FileDatasetVersion()
    .setDataUri(dataUri)
    .setDescription("Sample dataset created via SDK");

// Create or update the dataset
FileDatasetVersion createdDataset = (FileDatasetVersion) datasetsClient.createOrUpdateDatasetVersion(
    datasetName, 
    datasetVersion, 
    fileDataset
);

System.out.println("Created/Updated dataset:");
System.out.println("Name: " + createdDataset.getName());
System.out.println("Version: " + createdDataset.getVersion());
System.out.println("Data URI: " + createdDataset.getDataUri());

```

#### Async pattern for creating a dataset with a file
```java com.azure.ai.projects.DatasetsAsyncSample.createDatasetWithFile

String datasetName = Configuration.getGlobalConfiguration().get("DATASET_NAME", "my-dataset");
String datasetVersionString = Configuration.getGlobalConfiguration().get("DATASET_VERSION", "1.0");

Path filePath = getPath("product_info.md");

return datasetsAsyncClient.createDatasetWithFile(datasetName, datasetVersionString, filePath)
    .doOnNext(createdDatasetVersion -> 
        System.out.println("Created dataset version: " + createdDatasetVersion.getId()));

```

#### Async pattern for listing datasets
```java com.azure.ai.projects.DatasetsAsyncSample.listDatasets

System.out.println("Listing all datasets (latest versions):");
return datasetsAsyncClient.listLatestDatasetVersions()
    .doOnNext(dataset -> {
        System.out.println("\nDataset name: " + dataset.getName());
        System.out.println("Dataset Id: " + dataset.getId());
        System.out.println("Dataset version: " + dataset.getVersion());
        System.out.println("Dataset type: " + dataset.getType());
        if (dataset.getDescription() != null) {
            System.out.println("Description: " + dataset.getDescription());
        }
    });

```

For a complete example, check the samples below:
- [Datasets Sample][datasets_sample]
- [Datasets Async Sample][datasets_async_sample]

### Working with Deployments

#### Listing deployments
```java com.azure.ai.projects.DeploymentsSample.listDeployments

PagedIterable<Deployment> deployments = deploymentsClient.listDeployments();
for (Deployment deployment : deployments) {
    System.out.printf("Deployment name: %s%n", deployment.getName());
}

```

#### Getting a deployment
```java com.azure.ai.projects.DeploymentsSample.getDeployment

String deploymentName = Configuration.getGlobalConfiguration().get("DEPLOYMENT_NAME", "");
Deployment deployment = deploymentsClient.getDeployment(deploymentName);

System.out.printf("Deployment name: %s%n", deployment.getName());
System.out.printf("Deployment type: %s%n", deployment.getType().getValue());

```

#### Async pattern for listing deployments
```java com.azure.ai.projects.DeploymentsAsyncSample.listDeployments

return deploymentsAsyncClient.listDeployments()
    .doOnNext(deployment -> System.out.printf("Deployment name: %s%n", deployment.getName()));

```

For a complete example, check the samples below:
- [Deployments Sample][deployments_sample]
- [Deployments Async Sample][deployments_async_sample]

### Working with Evaluations

#### Creating an evaluation
```java com.azure.ai.projects.EvaluationsSample.createEvaluation

// Create an evaluation definition
String datasetName = Configuration.getGlobalConfiguration().get("DATASET_NAME", "test");
String version = Configuration.getGlobalConfiguration().get("DATASET_VERSION", "1");
DatasetVersion datasetVersion = datasetsClient.getDatasetVersion(datasetName, version);

InputDataset dataset = new InputDataset(datasetVersion.getId());
Evaluation evaluation = new Evaluation(
    dataset,
    mapOf("relevance",
        new EvaluatorConfiguration(EvaluatorId.RELEVANCE.getValue())
            .setInitParams(mapOf("deployment_name", BinaryData.fromObject("gpt-4o")))))
    .setDisplayName("Sample Evaluation")
    .setDescription("This is a sample evaluation created using the SDK");

// Create the evaluation
Evaluation createdEvaluation = evaluationsClient.createEvaluation(evaluation);

System.out.println("Created evaluation:");
System.out.println("Display Name: " + createdEvaluation.getDisplayName());
System.out.println("Status: " + createdEvaluation.getStatus());

```

#### Listing evaluations
```java com.azure.ai.projects.EvaluationsSample.listEvaluations

System.out.println("Listing all evaluations:");
evaluationsClient.listEvaluations().forEach(evaluation -> {
    System.out.println("Display Name: " + evaluation.getDisplayName());
    System.out.println("Status: " + evaluation.getStatus());
    System.out.println("Data Type: " + evaluation.getData().getType());
    
    if (evaluation.getDescription() != null) {
        System.out.println("Description: " + evaluation.getDescription());
    }
    
    System.out.println("Evaluators:");
    evaluation.getEvaluators().forEach((name, evaluator) -> {
        System.out.println("  - " + name + ": " + evaluator.getId());
    });
});

```

#### Getting an evaluation
```java com.azure.ai.projects.EvaluationsSample.getEvaluation

String evaluationId = Configuration.getGlobalConfiguration().get("EVALUATION_ID", "my-evaluation-id");

Evaluation evaluation = evaluationsClient.getEvaluation(evaluationId);

System.out.println("Retrieved evaluation:");
System.out.println("Display Name: " + evaluation.getDisplayName());
System.out.println("Status: " + evaluation.getStatus());
System.out.println("Data Type: " + evaluation.getData().getType());

if (evaluation.getDescription() != null) {
    System.out.println("Description: " + evaluation.getDescription());
}

if (evaluation.getTags() != null) {
    System.out.println("Tags:");
    evaluation.getTags().forEach((key, value) -> {
        System.out.println("  " + key + ": " + value);
    });
}

System.out.println("Evaluators:");
evaluation.getEvaluators().forEach((name, evaluator) -> {
    System.out.println("  - " + name + ": " + evaluator.getId());
    
    if (evaluator.getDataMapping() != null) {
        System.out.println("    Data Mapping:");
        evaluator.getDataMapping().forEach((k, v) -> {
            System.out.println("      " + k + " -> " + v);
        });
    }
});

```

#### Async pattern for creating an evaluation
```java com.azure.ai.projects.EvaluationsAsyncSample.createEvaluation

// Create an evaluation definition
String datasetName = Configuration.getGlobalConfiguration().get("DATASET_NAME", "test");
String version = Configuration.getGlobalConfiguration().get("DATASET_VERSION", "1");

return datasetsAsyncClient.getDatasetVersion(datasetName, version)
    .flatMap(datasetVersion -> {
        InputDataset dataset = new InputDataset(datasetVersion.getId());
        Evaluation evaluation = new Evaluation(
            dataset,
            mapOf("relevance",
                new EvaluatorConfiguration(EvaluatorId.RELEVANCE.getValue())
                    .setInitParams(mapOf("deployment_name", BinaryData.fromObject("gpt-4o")))))
            .setDisplayName("Sample Evaluation")
            .setDescription("This is a sample evaluation created using the SDK");

        // Create the evaluation
        return evaluationsAsyncClient.createEvaluation(evaluation);
    })
    .doOnNext(createdEvaluation -> {
        System.out.println("Created evaluation:");
        System.out.println("Display Name: " + createdEvaluation.getDisplayName());
        System.out.println("Status: " + createdEvaluation.getStatus());
    });

```

For a complete example, check the samples below:
- [Evaluations Sample][evaluations_sample]
- [Evaluations Async Sample][evaluations_async_sample]

### Working with Indexes

#### Creating or updating an index
```java com.azure.ai.projects.IndexesSample.createOrUpdateIndex

String indexName = Configuration.getGlobalConfiguration().get("INDEX_NAME", "my-index");
String indexVersion = Configuration.getGlobalConfiguration().get("INDEX_VERSION", "2.0");
String aiSearchConnectionName = Configuration.getGlobalConfiguration().get("AI_SEARCH_CONNECTION_NAME", "");
String aiSearchIndexName = Configuration.getGlobalConfiguration().get("AI_SEARCH_INDEX_NAME", "");

Index index = indexesClient.createOrUpdateIndexVersion(
    indexName,
    indexVersion,
    new AzureAISearchIndex()
        .setConnectionName(aiSearchConnectionName)
        .setIndexName(aiSearchIndexName)
);

System.out.println("Index created: " + index.getName());

```

#### Listing indexes
```java com.azure.ai.projects.IndexesSample.listIndexes

indexesClient.listLatestIndexVersions().forEach(index -> {
    System.out.println("Index name: " + index.getName());
    System.out.println("Index version: " + index.getVersion());
});

```

#### Getting an index
```java com.azure.ai.projects.IndexesSample.getIndex

String indexName = Configuration.getGlobalConfiguration().get("INDEX_NAME", "my-index");
String indexVersion = Configuration.getGlobalConfiguration().get("INDEX_VERSION", "1.0");

Index index = indexesClient.getIndexVersion(indexName, indexVersion);

System.out.println("Retrieved index:");
System.out.println("Name: " + index.getName());
System.out.println("Version: " + index.getVersion());
System.out.println("Type: " + index.getType());

```

#### Async pattern for listing indexes
```java com.azure.ai.projects.IndexesAsyncSample.listIndexes

return indexesAsyncClient.listLatestIndexVersions()
    .doOnNext(index -> {
        System.out.println("Index name: " + index.getName());
        System.out.println("Index version: " + index.getVersion());
    });

```

For a complete example, check the samples below:
- [Indexes Sample][indexes_sample]
- [Indexes Async Sample][indexes_async_sample]

### Working with Telemetry

#### Getting connection string
```java com.azure.ai.projects.TelemetrySample.getConnectionString

String connectionString = telemetryClient.getConnectionString();
System.out.println("Connection string: " + connectionString);

```

#### Async pattern for getting connection string
```java com.azure.ai.projects.TelemetryAsyncSample.getConnectionString

return telemetryAsyncClient.getConnectionString()
    .doOnNext(connectionString -> 
        System.out.println("Connection string (async): " + connectionString))
    .doOnError(error -> 
        System.err.println("Error retrieving connection string: " + error.getMessage()))
    .then();

```

For a complete example, check the samples below:
- [Telemetry Sample][telemetry_sample]
- [Telemetry Async Sample][telemetry_async_sample]

### Working with Agents

#### Creating an agent
```java com.azure.ai.projects.AgentsSample.createAgent

String agentName = "basic_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent");
PersistentAgent agent = agentsClient.createAgent(createAgentOptions);
System.out.println("Agent created: " + agent.getId());
return agent;

```

#### Deleting an agent
```java com.azure.ai.projects.AgentsSample.deleteAgent

AgentDeletionStatus deletionStatus = agentsClient.deleteAgent(agentId);
System.out.println("Agent: " + agentId);
System.out.println("Delete confirmation: " + deletionStatus.isDeleted());

```

#### Async pattern for creating an agent
```java com.azure.ai.projects.AgentsAsyncSample.createAgent

String agentName = "basic_example";
CreateAgentOptions createAgentOptions = new CreateAgentOptions("gpt-4o-mini")
    .setName(agentName)
    .setInstructions("You are a helpful agent");
    
return agentsAsyncClient.createAgent(createAgentOptions)
    .doOnNext(agent -> System.out.println("Agent created: " + agent.getId()));

```

#### Async pattern for deleting an agent
```java com.azure.ai.projects.AgentsAsyncSample.deleteAgent

return agentsAsyncClient.deleteAgent(agentId)
    .doOnNext(deletionStatus -> {
        System.out.println("Agent: " + agentId);
        System.out.println("Delete confirmation: " + deletionStatus.isDeleted());
    });

```

For a complete example, check the samples below:
- [Agents Sample][agents_sample]
- [Agents Async Sample][agents_async_sample]

### Working with Inference

#### Using embeddings client
```java com.azure.ai.projects.InferenceSample.embeddingsClientSample

List<String> promptList = new ArrayList<>();
String prompt = "Tell me 3 jokes about trains";
promptList.add(prompt);

EmbeddingsResult embeddings = embeddingsClient.embed(promptList);

for (EmbeddingItem item : embeddings.getData()) {
    System.out.printf("Index: %d.%n", item.getIndex());
    for (Float embedding : item.getEmbeddingList()) {
        System.out.printf("%f;", embedding);
    }
}

```

#### Using chat completions client
```java com.azure.ai.projects.InferenceSample.chatCompletionsClientSample

ChatCompletionsOptions options = new ChatCompletionsOptions(Arrays.asList(
    new ChatRequestUserMessage("How many feet are in a mile?")
));

ChatCompletions chatCompletions = chatCompletionsClient.complete(options);
System.out.println(chatCompletions.getChoice().getMessage().getContent());

```

#### Using image embeddings client
```java com.azure.ai.projects.InferenceSample.imageEmbeddingsClientSample

String imageUrl = "sample.png";
Path imagePath = SampleUtils.getPath(imageUrl);

EmbeddingsResult embeddings = imageEmbeddingsClient
    .embed(Arrays.asList(new ImageEmbeddingInput(imagePath, "png")));

for (EmbeddingItem item : embeddings.getData()) {
    System.out.printf("Index: %d.%n", item.getIndex());
    for (Float embedding : item.getEmbeddingList()) {
        System.out.printf("%f;", embedding);
    }
}

```

#### Async pattern for embeddings client
```java com.azure.ai.projects.InferenceAsyncSample.embeddingsClientSample

List<String> promptList = new ArrayList<>();
String prompt = "Tell me 3 jokes about trains";
promptList.add(prompt);

return embeddingsAsyncClient.embed(promptList)
    .flatMap(embeddings -> {
        for (EmbeddingItem item : embeddings.getData()) {
            System.out.printf("Index: %d.%n", item.getIndex());
            for (Float embedding : item.getEmbeddingList()) {
                System.out.printf("%f;", embedding);
            }
            System.out.println();
        }
        return Mono.empty();
    });

```

#### Async pattern for chat completions client
```java com.azure.ai.projects.InferenceAsyncSample.chatCompletionsClientSample

ChatCompletionsOptions options = new ChatCompletionsOptions(Arrays.asList(
    new ChatRequestUserMessage("How many feet are in a mile?")
));

return chatCompletionsAsyncClient.complete(options)
    .flatMap(chatCompletions -> {
        System.out.println(chatCompletions.getChoice().getMessage().getContent());
        return Mono.empty();
    });

```

#### Async pattern for image embeddings client
```java com.azure.ai.projects.InferenceAsyncSample.imageEmbeddingsClientSample

return Mono.fromCallable(() -> SampleUtils.getPath(imageUrl))
    .flatMap(imagePath -> 
        imageEmbeddingsAsyncClient.embed(Arrays.asList(new ImageEmbeddingInput(imagePath, "png")))
    )
    .flatMap(embeddings -> {
        for (EmbeddingItem item : embeddings.getData()) {
            System.out.printf("Index: %d.%n", item.getIndex());
            for (Float embedding : item.getEmbeddingList()) {
                System.out.printf("%f;", embedding);
            }
            System.out.println();
        }
        return Mono.empty();
    });

```

For a complete example, check the samples below:
- [Inference Sample][inference_sample]
- [Inference Async Sample][inference_async_sample]

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
[connections_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/ConnectionsClient.java
[connections_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/ConnectionsAsyncClient.java
[datasets_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/DatasetsClient.java
[datasets_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/DatasetsAsyncClient.java
[deployments_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/DeploymentsClient.java
[deployments_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/DeploymentsAsyncClient.java
[evaluations_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/EvaluationsClient.java
[evaluations_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/EvaluationsAsyncClient.java
[indexes_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/IndexesClient.java
[indexes_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/IndexesAsyncClient.java
[redteams_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/RedTeamsClient.java
[redteams_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/RedTeamsAsyncClient.java
[telemetry_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/TelemetryClient.java
[telemetry_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/TelemetryAsyncClient.java
[inference_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/InferenceClient.java
[inference_async_client]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/main/java/com/azure/ai/projects/InferenceAsyncClient.java

[connections_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/ConnectionsSample.java
[connections_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/ConnectionsAsyncSample.java
[agents_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/AgentsSample.java
[agents_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/AgentsAsyncSample.java
[datasets_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/DatasetsSample.java
[datasets_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/DatasetsAsyncSample.java
[deployments_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/DeploymentsSample.java
[deployments_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/DeploymentsAsyncSample.java
[evaluations_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/EvaluationsSample.java
[evaluations_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/EvaluationsAsyncSample.java
[indexes_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/IndexesSample.java
[indexes_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/IndexesAsyncSample.java
[telemetry_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/TelemetrySample.java
[telemetry_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/TelemetryAsyncSample.java
[inference_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/InferenceSample.java 
[inference_async_sample]: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/ai/azure-ai-projects/src/samples/java/com/azure/ai/projects/InferenceAsyncSample.java

`
