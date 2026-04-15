# Sketch ` `

## Naming problem - function in this case
`SVGTextAreaFigure`
During refactoring, I introduced a helper method called appendParagraphs() to handle interation over multiple
paragraphs. Initially, there was a existing method appendParagraph(), which I noticed later.
It led to confusion, and I decided to rename a helper method to appendTextParagraphs() temporarily.

## Monadic, dyadic, triadic polyadic - there was a mess polyadic function.
In this case - reduce number of arguments by creating objects \

## Don’t repeat yourself principle was violated and I had to do the new functions to reuse the code

## Again from the book, many code left was commented out. I deleted it as the book recommended.

## Vertical order of the functions was made by me and IDE ‘refactor method’, which follows the book suggestion
Book(G9) dead code in if block or in a try-catch block was very often in the code

## Functions should do one thing - "Do One Thing"
Violation reduced readability, made behaviour harder to understand and test.

`getTextShape()` in `SVGTextAreaFigure` does more
1. Validates cache
2. Prepares layout data
3. Computes margins
4. Creates tab stops
5. Iterates paragraphs
6. Appends outlines

`updateSelectionPath()` in `JFontChooser` does more
1. Decides whether update is needed
2. Handles null case
3. Searches family 4. Collection 5. Collections
6. Updates selection

## Short temporary names
`SVGTextFigure`
Short temporary names (tx, r, p, p0, g) reduce readability
`**cachedTextShape = tx.createTransformedShape(textLayout.getOutline(tx));
cachedTextShape = textLayout.getOutline(tx);`

## Good
`AbstractAttributedFigure` is well named, methods are clear, extension points obvious.
1. public abstract class AbstractAttributedFigure
2. public void draw(Graphics2D g) {
   if (get(FILL_COLOR) != null) {
   g.setColor(get(FILL_COLOR));
   drawFill(g);
   }
   if (get(STROKE_COLOR) != null && get(STROKE_WIDTH) >= 0d) {
   g.setStroke(...);
   g.setColor(get(STROKE_COLOR));
   drawStroke(g);
   }
   if (get(TEXT_COLOR) != null) {
   ...
   drawText(g);
   }
   }
3. protected abstract void drawFill(Graphics2D g);
   protected abstract void drawStroke(Graphics2D g);
   protected void drawText(Graphics2D g) { }