package org.jhotdraw.samples.svg.acceptance;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.jhotdraw.samples.svg.figures.SVGTextFigure;
import org.junit.Test;

import java.awt.geom.Rectangle2D;

import static org.junit.Assert.assertTrue;

public class SVGTextFigureAcceptanceTest
        extends ScenarioTest<
        SVGTextFigureAcceptanceTest.GivenSVGTextFigure,
        SVGTextFigureAcceptanceTest.WhenSVGTextFigure,
        SVGTextFigureAcceptanceTest.ThenSVGTextFigure> {

    @Test
    public void text_bounds_update_when_font_size_changes() {
        given().an_svg_text_figure_with_calculated_bounds();

        when().the_font_size_is_increased();

        then().the_text_bounds_should_be_larger();
    }

    public static class GivenSVGTextFigure extends Stage<GivenSVGTextFigure> {

        @ScenarioState
        SVGTextFigure figure;

        @ScenarioState
        Rectangle2D.Double originalBounds;

        public GivenSVGTextFigure an_svg_text_figure_with_calculated_bounds() {
            figure = new SVGTextFigure("Text");
            originalBounds = figure.getBounds();
            return self();
        }
    }

    public static class WhenSVGTextFigure extends Stage<WhenSVGTextFigure> {

        @ScenarioState
        SVGTextFigure figure;

        public WhenSVGTextFigure the_font_size_is_increased() {
            figure.setFontSize(48f);
            return self();
        }
    }

    public static class ThenSVGTextFigure extends Stage<ThenSVGTextFigure> {

        @ScenarioState
        SVGTextFigure figure;

        @ScenarioState
        Rectangle2D.Double originalBounds;

        public ThenSVGTextFigure the_text_bounds_should_be_larger() {
            Rectangle2D.Double newBounds = figure.getBounds();
            assertTrue(newBounds.height > originalBounds.height);
            return self();
        }
    }
}