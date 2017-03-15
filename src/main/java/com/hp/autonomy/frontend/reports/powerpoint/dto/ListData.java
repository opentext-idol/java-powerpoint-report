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
@AllArgsConstructor
public class ListData implements ComposableElement {

    private Document[] docs;

    /** Whether we should draw the document icon. */
    private boolean drawIcons;

    public ListData(final Document[] docs) {
        this(docs, true);
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
         * Optional base64-encoded JPEG/PNG thumbnail. It can either start with a data URL e.g. 'data:image/jpeg;base64,'
         * or if the data: schema is not provided, it's assumed to be a JPEG data URL.
         */
        private String thumbnail;
    }
}