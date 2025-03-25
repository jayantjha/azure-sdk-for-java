// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

/**
 * The collection of values associated with the event names of streaming update payloads. These correspond to the
 * expected downcast data type of the {@link StreamingUpdate} as well as to the expected data present in the
 * payload.
 */
public enum StreamingUpdateReason {
    /**
     * Indicates that there is no known reason associated with the streaming update.
     */
    UNKNOWN,
    /**
     * Indicates that an update was generated as part of a {@code thread.created} event.
     */
    THREAD_CREATED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.created} event.
     */
    RUN_CREATED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.queued} event.
     */
    RUN_QUEUED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.in_progress} event.
     */
    RUN_IN_PROGRESS,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.requires_action} event.
     */
    RUN_REQUIRES_ACTION,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.completed} event.
     */
    RUN_COMPLETED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.incomplete} event.
     */
    RUN_INCOMPLETE,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.failed} event.
     */
    RUN_FAILED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.cancelling} event.
     */
    RUN_CANCELLING,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.cancelled} event.
     */
    RUN_CANCELLED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.expired} event.
     */
    RUN_EXPIRED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.step.created} event.
     */
    RUN_STEP_CREATED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.step.in_progress} event.
     */
    RUN_STEP_IN_PROGRESS,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.step.delta} event.
     */
    RUN_STEP_UPDATED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.step.completed} event.
     */
    RUN_STEP_COMPLETED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.step.failed} event.
     */
    RUN_STEP_FAILED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.step.cancelled} event.
     */
    RUN_STEP_CANCELLED,
    /**
     * Indicates that an update was generated as part of a {@code thread.run.step.expired} event.
     */
    RUN_STEP_EXPIRED,
    /**
     * Indicates that an update was generated as part of a {@code thread.message.created} event.
     */
    MESSAGE_CREATED,
    /**
     * Indicates that an update was generated as part of a {@code thread.message.in_progress} event.
     */
    MESSAGE_IN_PROGRESS,
    /**
     * Indicates that an update was generated as part of a {@code thread.message.delta} event.
     */
    MESSAGE_UPDATED,
    /**
     * Indicates that an update was generated as part of a {@code thread.message.completed} event.
     */
    MESSAGE_COMPLETED,
    /**
     * Indicates that an update was generated as part of a {@code thread.message.failed} event.
     */
    MESSAGE_FAILED,
    /**
     * Indicates that an update was generated as part of a {@code thread.message.error} event.
     */
    ERROR,
    /**
     * Indicates the end of streaming update events. This value should never be typically observed.
     */
    DONE
}
