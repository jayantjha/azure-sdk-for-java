// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import com.azure.ai.projects.implementation.models.GetAppInsightsResponse;
import com.azure.ai.projects.implementation.models.GetWorkspaceResponse;
import com.azure.core.util.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelemetryAsyncClientTest extends AIProjectClientTestBase {

    @Test
    void testGetAppInsights() {
        TelemetryAsyncClient telemetryAsyncClient = getAIProjectClientBuilder().buildTelemetryAsyncClient();
        String appInsightsUrl = Configuration.getGlobalConfiguration().get("APP_INSIGHTS_URL", "");
        StepVerifier.create(telemetryAsyncClient.getAppInsights(appInsightsUrl))
            .assertNext(appInsightsResponse -> {
                assertNotNull(appInsightsResponse);
                assertNotNull(appInsightsResponse.getId());
            })
            .verifyComplete();
    }

    @Test
    void getAppInsightsConnectionString() {
        TelemetryAsyncClient telemetryAsyncClient = getAIProjectClientBuilder().buildTelemetryAsyncClient();
        ConnectionsAsyncClient connectionsAsyncClient = getAIProjectClientBuilder().buildConnectionsAsyncClient();

        StepVerifier
            .create(connectionsAsyncClient.getWorkspace()
                .flatMap(workspace -> {
                    String appInsightsUrl = workspace.getProperties().getApplicationInsights();
                    return telemetryAsyncClient.getAppInsights(appInsightsUrl);
                }))
            .assertNext(appInsightsResponse -> {
                assertNotNull(appInsightsResponse);
                assertNotNull(appInsightsResponse.getId());
                assertNotNull(appInsightsResponse.getProperties().getConnectionString());
            })
            .verifyComplete();
    }
}
