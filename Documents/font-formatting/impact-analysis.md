# Impact Analysis: Text Formatting Consistency

## Change Request

Improve the consistency and maintainability of text formatting behavior (font family, font size, and text style) when editing text objects in the SVG sample editor.

The goal of this impact analysis is to estimate which classes and packages are likely to be affected if the text formatting logic is modified or refactored.

---

## Method

The impact analysis was performed using both **dynamic** and **static** analysis.

### Dynamic analysis
The debugger-based concept location identified the runtime path followed when the user changes the font of a selected text figure:

```text
User changes font in UI
  -> FontChooserHandler.applySelectedFontToFigures()        [jhotdraw-gui]
     -> SVGTextFigure.set(...)                              [jhotdraw-samples]
        -> SVGAttributedFigure.set(...)                     [jhotdraw-samples]
           -> AbstractAttributedFigure.set(...)             [jhotdraw-core]
              -> key.put(attributes, newValue)
              -> fireAttributeChanged(...)
```

This runtime trace identified the core classes directly involved in the feature.

### Static analysis
Additional classes were identified by:
- finding usages of `FontChooserHandler`
- inspecting construction of `JFontChooser`
- inspecting related text figure classes in the same package
- inspecting imported SVG font attribute constants
- inspecting the inheritance and attribute-storage chain

This expanded the impact set beyond the classes reached directly during debugging.

---

## Table 1

| Package name                       | # of classes | Comments                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|------------------------------------|-------------:|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `org.jhotdraw.gui.action`          |            2 | `FontChooserHandler` — **CHANGED**. It applies the selected font to selected figures. `ButtonFactory` — **PROPAGATING**. It creates and wires the font chooser UI and registers the handler.                                                                                                                                                                                                                                                                                                                                                             |
| `org.jhotdraw.gui`                 |            1 | `JFontChooser` — **PROPAGATING**. It provides the selected font value and participates in the UI interaction chain, but the formatting logic is not primarily implemented here.                                                                                                                                                                                                                                                                                                                                                                          |
| `org.jhotdraw.gui.fontchooser`     |            1 | `DefaultFontChooserModel` — **PROPAGATING**. It supports font chooser state and selection changes. It appears in the call stack and may influence propagation of formatting selections.                                                                                                                                                                                                                                                                                                                                                                  |
| `org.jhotdraw.samples.svg.figures` |            4 | `SVGTextFigure` — **CHANGED**. It directly handles font-related attribute updates in `set(...)`. `SVGTextAreaFigure` — **CHANGED**. It contains similar font-handling logic and should be considered if formatting consistency is meant to apply to all text objects. `SVGAttributedFigure` — **PROPAGATING** because it participates in the inheritance chain and delegates to the core attribute mechanism. `SVGAttributeKeys` — **UNCHANGED**, as it defines SVG-specific constants such as `FONT_FACE`, `FONT_BOLD`, `FONT_ITALIC`, and `FONT_SIZE`. |
| `org.jhotdraw.draw.figure`         |            1 | `AbstractAttributedFigure` — **PROPAGATING**. It stores attributes through `key.put(attributes, newValue)` and triggers `fireAttributeChanged(...)`. It is central to propagation, but may remain unchanged if maintenance is localized to text formatting logic.                                                                                                                                                                                                                                                                                        |
| `org.jhotdraw.draw`                |            1 | `AttributeKey` — **UNCHANGED**. It is part of the attribute infrastructure and is used to store and retrieve formatting values.                                                                                                                                                                                                                                                                                                                                                                                                                          |
| **Total**                          |       **10** | **3 CHANGED, 5 PROPAGATING, 2 UNCHANGED**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |

---

## Rationale

The concept location activity located the classes directly executed when a font change occurs. Static analysis then expanded the estimated impact set by examining neighboring classes connected through construction, inheritance, constant usage, and package-level relationships.

The main directly affected classes are the controller (`FontChooserHandler`) and the concrete SVG text figure classes (`SVGTextFigure`, `SVGTextAreaFigure`). The font chooser UI and attribute infrastructure are also part of the impact set because they either construct the handler, provide the font value, or propagate the attribute update through the figure hierarchy.

The impact analysis shows that this maintenance task is localized around text formatting, but it crosses both the UI layer and the figure attribute layer.

---

## Trace of the Algorithm Execution

| Step | Class                      | Mark        | New classes added to EIS                                       |
|------|----------------------------|-------------|----------------------------------------------------------------|
| 1    | `FontChooserHandler`       | CHANGED     | `ButtonFactory`, `JFontChooser`, `SVGTextFigure`               |
| 2    | `SVGTextFigure`            | CHANGED     | `SVGAttributedFigure`, `SVGAttributeKeys`, `SVGTextAreaFigure` |
| 3    | `SVGTextAreaFigure`        | CHANGED     | —                                                              |
| 4    | `ButtonFactory`            | PROPAGATING | —                                                              |
| 5    | `JFontChooser`             | PROPAGATING | `DefaultFontChooserModel`                                      |
| 6    | `DefaultFontChooserModel`  | PROPAGATING | —                                                              |
| 7    | `SVGAttributedFigure`      | PROPAGATING | `AbstractAttributedFigure`                                     |
| 8    | `AbstractAttributedFigure` | PROPAGATING | `AttributeKey`                                                 |
| 9    | `SVGAttributeKeys`         | UNCHANGED   | —                                                              |
| 10   | `AttributeKey`             | UNCHANGED   | —                                                              |
| —    | **EIS exhausted**          |             |                                                                |

---

## Summary

The estimated impact set is larger than the initial concept location result. Dynamic analysis revealed the core runtime execution path, while static analysis added surrounding UI, model, and infrastructure classes that may be affected by a maintenance change.

The most likely directly modified classes are:

- `FontChooserHandler`
- `SVGTextFigure`
- `SVGTextAreaFigure`

The remaining classes either propagate the change through construction or inheritance, or provide infrastructure support without being likely direct modification targets.

This suggests that the feature is moderately localized, but still spans multiple packages and abstraction levels.