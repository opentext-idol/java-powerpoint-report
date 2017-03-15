/*
 * Copyright 2017 Hewlett Packard Enterprise Development, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.reports.powerpoint.dto.Anchor;
import com.hp.autonomy.frontend.reports.powerpoint.dto.DategraphData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.ListData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.MapData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.ReportData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.SunburstData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.TableData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.TextData;
import com.hp.autonomy.frontend.reports.powerpoint.dto.TopicMapData;
import java.io.File;
import java.io.FileInputStream;
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

    private static final String sampleJPEGWithoutHeader = "/9j/4AAQSkZJRgABAQAASABIAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAAeADIDASIAAhEBAxEB/8QAGQABAQEBAQEAAAAAAAAAAAAAAAcGBQQI/8QAJRAAAQQCAgICAgMAAAAAAAAAAQIDBAUABgcREiEIEyMxMkFR/8QAGAEBAQEBAQAAAAAAAAAAAAAAAAUDBgT/xAAjEQEAAQQBBAIDAAAAAAAAAAABEQACAwQhBRIxURNBBhax/9oADAMBAAIRAxEAPwD7NxjMtypuUrjzjjZN4hVC7N+jrX5qIqVBPmUJJ7USR0gfyUR78QroE9A9Zra+Tbz2a+Im69LT65WDl4Oa4KtTjIPK+X/HmswqJneIdjW2s+qiWdgwBHAgof8ASVFJf8nArorCWvtWEFJUB3msY5/06ZyY/wAWQa23k2UWUiHIkNIYLTTimg6CWy6JH1+JH5Q0W+/XllnN+LdYwDdfr3doXXT9dtqCj4Tkj2IkiUhqmYybcJcq2/KsTZ5VrqUqkFHsEyoYDpbIcQyso6JS4r8qSk+foJBUAkqAJyk5L39HN0zZu1dgC+3zCP1PkkpTGMZ46Uzl7VrlbuGs22pXAcMC6gv18r6leK/qdbKF+J/o9KPR/wBzqYzTFkvw3mTGxcMj6Tw0qTMfHHW4bsSTA3ndYchqBFrJ0iJZoju2kaMsqYRIW20CCgEoCmvrV4eiT7z2Xvx/1DZN8h79c3N9KfgWTFtGguyGnI7MlkDwLa1Nl9tHYCi0h0Nk/tPvKbjK/wCx9V7/AJPme6EniYYk8egD0AEAFJrJ6JxvUceythfpbO0eY2O1fuXosp1C2Y0h5RW79PSApKVKV2QpSv0Ous1mMZL2dnLuZXNnu7rmJfcEfwpTGMZhSv/Z";
    private static final String sampleJPEGImage = "data:image/jpeg;base64," + sampleJPEGWithoutHeader;
    private static final String samplePNGImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAAAFCAIAAAAVLyF7AAAAPUlEQVQI14WNSQoAMAgDo/b/Lxamh9JiF+gcZJJDlCbA1z0zawXcvq5HhApmVr1GSa6d55NFO/IYA47VQQfmMSztMBTTBAAAAABJRU5ErkJggg==";

    private PowerPointService pptxService;

    @Before
    public void before() {
        pptxService = new PowerPointServiceImpl(
            TemplateSource.DEFAULT,
            // Keep 1% of left and right margin free, and 2% of top and bottom margin free
            () -> new TemplateSettings(new Anchor(0.01, 0.02, 0.98, 0.96))
        );
    }

    private void testWrite(final XMLSlideShow pptx) throws IOException {
        final File temp = createTempFile("temp", ".pptx");

        if (!Boolean.valueOf(System.getProperty("keep.powerpoint.output", "false"))) {
            temp.deleteOnExit();
        }

        pptx.write(new FileOutputStream(temp));
    }

    @Test
    public void testValidateCorrectTemplate() throws TemplateLoadException, IOException {
        testResourceAsTemplate("validTemplateWithLogo.pptx");
    }

    @Test(expected = TemplateLoadException.class)
    public void testValidateBlankFile() throws TemplateLoadException, IOException {
        // Testing an empty file should not work.
        final File blankFile = createTempFile("blank", ".pptx");
        blankFile.deleteOnExit();

        new PowerPointServiceImpl(
            () -> new FileInputStream(blankFile),
            TemplateSettingsSource.DEFAULT
        ).validateTemplate();
    }

    @Test(expected = TemplateLoadException.class)
    public void testValidateImageFile() throws TemplateLoadException, IOException {
        // This isn't a template at all.
        testResourceAsTemplate("invalidTemplate.jpg");
    }

    @Test(expected = TemplateLoadException.class)
    public void testValidateWordFile() throws TemplateLoadException, IOException {
        // You actually get a different error from the POI for this, which is why we explicitly test it.
        testResourceAsTemplate("invalidTemplate.docx");
    }

    @Test(expected = TemplateLoadException.class)
    public void testValidateZipFile() throws TemplateLoadException, IOException {
        // PowerPoint files are internally zip files, so it's plausible that we'd need to treat this specially.
        testResourceAsTemplate("invalidTemplate.zip");
    }

    @Test(expected = TemplateLoadException.class)
    public void testValidateWrongSlideCountPowerPointFile() throws TemplateLoadException, IOException {
        // This template is a valid PowerPoint file with all the right components, but has the wrong number of slides.
        testResourceAsTemplate("invalidTemplate.pptx");
    }

    @Test(expected = TemplateLoadException.class)
    public void testValidateWrongComponentsPowerPointFile() throws TemplateLoadException, IOException {
        // This template is a valid PowerPoint file, but has a textbox instead of a line chart on the 2nd slide.
        testResourceAsTemplate("templateMissingComponents.pptx");
    }

    private static void testResourceAsTemplate(final String resource) throws TemplateLoadException {
        new PowerPointServiceImpl(
                () -> PowerPointServiceImplTest.class.getResourceAsStream(resource),
                TemplateSettingsSource.DEFAULT
        ).validateTemplate();
    }

    @Test
    public void testDateGraphTwoAxes() throws TemplateLoadException, IOException {
        final DategraphData data = createTwoAxisDategraphData();

        final XMLSlideShow pptx = pptxService.graph(data);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testDateGraphSingleAxis() throws TemplateLoadException, IOException {
        final DategraphData data = createSingleAxisDategraphData();

        final XMLSlideShow pptx = pptxService.graph(data);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testDateGraphMultipleSeries() throws TemplateLoadException, IOException {
        final DategraphData data = createTwoAxisMultipleSeriesDategraphData();

        final XMLSlideShow pptx = pptxService.graph(data);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testSunburst() throws TemplateLoadException, IOException {
        final SunburstData sunburst = createSunburstData();

        final XMLSlideShow pptx = pptxService.sunburst(sunburst);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testColoredFillSunburst() throws TemplateLoadException, IOException {
        final SunburstData sunburst = createSunburstData();
        sunburst.setColors(new String[]{ "#FF0000", "#00FF00", "#0000FF"});

        final XMLSlideShow pptx = pptxService.sunburst(sunburst);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testColoredStrokeSunburst() throws TemplateLoadException, IOException {
        final SunburstData sunburst = createSunburstData();
        sunburst.setStrokeColors(new String[]{ "#FFFF00", "#FF00FF", "#00FFFF"});

        final XMLSlideShow pptx = pptxService.sunburst(sunburst);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testColoredFillAndStrokeSunburst() throws TemplateLoadException, IOException {
        final SunburstData sunburst = createSunburstData();
        sunburst.setColors(new String[]{ "#FF0000", "#00FF00", "#0000FF"});
        sunburst.setStrokeColors(new String[]{ "#FFFF00", "#FF00FF", "#00FFFF"});

        final XMLSlideShow pptx = pptxService.sunburst(sunburst);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }


    @Test
    public void testListSingle() throws TemplateLoadException, IOException {
        final ListData listData = new ListData(new ListData.Document[]{
            new ListData.Document("title1", "5 months ago", "reference", "summary", sampleJPEGWithoutHeader)
        });

        final XMLSlideShow pptx = pptxService.list(listData, "Showing 1 to 1 of 1 results", "Sort by Relevance");
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testListPagination() throws TemplateLoadException, IOException {
        final ListData listData = createListData();

        final XMLSlideShow pptx = pptxService.list(listData, "Showing 1 to 10 of 10 results", "Sort by Relevance");
        testWrite(pptx);

        Assert.assertTrue(pptx.getSlides().size() > 1);
    }

    @Test
    public void testListPaginationNoIcons() throws TemplateLoadException, IOException {
        final ListData listData = createListData();
        listData.setDrawIcons(false);

        final XMLSlideShow pptx = pptxService.list(listData, "Showing 1 to 10 of 10 results", "Sort by Relevance");
        testWrite(pptx);

        Assert.assertTrue(pptx.getSlides().size() > 1);
    }

    @Test
    public void testListWithoutHeaders() throws TemplateLoadException, IOException {
        final ListData listData = createListData();

        final XMLSlideShow pptx = pptxService.list(listData, null, null);
        testWrite(pptx);

        Assert.assertTrue(pptx.getSlides().size() > 1);
    }

    private static ListData createListData() {
        return new ListData(new ListData.Document[]{
                new ListData.Document("title1", "5 months ago", "reference", "summary", null),
                new ListData.Document("title2", "5 months ago", null, "summary", null),
                new ListData.Document("title3", null, "reference", "summary", null),
                new ListData.Document("title4", "5 months ago", "reference", null, null),
                new ListData.Document("title5", "5 months ago", "reference", "summary", sampleJPEGImage),
                new ListData.Document("title6", "5 months ago", "reference", "summary", sampleJPEGImage),
                new ListData.Document("title7", "5 months ago", "reference", "summary", sampleJPEGImage),
                new ListData.Document("title8", "5 months ago", "reference", "summary", sampleJPEGImage),
                new ListData.Document("title9", "5 months ago", "reference", "summary", sampleJPEGImage),
                new ListData.Document("title10", "5 months ago", "reference", "summary", samplePNGImage)
            });
    }

    @Test
    public void testTable() throws TemplateLoadException, IOException {
        final TableData tableData = createTableData();

        final XMLSlideShow pptx = pptxService.table(tableData, "Animals");
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testTableWithoutTitle() throws TemplateLoadException, IOException {
        final TableData tableData = createTableData();

        final XMLSlideShow pptx = pptxService.table(tableData, null);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    private static TableData createTableData() {
        return new TableData(4, 2, new String[]{
                "Animal", "Count",
                "Cat", "1",
                "Dog", "1",
                "Mouse", "1",
                "Fish", "1"
            });
    }

    @Test
    public void testTopicMap() throws TemplateLoadException, IOException {
        final TopicMapData topicMap = createTopicMapData();

        final XMLSlideShow pptx = pptxService.topicmap(topicMap);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    private static TopicMapData createTopicMapData() throws IOException {
        return new ObjectMapper().readValue(PowerPointServiceImplTest.class.getResource("topicmap.json"), TopicMapData.class);
    }

    @Test
    public void testMap() throws TemplateLoadException, IOException {
        final MapData map = createMapData();

        final XMLSlideShow pptx = pptxService.map(map, "Test Map");
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testMapWithoutTitle() throws TemplateLoadException, IOException {
        final MapData map = createMapData();

        final XMLSlideShow pptx = pptxService.map(map, null);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    private static MapData createMapData() throws IOException {
        return new ObjectMapper().readValue(PowerPointServiceImplTest.class.getResource("map.json"), MapData.class);
    }

    @Test
    public void testReport() throws TemplateLoadException, IOException {
        final DategraphData dategraph = createTwoAxisDategraphData();

        final SunburstData sunburst = createSunburstData();

        final SunburstData bottomRightSunburst = createAlternativeSunburstData();

        final String titleFont = "Times New Roman";
        final double titleFontSize = 12;
        final double titleMargin = 5;
        final double widgetMargins = 3;
        final ReportData report = new ReportData(new ReportData.Child[] {
                new ReportData.Child(0, 0, 0.5, 1, "Left Dategraph", widgetMargins, titleMargin, titleFontSize, titleFont, dategraph),
                new ReportData.Child(0.5, 0, 0.5, 0.5, "Top Right Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, sunburst),
                new ReportData.Child(0.5, 0.5, 0.5, 0.5, "Bottom Right Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, bottomRightSunburst),
        });

        final XMLSlideShow pptx = pptxService.report(report, false);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testLinearReport() throws TemplateLoadException, IOException {
        final DategraphData dategraph = createTwoAxisDategraphData();

        final SunburstData sunburst = createSunburstData();

        final SunburstData bottomRightSunburst = createAlternativeSunburstData();

        final String titleFont = "Times New Roman";
        final double titleFontSize = 12;
        final double titleMargin = 5;
        final double widgetMargins = 3;
        final ReportData report = new ReportData(new ReportData.Child[] {
                new ReportData.Child(0, 0, 1, 1, "Slide#1 Dategraph", widgetMargins, titleMargin, titleFontSize, titleFont, dategraph),
                new ReportData.Child(0, 0, 1, 1, "Slide#2 Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, sunburst),
                new ReportData.Child(0, 0, 1, 1, "Slide#3 Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, bottomRightSunburst),
        });

        final XMLSlideShow pptx = pptxService.report(report, true);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 3);
    }

    @Test
    public void testComplicatedReport() throws TemplateLoadException, IOException {
        final ReportData report = createComplicatedReport(3);

        final XMLSlideShow pptx = pptxService.report(report, false);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testComplicatedReportWithoutWidgetMargins() throws TemplateLoadException, IOException {
        final ReportData report = createComplicatedReport(0);

        final XMLSlideShow pptx = pptxService.report(report, false);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testComplicatedReportWithoutTextOrMargins() throws TemplateLoadException, IOException {
        final ReportData report = createComplicatedReport(0);

        for(ReportData.Child child : report.getChildren()) {
            child.setTitle(null);
        }

        final XMLSlideShow pptx = pptxService.report(report, false);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void deserializeDefaultsTest() throws IOException {
        // Jackson loses the defaults if you have both @NoArgsConstructor and @AllArgsConstructor, even if you
        //   tag @JsonCreator on the @NoArgsConstructor. So defaults like a non-zero fontsize were being lost.
        final ObjectMapper objectMapper = new ObjectMapper();
        final ReportData.Child child = objectMapper.readValue("{}", ReportData.Child.class);

        Assert.assertNotEquals(0, child.getFontSize(), Double.MIN_VALUE);

        final Anchor anchor = objectMapper.readValue("{}", Anchor.class);
        Assert.assertEquals(1, anchor.getWidth(), Double.MIN_VALUE);

        final TextData.Paragraph paragraph = objectMapper.readValue("{}", TextData.Paragraph.class);
        Assert.assertNotEquals(0, paragraph.getFontSize(), Double.MIN_VALUE);

        final ListData listData = objectMapper.readValue("{}", ListData.class);
        Assert.assertNotEquals(0, listData.getDateFontSize(), Double.MIN_VALUE);
        Assert.assertNotEquals(0, listData.getRefFontSize(), Double.MIN_VALUE);
        Assert.assertNotEquals(0, listData.getSummaryFontSize(), Double.MIN_VALUE);
        Assert.assertNotEquals(0, listData.getTitleFontSize(), Double.MIN_VALUE);

        final ListData overriddenListData = objectMapper.readValue("{ \"titleFontSize\": 40 }", ListData.class);
        Assert.assertEquals(40, overriddenListData.getTitleFontSize(), Double.MIN_VALUE);
    }

    @Test
    public void testReportByDeserialization() throws TemplateLoadException, IOException {
        final ReportData report = new ObjectMapper().readValue(PowerPointServiceImplTest.class.getResource("report.json"), ReportData.class);

        final XMLSlideShow pptx = pptxService.report(report, false);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    @Test
    public void testMixedWidgetReportByDeserialization() throws TemplateLoadException, IOException {
        final ReportData report = new ObjectMapper().readValue(PowerPointServiceImplTest.class.getResource("multiwidgetreport.json"), ReportData.class);

        final XMLSlideShow pptx = pptxService.report(report, false);
        testWrite(pptx);

        Assert.assertEquals(pptx.getSlides().size(), 1);
    }

    private static ReportData createComplicatedReport(final double widgetMargins) throws IOException {
        final String titleFont = "Times New Roman";
        final double titleFontSize = 12;
        final double titleMargin = 5;

        final double x1 = 0,
                    x2 = 0.25,
                    x3 = 0.5,
                    x4 = 0.75,
                    y1 = 0,
                    y2 = 0.5;

        final TextData textData = new TextData(new TextData.Paragraph[]{
                new TextData.Paragraph(false, false, "Plain\n", "#FF0000", 8),
                new TextData.Paragraph(true, false, "Bold\n", "#00FF00", 10.0),
                new TextData.Paragraph(false, true, "Italic\n", "#0000FF", 12.0),
                new TextData.Paragraph(true, true, "BoldItalic\n", "#FFFF00", 14.0)
        });

        return new ReportData(new ReportData.Child[] {
                new ReportData.Child(x1, y1, 0.25, 0.5, "Dategraph", widgetMargins, titleMargin, titleFontSize, titleFont, createTwoAxisDategraphData()),
                new ReportData.Child(x2, y1, 0.25, 0.5, "Dategraph #2", widgetMargins, titleMargin, titleFontSize, titleFont, createSingleAxisDategraphData()),
                new ReportData.Child(x3, y1, 0.25, 0.5, "Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, createSunburstData()),
                new ReportData.Child(x4, y1, 0.25, 0.5, "Text Data", widgetMargins, titleMargin, titleFontSize, titleFont, textData),
                new ReportData.Child(x1, y2, 0.25, 0.5, "Map", widgetMargins, titleMargin, titleFontSize, titleFont, createMapData()),
                new ReportData.Child(x2, y2, 0.25, 0.5, "Table", widgetMargins, titleMargin, titleFontSize, titleFont, createTableData()),
                new ReportData.Child(x3, y2, 0.25, 0.5, "TopicMap", widgetMargins, titleMargin, titleFontSize, titleFont, createTopicMapData()),
                new ReportData.Child(x4, y2, 0.25, 0.5, "List", widgetMargins, titleMargin, titleFontSize, titleFont, createListData()),
        });
    }


    private static DategraphData createSingleAxisDategraphData() {
        return new DategraphData(
                new long[]{
                        1480690162, 1482394810, 1484099459, 1485804108
                },
                Arrays.asList(
                        new DategraphData.Row("#FF0000", "Red Line", false, new double[]{
                                87, 87, 124, 49
                        })
                )
        );
    }

    private static DategraphData createTwoAxisDategraphData() {
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

    private static DategraphData createTwoAxisMultipleSeriesDategraphData() {
        return new DategraphData(
                new long[]{
                        1480690162, 1482394810, 1484099459, 1485804108
                },
                Arrays.asList(
                        new DategraphData.Row("#FF0000", "Series 1a", false, new double[]{
                                87, 87, 124, 49
                        }), new DategraphData.Row("#00FF00", "Series 1b", false, new double[]{
                                12, 53, 63, 72
                        }), new DategraphData.Row("#0000FF", "Series 2a", true, new double[]{
                                1, 2, 3, 4
                        }), new DategraphData.Row("#00FFFF", "Series 2b", true, new double[]{
                                4, 3, 2, 1
                        })
                )
        );
    }

    private static SunburstData createSunburstData() {
        return new SunburstData(
                new String[] { "Red", "Green", "Blue"},
                new double[] { 1, 169, 130 },
                null,
                null,
                "RGB Colours"
                );
    }

    private static SunburstData createAlternativeSunburstData() {
        return new SunburstData(
                new String[] { "Cyan", "Magenta", "Yellow", "Black"},
                new double[] { 0.994, 0, 0.231, 0.337 },
                null,
                null,
                "CMYK Colours"
        );
    }

}
