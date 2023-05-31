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

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent a map.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapData implements ComposableElement {

    /**
     * Optional image identifier for a thumbnail image, which will be mapped to an image for embedding.
     * @see com.hp.autonomy.frontend.reports.powerpoint.ImageSource
     */
    private String image;

    /**
     * Array of markers.
     */
    private Marker[] markers;

    /**
     * Array of polygons
     */
    private Polygon[] polygons;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Marker {

        /** x-coordinate of the marker, represented as a fraction between 0 and 1. */
        private double x;

        /** y-coordinate of the marker, represented as a fraction between 0 and 1. */
        private double y;

        /** optional text description of the marker, will be used as a on-hover tooltip in PowerPoint when presenting on non-cluster markers. */
        private String text;

        /** flag indicating if the marker is a cluster marker or normal cluster (which are rendered differently). */
        private boolean cluster;

        /** colour of the marker, expressed as a hexadecimal string e.g. '#FF0000'. */
        private String color;

        /** colour of the marker text, expressed as a hexadecimal string e.g. '#FF0000'. */
        private String fontColor;

        /** whether a cluster marker is faded.  */
        private boolean fade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Polygon {

        /**
         * List of lists of (x,y) points, represented as fractions between 0 and 1.
         * The points should follow even-odd winding order, see the WKT spec for details.
         * */
        private double[][] points;

        /** optional text description of the marker, will be used as a on-hover tooltip in PowerPoint when presenting on non-cluster markers. */
        private String text;

        /** colour of the marker, expressed as a hexadecimal string e.g. '#FF0000'. */
        private String color;

    }
}

