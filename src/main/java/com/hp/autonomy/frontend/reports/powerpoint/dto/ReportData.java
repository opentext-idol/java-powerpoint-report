/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {

    private Child[] children;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class Child extends Anchor {
        private String title;

        private double margin = 3;
        private double textMargin = 5;
        private double fontSize = 12;
        private String fontFamily = "Metric-Light";

        public Child(final double x, final double y, final double width, final double height, final String title, final double margin, final double textMargin, final double fontSize, final String fontFamily, final ComposableElement data) {
            super(x, y, width, height);
            this.title = title;
            this.margin = margin;
            this.textMargin = textMargin;
            this.fontSize = fontSize;
            this.fontFamily = fontFamily;
            this.data = data;
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(name = "dategraph", value = DategraphData.class),
                @JsonSubTypes.Type(name = "list", value = ListData.class),
                @JsonSubTypes.Type(name = "map", value = MapData.class),
                @JsonSubTypes.Type(name = "sunburst", value = SunburstData.class),
                @JsonSubTypes.Type(name = "table", value = TableData.class),
                @JsonSubTypes.Type(name = "text", value = TextData.class),
                @JsonSubTypes.Type(name = "topicmap", value = TopicMapData.class)
        })
        private ComposableElement data;
    }

}
