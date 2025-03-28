// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datafactory.models.HttpReadSettings;

public final class HttpReadSettingsTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        HttpReadSettings model = BinaryData.fromString(
            "{\"type\":\"HttpReadSettings\",\"requestMethod\":\"datavgwvfvsqlyah\",\"requestBody\":\"dataoqk\",\"additionalHeaders\":\"datatnbuzvaxlt\",\"requestTimeout\":\"datanwhictsauv\",\"additionalColumns\":\"dataqzpfpbxljddkk\",\"maxConcurrentConnections\":\"datazsy\",\"disableMetricsCollection\":\"datakcld\",\"\":{\"xewnlpchhczqm\":\"dataeka\"}}")
            .toObject(HttpReadSettings.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        HttpReadSettings model = new HttpReadSettings().withMaxConcurrentConnections("datazsy")
            .withDisableMetricsCollection("datakcld")
            .withRequestMethod("datavgwvfvsqlyah")
            .withRequestBody("dataoqk")
            .withAdditionalHeaders("datatnbuzvaxlt")
            .withRequestTimeout("datanwhictsauv")
            .withAdditionalColumns("dataqzpfpbxljddkk");
        model = BinaryData.fromObject(model).toObject(HttpReadSettings.class);
    }
}
