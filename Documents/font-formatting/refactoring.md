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
## `JFontChooser`
## `DefaultFontChooserModel`
## `SVGAttributedFigure`
## `AbstractAttributedFigure`
## `SVGAttributeKeys`
## `AttributeKey`
## `ButtonFactory`