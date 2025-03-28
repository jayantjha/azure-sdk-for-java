// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.trafficmanager.models;

import com.azure.core.management.Region;

/** An immutable client-side representation of an Azure traffic manager profile nested profile endpoint. */
public interface TrafficManagerNestedProfileEndpoint extends TrafficManagerEndpoint {
    /**
     * Gets the nested traffic manager profile resource id.
     *
     * @return the nested traffic manager profile resource id
     */
    String nestedProfileId();

    /**
     * Gets the number of child endpoints to be online to consider nested profile as healthy.
     *
     * @return the number of child endpoints to be online to consider nested profile as healthy
     */
    long minimumChildEndpointCount();

    /**
     * Gets the location of the traffic that the endpoint handles.
     *
     * @return the location of the traffic that the endpoint handles
     */
    Region sourceTrafficLocation();
}
