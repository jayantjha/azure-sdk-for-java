// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.ai.openai.responses.models;

/**
 * Defines values for ResponsesComputerCallItemStatus.
 */
public enum ResponsesComputerCallItemStatus {
    /**
     * Enum value in_progress.
     */
    IN_PROGRESS("in_progress"),

    /**
     * Enum value completed.
     */
    COMPLETED("completed"),

    /**
     * Enum value incomplete.
     */
    INCOMPLETE("incomplete");

    /**
     * The actual serialized value for a ResponsesComputerCallItemStatus instance.
     */
    private final String value;

    ResponsesComputerCallItemStatus(String value) {
        this.value = value;
    }

    /**
     * Parses a serialized value to a ResponsesComputerCallItemStatus instance.
     * 
     * @param value the serialized value to parse.
     * @return the parsed ResponsesComputerCallItemStatus object, or null if unable to parse.
     */
    public static ResponsesComputerCallItemStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        ResponsesComputerCallItemStatus[] items = ResponsesComputerCallItemStatus.values();
        for (ResponsesComputerCallItemStatus item : items) {
            if (item.toString().equalsIgnoreCase(value)) {
                return item;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.value;
    }
}
