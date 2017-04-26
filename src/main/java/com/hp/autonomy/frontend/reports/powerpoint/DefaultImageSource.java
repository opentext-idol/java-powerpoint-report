/*
 * Copyright 2017 Hewlett Packard Enterprise Development, L.P.
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
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;

/**
 * Default implementation of ImageSource; fetches http: and https: URLs from the internet for embedding and treats all
 *   other image identifiers as base64-encoded data URLs.
 * If the identifier is missing a data: prefix, it'll be assumed to be JPEG.
 * To mitigate attempts to use this service as an open proxy to attack intranet sites, we require that web URLs have a
 *   path which end in '.jpeg', '.jpg', '.png' or '.gif'. You may want to override allowHttpURI to customize
 *   this if your service has better knowledge of which URIs are safe to visit.
 */
public class DefaultImageSource implements ImageSource {

    private static final String DATA_PREFIX = "data:";

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
        }
        else {
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
        }

        throw new IllegalArgumentException("Selected image cannot be fetched: " + imageId);
    }

    /**
     * Controls whether we should allow this URL to be downloaded.
     * @param uri the URI to check.
     * @return true if we should allow downloading it.
     */
    public boolean allowHttpURI(final URI uri) {
        final String path = uri.getPath().toLowerCase(Locale.US);
        return path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif");
    }
}
