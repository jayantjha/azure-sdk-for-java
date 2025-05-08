// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.implementation.models.FileDetails;
import com.azure.ai.agents.persistent.implementation.models.UploadFileRequest;
import com.azure.ai.agents.persistent.models.FileInfo;
import com.azure.ai.agents.persistent.models.FilePurpose;
import com.azure.ai.agents.persistent.models.VectorStore;
import com.azure.ai.agents.persistent.models.VectorStoreFile;
import com.azure.ai.agents.persistent.models.VectorStoreFileBatch;
import com.azure.core.http.HttpClient;
import com.azure.core.util.BinaryData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.azure.ai.agents.persistent.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VectorStoreFileBatchesAsyncClientTest extends ClientTestBase {

    private PersistentAgentsAdministrationClientBuilder clientBuilder;
    private VectorStoresAsyncClient vectorStoresAsyncClient;
    private VectorStoreFileBatchesAsyncClient vectorStoreFileBatchesAsyncClient;
    private FilesAsyncClient filesAsyncClient;
    private List<VectorStore> vectorStores = new ArrayList<>();
    private List<FileInfo> uploadedFiles = new ArrayList<>();

    private void setup(HttpClient httpClient) {
        clientBuilder = getClientBuilder(httpClient);
        vectorStoresAsyncClient = clientBuilder.buildVectorStoresAsyncClient();
        vectorStoreFileBatchesAsyncClient = clientBuilder.buildVectorStoreFileBatchesAsyncClient();
        filesAsyncClient = clientBuilder.buildFilesAsyncClient();
    }

    private FileInfo uploadFile(String fileName) {
        AtomicReference<FileInfo> fileRef = new AtomicReference<>();

        FileDetails fileDetails
            = new FileDetails(BinaryData.fromString("Sample text for testing upload")).setFilename(fileName);
        UploadFileRequest uploadFileRequest = new UploadFileRequest(fileDetails, FilePurpose.AGENTS);

        StepVerifier.create(filesAsyncClient.uploadFile(uploadFileRequest)).assertNext(uploadedFile -> {
            assertNotNull(uploadedFile, "Uploaded file should not be null");
            fileRef.set(uploadedFile);
            uploadedFiles.add(uploadedFile);
        }).verifyComplete();

        return fileRef.get();
    }

    // Helper method to create a vector store
    private VectorStore createVectorStore(String name) {
        AtomicReference<VectorStore> storeRef = new AtomicReference<>();

        StepVerifier.create(vectorStoresAsyncClient.createVectorStore(null, name, null, null, null, null))
            .assertNext(vectorStore -> {
                assertNotNull(vectorStore, "Vector store should not be null");
                storeRef.set(vectorStore);
                vectorStores.add(vectorStore);
            })
            .verifyComplete();

        return storeRef.get();
    }

    private VectorStoreFileBatch createVectorStoreFileBatch(String vectorStoreId, List<String> fileIds) {
        AtomicReference<VectorStoreFileBatch> batchRef = new AtomicReference<>();

        StepVerifier
            .create(vectorStoreFileBatchesAsyncClient.createVectorStoreFileBatch(vectorStoreId, fileIds, null, null))
            .assertNext(fileBatch -> {
                assertNotNull(fileBatch, "Vector store file batch should not be null");
                batchRef.set(fileBatch);
            })
            .verifyComplete();

        return batchRef.get();
    }

    // Test creation of a vector store file batch
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testCreateVectorStoreFileBatch(HttpClient httpClient) {
        setup(httpClient);

        String vectorStoreName = "test_create_vector_store_file_batch_async";
        VectorStore vectorStore = createVectorStore(vectorStoreName);

        FileInfo uploadedFile = uploadFile("testCreateVectorStoreFileBatchAsync.txt");
        List<String> fileIds = Arrays.asList(uploadedFile.getId());

        StepVerifier
            .create(
                vectorStoreFileBatchesAsyncClient.createVectorStoreFileBatch(vectorStore.getId(), fileIds, null, null))
            .assertNext(createdBatch -> {
                assertNotNull(createdBatch.getId(), "Vector store file batch ID should not be null");
            })
            .verifyComplete();
    }

    // Test retrieval of a vector store file batch
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testGetVectorStoreFileBatch(HttpClient httpClient) {
        setup(httpClient);
        String vectorStoreName = "test_get_vector_store_file_batch_async";
        VectorStore vectorStore = createVectorStore(vectorStoreName);

        FileInfo uploadedFile = uploadFile("testGetVectorStoreFileBatchAsync.txt");
        List<String> fileIds = Arrays.asList(uploadedFile.getId());
        VectorStoreFileBatch createdBatch = createVectorStoreFileBatch(vectorStore.getId(), fileIds);

        // Retrieve the file batch by its id
        StepVerifier
            .create(
                vectorStoreFileBatchesAsyncClient.getVectorStoreFileBatch(vectorStore.getId(), createdBatch.getId()))
            .assertNext(retrievedBatch -> {
                assertNotNull(retrievedBatch, "Retrieved file batch should not be null");
                assertEquals(createdBatch.getId(), retrievedBatch.getId(), "File batch IDs should match");
            })
            .verifyComplete();
    }

    @Disabled
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testListVectorStoreFileBatchFiles(HttpClient httpClient) {
        setup(httpClient);
        String vectorStoreName = "test_list_vector_store_file_batches_async";
        VectorStore vectorStore = createVectorStore(vectorStoreName);

        // Create file batch
        FileInfo uploadedFile = uploadFile("testListVectorStoreFileBatchesAsync.txt");
        List<String> fileIds = Arrays.asList(uploadedFile.getId());
        VectorStoreFileBatch createdBatch = createVectorStoreFileBatch(vectorStore.getId(), fileIds);

        // Test listing the files in the batch (disabled as it requires files to be processed)
        StepVerifier.create(
            vectorStoreFileBatchesAsyncClient.listVectorStoreFileBatchFiles(vectorStore.getId(), createdBatch.getId())
                .take(10)
                .collectList())
            .assertNext(files -> {
                assertNotNull(files, "Vector store batch files list should not be null");
                // Files might not be processed yet, so we don't assert count
            })
            .verifyComplete();
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testCancelVectorStoreFileBatch(HttpClient httpClient) {
        setup(httpClient);
        String vectorStoreName = "test_cancel_vector_store_file_batch_async";
        VectorStore vectorStore = createVectorStore(vectorStoreName);

        FileInfo uploadedFile = uploadFile("testCancelVectorStoreFileBatchAsync.txt");
        List<String> fileIds = Arrays.asList(uploadedFile.getId());
        VectorStoreFileBatch createdBatch = createVectorStoreFileBatch(vectorStore.getId(), fileIds);

        // Cancel the file batch
        StepVerifier
            .create(
                vectorStoreFileBatchesAsyncClient.cancelVectorStoreFileBatch(vectorStore.getId(), createdBatch.getId()))
            .assertNext(cancelledBatch -> {
                assertNotNull(cancelledBatch, "Cancelled batch should not be null");
                assertEquals(createdBatch.getId(), cancelledBatch.getId(), "Batch IDs should match");
            })
            .verifyComplete();
    }

    @AfterEach
    public void cleanup() {
        // Clean up uploaded files
        for (FileInfo fileInfo : uploadedFiles) {
            try {
                filesAsyncClient.deleteFile(fileInfo.getId()).block();
            } catch (Exception e) {
                System.out.println("Failed to clean up file: " + fileInfo.getFilename());
                System.out.println(e.getMessage());
            }
        }

        // Clean up vector stores
        for (VectorStore vectorStore : vectorStores) {
            try {
                vectorStoresAsyncClient.deleteVectorStore(vectorStore.getId()).block();
            } catch (Exception e) {
                System.out.println("Failed to clean up vector store: " + vectorStore.getName());
                System.out.println(e.getMessage());
            }
        }
    }
}
