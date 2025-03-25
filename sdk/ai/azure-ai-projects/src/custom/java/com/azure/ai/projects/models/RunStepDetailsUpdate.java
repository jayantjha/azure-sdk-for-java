// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.ArrayList;
import java.util.List;

import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The update type presented when run step details, including tool call progress, have changed.
 */
public class RunStepDetailsUpdate extends StreamingUpdate {
    
    private final RunStepDeltaChunk delta;
    private final RunStepDeltaToolCall toolCall;
    private final RunStepDeltaMessageCreation asMessageCreation;
    private final RunStepDeltaCodeInterpreterToolCall asCodeCall;
    private final RunStepDeltaFileSearchToolCall asFileSearchCall;
    private final RunStepDeltaFunctionToolCall asFunctionCall;

    /**
     * Creates a new RunStepDetailsUpdate with the specified step delta and optional tool call.
     *
     * @param stepDelta The step delta.
     * @param toolCall The tool call, or null.
     */
    RunStepDetailsUpdate(RunStepDeltaChunk stepDelta, RunStepDeltaToolCall toolCall) {
        super(StreamingUpdateReason.RUN_STEP_UPDATED);
        
        this.delta = stepDelta;
        this.toolCall = toolCall;
        
        this.asMessageCreation = stepDelta != null && stepDelta.getDelta() != null ? 
                (stepDelta.getDelta().getStepDetails() instanceof RunStepDeltaMessageCreation ? 
                        (RunStepDeltaMessageCreation) stepDelta.getDelta().getStepDetails() : null) : null;
                        
        this.asCodeCall = toolCall instanceof RunStepDeltaCodeInterpreterToolCall ? 
                (RunStepDeltaCodeInterpreterToolCall) toolCall : null;
                
        this.asFileSearchCall = toolCall instanceof RunStepDeltaFileSearchToolCall ? 
                (RunStepDeltaFileSearchToolCall) toolCall : null;
                
        this.asFunctionCall = toolCall instanceof RunStepDeltaFunctionToolCall ? 
                (RunStepDeltaFunctionToolCall) toolCall : null;
    }

    /**
     * Gets the step ID.
     *
     * @return The step ID.
     */
    public String getStepId() {
        return delta != null ? delta.getId() : null;
    }

    /**
     * Gets the created message ID.
     *
     * @return The message ID.
     */
    public String getCreatedMessageId() {
        return asMessageCreation != null && asMessageCreation.getMessageCreation() != null ? 
               asMessageCreation.getMessageCreation().getMessageId() : null;
    }

    /**
     * Gets the tool call ID.
     *
     * @return The tool call ID.
     */
    public String getToolCallId() {
        if (asCodeCall != null) {
            return asCodeCall.getId();
        } else if (asFileSearchCall != null) {
            return asFileSearchCall.getId();
        } else if (asFunctionCall != null) {
            return asFunctionCall.getId();
        } else if (toolCall != null && 
                   toolCall.getSerializedAdditionalRawData() != null && 
                   toolCall.getSerializedAdditionalRawData().containsKey("id")) {
            return toolCall.getSerializedAdditionalRawData().get("id").toString();
        } else {
            return null;
        }
    }

    /**
     * Gets the tool call index.
     *
     * @return The tool call index.
     */
    public Integer getToolCallIndex() {
        if (asCodeCall != null) {
            return asCodeCall.getIndex();
        } else if (asFileSearchCall != null) {
            return asFileSearchCall.getIndex();
        } else if (asFunctionCall != null) {
            return asFunctionCall.getIndex();
        } else {
            return null;
        }
    }

    /**
     * Gets the code interpreter input.
     *
     * @return The code interpreter input.
     */
    public String getCodeInterpreterInput() {
        return asCodeCall != null && asCodeCall.getCodeInterpreter() != null ? 
               asCodeCall.getCodeInterpreter().getInput() : null;
    }

    /**
     * Gets the code interpreter outputs.
     *
     * @return The code interpreter outputs.
     */
    public List<RunStepDeltaCodeInterpreterOutput> getCodeInterpreterOutputs() {
        return asCodeCall != null && asCodeCall.getCodeInterpreter() != null ? 
               asCodeCall.getCodeInterpreter().getOutputs() : null;
    }

    /**
     * Gets the function name.
     *
     * @return The function name.
     */
    public String getFunctionName() {
        return asFunctionCall != null && asFunctionCall.getFunction() != null ? 
               asFunctionCall.getFunction().getName() : null;
    }

    /**
     * Gets the function arguments.
     *
     * @return The function arguments.
     */
    public String getFunctionArguments() {
        return asFunctionCall != null && asFunctionCall.getFunction() != null ? 
               asFunctionCall.getFunction().getArguments() : null;
    }

    /**
     * Gets the function output.
     *
     * @return The function output.
     */
    public String getFunctionOutput() {
        return asFunctionCall != null && asFunctionCall.getFunction() != null ? 
               asFunctionCall.getFunction().getOutput() : null;
    }

    /**
     * Deserializes run step details updates from JSON.
     *
     * @param element The JSON element.
     * @param updateKind The update kind.
     * @return A list of streaming updates.
     */
    static List<StreamingUpdate> deserializeRunStepDetailsUpdates(JsonNode element, StreamingUpdateReason updateKind) {
        RunStepDeltaChunk stepDelta = RunStepDeltaChunk.deserializeRunStepDeltaChunk(element);
        List<StreamingUpdate> updates = new ArrayList<>();
        
        if (stepDelta != null && stepDelta.getDelta() != null && 
            stepDelta.getDelta().getStepDetails() instanceof RunStepDeltaMessageCreation) {
            updates.add(new RunStepDetailsUpdate(stepDelta, null));
        } else if (stepDelta != null && stepDelta.getDelta() != null && 
                  stepDelta.getDelta().getStepDetails() instanceof RunStepDeltaToolCallObject) {
            RunStepDeltaToolCallObject toolCalls = (RunStepDeltaToolCallObject) stepDelta.getDelta().getStepDetails();
            for (RunStepDeltaToolCall toolCall : toolCalls.getToolCalls()) {
                updates.add(new RunStepDetailsUpdate(stepDelta, toolCall));
            }
        }
        
        return updates;
    }
}
