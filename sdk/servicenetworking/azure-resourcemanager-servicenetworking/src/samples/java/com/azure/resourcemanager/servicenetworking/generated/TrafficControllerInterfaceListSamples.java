// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.servicenetworking.generated;

/**
 * Samples for TrafficControllerInterface List.
 */
public final class TrafficControllerInterfaceListSamples {
    /*
     * x-ms-original-file: 2025-03-01-preview/TrafficControllersGetList.json
     */
    /**
     * Sample code: Get Traffic Controllers List.
     * 
     * @param manager Entry point to TrafficControllerManager.
     */
    public static void
        getTrafficControllersList(com.azure.resourcemanager.servicenetworking.TrafficControllerManager manager) {
        manager.trafficControllerInterfaces().list(com.azure.core.util.Context.NONE);
    }
}
