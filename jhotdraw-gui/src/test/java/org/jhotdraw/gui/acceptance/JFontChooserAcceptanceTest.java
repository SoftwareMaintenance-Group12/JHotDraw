package org.jhotdraw.gui.acceptance;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.jhotdraw.gui.JFontChooser;
import org.junit.Test;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class JFontChooserAcceptanceTest
        extends ScenarioTest<
        JFontChooserAcceptanceTest.GivenFontChooser,
        JFontChooserAcceptanceTest.WhenFontChooser,
        JFontChooserAcceptanceTest.ThenFontChooser> {

    @Test
    public void selected_font_change_should_notify_listeners() {
        given().a_font_chooser();

        when().a_new_font_is_selected();

        then().the_selected_font_should_be_updated()
                .and().a_property_change_event_should_be_fired();
    }

    public static class GivenFontChooser extends Stage<GivenFontChooser> {

        @ScenarioState
        JFontChooser fontChooser;

        @ScenarioState
        Font selectedFont;

        @ScenarioState
        AtomicReference<PropertyChangeEvent> receivedEvent;

        public GivenFontChooser a_font_chooser() {
            fontChooser = new JFontChooser();
            selectedFont = new Font("Serif", Font.BOLD, 18);
            receivedEvent = new AtomicReference<>();

            fontChooser.addPropertyChangeListener(
                    JFontChooser.SELECTED_FONT_PROPERTY,
                    receivedEvent::set
            );

            return self();
        }
    }

    public static class WhenFontChooser extends Stage<WhenFontChooser> {

        @ScenarioState
        JFontChooser fontChooser;

        @ScenarioState
        Font selectedFont;

        public WhenFontChooser a_new_font_is_selected() {
            fontChooser.setSelectedFont(selectedFont);
            return self();
        }
    }

    public static class ThenFontChooser extends Stage<ThenFontChooser> {

        @ScenarioState
        JFontChooser fontChooser;

        @ScenarioState
        Font selectedFont;

        @ScenarioState
        AtomicReference<PropertyChangeEvent> receivedEvent;

        public ThenFontChooser the_selected_font_should_be_updated() {
            assertSame(selectedFont, fontChooser.getSelectedFont());
            return self();
        }

        public ThenFontChooser a_property_change_event_should_be_fired() {
            PropertyChangeEvent event = receivedEvent.get();

            assertNotNull(event);
            assertEquals(JFontChooser.SELECTED_FONT_PROPERTY, event.getPropertyName());
            assertSame(selectedFont, event.getNewValue());

            return self();
        }
    }
}