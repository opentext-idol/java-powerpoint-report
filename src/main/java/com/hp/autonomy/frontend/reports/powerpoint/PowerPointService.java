/*
 * Copyright 2017 Hewlett Packard Enterprise Development, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint;

import com.hp.autonomy.frontend.reports.powerpoint.dto.DategraphData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.ListData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.MapData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.ReportData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.SunburstData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.TableData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.TopicMapData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

/**
 * Primary interface to the PowerPoint API.
 * @see <a href="https://github.com/hpe-idol/java-powerpoint-report/" target="_blank">README.md</a> for examples and usage instructions.
 */
public interface PowerPointService {

    /**
     * Validates the template used to see if it has the correct slides.
     * @throws TemplateLoadException if the template is invalid.
     */
    void validateTemplate() throws TemplateLoadException;

    /**
     * Renders a topic map as a PowerPoint presentation.
     * @param topicmap the data representing the topic map paths.
     * @return a PowerPoint presentation with a single slide.
     * @throws TemplateLoadException if the template is invalid.
     */
    XMLSlideShow topicmap(TopicMapData topicmap) throws TemplateLoadException;

    /**
     * Renders a sunburst as a PowerPoint presentation. Only one level of sunburst is supported for now,
     * since it's actually represented by a doughnut chart (based on the first slide in the template).
     * @param sunburst the data representing the sunburst.
     * @return a PowerPoint presentation with a single slide.
     * @throws TemplateLoadException if the template is invalid.
     */
    XMLSlideShow sunburst(SunburstData sunburst) throws TemplateLoadException;

    /**
     * Renders a table as a PowerPoint presentation.
     * @param tableData the data representing the table.
     * @param title an optional title.
     * @return a PowerPoint presentation with a single slide.
     * @throws TemplateLoadException if the template is invalid.
     */
    XMLSlideShow table(TableData tableData, String title) throws TemplateLoadException;

    /**
     * Renders a map as a PowerPoint presentation.
     * @param map the data representing the map.
     * @param title an optional title.
     * @return a PowerPoint presentation with a single slide.
     * @throws TemplateLoadException if the template is invalid.
     */
    XMLSlideShow map(MapData map, String title) throws TemplateLoadException;

    /**
     * Renders a list of documents as a PowerPoint presentation, similar to how they're rendered in Find.
     * We only support thumbnail images if they're provided as base64 strings to embed into the document,
     *   since the POI API doesn't support links to external images.
     * @param documentList the data containing all the documents
     * @param results an optional string which will be shown in the top-left.
     * @param sortBy an optional string which will be shown in the top-right.
     * @return a PowerPoint presentation with the list results paginated over multiple slides.
     * @throws TemplateLoadException if the template is invalid.
     */
    XMLSlideShow list(ListData documentList, String results, String sortBy) throws TemplateLoadException;

    /**
     * Renders a date graph as a PowerPoint presentation.
     * This is drawn based on the line chart (which must be contained in the second slide in the template).
     * @param data the data representing the graph series to plot.
     * @return a PowerPoint presentation with a single slide.
     * @throws TemplateLoadException if the template is invalid.
     */
    XMLSlideShow graph(DategraphData data) throws TemplateLoadException;

    /**
     * Render a composite report consisting of multiple visualizations as a PowerPoint presentation.
     * @param report the data containing a list of visualizations to render.
     * @return a PowerPoint presentation with a single slide.
     * @throws TemplateLoadException if the template is invalid.
     */
    XMLSlideShow report(ReportData report) throws TemplateLoadException;

}
