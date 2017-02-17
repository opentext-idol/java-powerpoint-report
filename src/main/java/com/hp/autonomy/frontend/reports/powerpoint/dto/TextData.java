/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent text data to use in reports.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextData implements ComposableElement {

    /**
     * Array of individual text elements. There isn't an implicit newline after each block of text, to allow you to
     * put differently-formatted text elements adjacent to each other.
     */
    private Paragraph[] text;

    /**
     * DTO representing a single block of text with specific formatting.
     */
    @Data
    @NoArgsConstructor
    public static class Paragraph {

        /** Whether to render the text in bold. */
        private boolean bold;

        /** Whether to render the text in italic. */
        private boolean italic;

        /** The text to write. You can insert '\n' newlines for newlines. There isn't an implicit newline at the end. */
        private String text = "\n";

        /** The font colour. */
        private String color = "#000000";

        /** The font size. */
        private double fontSize = 12;

        public Paragraph(final boolean bold, final boolean italic, final String text, final String color, final double fontSize) {
            this.bold = bold;
            this.italic = italic;
            this.text = text;
            this.color = color;
            this.fontSize = fontSize;
        }
    }

}