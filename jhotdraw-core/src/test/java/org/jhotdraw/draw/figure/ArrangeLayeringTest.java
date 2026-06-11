package org.jhotdraw.draw.action;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ArrangeLayeringTest {

    private DrawingView mockView;
    private Drawing mockDrawing;
    private Figure mockFigureA;
    private Figure mockFigureB;
    private Figure mockFigureC;

    @Before
    public void setUp() {
        mockView = mock(DrawingView.class);
        mockDrawing = mock(Drawing.class);
        mockFigureA = mock(Figure.class);
        mockFigureB = mock(Figure.class);
        mockFigureC = mock(Figure.class);

        when(mockView.getDrawing()).thenReturn(mockDrawing);
    }

    // --- SendToBack Tests ---
    // Best case: single figure sent to back
    @Test
    public void testSendToBack_SingleFigure() {
        List<Figure> figures = Collections.singletonList(mockFigureA);
        when(mockDrawing.sort(figures)).thenReturn(figures);

        SendToBackAction.sendToBack(mockView, figures);

        verify(mockDrawing).sendToBack(mockFigureA);
    }

    // Best case: multiple figures sent to back — order preserved via sort
    @Test
    public void testSendToBack_MultipleFigures_SortCalled() {
        List<Figure> figures = Arrays.asList(mockFigureA, mockFigureB, mockFigureC);
        when(mockDrawing.sort(figures)).thenReturn(figures);

        SendToBackAction.sendToBack(mockView, figures);

        verify(mockDrawing).sort(figures);
        verify(mockDrawing).sendToBack(mockFigureA);
        verify(mockDrawing).sendToBack(mockFigureB);
        verify(mockDrawing).sendToBack(mockFigureC);
    }

    // Boundary case: empty selection — nothing should be called on drawing
    @Test
    public void testSendToBack_EmptySelection() {
        List<Figure> figures = Collections.emptyList();
        when(mockDrawing.sort(figures)).thenReturn(figures);

        SendToBackAction.sendToBack(mockView, figures);

        verify(mockDrawing, never()).sendToBack(any(Figure.class));
    }

    // --- BringToFront Tests ---
    // Best case: single figure brought to front
    @Test
    public void testBringToFront_SingleFigure() {
        List<Figure> figures = Collections.singletonList(mockFigureA);
        when(mockDrawing.sort(figures)).thenReturn(figures);

        BringToFrontAction.bringToFront(mockView, figures);

        verify(mockDrawing).bringToFront(mockFigureA);
    }

    // Best case: multiple figures — sort called before iterating
    @Test
    public void testBringToFront_MultipleFigures_SortCalled() {
        List<Figure> figures = Arrays.asList(mockFigureA, mockFigureB);
        when(mockDrawing.sort(figures)).thenReturn(figures);

        BringToFrontAction.bringToFront(mockView, figures);

        verify(mockDrawing).sort(figures);
        verify(mockDrawing).bringToFront(mockFigureA);
        verify(mockDrawing).bringToFront(mockFigureB);
    }

    // Boundary case: empty selection
    @Test
    public void testBringToFront_EmptySelection() {
        List<Figure> empty = Collections.emptyList();
        when(mockDrawing.sort(empty)).thenReturn(empty);

        BringToFrontAction.bringToFront(mockView, empty);

        verify(mockDrawing, never()).bringToFront(any(Figure.class));
    }

    // Symmetry test: sendToBack and bringToFront are inverse operations
    // This verifies the undo/redo contract described in the portfolio
    @Test
    public void testSymmetry_SendBackThenBringFront() {
        List<Figure> figures = Collections.singletonList(mockFigureA);
        when(mockDrawing.sort(figures)).thenReturn(figures);

        SendToBackAction.sendToBack(mockView, figures);
        BringToFrontAction.bringToFront(mockView, figures);

        verify(mockDrawing).sendToBack(mockFigureA);
        verify(mockDrawing).bringToFront(mockFigureA);
    }
}
