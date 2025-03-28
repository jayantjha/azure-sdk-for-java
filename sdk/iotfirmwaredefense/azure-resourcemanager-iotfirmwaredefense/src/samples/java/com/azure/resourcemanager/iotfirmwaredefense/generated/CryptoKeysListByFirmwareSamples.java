// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.iotfirmwaredefense.generated;

/**
 * Samples for CryptoKeys ListByFirmware.
 */
public final class CryptoKeysListByFirmwareSamples {
    /*
     * x-ms-original-file: specification/fist/resource-manager/Microsoft.IoTFirmwareDefense/stable/2024-01-10/examples/
     * CryptoKeys_ListByFirmware_MinimumSet_Gen.json
     */
    /**
     * Sample code: CryptoKeys_ListByFirmware_MinimumSet_Gen.
     * 
     * @param manager Entry point to IoTFirmwareDefenseManager.
     */
    public static void cryptoKeysListByFirmwareMinimumSetGen(
        com.azure.resourcemanager.iotfirmwaredefense.IoTFirmwareDefenseManager manager) {
        manager.cryptoKeys()
            .listByFirmware("FirmwareAnalysisRG", "default", "109a9886-50bf-85a8-9d75-000000000000",
                com.azure.core.util.Context.NONE);
    }

    /*
     * x-ms-original-file: specification/fist/resource-manager/Microsoft.IoTFirmwareDefense/stable/2024-01-10/examples/
     * CryptoKeys_ListByFirmware_MaximumSet_Gen.json
     */
    /**
     * Sample code: CryptoKeys_ListByFirmware_MaximumSet_Gen.
     * 
     * @param manager Entry point to IoTFirmwareDefenseManager.
     */
    public static void cryptoKeysListByFirmwareMaximumSetGen(
        com.azure.resourcemanager.iotfirmwaredefense.IoTFirmwareDefenseManager manager) {
        manager.cryptoKeys()
            .listByFirmware("FirmwareAnalysisRG", "default", "109a9886-50bf-85a8-9d75-000000000000",
                com.azure.core.util.Context.NONE);
    }
}
