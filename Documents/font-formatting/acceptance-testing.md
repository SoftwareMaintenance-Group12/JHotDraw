# Acceptance Testing

## Adding JGiven to `pom.xml (jhotdraw)` - root

`<jgiven.version>1.2.5</jgiven.version>` the newer versions break application

`<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>junit</groupId>
				<artifactId>junit</artifactId>
				<version>${junit.version}</version>
				<scope>test</scope>
			</dependency>
			<dependency>
				<groupId>com.tngtech.jgiven</groupId>
				<artifactId>jgiven-junit</artifactId>
				<version>${jgiven.version}</version>
				<scope>test</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>`

### add Inside jhotdraw-core/pom.xml; jhotdraw-gui; jhotdraw-samples/jhotdraw-samples-misc `
`<dependency>
<groupId>com.tngtech.jgiven</groupId>
<artifactId>jgiven-junit</artifactId>
<scope>test</scope>
</dependency>`

## Font chooser updates selected font
User story: As a drawing user, I want the font chooser to store and notify changes to the selected font so that font selection can be applied by the drawing tools.

BDD scenario:
Given I have a font chooser
When I select a new font
Then the selected font should be updated
And a property change event should be fired

Test target: `JFontChooser.setSelectedFont(...)`

Test location: `jhotdraw-gui/src/test/java/org/jhotdraw/gui/acceptance/JFontChooserAcceptanceTest.java`

## SVG text invalidates cache when font changes
User story: As a drawing user, I want SVG text bounds to update when the font changes so that the displayed text size is correct.
BDD scenario:
Given I have an SVG text figure with cached bounds/I want SVG text bounds to update
When I change the font size
Then the text shape and bounds should be recalculated/displayed text size is correct

Test target: `SVGTextFigure.set(...)`

Test location: `jhotdraw-samples/jhotdraw-samples-misc/src/test/java/org/jhotdraw/samples/svg/acceptance/SVGTextFigureAcceptanceTest.java`

## SVG text are detects text overflow
User story: As a drawing user, I want text overflow to be detected in SVG text areas so that I know when text does not fit.
BDD scenario:
Given I have small SVG text area
When I add long text
Then the figure should report text overflow

Test target: `SVGTextAreaFigure.isTextOverflow()`

Test location: `jhotdraw-samples/jhotdraw-samples-misc/src/test/java/org/jhotdraw/samples/svg/acceptance/SVGTextAreaAcceptanceTest.java`

Test was written as a JGiven acceptance test with one test class and inner stages classes, such as:
`GivenSVGTextArea`
`WhenSVGTextArea`
`ThenSVGTextArea`

## AttributeKey rejects invalid null values
User story: As a developer, I want non-null attributes to reject null values so that figure attributes stay valid.
BDD scenario:
Given I have AttributeKey that does not allow null
When I set its value to null
Then a NullPointerException should be thrown

Test target: `AttributeKey.set() or (put)`

Test location: `jhotdraw-core/src/test/java/org/jhotdraw/draw/acceptance/AttributeKeyAcceptanceTest.java`

## Summary
The implemented acceptance tests cover user-visible behavior related to font selection, SVG text sizing, SVG text 
overflow detection, and attribute validation. These tests are written with JGiven using Given/When/Then stages, which 
makes the tests readable as BDD acceptance scenarios instead of only low-level unit tests.
