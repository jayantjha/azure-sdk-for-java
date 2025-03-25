// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a single item of streamed Assistants API data.
 *
 * <p>Please note that this is the abstract base type. To access data, cast an instance of this type to an
 * appropriate, derived update type.</p>
 * 
 * <p>For messages: {@link MessageStatusUpdate}, {@link MessageContentUpdate}</p>
 * 
 * <p>For runs and run steps: {@link RunUpdate}, {@link RunStepUpdate}, {@link RunStepDetailsUpdate},
 * {@link RequiredActionUpdate}</p>
 * 
 * <p>For threads: {@link ThreadUpdate}</p>
 */
public abstract class StreamingUpdate {
    
    /**
     * A value indicating what type of event this update represents.
     *
     * <p>Many events share the same response type. For example, {@link StreamingUpdateReason#RUN_CREATED} and
     * {@link StreamingUpdateReason#RUN_COMPLETED} are both associated with a {@link ThreadRun} instance.
     * You can use the value of {@link #getUpdateKind()} to differentiate between these events when the type is not
     * sufficient to do so.</p>
     */
    private final StreamingUpdateReason updateKind;

    /**
     * Creates a new StreamingUpdate with the specified update kind.
     *
     * @param updateKind The kind of update.
     */
    protected StreamingUpdate(StreamingUpdateReason updateKind) {
        this.updateKind = updateKind;
    }

    /**
     * Gets the kind of update.
     *
     * @return The update kind.
     */
    public StreamingUpdateReason getUpdateKind() {
        return updateKind;
    }

    /**
     * Creates streaming updates from a server-sent event.
     *
     * @param eventType The event type of the server-sent event.
     * @param data The data of the server-sent event.
     * @return A list of streaming updates.
     */
    public static List<StreamingUpdate> fromEvent(String eventType, JsonNode data) {
        StreamingUpdateReason updateKind = StreamingUpdateReasonUtils.fromSseEventLabel(eventType);
        
        if (data == null) {
            return Collections.emptyList();
        }

        switch (updateKind) {
            case THREAD_CREATED:
                return ThreadUpdate.deserializeThreadCreationUpdates(data, updateKind);
            case RUN_CREATED:
            case RUN_QUEUED:
            case RUN_IN_PROGRESS:
            case RUN_COMPLETED:
            case RUN_INCOMPLETE:
            case RUN_FAILED:
            case RUN_CANCELLING:
            case RUN_CANCELLED:
            case RUN_EXPIRED:
                return RunUpdate.deserializeRunUpdates(data, updateKind);
            case RUN_REQUIRES_ACTION:
                return RequiredActionUpdate.deserializeRequiredActionUpdates(data);
            case RUN_STEP_CREATED:
            case RUN_STEP_IN_PROGRESS:
            case RUN_STEP_COMPLETED:
            case RUN_STEP_FAILED:
            case RUN_STEP_CANCELLED:
            case RUN_STEP_EXPIRED:
                return RunStepUpdate.deserializeRunStepUpdates(data, updateKind);
            case MESSAGE_CREATED:
            case MESSAGE_IN_PROGRESS:
            case MESSAGE_COMPLETED:
            case MESSAGE_FAILED:
                return MessageStatusUpdate.deserializeMessageStatusUpdates(data, updateKind);
            case RUN_STEP_UPDATED:
                return RunStepDetailsUpdate.deserializeRunStepDetailsUpdates(data, updateKind);
            case MESSAGE_UPDATED:
                return MessageContentUpdate.deserializeMessageContentUpdates(data, updateKind);
            default:
                return Collections.emptyList();
        }
    }

    /**
     * Generic typed version of StreamingUpdate.
     *
     * @param <T> The type of value in the update.
     */
    public static class GenericStreamingUpdate<T> extends StreamingUpdate {
        private final T value;

        /**
         * Creates a new StreamingUpdate with the specified value and update kind.
         *
         * @param value The value of the update.
         * @param updateKind The kind of update.
         */
        protected GenericStreamingUpdate(T value, StreamingUpdateReason updateKind) {
            super(updateKind);
            this.value = value;
        }

        /**
         * Gets the underlying value of the update.
         *
         * @return The value.
         */
        public T getValue() {
            return value;
        }
    }
}
