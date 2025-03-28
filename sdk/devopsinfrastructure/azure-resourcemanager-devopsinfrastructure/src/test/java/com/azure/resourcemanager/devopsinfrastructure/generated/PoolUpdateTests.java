// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.devopsinfrastructure.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.devopsinfrastructure.models.AgentProfile;
import com.azure.resourcemanager.devopsinfrastructure.models.FabricProfile;
import com.azure.resourcemanager.devopsinfrastructure.models.ManagedServiceIdentity;
import com.azure.resourcemanager.devopsinfrastructure.models.ManagedServiceIdentityType;
import com.azure.resourcemanager.devopsinfrastructure.models.OrganizationProfile;
import com.azure.resourcemanager.devopsinfrastructure.models.PoolUpdate;
import com.azure.resourcemanager.devopsinfrastructure.models.PoolUpdateProperties;
import com.azure.resourcemanager.devopsinfrastructure.models.ProvisioningState;
import com.azure.resourcemanager.devopsinfrastructure.models.ResourcePredictions;
import com.azure.resourcemanager.devopsinfrastructure.models.ResourcePredictionsProfile;
import com.azure.resourcemanager.devopsinfrastructure.models.UserAssignedIdentity;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class PoolUpdateTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        PoolUpdate model = BinaryData.fromString(
            "{\"identity\":{\"principalId\":\"rcjxvsnbyxqabn\",\"tenantId\":\"cpc\",\"type\":\"SystemAssigned,UserAssigned\",\"userAssignedIdentities\":{\"pbtoqcjmkl\":{\"clientId\":\"zafb\",\"principalId\":\"j\"},\"kudjkrlkhb\":{\"clientId\":\"vbqid\",\"principalId\":\"ajzyul\"}}},\"tags\":{\"ocxscpaierhhbcs\":\"epgzgqexz\",\"mmajtjaodx\":\"l\"},\"properties\":{\"provisioningState\":\"Canceled\",\"maximumConcurrency\":1399700283,\"organizationProfile\":{\"kind\":\"OrganizationProfile\"},\"agentProfile\":{\"kind\":\"AgentProfile\",\"resourcePredictions\":{},\"resourcePredictionsProfile\":{\"kind\":\"ResourcePredictionsProfile\"}},\"fabricProfile\":{\"kind\":\"FabricProfile\"},\"devCenterProjectResourceId\":\"ajionpimexgstxg\"}}")
            .toObject(PoolUpdate.class);
        Assertions.assertEquals(ManagedServiceIdentityType.SYSTEM_ASSIGNED_USER_ASSIGNED, model.identity().type());
        Assertions.assertEquals("epgzgqexz", model.tags().get("ocxscpaierhhbcs"));
        Assertions.assertEquals(ProvisioningState.CANCELED, model.properties().provisioningState());
        Assertions.assertEquals(1399700283, model.properties().maximumConcurrency());
        Assertions.assertEquals("ajionpimexgstxg", model.properties().devCenterProjectResourceId());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        PoolUpdate model = new PoolUpdate()
            .withIdentity(
                new ManagedServiceIdentity().withType(ManagedServiceIdentityType.SYSTEM_ASSIGNED_USER_ASSIGNED)
                    .withUserAssignedIdentities(
                        mapOf("pbtoqcjmkl", new UserAssignedIdentity(), "kudjkrlkhb", new UserAssignedIdentity())))
            .withTags(mapOf("ocxscpaierhhbcs", "epgzgqexz", "mmajtjaodx", "l"))
            .withProperties(new PoolUpdateProperties().withProvisioningState(ProvisioningState.CANCELED)
                .withMaximumConcurrency(1399700283)
                .withOrganizationProfile(new OrganizationProfile())
                .withAgentProfile(new AgentProfile().withResourcePredictions(new ResourcePredictions())
                    .withResourcePredictionsProfile(new ResourcePredictionsProfile()))
                .withFabricProfile(new FabricProfile())
                .withDevCenterProjectResourceId("ajionpimexgstxg"));
        model = BinaryData.fromObject(model).toObject(PoolUpdate.class);
        Assertions.assertEquals(ManagedServiceIdentityType.SYSTEM_ASSIGNED_USER_ASSIGNED, model.identity().type());
        Assertions.assertEquals("epgzgqexz", model.tags().get("ocxscpaierhhbcs"));
        Assertions.assertEquals(ProvisioningState.CANCELED, model.properties().provisioningState());
        Assertions.assertEquals(1399700283, model.properties().maximumConcurrency());
        Assertions.assertEquals("ajionpimexgstxg", model.properties().devCenterProjectResourceId());
    }

    // Use "Map.of" if available
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
