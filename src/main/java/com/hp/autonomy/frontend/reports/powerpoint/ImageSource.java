/*
 * Copyright 2017 Hewlett Packard Enterprise Development, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.reports.powerpoint;

/**
 * An interface to convert image identifiers into actual image data for embedding into PowerPoint.
 *
 * The default implementation (DefaultImageSource) fetches images and parses data URLs directly, but if you have a
 *   more compact representation to uniquely identify your images, you could provide your own custom ImageSource class
 *   to convert your compact image identifiers into actual image data.
 *
 * You might also want to override the DefaultImageSource's default whitelist, which only allows URLs with a path which
 *   ends with '.jpeg', '.jpg', '.png' or '.gif'.
 *
 *  @see com.hp.autonomy.frontend.reports.powerpoint.DefaultImageSource
 */
public interface ImageSource {

    /**
     * Converts an image identifier into image data.
     * @param imageId the image identifier.
     * @return image data corresponding to the image.
     * @throws IllegalArgumentException if we can't fetch the image.
     */
    ImageData getImageData(final String imageId) throws IllegalArgumentException;

    ImageSource DEFAULT = new DefaultImageSource();

}