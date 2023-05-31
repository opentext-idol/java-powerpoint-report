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

/**
 * An interface to convert image identifiers into actual image data for embedding into PowerPoint.
 *
 * The default implementation (DataUriImageSource) only handles data URIs, but if you have a more compact representation
 *   to uniquely identify your images, you could provide your own custom ImageSource class to convert your compact image
 *   identifiers into actual image data; which would save some round-trip data.
 *
 * If you have a mix of HTTP/HTTPs links and data uris, you can use WebAndDataUriImageSource. You may want to override
 *   WebAndDataUriImageSource.allowHttpURI which only allows URL paths ending in '.jpeg', '.jpg', '.png' or '.gif'
 *   with your own whitelist.
 *
 *  @see DataUriImageSource
 *  @see WebAndDataUriImageSource
 */
public interface ImageSource {

    /**
     * Converts an image identifier into image data.
     * @param imageId the image identifier.
     * @return image data corresponding to the image.
     * @throws IllegalArgumentException if we can't fetch the image.
     */
    ImageData getImageData(final String imageId) throws IllegalArgumentException;

    ImageSource DEFAULT = new DataUriImageSource();

}
