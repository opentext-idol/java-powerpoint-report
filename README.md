# HP Autonomy PowerPoint Report API

[![Build Status](https://travis-ci.org/hpe-idol/java-powerpoint-report.svg?branch=master)](https://travis-ci.org/hpe-idol/java-powerpoint-report)

Library to help generate PowerPoint reports; originally designed for the [Find UI](https://github.com/hpe-idol/find). 

It renders various visualizations from Find into PowerPoint, e.g. a topic map, list, sunburst (only one level, as a doughnut chart), table, map and date graph as individual PowerPoint presentations. 

It also has a `report` API to generate a composite report consisting of multiple visualizations.

This repo uses git-flow. develop is the development branch. master is the last known good branch.

## Usage

### Creating a service

The library is bundled with a default PowerPoint template and settings, which you can use with e.g.
```java
   PowerPointService service = new PowerPointServiceImpl();
```

Alternatively, you can provide your own template PowerPoint file and settings, e.g.
```java
    PowerPointServiceImpl pptxService = new PowerPointServiceImpl(
        // use a custom template
        () -> new FileInputStream("/path/to/my/template.pptx"),
        // reserve 8% of the top of the page (e.g. for logos etc.)
        () -> new TemplateSettings(new Anchor(0, 0.08, 1, 0.92))
    );

    // You should validate that your template is suitable before trying to generate reports.
    pptxService.validateTemplate();
```

You can edit the master slide on your PowerPoint template e.g. to add your logo to every slide; and reserve space for it by specifying custom anchor points.

The template must consist of two slides in the following order:
1. a slide with a doughnut chart
2. a slide with a line chart, with three data series: a time-based x-axis, a numeric primary y-axis, and a numeric secondary y-axis.
 
You can see an example template in [template.pptx](src/main/resources/com/hp/autonomy/frontend/reports/powerpoint/templates/template.pptx).

### Using the service

Once you have the service, you can create DTOs to represent your data; then call the methods exposed on the service to create PowerPoint presentations, e.g.
```java
    final DategraphData dategraph = new DategraphData(
            new long[]{
                1480690162, 1482394810, 1484099459, 1485804108
            },
            Arrays.asList(
                new DategraphData.Row("#FF0000", "Red Line", false, new double[]{
                    87, 87, 124, 49
                }),
                new DategraphData.Row("#00FF00", "Green Line", true, new double[]{
                    12, 53, 63, 72
                })
            )
    );

    final XMLSlideShow pptx = pptxService.graph(dategraph);
    
    // You can then do something with the slide show, e.g. write it to disk
    pptx.write(new FileOutputStream("dategraph.pptx"));
```

Similarly, you can compose multiple visualizations into a single slide
```java
    // Assume dategraph data is already declared as in above example
    //  final DateGraphData = ....

    final SunburstData topRightSunburst = new SunburstData( 
        new String[] { "Red", "Green", "Blue"},
        new double[] { 1, 169, 130 },
        "RGB Colours"
    );

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
        // Dategraph taking the full left pane
        new ReportData.Child(0, 0, 0.5, 1, "Left Dategraph", widgetMargins, titleMargin, titleFontSize, titleFont, dategraph),
        // Sunburst taking the top-right
        new ReportData.Child(0.5, 0, 0.5, 0.5, "Top Right Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, topRightSunburst),
        // Another sunburst taking the bottom-right
        new ReportData.Child(0.5, 0.5, 0.5, 0.5, "Bottom Right Sunburst", widgetMargins, titleMargin, titleFontSize, titleFont, bottomRightSunburst),
    });

    final XMLSlideShow pptx = pptxService.report(report);
    pptx.write(new FileOutputStream("report.pptx"));
```


## License
Copyright 2017 Hewlett Packard Enterprise Development LP

Licensed under the MIT License (the "License"); you may not use this project except in compliance with the License.
