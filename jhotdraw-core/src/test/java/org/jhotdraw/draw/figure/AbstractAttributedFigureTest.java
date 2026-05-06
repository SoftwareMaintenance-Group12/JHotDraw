package org.jhotdraw.draw.figure;

import org.jhotdraw.draw.AttributeKey;
import org.junit.Test;

import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import static org.junit.Assert.*;

public class AbstractAttributedFigureTest {

    private static final AttributeKey<String> TEST_KEY =
            new AttributeKey<>("testKey", String.class, "default");

    private static class TestAttributedFigure extends AbstractAttributedFigure {
        private Rectangle2D.Double bounds = new Rectangle2D.Double(10, 20, 30, 40);

        @Override
        protected void drawFill(Graphics2D g) {
        }

        @Override
        protected void drawStroke(Graphics2D g) {
        }

        @Override
        public Rectangle2D.Double getBounds() {
            return (Rectangle2D.Double) bounds.clone();
        }

        @Override
        public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
            bounds = new Rectangle2D.Double(
                    Math.min(anchor.x, lead.x),
                    Math.min(anchor.y, lead.y),
                    Math.abs(lead.x - anchor.x),
                    Math.abs(lead.y - anchor.y)
            );
        }
        @Override
        public boolean contains(Point2D.Double p){
            return bounds.contains(p);
        }

        @Override
        public Object getTransformRestoreData() {
            return null; //minimal stub
        }

        @Override
        public void restoreTransformTo(Object restoreData) {
            //do nothing
        }

        @Override
        public void transform(AffineTransform tx) {
            // minimal stub for tests that do not use transforms
        }
    }

    @Test
    public void getAttributes_shouldReturnCopy_notInternalMap() {
        TestAttributedFigure figure = new TestAttributedFigure();
        figure.set(TEST_KEY, "value1");

        Map<AttributeKey<?>, Object> attributesCopy = figure.getAttributes();
        attributesCopy.put(TEST_KEY, "changedOutside");

        assertEquals("value1", figure.get(TEST_KEY));
    }

    @Test
    public void setAttributeEnabled_shouldDisableAndEnableAttribute() {
        TestAttributedFigure figure = new TestAttributedFigure();

        assertTrue(figure.isAttributeEnabled(TEST_KEY));

        figure.setAttributeEnabled(TEST_KEY, false);
        assertFalse(figure.isAttributeEnabled(TEST_KEY));

        figure.setAttributeEnabled(TEST_KEY, true);
        assertTrue(figure.isAttributeEnabled(TEST_KEY));
    }

    @Test
    public void set_shouldStoreValue_whenAttributeIsEnabled() {
        TestAttributedFigure figure = new TestAttributedFigure();

        figure.set(TEST_KEY, "hello");

        assertTrue(figure.hasAttribute(TEST_KEY));
        assertEquals("hello", figure.get(TEST_KEY));
    }

    @Test
    public void set_shouldNotChangeValue_whenAttributeIsDisabled() {
        TestAttributedFigure figure = new TestAttributedFigure();
        figure.set(TEST_KEY, "original");
        figure.setAttributeEnabled(TEST_KEY, false);

        figure.set(TEST_KEY, "newValue");

        assertEquals("original", figure.get(TEST_KEY));
    }

    @Test
    public void removeAttribute_shouldRemoveStoredValueAndRestoreDefault() {
        TestAttributedFigure figure = new TestAttributedFigure();
        figure.set(TEST_KEY, "custom");

        assertTrue(figure.hasAttribute(TEST_KEY));

        figure.removeAttribute(TEST_KEY);

        assertFalse(figure.hasAttribute(TEST_KEY));
        assertEquals("default", figure.get(TEST_KEY));
    }

    @Test
    public void hasAttribute_shouldReturnFalse_whenAttributeWasNeverSet() {
        TestAttributedFigure figure = new TestAttributedFigure();

        assertFalse(figure.hasAttribute(TEST_KEY));
    }

    @Test
    public void clone_shouldCreateIndependentCopyOfAttributes() {
        TestAttributedFigure original = new TestAttributedFigure();
        original.set(TEST_KEY, "originalValue");

        TestAttributedFigure clone = (TestAttributedFigure) original.clone();
        clone.set(TEST_KEY, "cloneValue");

        assertEquals("originalValue", original.get(TEST_KEY));
        assertEquals("cloneValue", clone.get(TEST_KEY));
    }

    @Test
    public void clone_shouldCopyForbiddenAttributesState() {
        TestAttributedFigure original = new TestAttributedFigure();
        original.setAttributeEnabled(TEST_KEY, false);

        TestAttributedFigure clone = (TestAttributedFigure) original.clone();

        assertFalse(clone.isAttributeEnabled(TEST_KEY));
    }
}