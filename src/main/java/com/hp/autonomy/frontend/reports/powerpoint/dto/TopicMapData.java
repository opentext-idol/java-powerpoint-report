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

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent topic map data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicMapData implements ComposableElement {

    /** An array of polygons which make up the topic map. These are drawn in order, which is important due to transparency. */
    private Path[] paths;

    /**
     * A DTO representing an individual polygon on the topic map.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Path {
        /** The name of the polygon, which will be used to label it in the UI. */
        public String name;

        /** The starting gradient colour. */
        public String color;

        /** The finishing gradient colour. */
        public String color2;

        /** The opacity of the polygon, ranging from 0 to 1. */
        public double opacity;

        /** The points in the polygon, each of which should be a [x, y] array. */
        public ArrayList<double[]> points;

        /** How far from the leaf nodes, 0 for leaf nodes. */
        public int level;
    }
}

