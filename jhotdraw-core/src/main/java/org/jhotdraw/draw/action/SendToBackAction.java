/*
 * @(#)SendToBackAction.java
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
 * SendToBackAction moves selected figures to the back of the drawing's z-order.
 *
 * This action is part of the arrange feature. It is triggered by the UI when
 * the user chooses "Send to Back" for one or more selected figures. It relies
 * on the following connections:
 *
 * - {@link AbstractSelectedAction}: parent class that provides access to the
 * {@link DrawingEditor} and selection state. - {@link DrawingView}: obtains
 * selected figures and the associated drawing. - {@link Drawing}: the drawing
 * model that actually performs z-order changes. - {@link Figure}: the
 * individual drawable objects that are reordered.
 *
 * The action also creates an undoable edit so the command can be undone and
 * redone. Undo is implemented by calling the opposite arrange action,
 * {@link BringToFrontAction}.
 *
 * Dependencies: - DrawingEditor -> DrawingView -> Drawing model -
 * ResourceBundleUtil -> localized UI labels for this action - UndoableEdit
 * system -> undo/redo support
 *
 * Arrange feature context: - sendToBack(view, figures): reorder figures behind
 * others - bringToFront(view, figures): reorder figures to front of others
 *
 * The z-order is typically maintained by a collection in the drawing model
 * where index 0 is the back and later indices are in front.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SendToBackAction extends AbstractSelectedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.sendToBack";

    /**
     * Initialize the action.
     *
     * super(editor): initializes the action with the drawing editor context.
     * ResourceBundleUtil: loads the label/tooltip/icon from resource bundles.
     * updateEnabledState(): sets enabled/disabled depending on selection state.
     */
    public SendToBackAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels
                = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // Get the current drawing view from the editor.
        // The view contains the selected figures and a reference to the drawing.
        final DrawingView view = getView();

        // Copy the current selection to preserve order and avoid modifying
        // the selection list while performing the reorder operation.
        final LinkedList<Figure> figures = new LinkedList<>(view.getSelectedFigures());

        // Perform the arrange operation on the drawing model.
        sendToBack(view, figures);

        // Fire an undoable edit so this action can be undone/redone later.
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
                // Redo repeats the same arrange operation.
                SendToBackAction.sendToBack(view, figures);
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                // Undo is implemented by calling the opposite action.
                // This restores the arrange state by moving the same figures
                // back to the front.
                BringToFrontAction.bringToFront(view, figures);
            }
        });
    }

    /**
     * Reorders the given figures to the back of the drawing.
     *
     * This helper does the actual model update. It is static so it can be
     * reused in the main action and in redo logic without duplicating code.
     *
     * @param view the drawing view providing access to the model
     * @param figures figures to send to back
     */
    public static void sendToBack(DrawingView view, Collection<Figure> figures) {
        // Get the drawing model that stores figures in z-order.
        Drawing drawing = view.getDrawing();

        for (Figure figure : drawing.sort(figures)) { // Refactored: Sorted figures to maintain relative Z-order
            // Delegate the z-order change to the drawing implementation.
            // In a typical drawing implementation (e.g. AbstractCompositeFigure),
            // this will remove the figure from its current index and insert it at 0.
            drawing.sendToBack(figure);
        }
    }
}
