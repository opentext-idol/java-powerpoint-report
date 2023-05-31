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

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to represent time-series data to plot on a xy scatterplot chart with time on the x-axis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DategraphData implements ComposableElement {

    /** A list of timestamps as Unix epoch seconds. */
    private long[] timestamps;

    /* One of more row data series with values corresponding with the timestamps. */
    private List<Row> rows;

    public boolean validateInput() {
        final int length = this.timestamps.length;

        for(Row row : rows) {
            if (row.getValues().length != length) {
                return false;
            }
        }

        return length > 1 && !rows.isEmpty();
    }

    /**
     * A DTO representing a data series in a date chart.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Row {

        /** The colour to render the series data, should be a hexadecimal string e.g. #FF0000 */
        private String color;

        /** The label for the series data. */
        private String label;

        /** Whether the row is rendered on the primary or secondary y-axis. */
        private boolean secondaryAxis;

        /** List of values; should be the same length as the timestamps. */
        private double[] values;
    }
}
