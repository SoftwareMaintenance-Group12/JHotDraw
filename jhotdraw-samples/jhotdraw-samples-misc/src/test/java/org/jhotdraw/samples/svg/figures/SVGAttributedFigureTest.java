package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.AttributeKey;
import org.junit.Test;

import javax.swing.Action;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.OPACITY;
import static org.junit.Assert.*;

public class SVGAttributedFigureTest {

    private static class TestSVGAttributedFigure extends SVGTextAreaFigure {
        boolean fillDrawn;
        boolean strokeDrawn;
        boolean invalidated;

        @Override
        protected void drawFill(Graphics2D g) {
            fillDrawn = true;
        }

        @Override
        protected void drawStroke(Graphics2D g) {
            strokeDrawn = true;
        }

        @Override
        public Rectangle2D.Double getBounds() {
            return new Rectangle2D.Double(0, 0, 20, 20);
        }

        @Override
        public Rectangle2D.Double getDrawingArea() {
            return new Rectangle2D.Double(0, 0, 20, 20);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            invalidated = true;
        }
    }

    private Graphics2D createGraphics() {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setClip(0, 0, 50, 50);
        return g;
    }

    @Test
    public void drawDoesNothingWhenOpacityIsZero() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, Color.RED);
        figure.set(STROKE_COLOR, Color.BLACK);
        figure.set(STROKE_WIDTH, 1d);
        figure.set(OPACITY, 0d);

        Graphics2D g = createGraphics();
        figure.draw(g);
        g.dispose();

        assertFalse(figure.fillDrawn);
        assertFalse(figure.strokeDrawn);
    }

    @Test
    public void drawCallsFigureDrawingDirectlyWhenOpacityIsOne() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, Color.RED);
        figure.set(STROKE_COLOR, Color.BLACK);
        figure.set(STROKE_WIDTH, 1d);
        figure.set(OPACITY, 1d);

        Graphics2D g = createGraphics();
        figure.draw(g);
        g.dispose();

        assertTrue(figure.fillDrawn);
        assertTrue(figure.strokeDrawn);
    }

    @Test
    public void drawClampsOpacityAboveOneAndStillDraws() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, Color.RED);
        figure.set(STROKE_COLOR, Color.BLACK);
        figure.set(STROKE_WIDTH, 1d);
        figure.set(OPACITY, 2d);

        Graphics2D g = createGraphics();
        figure.draw(g);
        g.dispose();

        assertTrue(figure.fillDrawn);
        assertTrue(figure.strokeDrawn);
    }

    @Test
    public void drawClampsOpacityBelowZeroAndDoesNotDraw() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, Color.RED);
        figure.set(STROKE_COLOR, Color.BLACK);
        figure.set(STROKE_WIDTH, 1d);
        figure.set(OPACITY, -1d);

        Graphics2D g = createGraphics();
        figure.draw(g);
        g.dispose();

        assertFalse(figure.fillDrawn);
        assertFalse(figure.strokeDrawn);
    }

    @Test
    public void drawFigureDrawsFillWhenFillPaintExists() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, Color.BLUE);
        figure.set(STROKE_COLOR, null);
        figure.set(STROKE_WIDTH, 1d);

        Graphics2D g = createGraphics();
        figure.drawFigure(g);
        g.dispose();

        assertTrue(figure.fillDrawn);
        assertFalse(figure.strokeDrawn);
    }

    @Test
    public void drawFigureDrawsStrokeWhenStrokePaintExistsAndWidthIsPositive() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, null);
        figure.set(STROKE_COLOR, Color.BLACK);
        figure.set(STROKE_WIDTH, 2d);

        Graphics2D g = createGraphics();
        figure.drawFigure(g);
        g.dispose();

        assertFalse(figure.fillDrawn);
        assertTrue(figure.strokeDrawn);
    }

    @Test
    public void drawFigureDoesNotDrawStrokeWhenStrokeWidthIsZero() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, null);
        figure.set(STROKE_COLOR, Color.BLACK);
        figure.set(STROKE_WIDTH, 0d);

        Graphics2D g = createGraphics();
        figure.drawFigure(g);
        g.dispose();

        assertFalse(figure.strokeDrawn);
    }

    @Test
    public void drawFigureRestoresGraphicsTransformAfterApplyingFigureTransform() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(FILL_COLOR, Color.RED);
        figure.set(STROKE_COLOR, null);
        figure.set(TRANSFORM, AffineTransform.getTranslateInstance(10, 15));

        Graphics2D g = createGraphics();
        AffineTransform original = g.getTransform();

        figure.drawFigure(g);

        assertEquals(original, g.getTransform());
        g.dispose();
    }



    @Test
    public void setTransformInvalidatesFigure() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();

        figure.set(TRANSFORM, AffineTransform.getTranslateInstance(5, 5));

        assertTrue(figure.invalidated);
    }

    @Test
    public void getActionsReturnsEmptyCollectionWhenNoTransformExists() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();

        Collection<Action> actions = figure.getActions(new Point2D.Double(0, 0));

        assertNotNull(actions);
        assertTrue(actions.isEmpty());
    }

    @Test
    public void getActionsReturnsRemoveTransformActionWhenTransformExists() {
        TestSVGAttributedFigure figure = new TestSVGAttributedFigure();
        figure.set(TRANSFORM, AffineTransform.getTranslateInstance(5, 5));

        Collection<Action> actions = figure.getActions(new Point2D.Double(0, 0));

        assertNotNull(actions);
        assertEquals(1, actions.size());
    }
}