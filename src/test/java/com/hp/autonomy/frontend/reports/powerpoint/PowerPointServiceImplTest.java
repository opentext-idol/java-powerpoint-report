/*
 * Copyright 2017 Hewlett-Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint;

import com.hp.autonomy.frontend.reports.powerpoint.dto.DategraphData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.ReportData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.SunburstData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static java.io.File.createTempFile;

@RunWith(MockitoJUnitRunner.class)
public class PowerPointServiceImplTest {

    private PowerPointServiceImpl pptxService;

    @Before
    public void before() {
        pptxService = new PowerPointServiceImpl();
    }

    private void testWrite(final XMLSlideShow pptx) throws IOException {
        final File temp = createTempFile("temp", ".pptx");
        pptx.write(new FileOutputStream(temp));
    }

    @Test
    public void testDateGraph() throws SlideShowTemplate.LoadException, IOException {
        final DategraphData data = createDategraphData();

        final XMLSlideShow pptx = pptxService.graph(data);
        testWrite(pptx);
    }

    @Test
    public void testSunburst() throws SlideShowTemplate.LoadException, IOException {
        final SunburstData sunburst = createSunburstData();

        final XMLSlideShow pptx = pptxService.sunburst(sunburst);
        testWrite(pptx);
    }

    @Test
    public void testReport() throws SlideShowTemplate.LoadException, IOException {
        final DategraphData dategraph = createDategraphData();

        final SunburstData sunburst = createSunburstData();

        final SunburstData bottomRightSunburst = new SunburstData(
                new String[] { "Cyan", "Magenta", "Yellow", "Black"},
                new double[] { 0.994, 0, 0.231, 0.337 },
                "CMYK Colours"
        );

        final String titleFont = "Times New Roman";
        final double titleFontSize = 12;
        final double titleMargin = 5;
        final double widgetMargins = 3;
        final ReportData report = new ReportData(new ReportData.Child[] {
                new ReportData.Child(0, 0, 0.5, 1, "Left Dategraph", widgetMargins, titleMargin, titleFontSize, titleFont, dategraph),
                new ReportData.Child(0.5, 0, 0.5, 0.5, "Top Right Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, sunburst),
                new ReportData.Child(0.5, 0.5, 0.5, 0.5, "Bottom Right Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, bottomRightSunburst),
        });

        final XMLSlideShow pptx = pptxService.report(report);
        testWrite(pptx);
    }

    private static DategraphData createDategraphData() {
        return new DategraphData(
                new long[]{
                        1480690162, 1482394810, 1484099459, 1485804108
                },
                Arrays.asList(
                        new DategraphData.Row("#FF0000", "Red Line", false, new double[]{
                                87, 87, 124, 49
                        }), new DategraphData.Row("#00FF00", "Green Line", true, new double[]{
                                12, 53, 63, 72
                        })
                )
        );
    }

    private static SunburstData createSunburstData() {
        return new SunburstData(
                new String[] { "Red", "Green", "Blue"},
                new double[] { 1, 169, 130 },
                "RGB Colours"
            );
    }

}
