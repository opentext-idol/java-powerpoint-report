/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.POIXMLDocumentPart;
import static org.apache.poi.util.Units.EMU_PER_POINT;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;

/**
 * Internal implementation class to keep track of required elements from the template.
 */
class SlideShowTemplate {

    private static final String RELATION_NAMESPACE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";

    /** Parsed PowerPoint file from the template. */
    private final XMLSlideShow pptx;
    /** Doughnut chart XML object, cached so we can clone it. */
    private final ImmutablePair<XSLFChart, CTGraphicalObjectFrame> doughnutChart;
    /** Line chart XML object, cached so we can clone it.  */
    private final ImmutablePair<XSLFChart, CTGraphicalObjectFrame> graphChart;

    SlideShowTemplate(final InputStream inputStream) throws TemplateLoadException {
        try {
            // There should be a chart in slide 1 and a chart in slide 2
            pptx = new XMLSlideShow(inputStream);

            final List<XSLFSlide> slides = pptx.getSlides();

            if (slides.size() != 2) {
                throw new TemplateLoadException("Template powerpoint should have two slides, doughnut chart on slide 1 and time-axis line chart on slide 2");
            }

            XSLFSlide slide = slides.get(0);

            doughnutChart = getChart(slide, "First slide should have a doughnut chart");

            if (doughnutChart == null || ArrayUtils.isEmpty(doughnutChart.getLeft().getCTChart().getPlotArea().getDoughnutChartArray())) {
                throw new TemplateLoadException("First slide has the wrong chart type, should have a doughnut chart");
            }

            graphChart = getChart(slides.get(1), "Second slide should have a time-axis line chart");

            if (graphChart == null || ArrayUtils.isEmpty(graphChart.getLeft().getCTChart().getPlotArea().getLineChartArray())) {
                throw new TemplateLoadException("Second slide has the wrong chart type, should have a time-axis line chart");
            }

            // Remove the slides afterwards
            pptx.removeSlide(1);
            pptx.removeSlide(0);
        }
        catch(IOException e) {
            throw new TemplateLoadException("Error while loading slide show", e);
        }
    }

    /**
     * Get the doughnut chart from the first slide. Do not modify this object.
     * @return the doughnut chart from the first slide
     */
    XSLFChart getDoughnutChart() {
        return doughnutChart.getLeft();
    }

    /**
     * Creates a new clone of the doughnut chart XML from the first slide, for inclusion into a slide's shapes.
     * @param relId the relation id to the chart.
     * @param shapeId the shape id of the new shape.
     * @param shapeName the name of your choice for the shape.
     * @param anchor where the shape should be positioned on screen, or null to use the same position as the cloned chart.
     * @return a new clone of the doughnut chart XML.
     */
    CTGraphicalObjectFrame getDoughnutChartShapeXML(final String relId, final int shapeId, final String shapeName, final Rectangle2D.Double anchor) {
        return cloneShapeXML(doughnutChart.getRight(), relId, shapeId, shapeName, anchor);
    }

    /**
     * Get the graph line chart from the second slide. Do not modify this object.
     * @return the graph line chart from the second slide
     */
    XSLFChart getGraphChart() {
        return graphChart.getLeft();
    }

    /**
     * Creates a new clone of the graph chart XML from the first slide, for inclusion into a slide's shapes.
     * @param relId the relation id to the chart.
     * @param shapeId the shape id of the new shape.
     * @param shapeName the name of your choice for the shape.
     * @param anchor where the shape should be positioned on screen, or null to use the same position as the cloned chart.
     * @return a new clone of the graph chart XML.
     */
    CTGraphicalObjectFrame getGraphChartShapeXML(final String relId, final int shapeId, final String shapeName, final Rectangle2D.Double anchor) {
        return cloneShapeXML(graphChart.getRight(), relId, shapeId, shapeName, anchor);
    }

    /**
     * Get the template presentation with all slides removed.
     * @return the template presentation without any slides.
     */
    XMLSlideShow getSlideShow() {
        return pptx;
    }

    /**
     * Given an existing slide, search its relations to find a chart object.
     * @param slide a slide from the template.
     * @param error if we can't find a slide, this error message will be returned as the exception.
     * @return a pair containing the chart.xml data and the graphical object which represented it on the slide.
     * @throws TemplateLoadException if we can't find a chart object.
     */
    private ImmutablePair<XSLFChart, CTGraphicalObjectFrame> getChart(final XSLFSlide slide, final String error) throws TemplateLoadException {
        for(POIXMLDocumentPart.RelationPart part : slide.getRelationParts()) {
            if (part.getDocumentPart() instanceof XSLFChart) {
                final String relId = part.getRelationship().getId();

                for(XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFGraphicFrame) {
                        final CTGraphicalObjectFrame frameXML = (CTGraphicalObjectFrame) shape.getXmlObject();
                        final XmlObject[] children = frameXML.getGraphic().getGraphicData().selectChildren(new QName(XSSFRelation.NS_CHART, "chart"));

                        for(final XmlObject child : children) {
                            final String imageRel = child.getDomNode().getAttributes().getNamedItemNS(RELATION_NAMESPACE, "id").getNodeValue();

                            if (relId.equals(imageRel)) {
                                return new ImmutablePair<>(part.getDocumentPart(), frameXML);
                            }
                        }
                    }
                }
            }
        }

        throw new TemplateLoadException(error);
    }

    /**
     * Utility function to clone the graphical object which represents a chart on a slide.
     * @param base the object to clone.
     * @param relId the new relation ID we should insert.
     * @param shapeId the new shape ID we should insert.
     * @param shapeName the new shape name we should insert.
     * @param anchor the bounds of the new shape object in PowerPoint coordinates, if set, or null to use the existing clone's bounds.
     * @return a new clone object with the desired properties.
     */
    private CTGraphicalObjectFrame cloneShapeXML(final CTGraphicalObjectFrame base, final String relId, final int shapeId, final String shapeName, final Rectangle2D.Double anchor) {
        /* Based on viewing the raw chart.
          <p:graphicFrame>
            <p:nvGraphicFramePr>
              <p:cNvPr id="4" name="Chart 3"/>
              <p:cNvGraphicFramePr/>
              <p:nvPr>
                <p:extLst>
                  <p:ext uri="{D42A27DB-BD31-4B8C-83A1-F6EECF244321}">
                    <p14:modId xmlns:p14="http://schemas.microsoft.com/office/powerpoint/2010/main" val="866141002"/>
                  </p:ext>
                </p:extLst>
              </p:nvPr>
            </p:nvGraphicFramePr>
            <p:xfrm>
              <a:off x="0" y="0"/>
              <a:ext cx="12192000" cy="6858000"/>
            </p:xfrm>
            <a:graphic>
              <a:graphicData uri="http://schemas.openxmlformats.org/drawingml/2006/chart">
                <c:chart xmlns:c="http://schemas.openxmlformats.org/drawingml/2006/chart"
                         xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" r:id="rId2"/>
              </a:graphicData>
            </a:graphic>
          </p:graphicFrame>
         */
        final CTGraphicalObjectFrame copy = (CTGraphicalObjectFrame) base.copy();

        final CTNonVisualDrawingProps cNvPr = copy.getNvGraphicFramePr().getCNvPr();
        cNvPr.setName(shapeName);
        cNvPr.setId(shapeId);

        final XmlObject[] children = copy.getGraphic().getGraphicData().selectChildren(new QName(XSSFRelation.NS_CHART, "chart"));

        if (anchor != null) {
            final CTPoint2D off = copy.getXfrm().getOff();
            off.setX((int) (anchor.getX() * EMU_PER_POINT));
            off.setY((int) (anchor.getY() * EMU_PER_POINT));

            final CTPositiveSize2D ext = copy.getXfrm().getExt();
            ext.setCx((int) (anchor.getWidth()* EMU_PER_POINT));
            ext.setCy((int) (anchor.getHeight() * EMU_PER_POINT));
        }

        for(final XmlObject child : children) {
            child.getDomNode().getAttributes().getNamedItemNS(RELATION_NAMESPACE, "id").setNodeValue(relId);
        }

        return copy;
    }

}
