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
