// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.mediaservices.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.logging.ClientLogger;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.time.Duration;

/**
 * Describes the properties of a video overlay.
 */
@Fluent
public final class VideoOverlay extends Overlay {
    /*
     * The discriminator for derived types.
     */
    private String odataType = "#Microsoft.Media.VideoOverlay";

    /*
     * The location in the input video where the overlay is applied.
     */
    private Rectangle position;

    /*
     * The opacity of the overlay. This is a value in the range [0 - 1.0]. Default is 1.0 which mean the overlay is
     * opaque.
     */
    private Double opacity;

    /*
     * An optional rectangular window used to crop the overlay image or video.
     */
    private Rectangle cropRectangle;

    /**
     * Creates an instance of VideoOverlay class.
     */
    public VideoOverlay() {
    }

    /**
     * Get the odataType property: The discriminator for derived types.
     * 
     * @return the odataType value.
     */
    @Override
    public String odataType() {
        return this.odataType;
    }

    /**
     * Get the position property: The location in the input video where the overlay is applied.
     * 
     * @return the position value.
     */
    public Rectangle position() {
        return this.position;
    }

    /**
     * Set the position property: The location in the input video where the overlay is applied.
     * 
     * @param position the position value to set.
     * @return the VideoOverlay object itself.
     */
    public VideoOverlay withPosition(Rectangle position) {
        this.position = position;
        return this;
    }

    /**
     * Get the opacity property: The opacity of the overlay. This is a value in the range [0 - 1.0]. Default is 1.0
     * which mean the overlay is opaque.
     * 
     * @return the opacity value.
     */
    public Double opacity() {
        return this.opacity;
    }

    /**
     * Set the opacity property: The opacity of the overlay. This is a value in the range [0 - 1.0]. Default is 1.0
     * which mean the overlay is opaque.
     * 
     * @param opacity the opacity value to set.
     * @return the VideoOverlay object itself.
     */
    public VideoOverlay withOpacity(Double opacity) {
        this.opacity = opacity;
        return this;
    }

    /**
     * Get the cropRectangle property: An optional rectangular window used to crop the overlay image or video.
     * 
     * @return the cropRectangle value.
     */
    public Rectangle cropRectangle() {
        return this.cropRectangle;
    }

    /**
     * Set the cropRectangle property: An optional rectangular window used to crop the overlay image or video.
     * 
     * @param cropRectangle the cropRectangle value to set.
     * @return the VideoOverlay object itself.
     */
    public VideoOverlay withCropRectangle(Rectangle cropRectangle) {
        this.cropRectangle = cropRectangle;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoOverlay withInputLabel(String inputLabel) {
        super.withInputLabel(inputLabel);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoOverlay withStart(Duration start) {
        super.withStart(start);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoOverlay withEnd(Duration end) {
        super.withEnd(end);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoOverlay withFadeInDuration(Duration fadeInDuration) {
        super.withFadeInDuration(fadeInDuration);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoOverlay withFadeOutDuration(Duration fadeOutDuration) {
        super.withFadeOutDuration(fadeOutDuration);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VideoOverlay withAudioGainLevel(Double audioGainLevel) {
        super.withAudioGainLevel(audioGainLevel);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    @Override
    public void validate() {
        if (position() != null) {
            position().validate();
        }
        if (cropRectangle() != null) {
            cropRectangle().validate();
        }
        if (inputLabel() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException("Missing required property inputLabel in model VideoOverlay"));
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(VideoOverlay.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("inputLabel", inputLabel());
        jsonWriter.writeStringField("start", CoreUtils.durationToStringWithDays(start()));
        jsonWriter.writeStringField("end", CoreUtils.durationToStringWithDays(end()));
        jsonWriter.writeStringField("fadeInDuration", CoreUtils.durationToStringWithDays(fadeInDuration()));
        jsonWriter.writeStringField("fadeOutDuration", CoreUtils.durationToStringWithDays(fadeOutDuration()));
        jsonWriter.writeNumberField("audioGainLevel", audioGainLevel());
        jsonWriter.writeStringField("@odata.type", this.odataType);
        jsonWriter.writeJsonField("position", this.position);
        jsonWriter.writeNumberField("opacity", this.opacity);
        jsonWriter.writeJsonField("cropRectangle", this.cropRectangle);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of VideoOverlay from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of VideoOverlay if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the VideoOverlay.
     */
    public static VideoOverlay fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            VideoOverlay deserializedVideoOverlay = new VideoOverlay();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("inputLabel".equals(fieldName)) {
                    deserializedVideoOverlay.withInputLabel(reader.getString());
                } else if ("start".equals(fieldName)) {
                    deserializedVideoOverlay
                        .withStart(reader.getNullable(nonNullReader -> Duration.parse(nonNullReader.getString())));
                } else if ("end".equals(fieldName)) {
                    deserializedVideoOverlay
                        .withEnd(reader.getNullable(nonNullReader -> Duration.parse(nonNullReader.getString())));
                } else if ("fadeInDuration".equals(fieldName)) {
                    deserializedVideoOverlay.withFadeInDuration(
                        reader.getNullable(nonNullReader -> Duration.parse(nonNullReader.getString())));
                } else if ("fadeOutDuration".equals(fieldName)) {
                    deserializedVideoOverlay.withFadeOutDuration(
                        reader.getNullable(nonNullReader -> Duration.parse(nonNullReader.getString())));
                } else if ("audioGainLevel".equals(fieldName)) {
                    deserializedVideoOverlay.withAudioGainLevel(reader.getNullable(JsonReader::getDouble));
                } else if ("@odata.type".equals(fieldName)) {
                    deserializedVideoOverlay.odataType = reader.getString();
                } else if ("position".equals(fieldName)) {
                    deserializedVideoOverlay.position = Rectangle.fromJson(reader);
                } else if ("opacity".equals(fieldName)) {
                    deserializedVideoOverlay.opacity = reader.getNullable(JsonReader::getDouble);
                } else if ("cropRectangle".equals(fieldName)) {
                    deserializedVideoOverlay.cropRectangle = Rectangle.fromJson(reader);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedVideoOverlay;
        });
    }
}
