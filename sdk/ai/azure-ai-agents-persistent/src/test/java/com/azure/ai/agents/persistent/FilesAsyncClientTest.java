// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.implementation.models.FileDetails;
import com.azure.ai.agents.persistent.implementation.models.UploadFileRequest;
import com.azure.ai.agents.persistent.models.FileDeletionStatus;
import com.azure.ai.agents.persistent.models.FileInfo;
import com.azure.ai.agents.persistent.models.FileListResponse;
import com.azure.ai.agents.persistent.models.FilePurpose;
import com.azure.core.http.HttpClient;
import com.azure.core.util.BinaryData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static com.azure.ai.agents.persistent.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilesAsyncClientTest extends ClientTestBase {

    private PersistentAgentsAdministrationClientBuilder clientBuilder;
    private FilesAsyncClient filesAsyncClient;
    private List<FileInfo> uploadedFiles;

    private void setup(HttpClient httpClient) {
        clientBuilder = getClientBuilder(httpClient);
        filesAsyncClient = clientBuilder.buildFilesAsyncClient();
        uploadedFiles = new ArrayList<>();
    }

    private void uploadFile(String fileName) {
        FileDetails fileDetails
            = new FileDetails(BinaryData.fromString("Sample text for testing upload")).setFilename(fileName);
        UploadFileRequest uploadFileRequest = new UploadFileRequest(fileDetails, FilePurpose.AGENTS);

        StepVerifier.create(filesAsyncClient.uploadFile(uploadFileRequest)).assertNext(uploadedFile -> {
            assertNotNull(uploadedFile, "Uploaded file should not be null");
            assertEquals(fileName, uploadedFile.getFilename(), "File name should match");
            uploadedFiles.add(uploadedFile);
        }).verifyComplete();
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testUploadFile(HttpClient httpClient) {
        setup(httpClient);

        // upload new file
        String fileName = "upload_file_test.txt";
        uploadFile(fileName);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testGetFile(HttpClient httpClient) {
        setup(httpClient);

        // upload new file
        String fileName = "get_file_test.txt";
        uploadFile(fileName);

        // Retrieve file information
        String fileId = uploadedFiles.get(0).getId();
        StepVerifier.create(filesAsyncClient.getFile(fileId)).assertNext(fileInfo -> {
            assertNotNull(fileInfo, "FileInfo should not be null");
            assertEquals(fileName, fileInfo.getFilename(), "Retrieved file name should match");
        }).verifyComplete();
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testDeleteFile(HttpClient httpClient) {
        setup(httpClient);

        // upload new file
        String fileName = "delete_file_test.txt";
        uploadFile(fileName);

        // Delete the created file
        String fileId = uploadedFiles.get(0).getId();
        StepVerifier.create(filesAsyncClient.deleteFile(fileId)).assertNext(deletionStatus -> {
            assertNotNull(deletionStatus, "Deletion status should not be null");
            assertTrue(deletionStatus.isDeleted(), "File should be marked as deleted");
        }).verifyComplete();

        // Remove the file from our tracking list since it's been deleted
        uploadedFiles.clear();
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testListFiles(HttpClient httpClient) {
        setup(httpClient);

        // upload new file
        String fileName = "list_files_test.txt";
        uploadFile(fileName);

        StepVerifier.create(filesAsyncClient.listFiles()).assertNext(listResponse -> {
            assertNotNull(listResponse, "File list response should not be null");
            List<FileInfo> fileInfos = listResponse.getData();
            assertNotNull(fileInfos, "File list should not be null");
            assertTrue(fileInfos.size() > 0, "File list should not be empty");
        }).verifyComplete();
    }

    @Disabled
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.agents.persistent.TestUtils#getTestParameters")
    public void testGetFileContent(HttpClient httpClient) {
        setup(httpClient);

        // upload new file with known content
        String fileName = "content_test.txt";
        String fileContent = "Sample text for testing upload";
        FileDetails fileDetails = new FileDetails(BinaryData.fromString(fileContent)).setFilename(fileName);
        UploadFileRequest uploadFileRequest = new UploadFileRequest(fileDetails, FilePurpose.AGENTS);

        StepVerifier.create(filesAsyncClient.uploadFile(uploadFileRequest)).assertNext(uploadedFile -> {
            assertNotNull(uploadedFile, "Uploaded file should not be null");
            uploadedFiles.add(uploadedFile);

            // Get file content
            String fileId = uploadedFile.getId();
            StepVerifier.create(filesAsyncClient.getFileContent(fileId)).assertNext(content -> {
                assertNotNull(content, "File content should not be null");
                assertEquals(fileContent, content.toString(), "File content should match what was uploaded");
            }).verifyComplete();
        }).verifyComplete();
    }

    @AfterEach
    public void cleanup() {
        for (FileInfo fileInfo : uploadedFiles) {
            try {
                filesAsyncClient.deleteFile(fileInfo.getId()).block();
            } catch (Exception e) {
                System.out.println("Failed to clean up file: " + fileInfo.getFilename());
                System.out.println(e.getMessage());
            }
        }
    }
}
