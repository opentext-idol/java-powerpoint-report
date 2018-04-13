/*
 * Copyright 2017-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint;

/**
 * An interface to allow specifying service-wide settings.
 */
public interface TemplateSettingsSource {
    /**
     * This should return a TemplateSettings object containing any custom settings you wish to specify.
     * @return a settings object.
     */
    TemplateSettings getSettings();

    /**
     * Default implementation which returns default settings (i.e. the visualizations will be rendered to fill the slide).
     */
    TemplateSettingsSource DEFAULT = TemplateSettings::new;
}
