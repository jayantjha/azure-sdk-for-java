// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.databoxedge.generated;

/**
 * Samples for StorageAccountCredentials Get.
 */
public final class StorageAccountCredentialsGetSamples {
    /*
     * x-ms-original-file:
     * specification/databoxedge/resource-manager/Microsoft.DataBoxEdge/stable/2019-08-01/examples/SACGet.json
     */
    /**
     * Sample code: SACGet.
     * 
     * @param manager Entry point to DataBoxEdgeManager.
     */
    public static void sACGet(com.azure.resourcemanager.databoxedge.DataBoxEdgeManager manager) {
        manager.storageAccountCredentials()
            .getWithResponse("testedgedevice", "sac1", "GroupForEdgeAutomation", com.azure.core.util.Context.NONE);
    }
}
