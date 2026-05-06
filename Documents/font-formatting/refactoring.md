# Refactoring - Lab4

The goal of this lab is to improve **code maintainability, readability, and robustness** by identifying code smells and applying behavior-preserving refactorings.

The refactoring work focuses on the **font formatting feature** of JHotDraw.  
The identified smells and refactorings follow the principles described in **[Ker05] – Refactoring to Patterns**.

---

# Refactoring by class

---

# `FontChooserHandler`

## Issue: nested if in `propertyChange(PropertyChangeEvent evt)`

The method originally used *nested conditional statements*, which made the logic harder to read and understand.

```before
@Override
public void propertyChange(PropertyChangeEvent evt) {
    if (isUpdating++ == 0) {
        if ((evt.getPropertyName() == null && JFontChooser.SELECTED_FONT_PROPERTY == null) ||
            (evt.getPropertyName() != null && evt.getPropertyName().equals(JFontChooser.SELECTED_FONT_PROPERTY))) {
            applySelectedFontToFigures();
        }
    }
    isUpdating--;
}
```

### Refactoring Applied
**Consolidate Conditional Expression**

The nested condition was merged into a single expression.

```after
@Override
public void propertyChange(PropertyChangeEvent evt) {
    if (isUpdating++ == 0 &&
        ((evt.getPropertyName() == null && JFontChooser.SELECTED_FONT_PROPERTY == null) ||
         (evt.getPropertyName() != null && evt.getPropertyName().equals(JFontChooser.SELECTED_FONT_PROPERTY)))) {

        applySelectedFontToFigures();
    }
    isUpdating--;
}
```

### Result

- Reduced nesting
- Improved readability
- Clearer control flow

This follows the principle of simplifying conditional logic described in **[Ker05] Refactoring to Patterns**.

---

# `SVGTextFigure`

## Issue: redundant temporary variable in `getTool(Point2D.Double p)`

The method introduced a *temporary variable that was immediately returned*.  
This creates unnecessary code and reduces readability.

```before
@Override
public Tool getTool(Point2D.Double p) {
    if (isEditable() && contains(p)) {
        TextEditingTool tool = new TextEditingTool(this);
        return tool;
    }
    return null;
}
```

### Refactoring Applied
**Inline Temporary Variable**

```after
@Override
public Tool getTool(Point2D.Double p) {
    if (isEditable() && contains(p)) {
        return new TextEditingTool(this);
    }
    return null;
}
```

### Result

- Removes unnecessary variable
- Simplifies the method
- Improves readability

---

## Issue: unsafe boolean condition when checking underline attribute

The original code assumed that the attribute value was always non-null.

```before
if (get(FONT_UNDERLINE)) {
    textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
}
```

### Refactoring Applied
**Introduce Null-Safe Boolean Check**

```after
if (Boolean.TRUE.equals(get(FONT_UNDERLINE))) {
    textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
}
```

### Result

- Prevents potential `NullPointerException`
- Improves robustness

---

## Issue: verbose empty string checks

```before
String text = getText();
if (text == null || text.length() == 0) {
    text = " ";
}
```

### Refactoring Applied
**Replace Expression with Intention-Revealing Method**

```after
String text = getText();
if (text == null || text.isEmpty()) {
    text = " ";
}
```

### Result

Improves readability and expresses the intent more clearly.

---

## Issue: inconsistent use of attribute keys

```before
if (key.equals(SVGAttributeKeys.TRANSFORM)
    || key.equals(SVGAttributeKeys.FONT_FACE)
    || key.equals(SVGAttributeKeys.FONT_BOLD)
    || key.equals(SVGAttributeKeys.FONT_ITALIC)
    || key.equals(SVGAttributeKeys.FONT_SIZE)) {
    invalidate();
}
```

### Refactoring Applied
**Use Consistent Abstraction**

```after
if (key.equals(AttributeKeys.TRANSFORM)
    || key.equals(AttributeKeys.FONT_FACE)
    || key.equals(AttributeKeys.FONT_BOLD)
    || key.equals(AttributeKeys.FONT_ITALIC)
    || key.equals(AttributeKeys.FONT_SIZE)) {
    invalidate();
}
```

### Result

Reduces unnecessary dependency on SVG-specific keys.

---

## Issue: switch statement without default branch

```before
switch (level) {
    case -1:
        handles.add(new BoundsOutlineHandle(this, false, true));
        break;
    case 0:
        ...
        break;
    case 1:
        TransformHandleKit.addTransformHandles(this, handles);
        break;
}
```

### Refactoring Applied
**Add Explicit Default Case**

```after
switch (level) {
    case -1:
        handles.add(new BoundsOutlineHandle(this, false, true));
        break;
    case 0:
        ...
        break;
    case 1:
        TransformHandleKit.addTransformHandles(this, handles);
        break;
    default:
        // no handles
        break;
}
```

### Result

Improves robustness and makes fallback behavior explicit.

---

# `SVGTextAreaFigure`

## Issue: Long Method in `getTextShape()`

The method originally contained a **very large block of logic** performing many responsibilities:

- computing layout parameters
- computing tab stops
- iterating over paragraphs
- performing line layout
- appending shapes

This is a classic **Long Method** smell from **[Ker05]**.

### Before

```before
private Shape getTextShape() {
    if (cachedTextShape == null) {
        Path2D.Double shape;
        cachedTextShape = shape = new Path2D.Double();
        if (getText() != null || isEditable()) {
            Font font = getFont();
            boolean isUnderlined = Boolean.TRUE.equals(get(FONT_UNDERLINE));
            Insets2D.Double insets = getInsets();
            Rectangle2D.Double textRect = new Rectangle2D.Double(
                    bounds.x + insets.left,
                    bounds.y + insets.top,
                    bounds.width - insets.left - insets.right,
                    bounds.height - insets.top - insets.bottom);
            float leftMargin = (float) textRect.x;
            float rightMargin = (float) Math.max(leftMargin + 1, textRect.x + textRect.width);
            float verticalPos = (float) textRect.y;
            float maxVerticalPos = (float) (textRect.y + textRect.height);
            if (leftMargin < rightMargin) {
                float tabWidth = (float) (getTabSize() * font.getStringBounds("m", getFontRenderContext()).getWidth());
                float[] tabStops = new float[(int) (textRect.width / tabWidth)];
                for (int i = 0; i < tabStops.length; i++) {
                    tabStops[i] = (float) (textRect.x + (int) (tabWidth * (i + 1)));
                }
                if (getText() != null) {
                    String[] paragraphs = getText().split("\n");
                    for (int i = 0; i < paragraphs.length; i++) {
                        if (paragraphs[i].isEmpty()) {
                            paragraphs[i] = " ";
                        }
                        AttributedString as = new AttributedString(paragraphs[i]);
                        as.addAttribute(TextAttribute.FONT, font);
                        if (isUnderlined) {
                            as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                        }
                        int tabCount = paragraphs[i].split("\t").length - 1;
                        Rectangle2D.Double paragraphBounds = appendParagraph(
                                shape, as.getIterator(),
                                verticalPos, maxVerticalPos, leftMargin, rightMargin, tabStops, tabCount);
                        verticalPos = (float) (paragraphBounds.y + paragraphBounds.height);
                        if (verticalPos > textRect.y + textRect.height) {
                            break;
                        }
                    }
                }
            }
        }
    }
    return cachedTextShape;
}
```

### Refactoring Applied

**Extract Method**

The method was decomposed into helper methods:

```
createTextRect()
createTabStops(...)
appendParagraphs(...)
layoutLine(...)
appendLayouts(...)
findTabLocations(...)
moveToNextTabStop(...)
```

### After

```after
private Shape getTextShape() {
    if (cachedTextShape == null) {
        Path2D.Double shape;
        cachedTextShape = shape = new Path2D.Double();
        if (getText() != null || isEditable()) {
            Font font = getFont();
            boolean isUnderlined = Boolean.TRUE.equals(get(FONT_UNDERLINE));
            Rectangle2D.Double textRect = createTextRect();
            float leftMargin = (float) textRect.x;
            float rightMargin = (float) Math.max(leftMargin + 1, textRect.x + textRect.width);
            float verticalPos = (float) textRect.y;
            float maxVerticalPos = (float) (textRect.y + textRect.height);
            if (leftMargin < rightMargin) {
                float[] tabStops = createTabStops(font, textRect);
                if (getText() != null) {
                    appendParagraphs(font, isUnderlined, shape, verticalPos, maxVerticalPos, leftMargin, rightMargin, tabStops, textRect);
                }
            }
        }
    }
    return cachedTextShape;
}
```
### Result

- reduces complexity
- separates responsibilities
- improves readability
- easier to maintain

---

# `JFontChooser`

## Issue: Long Method in `updateSelectionPath(Font newValue)`

The original method contained complex nested logic and multiple search loops.

### Before

```before
protected void updateSelectionPath(Font newValue) {
    if (newValue == null || selectionPath == null || selectionPath.getPathCount() != 4
            || !((FontFaceNode) selectionPath.getLastPathComponent()).getFont().getFontName().equals(newValue.getFontName())) {
        if (newValue == null) {
            setSelectionPath(null);
        } else {
            TreePath path = selectionPath;
            FontCollectionNode oldCollection = (path != null && path.getPathCount() > 1) ? (FontCollectionNode) path.getPathComponent(1) : null;
            FontFamilyNode oldFamily = (path != null && path.getPathCount() > 2) ? (FontFamilyNode) path.getPathComponent(2) : null;
            FontFaceNode oldFace = (path != null && path.getPathCount() > 3) ? (FontFaceNode) path.getPathComponent(3) : null;
            FontCollectionNode newCollection = oldCollection;
            FontFamilyNode newFamily = oldFamily;
            FontFaceNode newFace = null;

            // search in the current family
            if (newFace == null && newFamily != null) {
                for (FontFaceNode face : newFamily.faces()) {
                    if (face.getFont().getFontName().equals(newValue.getFontName())) {
                        newFace = face;
                        break;
                    }
                }
            }

            // search in the current collection
            if (newFace == null && newCollection != null) {
                for (FontFamilyNode family : newCollection.families()) {
                    for (FontFaceNode face : family.faces()) {
                        if (face.getFont().getFontName().equals(newValue.getFontName())) {
                            newFamily = family;
                            newFace = face;
                            break;
                        }
                    }
                }
            }

            // search in all collections
            if (newFace == null) {
                TreeNode root = (TreeNode) getModel().getRoot();
                OuterLoop:
                for (int i = 0, n = root.getChildCount(); i < n; i++) {
                    FontCollectionNode collection = (FontCollectionNode) root.getChildAt(i);
                    for (FontFamilyNode family : collection.families()) {
                        for (FontFaceNode face : family.faces()) {
                            if (face.getFont().getFontName().equals(newValue.getFontName())) {
                                newCollection = collection;
                                newFamily = family;
                                newFace = face;
                                break OuterLoop;
                            }
                        }
                    }
                }
            }

            if (newFace != null) {
                setSelectionPath(new TreePath(new Object[]{
                        getModel().getRoot(), newCollection, newFamily, newFace
                }));
            } else {
                setSelectionPath(null);
            }
        }
    }
}
```

### Refactoring Applied
**Extract Method**

Helper methods were introduced:

```
needsSelectionPathUpdate(...)
findFaceInFamily(...)
findFaceInCollection(...)
findFaceInAllCollections(...)
```

A helper class was also introduced:

```
FontPathMatch
```

### After

```after
protected void updateSelectionPath(Font newValue) {
    if (!needsSelectionPathUpdate(newValue)) {
        return;
    }
    if (newValue == null) {
        setSelectionPath(null);
        return;
    }

    TreePath path = selectionPath;
    FontCollectionNode oldCollection = (path != null && path.getPathCount() > 1)
            ? (FontCollectionNode) path.getPathComponent(1) : null;
    FontFamilyNode oldFamily = (path != null && path.getPathCount() > 2)
            ? (FontFamilyNode) path.getPathComponent(2) : null;

    FontCollectionNode newCollection = oldCollection;
    FontFamilyNode newFamily = oldFamily;
    FontFaceNode newFace = null;

    newFace = findFaceInFamily(newFamily, newValue);

    if (newFace == null) {
        FontPathMatch match = findFaceInCollection(newCollection, newValue);
        if (match != null) {
            newFamily = match.family;
            newFace = match.face;
        }
    }

    if (newFace == null) {
        FontPathMatch match = findFaceInAllCollections(newValue);
        if (match != null) {
            newCollection = match.collection;
            newFamily = match.family;
            newFace = match.face;
        }
    }

    if (newFace != null) {
        setSelectionPath(new TreePath(new Object[]{
                getModel().getRoot(), newCollection, newFamily, newFace
        }));
    } else {
        setSelectionPath(null);
    }
}
```

### Result

- smaller method
- clearer algorithm structure
- improved maintainability

---

# `DefaultFontChooserModel`

## Issue: Magic String List in `setFonts(Font[] fonts)`

Font lists were embedded directly inside method calls.

### Before

```before
root.add(
        new FontCollectionNode(labels.getString("FontCollection.web"), collectFamiliesNamed(families,
                "Arial",
                "Arial Black",
                "Comic Sans MS",
                "Georgia",
                "Impact",
                "Times New Roman",
                "Trebuchet MS",
                "Verdana",
                "Webdings")));
```

### Refactoring Applied
**Replace Magic Values with Symbolic Constant**

```after
private static final String[] WEB_SAFE_FONTS = {
        "Arial",
        "Arial Black",
        "Comic Sans MS",
        "Georgia",
        "Impact",
        "Times New Roman",
        "Trebuchet MS",
        "Verdana",
        "Webdings"
};
```

and

```after
root.add(new FontCollectionNode(
        labels.getString("FontCollection.web"),
        collectFamiliesNamed(families, WEB_SAFE_FONTS)));
```

### Result

- improves readability
- avoids duplicated literal lists
- simplifies maintenance

---

# Larger Code Smells Identified (Future Refactoring)

During inspection of the feature area, additional design issues were observed.

## Duplicated behavior in text figure classes

Classes:

- `SVGTextFigure`
- `SVGTextAreaFigure`

share responsibilities for:

- text handling
- font attributes
- editing logic

Possible improvement:

```
Extract Superclass
AbstractSVGTextFigure
```

---

## Multiple responsibilities in `FontChooserHandler`

`FontChooserHandler` currently:

- listens to UI events
- updates figure fonts
- updates editor defaults
- manages undoable edits

Possible improvement:

```
Extract Method
Extract Class
```

---

### Refactoring Strategy

The applied refactorings follow the principles described in **[Ker05] – Refactoring to Patterns**.

The goal was to remove common code smells while preserving program behavior:

- **Long Method → Extract Method**  
  Large methods were decomposed into smaller helper methods to improve readability and separation of responsibilities.

- **Nested Conditional Logic → Consolidate Conditional Expression**  
  Reduces deep nesting and simplifies the control flow.

- **Temporary Variables → Inline Temporary Variable**  
  Eliminates unnecessary variables and makes the code easier to read.

- **Magic Values → Replace Magic Value with Symbolic Constant**  
  Improves maintainability and avoids hard-coded values inside methods.

These refactorings improve maintainability while keeping the system behavior unchanged.

# Conclusion

The refactorings performed in this lab improved the **maintainability, readability, and robustness** of the font formatting feature.

Applied refactorings include:

- **Consolidate Conditional Expression**
- **Inline Temporary Variable**
- **Extract Method**
- **Replace Magic Values with Symbolic Constant**