// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The update type presented when the status of a run step changes.
 */
public class RunStepUpdate extends StreamingUpdate.GenericStreamingUpdate<RunStep> {
    
    /**
     * Creates a new RunStepUpdate with the specified run step and update kind.
     *
     * @param runStep The run step.
     * @param updateKind The update kind.
     */
    RunStepUpdate(RunStep runStep, StreamingUpdateReason updateKind) {
        super(runStep, updateKind);
    }

    /**
     * Deserializes run step updates from JSON.
     *
     * @param element The JSON element.
     * @param updateKind The update kind.
     * @return A list of streaming updates.
     */
    static List<StreamingUpdate> deserializeRunStepUpdates(JsonNode element, StreamingUpdateReason updateKind) {
        RunStep runStep = RunStep.deserializeRunStep(element);
        List<StreamingUpdate> updates = new ArrayList<>();
        updates.add(new RunStepUpdate(runStep, updateKind));
        return updates;
    }
}
