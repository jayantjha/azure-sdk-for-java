// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.projects.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a streaming update to {@link ThreadMessage} content as part of the Assistants API.
 *
 * <p>Distinct {@link MessageContentUpdate} instances will be generated for each {@link MessageContent} part
 * and each content subcomponent, such as {@link TextAnnotationUpdate} instances, even if this information
 * arrived in the same response chunk.</p>
 */
public class MessageContentUpdate extends StreamingUpdate {
    
    private final MessageDeltaChunk delta;
    private final MessageDeltaTextContent textContent;
    private final MessageDeltaImageFileContent imageFileContent;
    private final TextAnnotationUpdate textAnnotation;

    /**
     * Creates a new MessageContentUpdate with the specified delta and content.
     *
     * @param delta The delta.
     * @param content The content.
     */
    MessageContentUpdate(MessageDeltaChunk delta, MessageDeltaContent content) {
        super(StreamingUpdateReason.MESSAGE_UPDATED);
        this.delta = delta;
        this.textContent = content instanceof MessageDeltaTextContent ? (MessageDeltaTextContent) content : null;
        this.imageFileContent = content instanceof MessageDeltaImageFileContent ? (MessageDeltaImageFileContent) content : null;
        this.textAnnotation = null;
    }

    /**
     * Creates a new MessageContentUpdate with the specified delta and annotation.
     *
     * @param delta The delta.
     * @param annotation The annotation.
     */
    MessageContentUpdate(MessageDeltaChunk delta, TextAnnotationUpdate annotation) {
        super(StreamingUpdateReason.MESSAGE_UPDATED);
        this.delta = delta;
        this.textContent = null;
        this.imageFileContent = null;
        this.textAnnotation = annotation;
    }

    /**
     * Gets the message ID.
     *
     * @return The message ID.
     */
    public String getMessageId() {
        return delta != null ? delta.getId() : null;
    }

    /**
     * Gets the message index.
     *
     * @return The message index.
     */
    public int getMessageIndex() {
        if (textContent != null && textContent.getIndex() != null) {
            return textContent.getIndex();
        } else if (imageFileContent != null && imageFileContent.getIndex() != null) {
            return imageFileContent.getIndex();
        } else if (textAnnotation != null) {
            return textAnnotation.getContentIndex();
        } else {
            return 0;
        }
    }

    /**
     * Gets the role.
     *
     * @return The role.
     */
    public MessageRole getRole() {
        return delta != null && delta.getDelta() != null ? delta.getDelta().getRole() : null;
    }

    /**
     * Gets the image file ID.
     *
     * @return The image file ID.
     */
    public String getImageFileId() {
        return imageFileContent != null && imageFileContent.getImageFile() != null ? 
               imageFileContent.getImageFile().getFileId() : null;
    }

    /**
     * Gets the text.
     *
     * @return The text.
     */
    public String getText() {
        return textContent != null && textContent.getText() != null ? textContent.getText().getValue() : null;
    }

    /**
     * Gets the text annotation.
     *
     * @return The text annotation.
     */
    public TextAnnotationUpdate getTextAnnotation() {
        return textAnnotation;
    }

    /**
     * Deserializes message content updates from JSON.
     *
     * @param element The JSON element.
     * @param updateKind The update kind.
     * @return A list of streaming updates.
     */
    static List<StreamingUpdate> deserializeMessageContentUpdates(JsonNode element, StreamingUpdateReason updateKind) {
        MessageDeltaChunk deltaObject = MessageDeltaChunk.deserializeMessageDeltaChunk(element);
        List<StreamingUpdate> updates = new ArrayList<>();
        
        if (deltaObject != null && deltaObject.getDelta() != null && deltaObject.getDelta().getContent() != null) {
            for (MessageDeltaContent deltaContent : deltaObject.getDelta().getContent()) {
                updates.add(new MessageContentUpdate(deltaObject, deltaContent));
                
                if (deltaContent instanceof MessageDeltaTextContent) {
                    MessageDeltaTextContent textContent = (MessageDeltaTextContent) deltaContent;
                    if (textContent.getText() != null && textContent.getText().getAnnotations() != null) {
                        for (MessageDeltaTextAnnotation internalAnnotation : textContent.getText().getAnnotations()) {
                            TextAnnotationUpdate annotation = new TextAnnotationUpdate(internalAnnotation);
                            updates.add(new MessageContentUpdate(deltaObject, annotation));
                        }
                    }
                }
            }
        }
        
        return updates;
    }
}
