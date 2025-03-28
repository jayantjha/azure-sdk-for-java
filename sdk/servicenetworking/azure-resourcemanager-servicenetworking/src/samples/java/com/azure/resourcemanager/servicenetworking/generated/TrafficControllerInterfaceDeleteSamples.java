// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.servicenetworking.generated;

/**
 * Samples for TrafficControllerInterface Delete.
 */
public final class TrafficControllerInterfaceDeleteSamples {
    /*
     * x-ms-original-file: 2025-03-01-preview/TrafficControllerDelete.json
     */
    /**
     * Sample code: Delete Traffic Controller.
     * 
     * @param manager Entry point to TrafficControllerManager.
     */
    public static void
        deleteTrafficController(com.azure.resourcemanager.servicenetworking.TrafficControllerManager manager) {
        manager.trafficControllerInterfaces().delete("rg1", "tc1", com.azure.core.util.Context.NONE);
    }
}
