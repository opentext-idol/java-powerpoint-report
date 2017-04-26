/*
 * Copyright 2017 Hewlett Packard Enterprise Development, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent a list of documents.
 */
@Data
@NoArgsConstructor
public class ListData implements ComposableElement {

    private Document[] docs;

    /** Whether we should draw the document icon. */
    private boolean drawIcons;

    /** Font size for document titles. */
    private double titleFontSize = 10;

    /** Font size for document date. */
    private double dateFontSize = 8;

    /** Font size for document reference. */
    private double refFontSize = titleFontSize;

    /** Font size for document summary text. */
    private double summaryFontSize = 9;

    public ListData(final Document[] docs) {
        this();
        this.setDocs(docs);
    }

    public ListData(final Document[] docs, final boolean drawIcons, final double titleFontSize, final double dateFontSize, final double refFontSize, final double summaryFontSize) {
        this.docs = docs;
        this.drawIcons = drawIcons;
        this.titleFontSize = titleFontSize;
        this.dateFontSize = dateFontSize;
        this.refFontSize = refFontSize;
        this.summaryFontSize = summaryFontSize;
    }

    /**
     * DTO for a single document.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {

        /** Optional title. */
        private String title;

        /** Optional date string. */
        private String date;

        /** Optional reference string. */
        private String ref;

        /** Optional summary. */
        private String summary;

        /**
         * Optional image identifier for a thumbnail image, which will be mapped to an image for embedding.
         * @see com.hp.autonomy.frontend.reports.powerpoint.ImageSource
         */
        private String thumbnail;
    }
}