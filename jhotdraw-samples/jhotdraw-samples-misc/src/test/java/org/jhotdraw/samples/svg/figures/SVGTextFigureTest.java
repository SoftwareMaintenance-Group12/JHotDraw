package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.handle.Handle;
import org.junit.Test;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Collection;

import static org.junit.Assert.*;

public class SVGTextFigureTest {

    @Test
    public void constructor_shouldInitializeWithProvidedText() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        assertEquals("Hello", figure.getText());
        assertFalse(figure.isEmpty());
    }

    @Test
    public void isEmpty_shouldReturnTrue_whenTextIsNull() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        figure.setText(null);

        assertTrue(figure.isEmpty());
    }

    @Test
    public void isEmpty_shouldReturnTrue_whenTextIsEmptyString() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        figure.setText("");

        assertTrue(figure.isEmpty());
    }

    @Test
    public void isEmpty_shouldReturnFalse_whenTextIsNotEmpty() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        assertFalse(figure.isEmpty());
    }

    @Test
    public void getCoordinates_shouldReturnDefensiveCopy() {
        SVGTextFigure figure = new SVGTextFigure("Hello");
        figure.setCoordinates(new Point2D.Double[]{ new Point2D.Double(10, 20) });

        Point2D.Double[] copy = figure.getCoordinates();
        copy[0].x = 999;
        copy[0].y = 999;

        Point2D.Double[] reread = figure.getCoordinates();
        assertEquals(10.0, reread[0].x, 0.0001);
        assertEquals(20.0, reread[0].y, 0.0001);
    }

    @Test
    public void setCoordinates_shouldCopyArrayStructure() {
        SVGTextFigure figure = new SVGTextFigure("Hello");
        Point2D.Double[] coordinates = { new Point2D.Double(10, 20) };

        figure.setCoordinates(coordinates);

        Point2D.Double[] returned = figure.getCoordinates();

        assertNotSame(coordinates, returned);
        assertEquals(10.0, returned[0].x, 0.0001);
        assertEquals(20.0, returned[0].y, 0.0001);
    }

    @Test
    public void getRotates_shouldReturnDefensiveCopy() {
        SVGTextFigure figure = new SVGTextFigure("Hello");
        figure.setRotates(new double[]{ 2.5 });

        double[] copy = figure.getRotates();
        copy[0] = 99.0;

        double[] reread = figure.getRotates();
        assertEquals(2.5, reread[0], 0.0001);
    }

    @Test
    public void getFillColor_shouldReturnBlack_whenFillColorIsNull() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        figure.set(org.jhotdraw.draw.AttributeKeys.FILL_COLOR, null);

        assertEquals(Color.black, figure.getFillColor());
    }

    @Test
    public void getFillColor_shouldReturnBlack_whenFillColorIsWhite() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        figure.set(org.jhotdraw.draw.AttributeKeys.FILL_COLOR, Color.white);

        assertEquals(Color.black, figure.getFillColor());
    }

    @Test
    public void getFillColor_shouldReturnWhite_whenFillColorIsNotWhite() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        figure.set(org.jhotdraw.draw.AttributeKeys.FILL_COLOR, Color.red);

        assertEquals(Color.white, figure.getFillColor());
    }

    @Test
    public void clone_shouldDeepCopyCoordinatesAndRotates() {
        SVGTextFigure original = new SVGTextFigure("Hello");
        original.setCoordinates(new Point2D.Double[]{ new Point2D.Double(10, 20) });
        original.setRotates(new double[]{ 1.5 });

        SVGTextFigure clone = original.clone();

        clone.getCoordinates()[0].x = 999;
        clone.getRotates()[0] = 99.0;

        assertEquals(10.0, original.getCoordinates()[0].x, 0.0001);
        assertEquals(1.5, original.getRotates()[0], 0.0001);
    }

    @Test
    public void createHandles_shouldReturnExpectedNumberOfHandles_forDetailLevelZero() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        Collection<Handle> handles = figure.createHandles(0);

        assertEquals(7, handles.size());
    }

    @Test
    public void createHandles_shouldReturnExpectedNumberOfHandles_forDetailLevelOne() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        Collection<Handle> handles = figure.createHandles(1);

        assertFalse(handles.isEmpty());
    }

    @Test
    public void createHandles_shouldReturnHoverHandle_forDetailLevelMinusOne() {
        SVGTextFigure figure = new SVGTextFigure("Hello");

        Collection<Handle> handles = figure.createHandles(-1);

        assertEquals(1, handles.size());
    }
}