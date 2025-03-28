// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.mobilenetwork.generated;

import com.azure.resourcemanager.mobilenetwork.models.Ambr;
import com.azure.resourcemanager.mobilenetwork.models.DataNetworkConfiguration;
import com.azure.resourcemanager.mobilenetwork.models.DataNetworkResourceId;
import com.azure.resourcemanager.mobilenetwork.models.PduSessionType;
import com.azure.resourcemanager.mobilenetwork.models.PreemptionCapability;
import com.azure.resourcemanager.mobilenetwork.models.PreemptionVulnerability;
import com.azure.resourcemanager.mobilenetwork.models.ServiceResourceId;
import com.azure.resourcemanager.mobilenetwork.models.SliceConfiguration;
import com.azure.resourcemanager.mobilenetwork.models.SliceResourceId;
import java.util.Arrays;

/**
 * Samples for SimPolicies CreateOrUpdate.
 */
public final class SimPoliciesCreateOrUpdateSamples {
    /*
     * x-ms-original-file:
     * specification/mobilenetwork/resource-manager/Microsoft.MobileNetwork/stable/2024-04-01/examples/SimPolicyCreate.
     * json
     */
    /**
     * Sample code: Create SIM policy.
     * 
     * @param manager Entry point to MobileNetworkManager.
     */
    public static void createSIMPolicy(com.azure.resourcemanager.mobilenetwork.MobileNetworkManager manager) {
        manager.simPolicies()
            .define("testPolicy")
            .withRegion("eastus")
            .withExistingMobileNetwork("rg1", "testMobileNetwork")
            .withUeAmbr(new Ambr().withUplink("500 Mbps").withDownlink("1 Gbps"))
            .withDefaultSlice(new SliceResourceId().withId(
                "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/rg1/providers/Microsoft.MobileNetwork/mobileNetworks/testMobileNetwork/slices/testSlice"))
            .withSliceConfigurations(Arrays.asList(new SliceConfiguration().withSlice(new SliceResourceId().withId(
                "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/rg1/providers/Microsoft.MobileNetwork/mobileNetworks/testMobileNetwork/slices/testSlice"))
                .withDefaultDataNetwork(new DataNetworkResourceId().withId(
                    "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/rg1/providers/Microsoft.MobileNetwork/mobileNetworks/testMobileNetwork/dataNetworks/testdataNetwork"))
                .withDataNetworkConfigurations(Arrays.asList(new DataNetworkConfiguration()
                    .withDataNetwork(new DataNetworkResourceId().withId(
                        "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/rg1/providers/Microsoft.MobileNetwork/mobileNetworks/testMobileNetwork/dataNetworks/testdataNetwork"))
                    .withSessionAmbr(new Ambr().withUplink("500 Mbps").withDownlink("1 Gbps"))
                    .withFiveQi(9)
                    .withAllocationAndRetentionPriorityLevel(9)
                    .withPreemptionCapability(PreemptionCapability.NOT_PREEMPT)
                    .withPreemptionVulnerability(PreemptionVulnerability.PREEMPTABLE)
                    .withDefaultSessionType(PduSessionType.IPV4)
                    .withAdditionalAllowedSessionTypes(Arrays.asList())
                    .withAllowedServices(Arrays.asList(new ServiceResourceId().withId(
                        "/subscriptions/00000000-0000-0000-0000-000000000000/resourceGroups/rg1/providers/Microsoft.MobileNetwork/mobileNetworks/testMobileNetwork/services/testService")))
                    .withMaximumNumberOfBufferedPackets(200)))))
            .withRegistrationTimer(3240)
            .create();
    }
}
