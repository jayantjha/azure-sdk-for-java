// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.
package com.azure.ai.openai.responses.models;

import com.azure.core.annotation.Generated;
import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.util.List;

/**
 * The ResponsesOutputContentText model.
 */
@Immutable
public final class ResponsesOutputContentText extends ResponsesContent {

    /*
     * The type property.
     */
    @Generated
    private ResponsesContentType type = ResponsesContentType.OUTPUT_TEXT;

    /*
     * The text property.
     */
    @Generated
    private final String text;

    /*
     * The annotations property.
     */
    @Generated
    private final List<ResponsesOutputTextAnnotation> annotations;

    /**
     * Get the type property: The type property.
     *
     * @return the type value.
     */
    @Generated
    @Override
    public ResponsesContentType getType() {
        return this.type;
    }

    /**
     * Get the text property: The text property.
     *
     * @return the text value.
     */
    @Generated
    public String getText() {
        return this.text;
    }

    /**
     * Get the annotations property: The annotations property.
     *
     * @return the annotations value.
     */
    @Generated
    public List<ResponsesOutputTextAnnotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * {@inheritDoc}
     */
    @Generated
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("text", this.text);
        jsonWriter.writeArrayField("annotations", this.annotations, (writer, element) -> writer.writeJson(element));
        jsonWriter.writeStringField("type", this.type == null ? null : this.type.toString());
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of ResponsesOutputContentText from the JsonReader.
     *
     * @param jsonReader The JsonReader being read.
     * @return An instance of ResponsesOutputContentText if the JsonReader was pointing to an instance of it, or null if
     * it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the ResponsesOutputContentText.
     */
    @Generated
    public static ResponsesOutputContentText fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            String text = null;
            List<ResponsesOutputTextAnnotation> annotations = null;
            ResponsesContentType type = ResponsesContentType.OUTPUT_TEXT;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();
                if ("text".equals(fieldName)) {
                    text = reader.getString();
                } else if ("annotations".equals(fieldName)) {
                    annotations = reader.readArray(reader1 -> ResponsesOutputTextAnnotation.fromJson(reader1));
                } else if ("type".equals(fieldName)) {
                    type = ResponsesContentType.fromString(reader.getString());
                } else {
                    reader.skipChildren();
                }
            }
            ResponsesOutputContentText deserializedResponsesOutputContentText
                = new ResponsesOutputContentText(text, annotations);
            deserializedResponsesOutputContentText.type = type;
            return deserializedResponsesOutputContentText;
        });
    }

    /**
     * Creates an instance of ResponsesOutputContentText class.
     *
     * @param text the text value to set.
     * @param annotations the annotations value to set.
     */
    @Generated
    public ResponsesOutputContentText(String text, List<ResponsesOutputTextAnnotation> annotations) {
        this.text = text;
        this.annotations = annotations;
    }
}
