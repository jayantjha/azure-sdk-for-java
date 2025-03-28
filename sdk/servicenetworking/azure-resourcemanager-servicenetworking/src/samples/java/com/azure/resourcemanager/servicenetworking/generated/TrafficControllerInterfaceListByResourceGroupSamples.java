// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.servicenetworking.generated;

/**
 * Samples for TrafficControllerInterface ListByResourceGroup.
 */
public final class TrafficControllerInterfaceListByResourceGroupSamples {
    /*
     * x-ms-original-file: 2025-03-01-preview/TrafficControllersGet.json
     */
    /**
     * Sample code: Get Traffic Controllers.
     * 
     * @param manager Entry point to TrafficControllerManager.
     */
    public static void
        getTrafficControllers(com.azure.resourcemanager.servicenetworking.TrafficControllerManager manager) {
        manager.trafficControllerInterfaces().listByResourceGroup("rg1", com.azure.core.util.Context.NONE);
    }
}
