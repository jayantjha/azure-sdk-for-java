package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.PersistentAgent;
import com.azure.core.test.TestProxyTestBase;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpRequest;
import com.azure.core.test.TestMode;
import com.azure.core.test.TestProxyTestBase;
import com.azure.core.test.models.CustomMatcher;
import com.azure.core.test.models.TestProxySanitizer;
import com.azure.core.test.models.TestProxySanitizerType;
import com.azure.core.util.Configuration;
import com.azure.identity.DefaultAzureCredentialBuilder;

import static com.azure.ai.agents.persistent.TestUtils.FAKE_API_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ClientTestBase extends TestProxyTestBase {

    protected PersistentAgentsAdministrationClientBuilder getClientBuilder(HttpClient httpClient) {

        PersistentAgentsAdministrationClientBuilder builder = new PersistentAgentsAdministrationClientBuilder()
            .httpClient(interceptorManager.isPlaybackMode() ? interceptorManager.getPlaybackClient() : httpClient);
        TestMode testMode = getTestMode();

        if (testMode != TestMode.LIVE) {

        }

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
        return builder;
    }


    protected void assertAgent(PersistentAgent agent) {
        assertNotNull(agent, "Agent should not be null");
        assertNotNull(agent.getId(), "Agent ID should not be null");
        assertNotNull(agent.getName(), "Agent name should not be null");
    }
}
