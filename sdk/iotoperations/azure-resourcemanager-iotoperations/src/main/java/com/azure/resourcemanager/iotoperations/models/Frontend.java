// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.iotoperations.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * The desired properties of the frontend instances of the Broker.
 */
@Fluent
public final class Frontend implements JsonSerializable<Frontend> {
    /*
     * The desired number of frontend instances (pods).
     */
    private int replicas;

    /*
     * Number of logical frontend workers per instance (pod).
     */
    private Integer workers;

    /**
     * Creates an instance of Frontend class.
     */
    public Frontend() {
    }

    /**
     * Get the replicas property: The desired number of frontend instances (pods).
     * 
     * @return the replicas value.
     */
    public int replicas() {
        return this.replicas;
    }

    /**
     * Set the replicas property: The desired number of frontend instances (pods).
     * 
     * @param replicas the replicas value to set.
     * @return the Frontend object itself.
     */
    public Frontend withReplicas(int replicas) {
        this.replicas = replicas;
        return this;
    }

    /**
     * Get the workers property: Number of logical frontend workers per instance (pod).
     * 
     * @return the workers value.
     */
    public Integer workers() {
        return this.workers;
    }

    /**
     * Set the workers property: Number of logical frontend workers per instance (pod).
     * 
     * @param workers the workers value to set.
     * @return the Frontend object itself.
     */
    public Frontend withWorkers(Integer workers) {
        this.workers = workers;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeIntField("replicas", this.replicas);
        jsonWriter.writeNumberField("workers", this.workers);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of Frontend from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of Frontend if the JsonReader was pointing to an instance of it, or null if it was pointing
     * to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the Frontend.
     */
    public static Frontend fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            Frontend deserializedFrontend = new Frontend();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("replicas".equals(fieldName)) {
                    deserializedFrontend.replicas = reader.getInt();
                } else if ("workers".equals(fieldName)) {
                    deserializedFrontend.workers = reader.getNullable(JsonReader::getInt);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedFrontend;
        });
    }
}
