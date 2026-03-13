# Concept location

## Change request 
is to improve maintainability of font formatting behaviour.

## Dynamic analysis
Used IntelliJ debugger while interacting with text formatting.

### Initial domain Classes and Responsibilities identified

| Domain Class               | Responsibility                                                                                |
|----------------------------|-----------------------------------------------------------------------------------------------|
| FontChooserHandler`        | Handles user interactions for selecting font properties and applies them to selected figures. |
| `SVGTextAreaFigure`        | Represents editable text areas and manages font-related attributes for text content.          |
| `SVGAttributedFigure`      | Handles SVG-specific attribute management and invalidation for figures.                       |
| `AbstractAttributedFigure` | Stores and updates figure attributes such as font, size, and style.                           |
| `AttributeKey`             | Provides type-safe access to figure attributes (font, style, etc.).                           |

## Observations
Font attributes propagate through AbstractAttributedFigure.set(...)