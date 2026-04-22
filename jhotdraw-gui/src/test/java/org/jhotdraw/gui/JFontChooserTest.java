package org.jhotdraw.gui;

import org.jhotdraw.gui.fontchooser.FontChooserModel;
import org.junit.Test;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JFontChooserTest {

    @Test
    public void approveSelectionFiresApproveAction() {
        JFontChooser chooser = new JFontChooser();
        ActionListener listener = mock(ActionListener.class);
        chooser.addActionListener(listener);

        chooser.approveSelection();

        verify(listener).actionPerformed(argThat(event ->
                event != null
                        && JFontChooser.APPROVE_SELECTION.equals(event.getActionCommand())
                        && event.getSource() == chooser
        ));
    }

    @Test
    public void cancelSelectionFiresCancelAction() {
        JFontChooser chooser = new JFontChooser();
        ActionListener listener = mock(ActionListener.class);
        chooser.addActionListener(listener);

        chooser.cancelSelection();

        verify(listener).actionPerformed(argThat(event ->
                event != null
                        && JFontChooser.CANCEL_SELECTION.equals(event.getActionCommand())
                        && event.getSource() == chooser
        ));
    }

    @Test
    public void removeActionListenerPreventsFurtherNotifications() {
        JFontChooser chooser = new JFontChooser();
        ActionListener listener = mock(ActionListener.class);
        chooser.addActionListener(listener);
        chooser.removeActionListener(listener);

        chooser.approveSelection();

        verify(listener, never()).actionPerformed(any(ActionEvent.class));
    }

    @Test
    public void setSelectedFontStoresSelectedFont() {
        JFontChooser chooser = new JFontChooser();
        Font font = new Font("Dialog", Font.BOLD, 14);

        chooser.setSelectedFont(font);

        assertEquals(font, chooser.getSelectedFont());
    }

    @Test
    public void setSelectedFontAcceptsNull() {
        JFontChooser chooser = new JFontChooser();
        chooser.setSelectedFont(new Font("Dialog", Font.PLAIN, 12));

        chooser.setSelectedFont(null);

        assertNull(chooser.getSelectedFont());
    }

    @Test
    public void setModelReplacesModelReference() {
        JFontChooser chooser = new JFontChooser();
        FontChooserModel model = mock(FontChooserModel.class);

        chooser.setModel(model);

        assertSame(model, chooser.getModel());
    }

    @Test
    public void setModelRegistersListenerOnNewModel() {
        JFontChooser chooser = new JFontChooser();
        FontChooserModel model = mock(FontChooserModel.class);

        chooser.setModel(model);

        verify(model, atLeastOnce()).addTreeModelListener(any());
    }

    @Test
    public void getAllFontsReturnsNonNullClone() {
        Font[] fonts1 = JFontChooser.getAllFonts();
        Font[] fonts2 = JFontChooser.getAllFonts();

        assertNotNull(fonts1);
        assertNotNull(fonts2);
        assertNotSame(fonts1, fonts2);
    }
}