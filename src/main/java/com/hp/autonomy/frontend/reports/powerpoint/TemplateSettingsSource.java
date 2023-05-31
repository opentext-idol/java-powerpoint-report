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
