package org.jhotdraw.samples.svg;

import java.io.File;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.samples.svg.figures.SVGGroupFigure;
import org.jhotdraw.samples.svg.figures.SVGImageFigure;
import org.jhotdraw.samples.svg.io.SVGInputFormat;
import org.jhotdraw.samples.svg.io.SVGZInputFormat;
import org.junit.Test;

import static org.junit.Assert.*;

public class SVGCreateFromFileToolTest {

    @Test
    public void isSvgFileReturnsTrueForSvgFile() {
        SVGCreateFromFileTool tool = createTool();

        boolean result = tool.isSvgFile(new File("drawing.svg"));

        assertTrue(result);
    }

    @Test
    public void isSvgFileReturnsTrueForSvgzFile() {
        SVGCreateFromFileTool tool = createTool();

        boolean result = tool.isSvgFile(new File("drawing.svgz"));

        assertTrue(result);
    }

    @Test
    public void isSvgFileReturnsFalseForPngFile() {
        SVGCreateFromFileTool tool = createTool();

        boolean result = tool.isSvgFile(new File("image.png"));

        assertFalse(result);
    }

    @Test
    public void isSvgFileHandlesUppercaseExtension() {
        SVGCreateFromFileTool tool = createTool();

        boolean result = tool.isSvgFile(new File("drawing.SVG"));

        assertTrue(result);
    }

    @Test
    public void createInputFormatReturnsSvgInputFormatForSvgFile() {
        SVGCreateFromFileTool tool = createTool();

        InputFormat result = tool.createInputFormat(new File("drawing.svg"));

        assertTrue(result instanceof SVGInputFormat);
    }

    @Test
    public void createInputFormatReturnsSvgzInputFormatForSvgzFile() {
        SVGCreateFromFileTool tool = createTool();

        InputFormat result = tool.createInputFormat(new File("drawing.svgz"));

        assertTrue(result instanceof SVGZInputFormat);
    }

    private SVGCreateFromFileTool createTool() {
        return new SVGCreateFromFileTool(
                new SVGImageFigure(),
                new SVGGroupFigure()
        );
    }
}