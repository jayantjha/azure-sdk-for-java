// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.analytics.purview.administration.generated;

// The Java test files under 'generated' package are generated for your reference.
// If you wish to modify these files, please copy them out of the 'generated' package, and modify there.
// See https://aka.ms/azsdk/dpg/java/tests for guide on adding a test.

import com.azure.analytics.purview.administration.MetadataPolicyClient;
import com.azure.analytics.purview.administration.MetadataPolicyClientBuilder;
import com.azure.analytics.purview.administration.MetadataRolesClient;
import com.azure.analytics.purview.administration.MetadataRolesClientBuilder;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.test.TestMode;
import com.azure.core.test.TestProxyTestBase;
import com.azure.core.test.utils.MockTokenCredential;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

class PurviewMetadataClientTestBase extends TestProxyTestBase {
    protected MetadataRolesClient metadataRolesClient;

    protected MetadataPolicyClient metadataPolicyClient;

    @Override
    protected void beforeTest() {
        MetadataRolesClientBuilder metadataRolesClientbuilder = new MetadataRolesClientBuilder()
            .endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .httpClient(getHttpClientOrUsePlayback(getHttpClients().findFirst().orElse(null)))
            .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC));
        if (getTestMode() == TestMode.PLAYBACK) {
            metadataRolesClientbuilder.credential(new MockTokenCredential());
        } else if (getTestMode() == TestMode.RECORD) {
            metadataRolesClientbuilder.addPolicy(interceptorManager.getRecordPolicy())
                .credential(new DefaultAzureCredentialBuilder().build());
        } else if (getTestMode() == TestMode.LIVE) {
            metadataRolesClientbuilder.credential(new DefaultAzureCredentialBuilder().build());
        }
        metadataRolesClient = metadataRolesClientbuilder.buildClient();

        MetadataPolicyClientBuilder metadataPolicyClientbuilder = new MetadataPolicyClientBuilder()
            .endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT", "endpoint"))
            .httpClient(getHttpClientOrUsePlayback(getHttpClients().findFirst().orElse(null)))
            .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC));
        if (getTestMode() == TestMode.PLAYBACK) {
            metadataPolicyClientbuilder.credential(new MockTokenCredential());
        } else if (getTestMode() == TestMode.RECORD) {
            metadataPolicyClientbuilder.addPolicy(interceptorManager.getRecordPolicy())
                .credential(new DefaultAzureCredentialBuilder().build());
        } else if (getTestMode() == TestMode.LIVE) {
            metadataPolicyClientbuilder.credential(new DefaultAzureCredentialBuilder().build());
        }
        metadataPolicyClient = metadataPolicyClientbuilder.buildClient();

    }
}
