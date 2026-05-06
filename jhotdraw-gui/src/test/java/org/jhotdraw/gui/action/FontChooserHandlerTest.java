package org.jhotdraw.gui.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.gui.JFontChooser;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JPopupMenu;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import static org.mockito.Mockito.*;

public class FontChooserHandlerTest {

    private DrawingEditor editor;
    private DrawingView view;
    private JFontChooser fontChooser;
    private JPopupMenu popupMenu;
    private AttributeKey<Font> key;
    private FontChooserHandler handler;

    @Before
    public void setUp() {
        editor = mock(DrawingEditor.class);
        view = mock(DrawingView.class);
        fontChooser = mock(JFontChooser.class);
        popupMenu = mock(JPopupMenu.class);
        key = mock(AttributeKey.class);

        when(editor.getActiveView()).thenReturn(view);
        when(editor.isEnabled()).thenReturn(true);
        when(view.getSelectionCount()).thenReturn(0);

        handler = new FontChooserHandler(editor, key, fontChooser, popupMenu);
    }

    @Test
    public void actionPerformedCancelSelectionHidesPopupMenu() {
        ActionEvent evt = new ActionEvent(this, 0, JFontChooser.CANCEL_SELECTION);

        handler.actionPerformed(evt);

        verify(popupMenu).setVisible(false);
    }

    @Test
    public void actionPerformedWithNullCommandDoesNotCrashAndHidesPopupMenu() {
        ActionEvent evt = new ActionEvent(this, 0, null);

        handler.actionPerformed(evt);

        verify(popupMenu).setVisible(false);
    }

    @Test
    public void propertyChangeWithDifferentPropertyDoesNothing() {
        PropertyChangeEvent evt = new PropertyChangeEvent(
                this,
                "OTHER_PROPERTY",
                null,
                null
        );

        handler.propertyChange(evt);

        // passes if no exception occurs
    }

    @Test
    public void propertyChangeWithNullPropertyNameDoesNotCrash() {
        PropertyChangeEvent evt = new PropertyChangeEvent(
                this,
                null,
                null,
                null
        );

        handler.propertyChange(evt);

        // specifically validates null-safe refactoring
    }

    @Test
    public void updateEnabledStateDisablesChooserWhenNoSelection() {
        when(view.getSelectionCount()).thenReturn(0);

        handler.updateEnabledState();

        verify(fontChooser, atLeastOnce()).setEnabled(false);
        verify(popupMenu, atLeastOnce()).setEnabled(false);
    }

    @Test
    public void updateEnabledStateEnablesChooserWhenSelectionExists() {
        when(view.getSelectionCount()).thenReturn(2);

        handler.updateEnabledState();

        verify(fontChooser, atLeastOnce()).setEnabled(true);
        verify(popupMenu, atLeastOnce()).setEnabled(true);
    }
}