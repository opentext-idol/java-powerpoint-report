/*
 * Copyright 2017 Hewlett Packard Enterprise Development, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO representing a composite report consisting of one or more children.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {

    /** An array of child visualizations to render. */
    private Child[] children;

    /**
     * A DTO representing an individual visualization to render in a report, with bounds and an optional title.
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class Child extends Anchor {
        /** Optional title. */
        private String title;

        /** Optional margin, can be set to zero if you don't want space around the visualizations. */
        private double margin = 3;

        /** Optional text margin, will be used to vertically separate the title from the visualization if the title is specified. */
        private double textMargin = 5;

        /** Font size for the optional title. */
        private double fontSize = 12;

        /** Font family for the optional title.  */
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

        /** Data to use describing the child visualization. */
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
