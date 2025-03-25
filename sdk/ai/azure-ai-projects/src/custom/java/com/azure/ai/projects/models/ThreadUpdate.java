// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The update type presented when a streamed event indicates a thread was created.
 */
public class ThreadUpdate extends StreamingUpdate.GenericStreamingUpdate<AgentThread> {
    
    /**
     * Creates a new ThreadUpdate with the specified thread.
     *
     * @param thread The thread.
     */
    ThreadUpdate(AgentThread thread) {
        super(thread, StreamingUpdateReason.THREAD_CREATED);
    }

    /**
     * Gets the ID of the thread.
     *
     * @return The thread ID.
     */
    public String getId() {
        return getValue().getId();
    }

    /**
     * Gets the metadata of the thread.
     *
     * @return The thread metadata.
     */
    public Map<String, String> getMetadata() {
        return getValue().getMetadata();
    }

    /**
     * Gets the creation time of the thread.
     *
     * @return The creation time.
     */
    public OffsetDateTime getCreatedAt() {
        return getValue().getCreatedAt();
    }

    /**
     * Gets the tool resources of the thread.
     *
     * @return The tool resources.
     */
    public ToolResources getToolResources() {
        return getValue().getToolResources();
    }

    /**
     * Deserializes thread creation updates from JSON.
     *
     * @param element The JSON element.
     * @param updateKind The update kind.
     * @return A list of streaming updates.
     */
    static List<StreamingUpdate> deserializeThreadCreationUpdates(JsonNode element, StreamingUpdateReason updateKind) {
        AgentThread thread = AgentThread.deserializeAgentThread(element);
        
        if (updateKind == StreamingUpdateReason.THREAD_CREATED) {
            List<StreamingUpdate> updates = new ArrayList<>();
            updates.add(new ThreadUpdate(thread));
            return updates;
        } else {
            List<StreamingUpdate> updates = new ArrayList<>();
            updates.add(new StreamingUpdate.GenericStreamingUpdate<>(thread, updateKind));
            return updates;
        }
    }
}
