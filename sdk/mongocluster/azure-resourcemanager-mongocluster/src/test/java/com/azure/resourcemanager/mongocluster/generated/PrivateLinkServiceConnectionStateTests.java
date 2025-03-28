// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.mongocluster.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.mongocluster.models.PrivateEndpointServiceConnectionStatus;
import com.azure.resourcemanager.mongocluster.models.PrivateLinkServiceConnectionState;
import org.junit.jupiter.api.Assertions;

public final class PrivateLinkServiceConnectionStateTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        PrivateLinkServiceConnectionState model = BinaryData
            .fromString("{\"status\":\"Approved\",\"description\":\"amiheognarxz\",\"actionsRequired\":\"heotusiv\"}")
            .toObject(PrivateLinkServiceConnectionState.class);
        Assertions.assertEquals(PrivateEndpointServiceConnectionStatus.APPROVED, model.status());
        Assertions.assertEquals("amiheognarxz", model.description());
        Assertions.assertEquals("heotusiv", model.actionsRequired());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        PrivateLinkServiceConnectionState model
            = new PrivateLinkServiceConnectionState().withStatus(PrivateEndpointServiceConnectionStatus.APPROVED)
                .withDescription("amiheognarxz")
                .withActionsRequired("heotusiv");
        model = BinaryData.fromObject(model).toObject(PrivateLinkServiceConnectionState.class);
        Assertions.assertEquals(PrivateEndpointServiceConnectionStatus.APPROVED, model.status());
        Assertions.assertEquals("amiheognarxz", model.description());
        Assertions.assertEquals("heotusiv", model.actionsRequired());
    }
}
