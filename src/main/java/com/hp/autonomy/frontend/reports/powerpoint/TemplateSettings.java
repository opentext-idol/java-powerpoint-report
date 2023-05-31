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

package com.hp.autonomy.frontend.reports.powerpoint;

import com.hp.autonomy.frontend.reports.powerpoint.dto.Anchor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Template settings which affect rendering, set on a service-wide level.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateSettings {

    /**
     * Anchor points which control how much of the powerpoint area we'll draw on, useful for reserving
     * space for your own graphics/logos in the master slide.
     */
    private Anchor anchor = new Anchor();
}
