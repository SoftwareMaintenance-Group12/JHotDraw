# Unit testing
P.S. `SVGTextAreaFigure` is. probably, the strongest case in terms of refactoring.
## Adding JUnit to `pom.xml (jhotdraw)`
`<properties>
	<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	<maven.compiler.source>1.8</maven.compiler.source>
	<maven.compiler.target>1.8</maven.compiler.target>
	<junit.version>4.13.2</junit.version> 
</properties>`
and
`<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</dependencyManagement>`

## Adding to other modules
After: does not mean that all modules automatically get JUnit, add in each module
### Inside jhotdraw-core/pom.xml; jhotdraw-gui; jhotdraw-samples/jhotdraw-samples-misc
`<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>`

### Adding src/test/java to each module

## Testing
### `AttributeKey`
Part of the core attribute-handling logic used by the font-formatting feature. Controls typed access 
to figure attributes, default values, null handling and attribute assignment behaviour.

#### Methods/behaviours verified:
- `get(Map)` returns stored value of default value when key is missing
- `put(Map, Value)` stores value and returns previous
- `isAssignable(Object)` accepts values of correct type and rejects invalid types
- null-handling rules enforced when null values are disallowed
- equals, hashCode and toString behave consistently
- clone-based behaviour verified for cloneable values

#### Best-case scenario tested
- valid attribute value stored and retrieved correctly
- keys with same name compare equal
- valid type accepted by isAssignable

#### Boundary case tested
- missing key returns default value
- null value rejected when null is not allowed
- wrong type rejected by isAssignable
- clone behaviour checked using cloneable object

### Tests are passed.

### `AbstractAttributedFigure`
Contains core business logic for managing figure attributes. Relevant for selected font-formatting
feature bc formatting values are stored and controlled through the figure attribute system.
the class also includes logic for enabling/dis attributes, cloning attribute state,
and protecting internal attribute storage.

#### Methods/behaviours verified:
- `getAttributes()` returns defensive copy of internal attribute map
- `setAttributeEnabled()` and `isAttributeEnabled()` correctly disable and re-enable an attribute
- `set()` stores value when attribute is enabled
- `set()` does not change value when attribute is disabled
- `removeAttribute()` removes stored value and restores default-value behaviour
- `hasAttribute()` reports whether an attribute is explicitly stored
- `clone()` creates an independent copy of attributes and preserves forbidden-attribute state

#### Best-case scenario tested
- a valid attribute value is stored and retrieved correctly 
- an attribute can be disabled and then enabled again
- cloning a figure preserves the existing attribute state

#### Boundary case tested
- modifying map returned by `getAttributes()` does not affect figure's internal state
- setting a disabled attribute does not overwrite the previous stored value
- `hasAttribute()` returns false when no value has been set
- removing an attribute restores access to default value

### DefaultFontChooserModel
Contains important logic for organizing fonts into families and collections for the font chooser. This 
is relevant to the font-formatting feature because it controls how available fonts are grouped and presented.

#### Methods/behaviours verified:
- `setFonts(Font[] fonts)` groups fonts into family nodes
- fonts with the same family name are merged into one family node
- `collectFamiliesNamed(...)` returns only requested font families
- `getRoot(), getChild(...), getChildCount(...), isLeaf(...), and 
getIndexOfChild(...)` provide consistent tree-model behavior

#### Best-case scenario tested
- valid font arrays are grouped into the expected family structure
- requested family names are collected correctly
- root and child nodes are available after loading fonts

#### Boundary case tested
- an empty font array still creates a valid tree structure
- non-matching font-family names return an empty result
- duplicate family names do not create duplicate family nodes 

### `SVGAttributeKeys`
Contains important SVG-specific business logic for computing fill and stroke paint, applying default SVG attributes,
and calculating hit-detection growth. This is directly relevant to the selected feature because SVG text rendering and
font formatting depend on correct attribute handling and paint computation.

#### Add mockito
`<dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.12.0</version>
            <scope>test</scope>
</dependency>`

#### Methods/behaviours verified:
    `getFillPaint(Figure f)`
- returns fill color when no gradient is present
- applies opacity to color when opacity is less than 1
- returns null when no fill is defined
 `getStrokePaint(Figure f)`
- returns stroke color when no gradient is present
- applies opacity to stroke color
- returns null when no stroke is defined
  `setDefaults(Figure f)`
- initializes the figure with correct SVG default values
- sets fill, stroke, and stroke-related properties
  `getPerpendicularHitGrowth(Figure f, double factor)`
- uses fill-based growth when no stroke is present
- includes stroke width when stroke is present
- 
#### Best-case scenario tested
- fill and stroke paints are returned correctly for fully opaque colors
- default SVG attributes are correctly applied to a figure
- hit growth is computed correctly when stroke is present

#### Boundary case tested
- null fill or stroke color results in null paint when no gradient exists
- opacity values less than 1.0 correctly modify alpha channel
- hit growth behaves differently depending on whether stroke is present or absent

### `SVGTextFigure`
Contains core logic for handling SVG text elements, including text content, coordinates, rotation,
fill-color behaviour, cloning, handle creation. This is directly relevant to selected feature because
font formatting and SVG text rendering depend on correct management of text attributes and geometry.

#### Methods/behaviours verified:
- constructor and `setText() / getText()` correctly manage text content
- `isEmpty()` correctly detects null and empty text
- `setCoordinates()` copies the coordinate array structure (but not the contained objects)
- `getCoordinates()` returns cloned coordinate points (deep copy on read)
- `setRotates() / getRotates()` correctly copy rotation values
- `getFillColor()` returns the expected contrasting color depending on fill color
- `clone()` creates an independent copy of coordinates and rotation data
- `createHandles(int)` returns the expected handle structure for different detail levels

#### Best-case scenario tested
- text content is correctly stored and retrieved
- non-empty text is correctly identified as non-empty
- coordinates and rotations are correctly set and retrieved
- handles are created correctly for standard detail levels
- cloning preserves state but creates independent copies

#### Boundary case tested
- null text and empty string are treated as empty (`isEmpty() == true`)
- modifying arrays returned by getters does not affect internal state
- modifying input arrays after `setCoordinates()` does not affect structure (array copy)
- different `detailLevel` values produce different handle collections
- fill color logic correctly handles null, white and non-white colors

#### Observation:
`setCoordinates()` performs shallow copy of the array
`getCoordinates()` performs a deep copy of the elements

### `SVGTextAreaFigure`
Contains important SVG text-area business logic for managing text content, computing layout and preferred size,
handling bounds and insets, detecting text overflow, and supporting editing interaction through tools and handles.
This is directly relevant to the selected feature because SVG text rendering and font formatting depend on correct
text layout, sizing, and interaction behavior.

#### Methods/behaviours verified:
    `SVGTextAreaFigure()`
- initializes the figure with default text "Text"
- creates an editable text area by default
  `setText(String newText) / getText()`
- stores and returns text correctly
  `getTextColumns()`
- returns at least 4 columns for null or short text
- returns actual text length when longer than 4
  `isEmpty()`
- returns true for null text
- returns true for empty text
- returns false for non-empty text
  `setBounds(Point2D.Double anchor, Point2D.Double lead)`
- normalizes coordinates correctly when anchor/lead are reversed
- enforces a minimum positive width and height
  `getInsets()`
- returns zero insets when no stroke color is defined
- includes half of the stroke width (rounded up) when stroke is present
  `getTextColor()`
- returns the fill color used for text rendering
  `getFillColor()`
- returns black when fill color is white
- returns white when fill color is not white
  `setFontSize(float size) / getFontSize()`
- correctly sets and retrieves font size without transform
- correctly compensates for transformation scaling
  `getTool(Point2D.Double p)`
- returns null when the figure is not editable
- returns null when the point is outside the figure
- tool selection depends on hit-testing against rendered text
  `createHandles(int detailLevel)`
- creates bounds outline handle for hover level
- includes resize, font size, text overflow, and link handles for interaction
  `getPreferredTextSize(double maxWidth)`
- computes valid text size for normal content
- handles empty paragraphs correctly
- handles tabbed text correctly
  `isTextOverflow()`
- returns true when text exceeds available bounds
- returns false when sufficient space is available
  `clone()`
- creates an independent copy with duplicated bounds and text

#### Best-case scenario tested
- text is stored and retrieved correctly
- bounds are computed correctly for standard text areas
- font size behaves correctly with and without transformations
- preferred text size is computed correctly for normal text
- editing-related handles are created correctly

#### Boundary case tested
- null and empty text handling
- minimum bounds enforcement (avoiding zero size)
- empty paragraphs ("") handled without breaking layout
- tabbed text does not break layout calculations
- insets vary correctly depending on stroke presence
- overflow detection reacts correctly to small vs large bounds
- tool retrieval correctly returns null when interaction is not possible

#### Refactoring (previously)
During refactoring, complex text layout logic was decomposed into smaller helper methods such as 
paragraph normalization, tab stop calculation, and layout processing. These changes improved readability ,
maintainability. The correctness of the refactoring was verified through behavior-driven unit tests, 
particularly via `getPreferredTextSize, isTextOverflow, and bounds-related methods`.

### `FontChooserHandler`
Handles interaction between the font chooser UI, popup menu, and drawing editor. It updates the enabled state 
of font-related controls and reacts to action/property change events related to font selection. This is directly 
relevant to the selected feature because font formatting changes in the UI must be handled safely and consistently.

#### Add mockito
`<dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.12.0</version>
            <scope>test</scope>
</dependency>`

#### Methods/behaviours verified:
  `actionPerformed(ActionEvent evt)`
- hides the popup menu when cancel action is triggered
- safely handles null action commands
- always closes the popup menu after handling the event
  `updateEnabledState()`
- disables font chooser and popup menu when no figures are selected
- enables font chooser and popup menu when selection exists
  `propertyChange(PropertyChangeEvent evt)`
- ignores unrelated property changes
- safely handles null property names without throwing exceptions

#### Best-case scenario tested
- font chooser controls become enabled when figures are selected
- cancel action closes the popup menu correctly
- unrelated property changes are ignored safely

#### Boundary case tested
- null action command does not cause failure
- null property name does not cause failure
- no selected figures disables font-related UI controls

### Refactoring validation
The original implementation used compound null-check logic for comparing action commands and property names. 
After refactoring, this logic was simplified to explicit null checks followed by .equals(...). The tests verify 
that null command/property values are handled safely and that the UI state logic still behaves correctly.

### `JFontChooser`
Contains important font chooser business logic for firing approve/cancel actions, managing the selected font and 
chooser model, and loading available fonts from the graphics environment. This is directly relevant to the selected 
feature because font formatting depends on correct font selection events, model updates, and safe font retrieval.

#### Add mockito
Mockito is used to mock action listeners and chooser models so the public behaviour of the font
chooser can be verified without depending on full UI rendering internals.
`<dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.12.0</version>
            <scope>test</scope>
</dependency>`

#### Methods/behaviours verified:
  `approveSelection()`
- fires an action event with command APPROVE_SELECTION
  `cancelSelection()`
- fires an action event with command CANCEL_SELECTION
  `addActionListener(ActionListener l) / removeActionListener(ActionListener l)`
- notifies registered listeners
- stops notifying removed listeners
  `setSelectedFont(Font newValue) / getSelectedFont()`
- stores and returns the selected font correctly
- accepts null safely
  `setModel(FontChooserModel newValue) / getModel()`
- replaces the current model reference
- registers a tree model listener on the new model
  `getAllFonts()`
- returns a non-null array of fonts
- returns a cloned array rather than the same array instance
- 
#### Best-case scenario tested
- approve and cancel operations notify listeners correctly
- selected font is stored and returned correctly
- chooser model can be replaced successfully
- available fonts can be retrieved successfully

#### Boundary case tested
- removed listeners no longer receive events
- selected font can be set to null safely
- `getAllFonts()` returns an independent cloned array rather than reusing the same reference

### `SVGAttributedFigure`
Contains important SVG attribute-based drawing logic for handling opacity, fill and stroke rendering, transforms, 
and transform-related actions. This is directly relevant to the selected feature because SVG rendering and formatting 
depend on correct application of paint, stroke, opacity, and transform behavior.

#### Methods/behaviours verified:
  `draw(Graphics2D g)`
- does not draw when opacity is 0
- draws normally when opacity is 1
- clamps opacity values above 1 to the valid drawable range
- clamps opacity values below 0 to the valid drawable range
  `drawFigure(Graphics2D g)`
- draws fill when fill paint is available
- draws stroke when stroke paint is available and stroke width is positive
- does not draw stroke when stroke width is 0
- restores the original graphics transform after drawing
  `set(AttributeKey<T> key, T newValue)`
- invalidates the figure when the transform attribute changes
  `getActions(Point2D.Double p)`
- returns no actions when no transform is present
- returns a remove-transform action when a transform is present

#### Best-case scenario tested
- figures render fill and stroke correctly under normal opacity
- transforms are applied during drawing and graphics state is restored afterward
- transform-related actions are available when relevant

#### Boundary case tested
- opacity 0 prevents drawing
- opacity values outside the valid range are clamped safely
- stroke is skipped when stroke width is 0
- no transform results in no transform-removal action