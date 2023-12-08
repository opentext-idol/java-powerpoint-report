# OpenText IDOL PowerPoint Report API

Library to help generate PowerPoint reports; originally designed for the [Find UI](https://github.com/opentext-idol/find). 

It renders various visualizations from Find into PowerPoint, e.g. a topic map, list, sunburst (only one level, as a doughnut chart), table, map and date graph as individual PowerPoint presentations. 

It also has a report API to generate a composite report consisting of multiple visualizations rendered onto a single slide.

This repo uses git-flow. develop is the development branch. master is the last known good branch.

See https://opentext-idol.github.io/java-powerpoint-report/ for [project information](https://opentext-idol.github.io/java-powerpoint-report/project-info.html) and [Javadoc](https://opentext-idol.github.io/java-powerpoint-report/apidocs/index.html).

## Usage

### Importing

You can import this library from Maven by including it in your pom.xml.

    <dependency>
        <groupId>com.hp.autonomy.frontend.reports.powerpoint</groupId>
        <artifactId>powerpoint-report</artifactId>
        <version>2.1.0-SNAPSHOT</version>
    </dependency>

### Creating a service

The primary interface to working with this API is the [```PowerPointService```](src/main/java/com/hp/autonomy/frontend/reports/powerpoint/PowerPointService.java) interface, which is implemented in  [```PowerPointServiceImpl```](src/main/java/com/hp/autonomy/frontend/reports/powerpoint/PowerPointServiceImpl.java). 

The library is bundled with a default PowerPoint template, settings and image source which you can use e.g.
```java
   PowerPointService pptxService = new PowerPointServiceImpl();
```

Alternatively, you can provide your own template PowerPoint file, settings and image source, e.g.
```java
    PowerPointServiceImpl pptxService = new PowerPointServiceImpl(
        // use a custom template
        () -> new FileInputStream("/path/to/my/template.pptx"),
        // reserve 8% of the top of the page (e.g. for logos etc.)
        () -> new TemplateSettings(new Anchor(0, 0.08, 1, 0.92)),
        new WebAndDataUriImageSource() {
            @Override
            public boolean allowHttpURI(final URI uri) {
                // deny all external HTTP URLs to prevent malicious users from using this as an open proxy
                return false;
            }
        }
    );

    // You should validate that your template is suitable before trying to generate reports.
    pptxService.validateTemplate();
```

You can edit the master slide on your PowerPoint template e.g. to add your logo to every slide; and reserve space for your content by specifying custom anchor points as above.

The template must consist of two slides in the following order:

1. a slide with a doughnut chart
2. a slide with a xy scatterplot chart, with three data series: 
- a time-based x-axis
- a numeric primary y-axis
- a numeric secondary y-axis.
 
You can see an example template in [template.pptx](src/main/resources/com/hp/autonomy/frontend/reports/powerpoint/templates/template.pptx), and an example of embedding your logo into the default master slide in [validTemplateWithLogo.pptx](src/test/resources/com/hp/autonomy/frontend/reports/powerpoint/validTemplateWithLogo.pptx).

The image source controls how image identifiers are converted to images to be embedded into the PowerPoint file. 
The default implementation (DataUriImageSource) embeds base64-encoded images directly.

You can use WebAndDataUriImageSource instead if you also want to allow downloading HTTP and HTTPS URLs for embedding (since PowerPoint doesn't allow external image links).
By default it requires that all HTTP/HTTPS URLs end with a '.jpeg', '.jpg', '.png' or '.gif' extension to mitigate attacks from malicious users using it as an open proxy; you may want to override the allowHttpURI() function as above if you have a list of sites to whitelist. 

### Using the service

Once you have the service, you can create data transfer objects (DTOs) to represent your data; then call the methods exposed on the service to create PowerPoint presentations, e.g.
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

Similarly, you can compose multiple visualizations into a single slide:
```java
    // Assume dategraph data is already declared as in above example
    //  final DateGraphData dategraph = ....

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

    final XMLSlideShow pptx = pptxService.report(report, false);
    pptx.write(new FileOutputStream("report.pptx"));
```

In practice, if you're working with JSON data, it's probably easiest to use Jackson to deserialize JSON straight into your DTO, e.g. given [topicmap.json](src/test/resources/com/hp/autonomy/frontend/reports/powerpoint/topicmap.json) you can produce a ```TopicMapData``` object for use in the API.
```java
    //   final String json = ...;
    final TopicMapData topicMap = new ObjectMapper().readValue(json, TopicMapData.class);
    
    final XMLSlideShow pptx = pptxService.topicmap(topicMap);
    pptx.write(new FileOutputStream("topicmap.pptx"));
```

## License
Copyright 2017-2018 OpenText.

Licensed under the MIT License (the "License"); you may not use this project except in compliance with the License.
