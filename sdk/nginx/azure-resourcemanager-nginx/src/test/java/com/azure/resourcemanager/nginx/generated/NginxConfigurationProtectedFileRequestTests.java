// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.nginx.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.nginx.models.NginxConfigurationProtectedFileRequest;
import org.junit.jupiter.api.Assertions;

public final class NginxConfigurationProtectedFileRequestTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        NginxConfigurationProtectedFileRequest model = BinaryData
            .fromString("{\"content\":\"ufizuckyf\",\"virtualPath\":\"rfidfvzwdz\",\"contentHash\":\"tymw\"}")
            .toObject(NginxConfigurationProtectedFileRequest.class);
        Assertions.assertEquals("ufizuckyf", model.content());
        Assertions.assertEquals("rfidfvzwdz", model.virtualPath());
        Assertions.assertEquals("tymw", model.contentHash());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        NginxConfigurationProtectedFileRequest model
            = new NginxConfigurationProtectedFileRequest().withContent("ufizuckyf")
                .withVirtualPath("rfidfvzwdz")
                .withContentHash("tymw");
        model = BinaryData.fromObject(model).toObject(NginxConfigurationProtectedFileRequest.class);
        Assertions.assertEquals("ufizuckyf", model.content());
        Assertions.assertEquals("rfidfvzwdz", model.virtualPath());
        Assertions.assertEquals("tymw", model.contentHash());
    }
}
