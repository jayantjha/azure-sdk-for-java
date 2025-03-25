// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

/**
 * Utility class for StreamingUpdateReason serialization/deserialization.
 */
public class StreamingUpdateReasonUtils {
    
    /**
     * Converts a StreamingUpdateReason to its corresponding SSE event label.
     *
     * @param value The StreamingUpdateReason to convert.
     * @return The corresponding SSE event label.
     */
    public static String toSseEventLabel(StreamingUpdateReason value) {
        if (value == null) {
            return "";
        }
        
        switch (value) {
            case THREAD_CREATED:
                return "thread.created";
            case RUN_CREATED:
                return "thread.run.created";
            case RUN_QUEUED:
                return "thread.run.queued";
            case RUN_IN_PROGRESS:
                return "thread.run.in_progress";
            case RUN_REQUIRES_ACTION:
                return "thread.run.requires_action";
            case RUN_COMPLETED:
                return "thread.run.completed";
            case RUN_FAILED:
                return "thread.run.failed";
            case RUN_CANCELLING:
                return "thread.run.cancelling";
            case RUN_CANCELLED:
                return "thread.run.cancelled";
            case RUN_EXPIRED:
                return "thread.run.expired";
            case RUN_STEP_CREATED:
                return "thread.run.step.created";
            case RUN_STEP_IN_PROGRESS:
                return "thread.run.step.in_progress";
            case RUN_STEP_UPDATED:
                return "thread.run.step.delta";
            case RUN_STEP_COMPLETED:
                return "thread.run.step.completed";
            case RUN_STEP_FAILED:
                return "thread.run.step.failed";
            case RUN_STEP_CANCELLED:
                return "thread.run.step.cancelled";
            case RUN_STEP_EXPIRED:
                return "thread.run.step.expired";
            case MESSAGE_CREATED:
                return "thread.message.created";
            case MESSAGE_IN_PROGRESS:
                return "thread.message.in_progress";
            case MESSAGE_UPDATED:
                return "thread.message.delta";
            case MESSAGE_COMPLETED:
                return "thread.message.completed";
            case MESSAGE_FAILED:
                return "thread.message.incomplete";
            case ERROR:
                return "error";
            case DONE:
                return "done";
            default:
                return "";
        }
    }

    /**
     * Converts an SSE event label to its corresponding StreamingUpdateReason.
     *
     * @param label The SSE event label to convert.
     * @return The corresponding StreamingUpdateReason.
     */
    public static StreamingUpdateReason fromSseEventLabel(String label) {
        if (label == null) {
            return StreamingUpdateReason.UNKNOWN;
        }
        
        switch (label) {
            case "thread.created":
                return StreamingUpdateReason.THREAD_CREATED;
            case "thread.run.created":
                return StreamingUpdateReason.RUN_CREATED;
            case "thread.run.queued":
                return StreamingUpdateReason.RUN_QUEUED;
            case "thread.run.in_progress":
                return StreamingUpdateReason.RUN_IN_PROGRESS;
            case "thread.run.requires_action":
                return StreamingUpdateReason.RUN_REQUIRES_ACTION;
            case "thread.run.completed":
                return StreamingUpdateReason.RUN_COMPLETED;
            case "thread.run.incomplete":
                return StreamingUpdateReason.RUN_INCOMPLETE;
            case "thread.run.failed":
                return StreamingUpdateReason.RUN_FAILED;
            case "thread.run.cancelling":
                return StreamingUpdateReason.RUN_CANCELLING;
            case "thread.run.cancelled":
                return StreamingUpdateReason.RUN_CANCELLED;
            case "thread.run.expired":
                return StreamingUpdateReason.RUN_EXPIRED;
            case "thread.run.step.created":
                return StreamingUpdateReason.RUN_STEP_CREATED;
            case "thread.run.step.in_progress":
                return StreamingUpdateReason.RUN_STEP_IN_PROGRESS;
            case "thread.run.step.delta":
                return StreamingUpdateReason.RUN_STEP_UPDATED;
            case "thread.run.step.completed":
                return StreamingUpdateReason.RUN_STEP_COMPLETED;
            case "thread.run.step.failed":
                return StreamingUpdateReason.RUN_STEP_FAILED;
            case "thread.run.step.cancelled":
                return StreamingUpdateReason.RUN_STEP_CANCELLED;
            case "thread.run.step.expired":
                return StreamingUpdateReason.RUN_STEP_EXPIRED;
            case "thread.message.created":
                return StreamingUpdateReason.MESSAGE_CREATED;
            case "thread.message.in_progress":
                return StreamingUpdateReason.MESSAGE_IN_PROGRESS;
            case "thread.message.delta":
                return StreamingUpdateReason.MESSAGE_UPDATED;
            case "thread.message.completed":
                return StreamingUpdateReason.MESSAGE_COMPLETED;
            case "thread.message.incomplete":
                return StreamingUpdateReason.MESSAGE_FAILED;
            case "error":
                return StreamingUpdateReason.ERROR;
            case "done":
                return StreamingUpdateReason.DONE;
            default:
                return StreamingUpdateReason.UNKNOWN;
        }
    }
}
