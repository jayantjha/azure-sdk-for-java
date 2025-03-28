// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.labservices.models;

import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.labservices.fluent.models.ImageInner;
import java.io.IOException;
import java.util.List;

/**
 * Paged list of Lab services virtual machine images.
 */
@Immutable
public final class PagedImages implements JsonSerializable<PagedImages> {
    /*
     * The array page of virtual machine images.
     */
    private List<ImageInner> value;

    /*
     * The link to get the next page of image results.
     */
    private String nextLink;

    /**
     * Creates an instance of PagedImages class.
     */
    public PagedImages() {
    }

    /**
     * Get the value property: The array page of virtual machine images.
     * 
     * @return the value value.
     */
    public List<ImageInner> value() {
        return this.value;
    }

    /**
     * Get the nextLink property: The link to get the next page of image results.
     * 
     * @return the nextLink value.
     */
    public String nextLink() {
        return this.nextLink;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (value() != null) {
            value().forEach(e -> e.validate());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of PagedImages from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of PagedImages if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IOException If an error occurs while reading the PagedImages.
     */
    public static PagedImages fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            PagedImages deserializedPagedImages = new PagedImages();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("value".equals(fieldName)) {
                    List<ImageInner> value = reader.readArray(reader1 -> ImageInner.fromJson(reader1));
                    deserializedPagedImages.value = value;
                } else if ("nextLink".equals(fieldName)) {
                    deserializedPagedImages.nextLink = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedPagedImages;
        });
    }
}
