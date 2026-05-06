package org.jhotdraw.samples.svg.acceptance;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.jhotdraw.samples.svg.figures.SVGTextAreaFigure;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertTrue;

public class SVGTextAreaAcceptanceTest
        extends ScenarioTest<
        SVGTextAreaAcceptanceTest.GivenSVGTextArea,
        SVGTextAreaAcceptanceTest.WhenSVGTextArea,
        SVGTextAreaAcceptanceTest.ThenSVGTextArea> {

    @Test
    public void text_area_reports_overflow_when_text_does_not_fit() {
        given().a_small_svg_text_area();
        when().long_text_is_added();
        then().the_text_area_should_report_overflow();
    }

    public static class GivenSVGTextArea extends Stage<GivenSVGTextArea> {

        @ScenarioState
        SVGTextAreaFigure figure;

        public GivenSVGTextArea a_small_svg_text_area() {
            figure = new SVGTextAreaFigure();
            //small bounds are used so the long text cannot fit inside the text area
            figure.setBounds(
                    new Point2D.Double(0, 0),
                    new Point2D.Double(30, 10)
            );
            return self();
        }
    }

    public static class WhenSVGTextArea extends Stage<WhenSVGTextArea> {

        @ScenarioState
        SVGTextAreaFigure figure;

        public WhenSVGTextArea long_text_is_added() {
            figure.setText("This is a very long text that should not fit inside the small SVG text area.");
            return self();
        }
    }

    public static class ThenSVGTextArea extends Stage<ThenSVGTextArea> {

        @ScenarioState
        SVGTextAreaFigure figure;

        public ThenSVGTextArea the_text_area_should_report_overflow() {
            assertTrue(figure.isTextOverflow());
            return self();
        }
    }
}