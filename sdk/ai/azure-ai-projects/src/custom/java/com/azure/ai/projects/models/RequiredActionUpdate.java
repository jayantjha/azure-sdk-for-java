// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The update type presented when the status of a {@link ThreadRun} has changed to {@code requires_action},
 * indicating that tool output submission or another intervention is needed for the run to continue.
 *
 * <p>Distinct {@link RequiredActionUpdate} instances will generated for each required action, meaning that
 * parallel function calling will present multiple updates even if the tool calls arrive at the same time.</p>
 */
public class RequiredActionUpdate extends RunUpdate {
    
    private final RequiredAction requiredAction;

    /**
     * Creates a new RequiredActionUpdate with the specified run and action.
     *
     * @param run The run.
     * @param action The required action.
     */
    RequiredActionUpdate(ThreadRun run, RequiredAction action) {
        super(run, StreamingUpdateReason.RUN_REQUIRES_ACTION);
        this.requiredAction = action;
    }

    /**
     * Gets the function name.
     *
     * @return The function name.
     */
    public String getFunctionName() {
        return getAsFunctionCall() != null ? getAsFunctionCall().getName() : null;
    }

    /**
     * Gets the function arguments.
     *
     * @return The function arguments.
     */
    public String getFunctionArguments() {
        return getAsFunctionCall() != null ? getAsFunctionCall().getArguments() : null;
    }

    /**
     * Gets the tool call ID.
     *
     * @return The tool call ID.
     */
    public String getToolCallId() {
        return getAsFunctionCall() != null ? getAsFunctionCall().getId() : null;
    }

    /**
     * Gets the full, deserialized {@link ThreadRun} instance associated with this streaming required action update.
     *
     * @return The thread run.
     */
    public ThreadRun getThreadRun() {
        return getValue();
    }

    private RequiredFunctionToolCall getAsFunctionCall() {
        return requiredAction instanceof RequiredFunctionToolCall ? (RequiredFunctionToolCall) requiredAction : null;
    }

    /**
     * Deserializes required action updates from JSON.
     *
     * @param element The JSON element.
     * @return A list of streaming updates.
     */
    static List<StreamingUpdate> deserializeRequiredActionUpdates(JsonNode element) {
        ThreadRun run = ThreadRun.deserializeThreadRun(element);
        List<StreamingUpdate> updates = new ArrayList<>();
        
        if (run != null && run.getRequiredActions() != null) {
            for (RequiredAction action : run.getRequiredActions()) {
                updates.add(new RequiredActionUpdate(run, action));
            }
        }
        
        return updates;
    }
}
