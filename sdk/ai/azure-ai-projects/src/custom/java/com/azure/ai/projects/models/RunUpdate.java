// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The update type presented when the status of a {@link ThreadRun} has changed.
 */
public class RunUpdate extends StreamingUpdate.GenericStreamingUpdate<ThreadRun> {
    
    /**
     * Creates a new RunUpdate with the specified run and update kind.
     *
     * @param run The run.
     * @param updateKind The update kind.
     */
    RunUpdate(ThreadRun run, StreamingUpdateReason updateKind) {
        super(run, updateKind);
    }

    /**
     * Deserializes run updates from JSON.
     *
     * @param element The JSON element.
     * @param updateKind The update kind.
     * @return A list of streaming updates.
     */
    static List<StreamingUpdate> deserializeRunUpdates(JsonNode element, StreamingUpdateReason updateKind) {
        ThreadRun run = ThreadRun.deserializeThreadRun(element);
        List<StreamingUpdate> updates = new ArrayList<>();
        updates.add(new RunUpdate(run, updateKind));
        return updates;
    }
}
