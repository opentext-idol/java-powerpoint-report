/*
 * Copyright 2017 Hewlett-Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint;

import com.hp.autonomy.frontend.reports.powerpoint.dto.DategraphData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.ListData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.ReportData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.SunburstData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static java.io.File.createTempFile;

@RunWith(MockitoJUnitRunner.class)
public class PowerPointServiceImplTest {

    private final String sampleImage = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAASABIAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAAeADIDASIAAhEBAxEB/8QAGQABAQEBAQEAAAAAAAAAAAAAAAcGBQQI/8QAJRAAAQQCAgICAgMAAAAAAAAAAQIDBAUABgcREiEIEyMxMkFR/8QAGAEBAQEBAQAAAAAAAAAAAAAAAAUDBgT/xAAjEQEAAQQBBAIDAAAAAAAAAAABEQACAwQhBRIxURNBBhax/9oADAMBAAIRAxEAPwD7NxjMtypuUrjzjjZN4hVC7N+jrX5qIqVBPmUJJ7USR0gfyUR78QroE9A9Zra+Tbz2a+Im69LT65WDl4Oa4KtTjIPK+X/HmswqJneIdjW2s+qiWdgwBHAgof8ASVFJf8nArorCWvtWEFJUB3msY5/06ZyY/wAWQa23k2UWUiHIkNIYLTTimg6CWy6JH1+JH5Q0W+/XllnN+LdYwDdfr3doXXT9dtqCj4Tkj2IkiUhqmYybcJcq2/KsTZ5VrqUqkFHsEyoYDpbIcQyso6JS4r8qSk+foJBUAkqAJyk5L39HN0zZu1dgC+3zCP1PkkpTGMZ46Uzl7VrlbuGs22pXAcMC6gv18r6leK/qdbKF+J/o9KPR/wBzqYzTFkvw3mTGxcMj6Tw0qTMfHHW4bsSTA3ndYchqBFrJ0iJZoju2kaMsqYRIW20CCgEoCmvrV4eiT7z2Xvx/1DZN8h79c3N9KfgWTFtGguyGnI7MlkDwLa1Nl9tHYCi0h0Nk/tPvKbjK/wCx9V7/AJPme6EniYYk8egD0AEAFJrJ6JxvUceythfpbO0eY2O1fuXosp1C2Y0h5RW79PSApKVKV2QpSv0Ous1mMZL2dnLuZXNnu7rmJfcEfwpTGMZhSv/Z";

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

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testSunburst() throws SlideShowTemplate.LoadException, IOException {
        final SunburstData sunburst = createSunburstData();

        final XMLSlideShow pptx = pptxService.sunburst(sunburst);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }


    @Test
    public void testListSingle() throws SlideShowTemplate.LoadException, IOException {
        final ListData listData = new ListData(new ListData.Document[]{
            new ListData.Document("title1", "5 months ago", "reference", "summary", null)
        });

        final XMLSlideShow pptx = pptxService.list("Showing 1 to 1 of 1 results", "Sort by Relevance", listData);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testListPagination() throws SlideShowTemplate.LoadException, IOException {
        final ListData listData = new ListData(new ListData.Document[]{
            new ListData.Document("title1", "5 months ago", "reference", "summary", null),
            new ListData.Document("title2", "5 months ago", null, "summary", null),
            new ListData.Document("title3", null, "reference", "summary", null),
            new ListData.Document("title4", "5 months ago", "reference", null, null),
            new ListData.Document("title5", "5 months ago", "reference", "summary", sampleImage),
            new ListData.Document("title6", "5 months ago", "reference", "summary", null),
            new ListData.Document("title7", "5 months ago", "reference", "summary", null),
            new ListData.Document("title8", "5 months ago", "reference", "summary", null),
            new ListData.Document("title9", "5 months ago", "reference", "summary", null),
            new ListData.Document("title10", "5 months ago", "reference", "summary", null)
        });

        final XMLSlideShow pptx = pptxService.list("Showing 1 to 10 of 10 results", "Sort by Relevance", listData);
        testWrite(pptx);

        Assert.assertTrue(pptx.getSlides().size() > 1);
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

        Assert.assertEquals(pptx.getSlides().size(), 1);
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