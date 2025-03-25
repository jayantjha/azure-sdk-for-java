// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.Map;
import com.azure.core.annotation.Fluent;
import com.azure.core.util.BinaryData;

/**
 * Abstract base class for run step delta tool calls.
 */
@Fluent
public abstract class RunStepDeltaToolCall {
    
    /**
     * Keeps track of any properties unknown to the library.
     * <p>
     * To assign an object to the value of this property use {@link BinaryData#fromObject(Object)}.
     * </p>
     * <p>
     * To assign an already formatted json string to this property use {@link BinaryData#fromString(String)}.
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>
     * BinaryData.fromObject("foo") creates a payload of "foo".
     * </li>
     * <li>
     * BinaryData.fromString("\"foo\"") creates a payload of "foo".
     * </li>
     * <li>
     * BinaryData.fromObject(Map.of("key", "value")) creates a payload of {"key": "value"}.
     * </li>
     * <li>
     * BinaryData.fromString("{\"key\": \"value\"}") creates a payload of {"key": "value"}.
     * </li>
     * </ul>
     * </p>
     */
    private Map<String, BinaryData> serializedAdditionalRawData;
    
    /**
     * Gets the serialized additional raw data.
     *
     * @return The serialized additional raw data.
     */
    public Map<String, BinaryData> getSerializedAdditionalRawData() {
        return serializedAdditionalRawData;
    }
    
    /**
     * Sets the serialized additional raw data.
     *
     * @param serializedAdditionalRawData The serialized additional raw data.
     * @return The updated RunStepDeltaToolCall instance.
     */
    public RunStepDeltaToolCall setSerializedAdditionalRawData(Map<String, BinaryData> serializedAdditionalRawData) {
        this.serializedAdditionalRawData = serializedAdditionalRawData;
        return this;
    }
}
