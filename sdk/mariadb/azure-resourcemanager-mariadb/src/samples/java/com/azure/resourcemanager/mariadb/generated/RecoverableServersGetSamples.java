// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.mariadb.generated;

/**
 * Samples for RecoverableServers Get.
 */
public final class RecoverableServersGetSamples {
    /*
     * x-ms-original-file:
     * specification/mariadb/resource-manager/Microsoft.DBforMariaDB/stable/2018-06-01/examples/RecoverableServersGet.
     * json
     */
    /**
     * Sample code: ReplicasListByServer.
     * 
     * @param manager Entry point to MariaDBManager.
     */
    public static void replicasListByServer(com.azure.resourcemanager.mariadb.MariaDBManager manager) {
        manager.recoverableServers().getWithResponse("testrg", "testsvc4", com.azure.core.util.Context.NONE);
    }
}
