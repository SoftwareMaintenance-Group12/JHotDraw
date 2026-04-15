# SOLID examples in context of JHotDraw study 
## SRP — Single Responsibility Principle
Strongest in my case, as the ONE CLASS had to many reasons to change because of the METHODS that
were responsible for formatting, layout, control logic together.
### `SVGTextAreaFigure`
Has too many reasons to change and violates single responsibility. 
1. Text layout
2. Paragraph normalization
3. Tab-stop creation
4. Line measuring
5. Shape generation
6. Bounds handling
7. Editing-related behavior
8. 
### `JFontChooser`
Has too many reasons to change:
1. UI component behavior
2. Selection font and path state
3. Model synchronization
4. Font loading
5. Tree search/update logic
6. Event firing
7. Font loading

### `FontChooserHandler`
1. Event handling changes
2. Font application workflow changes
3. Undo behaviour
4. Editor default update
5. Popup interaction

### `DefaultFontChooserModel`
Stores and exposes tree model, also build all category structure. 

### `SVGTextFigure`
Multiple reasons to change:
1. Text rendering
2. Geometry/bound changes
3. Transformation handling changes
4. Editing 
5. Cache

### `SVGTextAreaFigure`
1. Paragraph processing
2. Line breaking 
3. Tab-stop
4. Overflow detection
5. Editing support
6. Rendering
7. Geometry

### `AbstractAttributedFigure`
Slightly violates because
1. Attributed storage
2. Attribute lifecycle
3. Rendering workflow
4. Serialisation (DOM)
5. Geometry (drawing area)

### `SVGAttributedFigure`
Violates
1. SVG rendering logic
2. Opacity buffering
3. Transformation handling
4. UI handling

### `AttributeKey` - good
Represents and manages an attribute definition plus typed access behaviour

### `SVGAttributeKeys`
Weaker than AttributeKeys because
1. Defines constants
2. Computes paint
3. Sets defaults
4. Computes hit growth

## OCP — Open/Closed Principle
Problem: modifying existing large methods every time behaviour changes. 
Refactoring into smaller helpers and clearer abstractions made extension easier.

### `DefaultFontChooserModel.setFonts()` design is not open for extension, as the method itself should be modified.

### `SVGTextAreaFigure` good example, because most likely extending layout will require direct modification
of te class.

### `AbstractAttributedFigure` good example, because can be extended, overriden (drawFill, drawStroke, drawText),
no need to modify base class.

### `AttributeKey` - strong
Can add new attributes without redesigning the whole figure API.
`public static final AttributeKey<Double> OPACITY = ...
public static final AttributeKey<TextAnchor> TEXT_ANCHOR = ...`

## LSP — Liskov Substitution Principle
Is inheritance involved? Subclasses should behave consistently when used through a common parent.
### `AbstractAttributedFigure` - good. Any subclass can replace this class, respects draw() contract, 
implements required methods.

## ISP — Interface Segregation Principle
Depend on broader interfaces than it actually needs?

## DIP — Dependency Inversion Principle
A bridge to Clean Architecture. If business logic depends on UI classes or concrete drawing implementations,
then a DIP issue. Business rules should be independent. 

### 1. The font formatting workflow is coupled to UI and framework classes such as `JFontChooser` and `JPopMenu`.
This makes higher-level behaviour depend on details instead of depending on abstractions.
### 2. In `SVGTextAreaFigure` and `SVGTextFigure` 
high-level text and editing behaviour depends directly on concrete Java2D and JhotDraw framework classes rather than on more stable abstractions.
### 3. `AbstractAttributedFigure` - weak, depends on Graphics2D, BufferedImage, AlphaComposite.
### 4. `AttributeKey` better, but still depends on Figure, undo framework and reflection helper Methods.Invoke

# Explained Clean architecture in context of JHotDraw study
## `JFontchooser` is mainly Frameworks & Drivers / Interface Adapter code. Why?
1. Extends `JComponent`
2. Depends on Swing/AWT classes, like `JDialog`, `TreePath`, `UIManager`, `ActionEvent` and `GraphicsEnvironment`.
3. Manages UI state as font and path selection, dialog return value and lazy loading of system fonts. 

It is clearly `outer-layer` code, not an entity or use case. It is allowed to depend on frameworks because frameworks and UI belong outside the core.

Problem: too much decision logic.
1. Selection synchronisation logic
2. Tree-path reconstruction logic
3. Search logic across collection
4. Asynchronous font loading logic

Instead: View should be simple, and presenters/adapters should translate data around it.

## `FontChooserHandler`is an interface Adapter, with some logic for use-case role. Why?
1. Reacts to UI events
2. Translates selection changes into updates on figures and editor defaults.
3. Constructs an undoable edit.

So, behaves like `controller`: user interaction comes in, handler converts it into operations on the model. 
But problem: class does not stay in -> does not forward a request. It performs the font-application workflow itself.

### Problem: this class mixes adapter logic and application behaviour. As it:
1. Listens for chooser events
2. Checks action/property names
3. Applies selected font to all selected figures
4. Updates editor default attributes
5. Creates undo/redo behaviour
6. Manages popup visibility
7. Synchronises state with current selection

Meaning: this class combines
1. Controller/adapter behaviour
2. Update
3. Undo management
4. UI state synchronisation

### Outcome: A Clean Architecture interpretation suggests that the operation “apply selected font to selected figures” should be modeled as a dedicated 
use case or interactor. In the current design, that logic is embedded directly in FontChooserHandler, which couples the operation to UI-driven control flow.

## `DefaultFontChooserModel` is an Interface Adapter / presentation-side model. Why?

1. Organises fonts into UI-facing collections
2. Builds the tree structure the chooser needs
3. Contains presentation-oriented categories like serif, sans serif, scripts, monospaced, decorative, symbols.

Is not an entity in Clean Architecture. 

The font-formatting feature is architecturally centered around Swing UI classes and event handlers rather than around an explicit 
use-case boundary. This suggests a design driven more by delivery mechanism than by application-level interaction structure.

### Problem: is presentation-oriented, not domain-oriented. Contains knowledge how fonts should be grouped.
1. Web-safe fonts
2. System fonts
3. serif/sans-serif
4. monospaced
5. decorative
6. symbols etc.

This is presentation and classification for the font chooser UI, not a stable domain rule.

## `SVGTextFigure` is a model-side figure class, but not a pure inner-layer entity. Why?
1. Represents text figure in drawing
2. Stores state like coordinates, rotation, edit
3. Computes bounds and drawing area
4. Handles transforms
5. Creates editing tools and handles
6. Relies on Java2D/SVG?JHotDraw framework APIs

### So, it is somewhere between model/domain and framework-dependent graphical object. 
Not pure Clean Architecture, because tightly coupled to
1. Graphics2D
2. TextLayout
3. AffineTransform
4. and JHotDraw handles and tools

## `SVGTextAreaFigure`, here multiple levels are blurred together.
Represents text-area figure, and also:
1. Text storage
2. Line layout
3. paragraph processing
4. Tab-stop logic
5. Rendering preparation
6. Bounds handling
7. overflow logic
8. Editing/tool integration
9. Transform support

Class combines:
1. Figure state
2. Layout behaviour
3. Rendering and interaction support

### Solution: separate text model, layout and rendering services, editor adapters.

Moreover: `SVGTextAreaFigure` and `SVGTextFigure` share same responsibilities, as both:
1. getFont()
2. getTextColor()
3. getFillColor()
4. setFontSize()
5. getFontSize() 

meaning that code is duplicated. 

### Solution: add abstractions such as `AbstractSVGTextFigure`, `TextLayoutSupport` and `AbstarctTextFigureBehaviour`. 

## `AbstractAttributedFigure` - core abstraction, close to Entity. Why?
1. Manages attributes (HashMap<AttributeKey, Object>)
2. Defines attribute lifecycle (set, get, restore, clone)
3. Provides default drawing workflow (draw)
4. Defines extension points for drawFill(), drawStroke(), drawText()

Because:
1. Defines general rules for figures
2. Reused across many figure types
3. Contains stable logic (attributes, serialization)

But still depends on Graphics2D, BasicStroke and AWT rendering concepts, and it should not
depend on UI or frameworks.

### Good: use of abstraction and inheritance. Why? Defines:
1. Reusable abstraction
2. Clear extension points (drawFill, drawStroke, drawText)
3. Attribute handling in one place

Shared logic is centralised and subclasses specialise behaviour. 
Matches Clean Architecture idea of `stable inner policies reused by outer layers.`

## `SVGAttributedFigure` - adapter, specialisation layer. Why?
Builds on top of `AbstractAttributedFigure` and adds:
1. SVG-specific behaviour 
2. Opacity handling
3. Transform handling
4. Fill/stroke logic using SVG attributes
5. UI action (get Actions())

### It bridges generic figure logic (inner layer) and SVG rendering + UI actions (outer layer).

## `AttributeKey` is stable abstraction for attribute access. Why?
is general mechanism.
Represents:
1. Name of attribute
2. Type
3. Default value
4. Whether null is allowed
5. Helper for get, set, clone, undoable set

### Good: string reusable abstraction. Why? Close to core abstraction.
1. Generic
2. Reusable across many figures
3. Not tied to SVG text figures
4. Creates typed access to attributes
5. Centralises attribute-related operations. 

## `SVGAttributeKeys` specialises the general attribute system for SVG feature.
is SVG-specific configuration and helper logic.
Defines:
1. SVG-specific keys like TEXT_ANCHOR, FILL-GRADIENT, OPACITY, LINK
2. Utility methods like: GetFillPaint(Figure f), getStrokePaint(Figure f), setDefaults(Figure f),
getPerpendicularHitGrowth(Figure f, double factor).

### It is specialisation layer on top of the general attribute mechanism.
SVGAttributeKeys depends on AttributeKey and AttributeKeys and not the other way around.

But: also contains behaviour. Convenient but mixes responsibilities.
1. Paint creation
2. Default initialisation
3. Hit-growth calculations
