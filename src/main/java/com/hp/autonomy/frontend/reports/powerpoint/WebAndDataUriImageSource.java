/*
 * Copyright 2017-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.reports.powerpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;

/**
 * This ImageSource implementation fetches http: and https: URLs from the internet for embedding and treats all other
 *   image identifiers as base64-encoded data URLs.
 * If the identifier is missing a data: prefix, it'll be assumed to be JPEG.
 * To mitigate attempts to use this service as an open proxy to attack intranet sites, we require that web URLs have a
 *   path which end in '.jpeg', '.jpg', '.png' or '.gif'. You may want to override allowHttpURI to customize
 *   this if your service has better knowledge of which URIs are safe to visit.
 */
public class WebAndDataUriImageSource extends DataUriImageSource {

    /**
     * Accepts HTTP/HTTPs URLs or base64-encoded image data and converts them to image data.
     * @param imageId the image identifier.
     * @return image data corresponding to the image.
     * @throws IllegalArgumentException if we can't fetch the image.
     */
    @Override
    public ImageData getImageData(final String imageId) throws IllegalArgumentException {
        if(imageId.startsWith("https:") || imageId.startsWith("http:")) {
            try {
                final URI uri = new URI(imageId);

                if(allowHttpURI(uri)) {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    final HttpURLConnection conn = (HttpURLConnection) new URL(imageId).openConnection();
                    conn.connect();
                    final String contentType = conn.getContentType();

                    for(final PictureData.PictureType pictureType : PictureData.PictureType.values()) {
                        if(pictureType.contentType.equalsIgnoreCase(contentType)) {
                            try(final InputStream input = conn.getInputStream()) {
                                IOUtils.copy(input, baos);
                                return new ImageData(pictureType, baos.toByteArray());
                            }
                        }
                    }

                    throw new IllegalArgumentException("Selected image URI uses an unsupported content type: " + imageId);
                }
            }
            catch(URISyntaxException | IOException e) {
                throw new IllegalArgumentException("Selected image URI cannot be fetched: " + imageId, e);
            }

            throw new IllegalArgumentException("Selected image cannot be fetched: " + imageId);
        }
        else {
            return super.getImageData(imageId);
        }
    }

    /**
     * Controls whether we should allow a URL to be downloaded.
     * @param uri the URI to check.
     * @return true if we should allow downloading it.
     */
    public boolean allowHttpURI(final URI uri) {
        final String path = uri.getPath().toLowerCase(Locale.US);
        return path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif");
    }
}
