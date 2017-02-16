/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicMapData implements ComposableElement {

    private Path[] paths;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Path {
        public String name;
        public String color;
        public String color2;
        public double opacity;
        public ArrayList<double[]> points;
    }
}

