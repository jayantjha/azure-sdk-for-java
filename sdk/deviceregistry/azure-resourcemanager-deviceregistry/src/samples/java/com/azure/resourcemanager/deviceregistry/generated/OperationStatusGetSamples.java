// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.deviceregistry.generated;

/**
 * Samples for OperationStatus Get.
 */
public final class OperationStatusGetSamples {
    /*
     * x-ms-original-file: 2024-11-01/Get_OperationStatus.json
     */
    /**
     * Sample code: Get_OperationStatus.
     * 
     * @param manager Entry point to DeviceRegistryManager.
     */
    public static void getOperationStatus(com.azure.resourcemanager.deviceregistry.DeviceRegistryManager manager) {
        manager.operationStatus()
            .getWithResponse("testLocation", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", com.azure.core.util.Context.NONE);
    }
}
