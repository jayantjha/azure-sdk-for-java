// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The update type presented when the status of a message changes.
 */
public class MessageStatusUpdate extends StreamingUpdate.GenericStreamingUpdate<ThreadMessage> {
    
    /**
     * Creates a new MessageStatusUpdate with the specified message and update kind.
     *
     * @param message The message.
     * @param updateKind The update kind.
     */
    MessageStatusUpdate(ThreadMessage message, StreamingUpdateReason updateKind) {
        super(message, updateKind);
    }

    /**
     * Deserializes message status updates from JSON.
     *
     * @param element The JSON element.
     * @param updateKind The update kind.
     * @return A list of streaming updates.
     */
    static List<StreamingUpdate> deserializeMessageStatusUpdates(JsonNode element, StreamingUpdateReason updateKind) {
        ThreadMessage message = ThreadMessage.deserializeThreadMessage(element);
        List<StreamingUpdate> updates = new ArrayList<>();
        updates.add(new MessageStatusUpdate(message, updateKind));
        return updates;
    }
}
