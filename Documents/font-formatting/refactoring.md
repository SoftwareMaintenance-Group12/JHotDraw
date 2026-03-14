# Refactoring - Lab4
Is to improve code maintainability, readability, and robustness by identifying and addressing code smells

# Refactoring by class
## `FontChooserHandler` 
### Issue: nested if in `propertyChange(PropertyChangeEvent evt)`
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
Refactoring Applied: *Consolidate Conditional Expression* - The nested condition was merged into a single expression.
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
Result: follows the principle of simplifying conditional logic as recommended in [Ker05] - *Refactoring to Patterns*.
Reduced nesting, Improved readability, Clearer control flow.

## `SVGTextFigure`
### Issue: redundant temporary variable in `getTool(Point2D.Double p)`

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

Refactoring Applied: *Inline Temporary Variable*

```after
@Override
public Tool getTool(Point2D.Double p) {
    if (isEditable() && contains(p)) {
        return new TextEditingTool(this);
    }
    return null;
}
```
Result: removes unnecessary variable, simplifies the method and improves readability as suggested in [Ker05].
---

### Issue: unsafe boolean condition when checking underline attribute

The original code assumed that the attribute value was always non-null.  
However attributes in JHotDraw may return `null`, which could lead to fragile behavior.

```before
if (get(FONT_UNDERLINE)) {
    textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
}
```

Refactoring Applied: *Introduce Null-Safe Boolean Check*

```after
if (Boolean.TRUE.equals(get(FONT_UNDERLINE))) {
    textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
}
```

Result: improves robustness by avoiding potential null dereference and making the condition safer.


---

### Issue: verbose empty string checks

The code used the older pattern `text.length() == 0` to check for empty strings.  
This reduces readability and is less idiomatic in modern Java.

```before
String text = getText();
if (text == null || text.length() == 0) {
    text = " ";
}
```

Refactoring Applied: *Replace Expression with Intention-Revealing Method*

```after
String text = getText();
if (text == null || text.isEmpty()) {
    text = " ";
}
```

Result: clearer intention and improved readability.


---

### Issue: inconsistent use of attribute keys

The class used both `SVGAttributeKeys` and `AttributeKeys` for attributes that belong to the general attribute system.

```before
if (key.equals(SVGAttributeKeys.TRANSFORM)
    || key.equals(SVGAttributeKeys.FONT_FACE)
    || key.equals(SVGAttributeKeys.FONT_BOLD)
    || key.equals(SVGAttributeKeys.FONT_ITALIC)
    || key.equals(SVGAttributeKeys.FONT_SIZE)) {
    invalidate();
}
```

Refactoring Applied: *Use Consistent Abstraction*

```after
if (key.equals(AttributeKeys.TRANSFORM)
    || key.equals(AttributeKeys.FONT_FACE)
    || key.equals(AttributeKeys.FONT_BOLD)
    || key.equals(AttributeKeys.FONT_ITALIC)
    || key.equals(AttributeKeys.FONT_SIZE)) {
    invalidate();
}
```

Result: improves abstraction consistency and reduces unnecessary dependency on SVG-specific keys.


---

### Issue: switch statement without default branch

The `switch` statement handling `detailLevel` did not include a default branch.  
Although the expected values are controlled, explicitly handling unexpected values improves defensive programming.

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

Refactoring Applied: *Add Explicit Default Case*

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

Result: improves robustness and clarifies fallback behavior.


---

# Larger Code Smells Identified (Future Refactoring)

During inspection of the feature area, some **larger design issues** were also identified but were not refactored in this lab.

## Duplicated behavior in text figure classes

Classes such as:

- `SVGTextFigure`
- `SVGTextAreaFigure`

share similar responsibilities for:

- text content management
- font attribute handling
- editing behavior
- attribute invalidation

This suggests a potential **Extract Superclass** refactoring.

Possible improvement:

```
AbstractSVGTextFigure
```

Benefits:

- reduce duplicated logic
- centralize shared behavior
- improve maintainability


---

## Multiple responsibilities in `FontChooserHandler`

`FontChooserHandler` currently performs several tasks:

- listening to UI events
- applying fonts to figures
- updating editor defaults
- managing undoable edits

This suggests the class may violate the **Single Responsibility Principle**.

Possible refactorings:

- *Extract Method*
- *Extract Class*

Benefits:

- clearer separation of concerns
- easier testing and maintenance


---

# Conclusion

The refactorings performed in this lab focused on improving **maintainability, readability, and robustness** of the font formatting feature.

The applied refactorings include:

- *Consolidate Conditional Expression*
- *Inline Temporary Variable*
- *Introduce Null-Safe Boolean Check*
- *Use Consistent Attribute Abstraction*

All refactorings preserve the original system behavior while improving the internal structure of the code, which aligns with the principles described in **[Ker05] Refactoring to Patterns**.

## `SVGTextAreaFigure`

### Issue: Long Method in `getTextShape()`

The method `getTextShape()` originally contained **a very large block of logic** responsible for:

- preparing layout parameters
- computing tab stops
- iterating over text paragraphs
- building attributed text
- performing line layout
- appending shapes

This resulted in a **long and complex method**, which is a classic code smell described in Chapter 4 of [Ker05].

Large methods are difficult to read, understand, and maintain because they mix multiple responsibilities in a single block of code.

---

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
                                verticalPos, maxVerticalPos,
                                leftMargin, rightMargin,
                                tabStops, tabCount);

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

This method performs **many responsibilities in one place**, making it difficult to understand.

---

### Refactoring Applied

**Extract Method**

The logic was decomposed into smaller helper methods:

- `createTextRect()`
- `createTabStops(...)`
- `appendParagraphs(...)`
- `layoutLine(...)`
- `appendLayouts(...)`
- `findTabLocations(...)`
- `moveToNextTabStop(...)`

---

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
                    appendParagraphs(
                        font,
                        isUnderlined,
                        shape,
                        verticalPos,
                        maxVerticalPos,
                        leftMargin,
                        rightMargin,
                        tabStops,
                        textRect
                    );
                }
            }
        }
    }
    return cachedTextShape;
}
```

---

### Result

This refactoring significantly improves code quality:

- reduces method complexity
- improves readability
- separates responsibilities
- makes the code easier to maintain and test

The refactoring follows the **Extract Method** pattern described in [Ker05], which is commonly used to break down large methods into smaller, reusable components.

---

### Additional Extracted Helper Methods

To support the refactoring, several helper methods were introduced, including:

```
createTextRect()
createTabStops(...)
appendParagraphs(...)
layoutLine(...)
appendLayouts(...)
findTabLocations(...)
moveToNextTabStop(...)
```

These methods isolate specific responsibilities such as layout computation, paragraph processing, and tab handling, making the overall design cleaner and easier to understand.

## `JFontChooser`

### Issue: Long Method in `updateSelectionPath(Font newValue)`

The method `updateSelectionPath(Font newValue)` originally contained **too much logic in one place**.  
It handled:

- checking whether an update was needed
- handling the `null` case
- extracting the current selection path parts
- searching in the current family
- searching in the current collection
- searching in all collections
- creating the final `TreePath`

This is a **Long Method** smell from [Ker05].  
The method had several responsibilities and multiple nested search blocks, which made it harder to read and maintain.

---

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

            if (newFace == null && newFamily != null) {
                for (FontFaceNode face : newFamily.faces()) {
                    if (face.getFont().getFontName().equals(newValue.getFontName())) {
                        newFace = face;
                        break;
                    }
                }
            }

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

The method mixes decision logic, traversal logic, and result construction in one block.

---

### Refactoring Applied

**Extract Method**

The method was broken into smaller helper methods:

- `needsSelectionPathUpdate(...)`
- `findFaceInFamily(...)`
- `findFaceInCollection(...)`
- `findFaceInAllCollections(...)`

A small helper class was also introduced:

- `FontPathMatch`

This class groups together:

- `collection`
- `family`
- `face`

so the search methods can return a single object instead of updating several variables separately.

---

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

---

### Result

This refactoring improves the method in several ways:

- reduces method length
- removes deeply nested search logic
- makes the intent of each step clearer
- separates searching responsibilities into focused helper methods
- improves maintainability and readability

This follows the **Extract Method** refactoring from [Ker05].

---

### Additional Helper Structure

A helper class was introduced to return a complete search result:

```java
private static class FontPathMatch {
    private final FontCollectionNode collection;
    private final FontFamilyNode family;
    private final FontFaceNode face;

    private FontPathMatch(FontCollectionNode collection, FontFamilyNode family, FontFaceNode face) {
        this.collection = collection;
        this.family = family;
        this.face = face;
    }
}
```

This makes the search logic cleaner because the caller receives a single object instead of manually tracking multiple related variables.

---

### Summary of the Improvement

The original `updateSelectionPath(...)` method was difficult to read because it combined multiple responsibilities in one place.  
After refactoring, the method reads more like a high-level algorithm:

1. check whether an update is needed
2. handle the null case
3. search in the current family
4. search in the current collection
5. search in all collections
6. update the selection path

This makes the code easier to understand and better aligned with the refactoring principles in [Ker05].

## `DefaultFontChooserModel`
## `SVGAttributedFigure`
## `AbstractAttributedFigure`
## `SVGAttributeKeys`
## `AttributeKey`
## `ButtonFactory`