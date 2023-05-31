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

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anchor points to use when drawing a report. All units should be fractional between 0 and 1,
 * e.g. x=0.1 means start drawing 10% from the left edge of the available space, and
 *      w=0.5 means we only draw on half of the available width.
 */
@Data
@NoArgsConstructor
public class Anchor {

    public Anchor(final double x, final double y, final double width, final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** The left margin of the visualization, expressed as fraction between 0 and 1 of the available space. */
    private double x;

    /** The top margin of the visualization, expressed as fraction between 0 and 1 of the available space. */
    private double y;

    /** The width of the visualization, expressed as fraction between 0 and 1 of the available space. */
    private double width = 1;

    /** The height of the visualization, expressed as fraction between 0 and 1 of the available space. */
    private double height = 1;

}
