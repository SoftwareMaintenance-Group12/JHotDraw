package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.FontSizeHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TextOverflowHandle;
import org.jhotdraw.draw.tool.TextAreaEditingTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Insets2D;
import org.junit.Test;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.FONT_SIZE;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;
import static org.jhotdraw.draw.AttributeKeys.TEXT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import static org.junit.Assert.*;

public class SVGTextAreaFigureTest {

    @Test
    public void defaultConstructorSetsDefaultTextAndEditable() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        assertEquals("Text", figure.getText());
        assertFalse(figure.isEmpty());
        assertTrue(figure.isEditable());
    }

    @Test
    public void setTextUpdatesStoredText() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        figure.setText("Hello SVG");

        assertEquals("Hello SVG", figure.get(TEXT));
        assertEquals("Hello SVG", figure.getText());
    }

    @Test
    public void getTextColumnsReturnsAtLeastFourForShortOrNullText() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        figure.setText(null);
        assertEquals(4, figure.getTextColumns());

        figure.setText("abc");
        assertEquals(4, figure.getTextColumns());
    }

    @Test
    public void getTextColumnsReturnsActualLengthForLongerText() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.setText("abcdef");

        assertEquals(6, figure.getTextColumns());
    }

    @Test
    public void isEmptyReturnsTrueForNullOrEmptyText() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        figure.setText(null);
        assertTrue(figure.isEmpty());

        figure.setText("");
        assertTrue(figure.isEmpty());
    }

    @Test
    public void isEmptyReturnsFalseForNonEmptyText() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.setText("content");

        assertFalse(figure.isEmpty());
    }

    @Test
    public void setBoundsNormalizesCoordinatesAndPreservesPositiveSize() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        figure.setBounds(new Point2D.Double(20, 30), new Point2D.Double(10, 5));

        Rectangle2D.Double bounds = figure.getBounds();
        assertEquals(10.0, bounds.x, 0.0001);
        assertEquals(5.0, bounds.y, 0.0001);
        assertEquals(10.0, bounds.width, 0.0001);
        assertEquals(25.0, bounds.height, 0.0001);
    }

    @Test
    public void setBoundsUsesMinimumDimensionForDegenerateBounds() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        figure.setBounds(new Point2D.Double(10, 10), new Point2D.Double(10, 10));

        Rectangle2D.Double bounds = figure.getBounds();
        assertEquals(0.1, bounds.width, 0.0001);
        assertEquals(0.1, bounds.height, 0.0001);
    }

    @Test
    public void getInsetsReturnsZeroWhenNoStrokeColorIsDefined() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.set(STROKE_COLOR, null);

        Insets2D.Double insets = figure.getInsets();

        assertEquals(0.0, insets.top, 0.0001);
        assertEquals(0.0, insets.left, 0.0001);
        assertEquals(0.0, insets.bottom, 0.0001);
        assertEquals(0.0, insets.right, 0.0001);
    }

    @Test
    public void getInsetsIncludesHalfStrokeWidthRoundedUpWhenStrokeExists() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.set(STROKE_COLOR, Color.BLACK);
        figure.set(STROKE_WIDTH, 3d);

        Insets2D.Double insets = figure.getInsets();

        assertEquals(2.0, insets.top, 0.0001);
        assertEquals(2.0, insets.left, 0.0001);
        assertEquals(2.0, insets.bottom, 0.0001);
        assertEquals(2.0, insets.right, 0.0001);
    }

    @Test
    public void getFillColorReturnsBlackWhenTextColorIsWhite() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.set(FILL_COLOR, Color.WHITE);

        assertEquals(Color.BLACK, figure.getFillColor());
    }

    @Test
    public void getFillColorReturnsWhiteWhenTextColorIsNotWhite() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.set(FILL_COLOR, Color.BLUE);

        assertEquals(Color.WHITE, figure.getFillColor());
    }

    @Test
    public void getTextColorReturnsFillColor() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.set(FILL_COLOR, Color.RED);

        assertEquals(Color.RED, figure.getTextColor());
    }

    @Test
    public void setFontSizeAndGetFontSizeWorkWithoutTransform() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        figure.setFontSize(18f);

        assertEquals(18f, figure.getFontSize(), 0.0001f);
        assertEquals(18f, figure.get(FONT_SIZE), 0.0001f);
    }

    @Test
    public void setFontSizeAccountsForTransformAndGetFontSizeRestoresVisualSize() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();
        figure.set(TRANSFORM, AffineTransform.getScaleInstance(1.0, 2.0));

        figure.setFontSize(20f);

        assertEquals(10f, figure.get(FONT_SIZE), 0.0001f);
        assertEquals(20f, figure.getFontSize(), 0.0001f);
    }

    @Test
    public void getToolReturnsNullWhenFigureIsNotEditable() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure("Hello");
        figure.setBounds(new Point2D.Double(0, 0), new Point2D.Double(200, 80));
        figure.setEditable(false);

        Tool tool = figure.getTool(new Point2D.Double(10, 20));

        assertNull(tool);
    }

    @Test
    public void createHandlesForHoverContainsBoundsOutlineHandle() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        Collection<Handle> handles = figure.createHandles(-1);

        assertEquals(1, handles.size());
        assertTrue(handles.iterator().next() instanceof BoundsOutlineHandle);
    }

    @Test
    public void createHandlesForDetailLevelZeroContainsFontAndOverflowHandles() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure();

        Collection<Handle> handles = figure.createHandles(0);

        assertTrue(handles.stream().anyMatch(h -> h instanceof FontSizeHandle));
        assertTrue(handles.stream().anyMatch(h -> h instanceof TextOverflowHandle));
        assertTrue(handles.stream().anyMatch(h -> h.getClass().getSimpleName().equals("LinkHandle")));
    }

    @Test
    public void preferredTextSizeHandlesEmptyParagraphs() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure("line1\n\nline3");

        Dimension2DDouble size = figure.getPreferredTextSize(200);

        assertNotNull(size);
        assertTrue(size.width >= 0);
        assertTrue(size.height > 0);
    }

    @Test
    public void preferredTextSizeHandlesTabbedText() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure("a\tb\tc");

        Dimension2DDouble size = figure.getPreferredTextSize(300);

        assertNotNull(size);
        assertTrue(size.width >= 0);
        assertTrue(size.height >= 0);
    }

    @Test
    public void isTextOverflowReturnsTrueWhenTextExceedsAvailableHeight() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure(
                "line1\nline2\nline3\nline4\nline5\nline6\nline7\nline8"
        );
        figure.setBounds(new Point2D.Double(0, 0), new Point2D.Double(80, 20));

        assertTrue(figure.isTextOverflow());
    }

    @Test
    public void isTextOverflowReturnsFalseWhenThereIsEnoughSpace() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure("short text");
        figure.setBounds(new Point2D.Double(0, 0), new Point2D.Double(400, 200));

        assertFalse(figure.isTextOverflow());
    }

    @Test
    public void cloneCreatesIndependentCopyOfBoundsAndText() {
        SVGTextAreaFigure original = new SVGTextAreaFigure("Clone me");
        original.setBounds(new Point2D.Double(0, 0), new Point2D.Double(100, 50));

        SVGTextAreaFigure copy = original.clone();

        assertNotSame(original, copy);
        assertNotSame(original.getBounds(), copy.getBounds());
        assertEquals(original.getText(), copy.getText());
        assertEquals(original.getBounds(), copy.getBounds());
    }

    @Test
    public void setTransformInvalidatesAndStillAllowsFontSizeQuery() {
        SVGTextAreaFigure figure = new SVGTextAreaFigure("Test");
        figure.setBounds(new Point2D.Double(0, 0), new Point2D.Double(200, 80));

        Rectangle2D.Double before = figure.getDrawingArea();
        figure.set(AttributeKeys.TRANSFORM, AffineTransform.getTranslateInstance(10, 15));
        Rectangle2D.Double after = figure.getDrawingArea();

        assertNotNull(before);
        assertNotNull(after);
        assertNotEquals(before, after);
    }
}