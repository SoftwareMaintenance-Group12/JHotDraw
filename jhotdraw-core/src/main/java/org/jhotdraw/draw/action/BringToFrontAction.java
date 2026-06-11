/*
 * @(#)BringToFrontAction.java
 *
 * Copyright (c) 2003-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.figure.Figure;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * BringToFrontAction moves selected figures to the front of the drawing's
 * z-order.
 *
 * This action is the counterpart to {@link SendToBackAction} in the arrange
 * feature. It is invoked when the user selects "Bring to Front." It uses the
 * drawing model to reorder figures and also provides undo/redo integration.
 *
 * Dependencies and connections: - {@link AbstractSelectedAction}: gives access
 * to drawing editor and selection. - {@link DrawingView}: provides the selected
 * figures and the drawing model. - {@link Drawing}: performs the actual z-order
 * operations. - {@link Figure}: the individual drawing elements whose order
 * changes.
 *
 * Unlike SendToBackAction, this action sorts the selected figures before moving
 * them. This preserves relative ordering among selected figures while moving
 * them forward.
 *
 * Arrange feature context: - bringToFront(view, figures): reorder selected
 * figures to be on top. - sendToBack(view, figures): opposite arrange action
 * used for undo.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BringToFrontAction extends AbstractSelectedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.bringToFront";

    /**
     * Initialize the action and configure UI text/icon from resources.
     */
    public BringToFrontAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels
                = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // Get the current drawing view from the editor.
        final DrawingView view = getView();

        // Copy the selected figures so we can use the exact selection set
        // for undo/redo and for the arrange operation.
        final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());

        // Perform the item reordering in the drawing model.
        bringToFront(view, figures);

        // Register undo/redo support for this arrange action.
        fireUndoableEditHappened(new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                ResourceBundleUtil labels
                        = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                return labels.getTextProperty(ID);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                // Redo repeats the bring-to-front operation.
                BringToFrontAction.bringToFront(view, figures);
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                // Undo reverses the operation by sending the same figures to back.
                SendToBackAction.sendToBack(view, figures);
            }
        });
    }

    /**
     * Reorders the collection of figures so each figure is moved to the front.
     *
     * The drawing model may implement this by appending figures to the end of
     * an internal list, where the end of the list represents the front.
     *
     * The call to drawing.sort(figures) ensures the selected figures are
     * processed in z-order from back to front, preserving their relative order.
     *
     * @param view the drawing view providing access to the drawing model
     * @param figures the figures to bring to front
     */
    public static void bringToFront(DrawingView view, Collection<Figure> figures) {
        assert view != null : "DrawingView must not be null";
        assert figures != null : "Figures collection must not be null";
        Drawing drawing = view.getDrawing();
        for (Figure figure : drawing.sort(figures)) {
            drawing.bringToFront(figure);
        }
    }
}
