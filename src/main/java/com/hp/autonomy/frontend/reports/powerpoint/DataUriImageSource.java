/*
 * Copyright 2017-2018 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */
package com.hp.autonomy.frontend.reports.powerpoint;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.sl.usermodel.PictureData;

/**
 * Default implementation of ImageSource; treats all image identifiers as base64-encoded data URLs.
 * If the identifier is missing a data: prefix, it'll be assumed to be JPEG.
 */
public class DataUriImageSource implements ImageSource {

    private static final String DATA_PREFIX = "data:";

    /**
     * Accepts base64-encoded image data and converts them to image data.
     * @param imageId the image identifier.
     * @return image data corresponding to the image.
     * @throws IllegalArgumentException if we can't fetch the image.
     */
    @Override
    public ImageData getImageData(final String imageId) throws IllegalArgumentException {
        // assume it's base64 data
        final String contentType, data;

        if(imageId.startsWith(DATA_PREFIX)) {
            // e.g. 'data:image/png;base64,iVBOR....'
            contentType = imageId.substring(DATA_PREFIX.length(), imageId.indexOf(';'));
            data = imageId.substring(imageId.indexOf(","));
        }
        else {
            // assume it's the data part of the base64-encoded JPEG, since Find sends list thumbnails this way
            contentType = "image/jpeg";
            data = imageId;
        }

        for(final PictureData.PictureType pictureType : PictureData.PictureType.values()) {
            if(pictureType.contentType.equalsIgnoreCase(contentType)) {
                return new ImageData(pictureType, Base64.decodeBase64(data));
            }
        }

        throw new IllegalArgumentException("Unable to parse data URI: " + imageId);
    }
}
