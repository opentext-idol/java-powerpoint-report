/*
 * Copyright 2017 Hewlett Packard Enterprise Development, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
