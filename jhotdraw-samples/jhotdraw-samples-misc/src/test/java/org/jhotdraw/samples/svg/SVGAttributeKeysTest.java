package org.jhotdraw.samples.svg;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.figure.Figure;
import org.junit.Test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SVGAttributeKeysTest {

    @Test
    public void getFillPaint_shouldReturnFillColor_whenOpacityIsOne() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.FILL_OPACITY)).thenReturn(1d);
        when(figure.get(SVGAttributeKeys.FILL_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.FILL_COLOR)).thenReturn(Color.RED);

        Paint result = SVGAttributeKeys.getFillPaint(figure);

        assertEquals(Color.RED, result);
    }

    @Test
    public void getFillPaint_shouldApplyAlphaToFillColor_whenOpacityIsLessThanOne() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.FILL_OPACITY)).thenReturn(0.5d);
        when(figure.get(SVGAttributeKeys.FILL_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.FILL_COLOR)).thenReturn(new Color(255, 0, 0));

        Paint result = SVGAttributeKeys.getFillPaint(figure);

        assertTrue(result instanceof Color);
        Color color = (Color) result;
        assertEquals(255, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
        assertEquals((int) (0.5d * 255), color.getAlpha());
    }

    @Test
    public void getFillPaint_shouldReturnNull_whenNoGradientAndNoFillColor() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.FILL_OPACITY)).thenReturn(1d);
        when(figure.get(SVGAttributeKeys.FILL_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.FILL_COLOR)).thenReturn(null);

        Paint result = SVGAttributeKeys.getFillPaint(figure);

        assertNull(result);
    }

    @Test
    public void getStrokePaint_shouldReturnStrokeColor_whenOpacityIsOne() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.STROKE_OPACITY)).thenReturn(1d);
        when(figure.get(SVGAttributeKeys.STROKE_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.STROKE_COLOR)).thenReturn(Color.BLUE);

        Paint result = SVGAttributeKeys.getStrokePaint(figure);

        assertEquals(Color.BLUE, result);
    }

    @Test
    public void getStrokePaint_shouldApplyAlphaToStrokeColor_whenOpacityIsLessThanOne() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.STROKE_OPACITY)).thenReturn(0.25d);
        when(figure.get(SVGAttributeKeys.STROKE_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.STROKE_COLOR)).thenReturn(new Color(0, 0, 255));

        Paint result = SVGAttributeKeys.getStrokePaint(figure);

        assertTrue(result instanceof Color);
        Color color = (Color) result;
        assertEquals(0, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(255, color.getBlue());
        assertEquals((int) (0.25d * 255), color.getAlpha());
    }

    @Test
    public void getStrokePaint_shouldReturnNull_whenNoGradientAndNoStrokeColor() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.STROKE_OPACITY)).thenReturn(1d);
        when(figure.get(SVGAttributeKeys.STROKE_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.STROKE_COLOR)).thenReturn(null);

        Paint result = SVGAttributeKeys.getStrokePaint(figure);

        assertNull(result);
    }

    @Test
    public void setDefaults_shouldSetExpectedSvgDefaults() {
        Figure figure = mock(Figure.class);

        SVGAttributeKeys.setDefaults(figure);

        verify(figure).set(SVGAttributeKeys.FILL_COLOR, Color.black);
        verify(figure).set(SVGAttributeKeys.WINDING_RULE, AttributeKeys.WindingRule.NON_ZERO);
        verify(figure).set(SVGAttributeKeys.STROKE_COLOR, null);
        verify(figure).set(SVGAttributeKeys.STROKE_WIDTH, 1d);
        verify(figure).set(SVGAttributeKeys.STROKE_CAP, BasicStroke.CAP_BUTT);
        verify(figure).set(SVGAttributeKeys.STROKE_JOIN, BasicStroke.JOIN_MITER);
        verify(figure).set(SVGAttributeKeys.STROKE_MITER_LIMIT, 4d);
        verify(figure).set(SVGAttributeKeys.IS_STROKE_MITER_LIMIT_FACTOR, false);
        verify(figure).set(SVGAttributeKeys.STROKE_DASHES, null);
        verify(figure).set(SVGAttributeKeys.STROKE_DASH_PHASE, 0d);
        verify(figure).set(SVGAttributeKeys.IS_STROKE_DASH_FACTOR, false);
    }

    @Test
    public void getPerpendicularHitGrowth_shouldUseFillGrowth_whenNoStrokeColorAndNoStrokeGradient() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.STROKE_COLOR)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.STROKE_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.FILL_UNDER_STROKE)).thenReturn(AttributeKeys.Underfill.FULL);
        when(figure.get(SVGAttributeKeys.STROKE_WIDTH)).thenReturn(2d);
        when(figure.get(SVGAttributeKeys.STROKE_TYPE)).thenReturn(AttributeKeys.StrokeType.BASIC);
        when(figure.get(SVGAttributeKeys.STROKE_PLACEMENT)).thenReturn(AttributeKeys.StrokePlacement.CENTER);
        when(figure.get(SVGAttributeKeys.STROKE_CAP)).thenReturn(BasicStroke.CAP_BUTT);
        when(figure.get(SVGAttributeKeys.STROKE_JOIN)).thenReturn(BasicStroke.JOIN_MITER);
        when(figure.get(SVGAttributeKeys.STROKE_MITER_LIMIT)).thenReturn(4d);
        when(figure.get(SVGAttributeKeys.IS_STROKE_MITER_LIMIT_FACTOR)).thenReturn(false);

        double result = SVGAttributeKeys.getPerpendicularHitGrowth(figure, 1.0);

        assertEquals(AttributeKeys.getPerpendicularFillGrowth(figure, 1.0), result, 0.0001);
    }

    @Test
    public void getPerpendicularHitGrowth_shouldIncludeStrokeWidth_whenStrokeExists() {
        Figure figure = mock(Figure.class);

        when(figure.get(SVGAttributeKeys.STROKE_COLOR)).thenReturn(Color.BLACK);
        when(figure.get(SVGAttributeKeys.STROKE_GRADIENT)).thenReturn(null);
        when(figure.get(SVGAttributeKeys.FILL_UNDER_STROKE)).thenReturn(AttributeKeys.Underfill.FULL);
        when(figure.get(SVGAttributeKeys.STROKE_WIDTH)).thenReturn(2d);
        when(figure.get(SVGAttributeKeys.STROKE_TYPE)).thenReturn(AttributeKeys.StrokeType.BASIC);
        when(figure.get(SVGAttributeKeys.STROKE_PLACEMENT)).thenReturn(AttributeKeys.StrokePlacement.CENTER);
        when(figure.get(SVGAttributeKeys.STROKE_CAP)).thenReturn(BasicStroke.CAP_BUTT);
        when(figure.get(SVGAttributeKeys.STROKE_JOIN)).thenReturn(BasicStroke.JOIN_MITER);
        when(figure.get(SVGAttributeKeys.STROKE_MITER_LIMIT)).thenReturn(4d);
        when(figure.get(SVGAttributeKeys.IS_STROKE_MITER_LIMIT_FACTOR)).thenReturn(false);

        double result = SVGAttributeKeys.getPerpendicularHitGrowth(figure, 1.0);
        double expected = AttributeKeys.getPerpendicularDrawGrowth(figure, 1.0)
                + AttributeKeys.getStrokeTotalWidth(figure, 1.0) / 2d;

        assertEquals(expected, result, 0.0001);
    }
}