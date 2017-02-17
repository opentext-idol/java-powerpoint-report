/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent a table of data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableData implements ComposableElement {

    /** Number of rows. */
    private int rows;

    /** Number of columns. */
    private int cols;

    /** Contents of the table in order, there should be {@code rows * cols} cells. List values from the first row, then the second row, and so on. */
    private String[] cells;

    public boolean validateInput() {
        return rows > 0 && cols > 0 && cells != null && cells.length == rows * cols;
    }
}