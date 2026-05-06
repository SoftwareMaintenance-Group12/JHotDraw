# Concept location
Change request is to improve maintainability of font formatting behaviour.

## Dynamic analysis
The font formatting was analysed with IntelliJ debugger, with entry point `org.jhotdraw.samples.svg.Main`. Breakpoints on places responsible for font attributes. 
Program was observed in debug mode, while interacting with UI.

## Debugging steps

### 1. Breakpoint in `FontChooserHandler.applySelectedFontToFigures()`,
Line: 65 , Module: `jhotdraw-gui`, Package: `org.jhotdraw.gui.action`.

#### UI trigger
1. Start org.jhotdraw.samples.svg.Main; 2. Create text element; 3. Select text object; 4. Change the font.

#### Execution was paused at breakpoint
`FontChooserHandler.applySelectedFontToFigures()`
Line: 70. `figure.set(key, fontChooser.getSelectedFont());`

### 2. Step into `figure.set(...)`,
Class: `SVGTextFigure`; Method: `set(AttributeKey<T> key, T newValue)`; Line: 299; Module: `jhotdraw-samples`; Package: `org.jhotdraw.samples.svg.figures`

``` t
Observed condition:
if (key.equals(SVGAttributeKeys.TRANSFORM)
|| key.equals(SVGAttributeKeys.FONT_FACE)
|| key.equals(SVGAttributeKeys.FONT_BOLD)
|| key.equals(SVGAttributeKeys.FONT_ITALIC)
|| key.equals(SVGAttributeKeys.FONT_SIZE))
```
This confirms that the figure reacts to font-related attribute changes.

Delegation occurs via:
`super.set(key, newValue)`; Line: 306

### 3. Step Into SVGAttributedFigure
Class: `SVGAttributedFigure`; Method: `set(AttributeKey<T> key, T newValue)`; Line: 102; Module: `jhotdraw-samples`, Package: `org.jhotdraw.samples.svg.figures`

Purpose of this class: handles SVG-specific attribute behavior, invalidates the figure if certain attributes change

Delegation continues through:
`super.set(key, newValue)`; Line: 105

### 4. Step Into AbstractAttributedFigure
Final stop:
Class: `AbstractAttributedFigure`; Method: `set(AttributeKey<T> key, T newValue);` Line: 96; Module: `jhotdraw-core`; Package: `org.jhotdraw.draw.figure`

Here the attribute is actually stored. Critical line:
`T oldValue = key.put(attributes, newValue);` Line: 99

This writes new font attribute to the figure attribute map. Immediately after, observers are notified.
`fireAttributeChanged(key, oldValue, newValue);` Line: 100

### Observer Runtime flow
```text
User changes font in UI
  -> FontChooserHandler.applySelectedFontToFigures()         [jhotdraw-gui]
     -> SVGTextFigure.set(...)                               [jhotdraw-samples]
        -> SVGAttributedFigure.set(...)                      [jhotdraw-samples]
           -> AbstractAttributedFigure.set(...)              [jhotdraw-core]
              -> key.put(attributes, newValue)
              -> fireAttributeChanged(...)
```

| #   | Domain Class                 | Module              | Responsibility                                                                           |
|-----|------------------------------|---------------------|------------------------------------------------------------------------------------------|
| 1   | `FontChooserHandler`         | jhotdraw-gui        | Handles the font chooser UI and applies selected font attributes to selected figures.    |
| 2   | `SVGTextFigure`              | jhotdraw-samples    | Represents text figures and handles font-related attribute changes.                      |
| 3   | `SVGAttributedFigure`        | jhotdraw-samples    | Base class for SVG figures supporting attribute-based styling.                           |
| 4   | `AbstractAttributedFigure`   | jhotdraw-core       | Core implementation storing and managing figure attributes.                              |
| 5   | `AttributeKey`               | jhotdraw-core       | Represents attribute keys used to store figure properties such as fonts.                 |


## Conclusions:
Dynamic analysis revealed that the font formatting feature propagates from the UI controller (FontChooserHandler) to the figure classes through a chain 
of set() method calls. The actual storage of the font attribute occurs in AbstractAttributedFigure using the attribute map managed by AttributeKey. 
This architecture allows JHotDraw to apply formatting changes consistently across different figure types.