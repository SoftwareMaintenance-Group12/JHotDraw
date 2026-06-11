/*
 * @(#)ImageTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.ImageHolderFigure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.samples.svg.io.SVGInputFormat;
import org.jhotdraw.samples.svg.io.SVGZInputFormat;

/**
 * A tool to create new figures from an input file. If the file holds a bitmap
 * image, this tool creates a SVGImageFigure. If the file holds a SVG or a SVGZ
 * image, ths tool creates a SVGGroupFigure.
 * <p>
 * Immediately, after the
 * ImageTool has been activated, it opens a JFileChooser, letting the user
 * specify a file. The the user then performs the following mouse gesture: <ol>
 * <li>Press the mouse button and drag the mouse over the DrawingView. This
 * defines the bounds of the created Figure.</li> </ol>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGCreateFromFileTool extends CreationTool {

    private static final long serialVersionUID = 1L;
    protected FileDialog fileDialog;
    protected JFileChooser fileChooser;
    protected CompositeFigure groupPrototype;
    protected ImageHolderFigure imagePrototype;
    protected boolean useFileDialog;

    /**
     * Creates a new instance.
     */
    public SVGCreateFromFileTool(ImageHolderFigure imagePrototype, CompositeFigure groupPrototype) {
        super(imagePrototype);
        this.groupPrototype = groupPrototype;
        this.imagePrototype = imagePrototype;
    }

    /**
     * Creates a new instance.
     */
    public SVGCreateFromFileTool(ImageHolderFigure imagePrototype, CompositeFigure groupPrototype, Map<AttributeKey<?>, Object> attributes) {
        super(imagePrototype, attributes);
        this.groupPrototype = groupPrototype;
        this.imagePrototype = imagePrototype;
    }

    public void setUseFileDialog(boolean newValue) {
        useFileDialog = newValue;
        if (useFileDialog) {
            fileChooser = null;
        } else {
            fileDialog = null;
        }
    }

    public boolean isUseFileDialog() {
        return useFileDialog;
    }

    @Override
    public void activate(DrawingEditor editor) {
        super.activate(editor);

        final DrawingView view = getView();
        if (view == null) {
            return;
        }

        final File file = chooseFile(view);
        if (file == null) {
            finishToolIfNeeded();
            return;
        }

        handleSelectedFile(file, view);
    }

    private File chooseFile(DrawingView view) {
        if (useFileDialog) {
            getFileDialog().setVisible(true);
            if (getFileDialog().getFile() != null) {
                return new File(getFileDialog().getDirectory(), getFileDialog().getFile());
            }
            return null;
        }

        if (getFileChooser().showOpenDialog(view.getComponent()) == JFileChooser.APPROVE_OPTION) {
            return getFileChooser().getSelectedFile();
        }

        return null;
    }

    private void handleSelectedFile(final File file, final DrawingView view) {
        if (isSvgFile(file)) {
            loadSvgFile(file, view);
        } else {
            loadBitmapImage(file, view);
        }
    }

    private boolean isSvgFile(File file) {
        String fileName = file.getName().toLowerCase(Locale.ENGLISH);
        return fileName.endsWith(".svg") || fileName.endsWith(".svgz");
    }

    private void loadSvgFile(final File file, final DrawingView view) {
        prototype = groupPrototype.clone();

        new SwingWorker<Drawing, Drawing>() {
            @Override
            protected Drawing doInBackground() throws Exception {
                Drawing drawing = new DefaultDrawing();
                InputFormat inputFormat = createInputFormat(file);
                inputFormat.read(file.toURI(), drawing);
                return drawing;
            }

            @Override
            protected void done() {
                try {
                    applySvgDrawing(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    failed(view, ex);
                } catch (ExecutionException ex) {
                    failed(view, ex);
                }
            }
        }.execute();
    }

    private InputFormat createInputFormat(File file) {
        String fileName = file.getName().toLowerCase(Locale.ENGLISH);
        return fileName.endsWith(".svg") ? new SVGInputFormat() : new SVGZInputFormat();
    }

    private void applySvgDrawing(Drawing drawing) {
        CompositeFigure parent = getSvgParentFigure();

        if (createdFigure != null) {
            parent.willChange();
        }

        for (Figure figure : drawing.getChildren()) {
            if (createdFigure == null) {
                parent.basicAdd(figure);
            } else {
                parent.add(figure);
            }
        }

        if (createdFigure != null) {
            parent.changed();
        }
    }

    private CompositeFigure getSvgParentFigure() {
        if (createdFigure == null) {
            return (CompositeFigure) prototype;
        }
        return (CompositeFigure) createdFigure;
    }

    private void loadBitmapImage(final File file, final DrawingView view) {
        prototype = imagePrototype;
        final ImageHolderFigure loaderFigure = (ImageHolderFigure) prototype.clone();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loaderFigure.loadImage(file);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    applyLoadedImage(loaderFigure, view);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    failed(view, ex);
                } catch (ExecutionException ex) {
                    failed(view, ex);
                }
            }
        }.execute();
    }

    private void applyLoadedImage(ImageHolderFigure loaderFigure, DrawingView view) {
        try {
            ImageHolderFigure targetFigure = getImageTargetFigure();
            targetFigure.setImage(loaderFigure.getImageData(), loaderFigure.getBufferedImage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    view.getComponent(),
                    ex.getMessage(),
                    null,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageHolderFigure getImageTargetFigure() {
        if (createdFigure == null) {
            return (ImageHolderFigure) prototype;
        }
        return (ImageHolderFigure) createdFigure;
    }

    private void failed(DrawingView view, Throwable throwable) {
        Logger.getLogger(SVGCreateFromFileTool.class.getName()).log(Level.SEVERE, null, throwable);
        JOptionPane.showMessageDialog(
                view.getComponent(),
                throwable.getMessage(),
                null,
                JOptionPane.ERROR_MESSAGE);

        if (createdFigure != null) {
            getDrawing().remove(createdFigure);
        }

        fireToolDone();
    }

    private void finishToolIfNeeded() {
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
    }

    @Override
    protected Figure createFigure() {
        if (prototype instanceof CompositeFigure) {
            // we must not apply default attributs to the composite figure,
            // because this would change the look of the figures that we
            // read from the SVG file.
            return prototype.clone();
        } else {
            return super.createFigure();
        }
    }

    private JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        return fileChooser;
    }

    private FileDialog getFileDialog() {
        if (fileDialog == null) {
            fileDialog = new FileDialog(new Frame());
        }
        return fileDialog;
    }
}
