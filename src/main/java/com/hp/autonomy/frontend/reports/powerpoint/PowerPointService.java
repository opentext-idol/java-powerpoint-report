/*
 * Copyright 2017 Hewlett-Packard Enterprise Development Company, L.P.
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

public interface PowerPointService {

    void validateTemplate() throws SlideShowTemplate.LoadException;

    XMLSlideShow topicmap(TopicMapData data) throws SlideShowTemplate.LoadException;

    XMLSlideShow sunburst(SunburstData data) throws SlideShowTemplate.LoadException;

    XMLSlideShow table(TableData tableData, String title) throws SlideShowTemplate.LoadException;

    XMLSlideShow map(MapData map, String title) throws SlideShowTemplate.LoadException;

    XMLSlideShow list(ListData documentList, String results, String sortBy) throws SlideShowTemplate.LoadException;

    XMLSlideShow graph(DategraphData data) throws SlideShowTemplate.LoadException;

    XMLSlideShow report(ReportData report) throws SlideShowTemplate.LoadException;

}
