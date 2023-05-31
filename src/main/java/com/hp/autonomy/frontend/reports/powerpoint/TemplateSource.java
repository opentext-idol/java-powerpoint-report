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

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface to get the PowerPoint template.
 */
public interface TemplateSource {
    /**
     * This should return a new {@link InputStream} containing the PowerPoint template we'll use.
     * This method may be called multiple times (typically once per generated PowerPoint).
     * The template has specific requirements, see <a href="https://github.com/opentext-idol/java-powerpoint-report/" target="_blank">README.md</a> for details.
     * @return stream containing the data for the PowerPoint template.
     * @throws IOException if there's an IO error.
     */
    InputStream getInputStream() throws IOException;

    /**
     * This is the default implementation of the TemplateSource, which uses the default template bundled
     * with the project.
     */
    TemplateSource DEFAULT = () -> TemplateSource.class.getResourceAsStream("/com/hp/autonomy/frontend/reports/powerpoint/templates/template.pptx");
}
