/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListData implements ComposableElement {

    private Document[] docs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String title;
        private String date;
        private String ref;
        private String summary;
        private String thumbnail;
    }
}