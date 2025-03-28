// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.security.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.security.SecurityManager;
import com.azure.resourcemanager.security.models.AdvancedThreatProtectionSetting;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class AdvancedThreatProtectionsCreateWithResponseMockTests {
    @Test
    public void testCreateWithResponse() throws Exception {
        String responseStr = "{\"properties\":{\"isEnabled\":false},\"id\":\"blml\",\"name\":\"owxih\",\"type\":\"p\"}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        SecurityManager manager = SecurityManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        AdvancedThreatProtectionSetting response = manager.advancedThreatProtections()
            .define()
            .withExistingResourceId("urnnqbnqbpiz")
            .withIsEnabled(true)
            .create();

        Assertions.assertEquals(false, response.isEnabled());
    }
}
