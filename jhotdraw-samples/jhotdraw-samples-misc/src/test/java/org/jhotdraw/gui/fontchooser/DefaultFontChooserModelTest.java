package org.jhotdraw.gui.fontchooser;

import org.junit.Test;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.Font;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DefaultFontChooserModelTest {

    @Test
    public void setFonts_shouldMergeFontsIntoSingleFamilyNode_whenFamilyNamesAreEqual() {
        DefaultFontChooserModel model = new DefaultFontChooserModel();

        Font[] fonts = {
                new Font("Dialog", Font.PLAIN, 12),
                new Font("Dialog", Font.BOLD, 12),
                new Font("Serif", Font.PLAIN, 12)
        };

        model.setFonts(fonts);

        Object root = model.getRoot();
        assertNotNull(root);
        assertTrue(model.getChildCount(root) > 0);

        FontCollectionNode allFonts = (FontCollectionNode) model.getChild(root, 0);
        assertNotNull(allFonts);

        int dialogFamilyCount = 0;
        int serifFamilyCount = 0;

        for (FontFamilyNode family : allFonts.families()) {
            if ("Dialog".equals(family.getName())) {
                dialogFamilyCount++;
                assertEquals(2, family.getChildCount());
            }
            if ("Serif".equals(family.getName())) {
                serifFamilyCount++;
                assertEquals(1, family.getChildCount());
            }
        }

        assertEquals(1, dialogFamilyCount);
        assertEquals(1, serifFamilyCount);
    }

    @Test
    public void collectFamiliesNamed_shouldReturnOnlyFamiliesWithRequestedNames() {
        DefaultFontChooserModel model = new DefaultFontChooserModel();

        ArrayList<FontFamilyNode> families = new ArrayList<>();
        families.add(new FontFamilyNode("Arial"));
        families.add(new FontFamilyNode("Verdana"));
        families.add(new FontFamilyNode("Courier New"));

        ArrayList<FontFamilyNode> result =
                model.collectFamiliesNamed(families, "Arial", "Verdana");

        assertEquals(2, result.size());
        assertEquals("Arial", result.get(0).getName());
        assertEquals("Verdana", result.get(1).getName());
    }

    @Test
    public void collectFamiliesNamed_shouldReturnEmptyList_whenNoNamesMatch() {
        DefaultFontChooserModel model = new DefaultFontChooserModel();

        ArrayList<FontFamilyNode> families = new ArrayList<>();
        families.add(new FontFamilyNode("Arial"));
        families.add(new FontFamilyNode("Verdana"));

        ArrayList<FontFamilyNode> result =
                model.collectFamiliesNamed(families, "NonExistingFont");

        assertTrue(result.isEmpty());
    }

    @Test
    public void getRoot_shouldReturnNonNullRoot() {
        DefaultFontChooserModel model = new DefaultFontChooserModel();

        assertNotNull(model.getRoot());
    }

    @Test
    public void setFonts_shouldCreateCollections_evenWhenFontArrayIsEmpty() {
        DefaultFontChooserModel model = new DefaultFontChooserModel();

        model.setFonts(new Font[0]);

        Object root = model.getRoot();

        assertNotNull(root);
        assertTrue(model.getChildCount(root) > 0);
        assertFalse(model.isLeaf(root));
    }

    @Test
    public void treeMethods_shouldReturnConsistentChildAccess() {
        DefaultFontChooserModel model = new DefaultFontChooserModel();

        model.setFonts(new Font[]{
                new Font("Dialog", Font.PLAIN, 12)
        });

        Object root = model.getRoot();
        Object firstChild = model.getChild(root, 0);

        assertEquals(0, model.getIndexOfChild(root, firstChild));
        assertFalse(model.isLeaf(root));
    }
}