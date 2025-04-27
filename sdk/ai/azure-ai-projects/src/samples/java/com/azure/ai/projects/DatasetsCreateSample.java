package com.azure.ai.projects;

import com.azure.ai.projects.models.DatasetVersion;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatasetsCreateSample {

    public static void main(String[] args) throws IOException, URISyntaxException {
        DatasetsClient datasetsClient
            = new AIProjectClientBuilder().endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildDatasetsClient();

        // BEGIN:com.azure.ai.projects.DatasetsCreateSample

        String datasetName = Configuration.getGlobalConfiguration().get("DATASET_NAME", "my-dataset");
        String datasetVersionString = Configuration.getGlobalConfiguration().get("DATASET_VERSION", "1.0");

        Path filePath = getPath("product_info.md");
        byte[] content = Files.readAllBytes(filePath);

        DatasetVersion createdDatasetVersion = datasetsClient.createDatasetWithFile(datasetName, datasetVersionString, filePath);

        System.out.println("Created dataset version: " + createdDatasetVersion.getId());

        // END:com.azure.ai.projects.DatasetsCreateSample
    }

    public static Path getPath(String fileName) throws FileNotFoundException, URISyntaxException {
        URL resource = DatasetsCreateSample.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new FileNotFoundException("File not found");
        }

        File file = new File(resource.toURI());
        return file.toPath();
    }
}
