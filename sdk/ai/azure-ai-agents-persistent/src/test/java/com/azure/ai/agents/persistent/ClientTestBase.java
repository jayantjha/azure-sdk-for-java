// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.PersistentAgent;
import com.azure.ai.agents.persistent.models.RunStatus;
import com.azure.ai.agents.persistent.models.ThreadRun;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.test.TestMode;
import com.azure.core.test.TestProxyTestBase;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import static com.azure.ai.agents.persistent.TestUtils.FAKE_API_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class ClientTestBase extends TestProxyTestBase {

    protected PersistentAgentsAdministrationClientBuilder getClientBuilder(HttpClient httpClient) {

        PersistentAgentsAdministrationClientBuilder builder = new PersistentAgentsAdministrationClientBuilder()
            .httpClient(interceptorManager.isPlaybackMode() ? interceptorManager.getPlaybackClient() : httpClient);
        TestMode testMode = getTestMode();

        if (testMode == TestMode.PLAYBACK) {
            builder.endpoint("https://localhost:8080").credential(new AzureKeyCredential(FAKE_API_KEY));
        } else if (testMode == TestMode.RECORD) {
            builder.addPolicy(interceptorManager.getRecordPolicy())
                .endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT"))
                .credential(new DefaultAzureCredentialBuilder().build());
        } else {
            builder.endpoint(Configuration.getGlobalConfiguration().get("ENDPOINT"))
                .credential(new DefaultAzureCredentialBuilder().build());
        }

        String serviceVersion = Configuration.getGlobalConfiguration().get("SERVICE_VERSION");
        if (serviceVersion != null) {
            builder.serviceVersion(AgentsServiceVersion.valueOf(serviceVersion));
        }
        return builder;
    }

    protected void assertAgent(PersistentAgent agent) {
        assertNotNull(agent, "Agent should not be null");
        assertNotNull(agent.getId(), "Agent ID should not be null");
        assertNotNull(agent.getName(), "Agent name should not be null");
    }

    protected void waitForRunCompletion(ThreadRun threadRun, RunsClient runsClient) {
        int retryLeft = 20;
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                fail("Thread sleep interrupted " + e.getMessage());
            }
            threadRun = runsClient.getRun(threadRun.getThreadId(), threadRun.getId());
        } while ((--retryLeft > 0)
            && ((threadRun.getStatus() == RunStatus.QUEUED)
                || (threadRun.getStatus() == RunStatus.IN_PROGRESS)
                || (threadRun.getStatus() == RunStatus.REQUIRES_ACTION)));

        if (threadRun.getStatus() == RunStatus.FAILED || retryLeft == 0) {
            fail("Run failed or couldn't complete in time");
        }
    }
}
