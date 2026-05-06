package org.jhotdraw.draw;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AttributeKeyTest {

    @Test
    public void get_shouldReturnDefaultValue_whenKeyIsMissingFromMap() {
        AttributeKey<String> key = new AttributeKey<>("fontName", String.class, "Arial");
        Map<AttributeKey<?>, Object> attributes = new HashMap<>();

        String result = key.get(attributes);

        assertEquals("Arial", result);
    }

    @Test
    public void put_shouldStoreValueAndReturnOldValue() {
        AttributeKey<String> key = new AttributeKey<>("fontName", String.class, "Arial");
        Map<AttributeKey<?>, Object> attributes = new HashMap<>();

        Object firstOldValue = key.put(attributes, "Helvetica");
        Object secondOldValue = key.put(attributes, "Courier");

        assertNull(firstOldValue);
        assertEquals("Helvetica", secondOldValue);
        assertEquals("Courier", key.get(attributes));
    }

    @Test(expected = NullPointerException.class)
    public void put_shouldThrowException_whenNullIsNotAllowed() {
        AttributeKey<String> key = new AttributeKey<>("fontName", String.class, "Arial", false);
        Map<AttributeKey<?>, Object> attributes = new HashMap<>();

        key.put(attributes, null);
    }

    @Test
    public void isAssignable_shouldReturnTrue_forCorrectType() {
        AttributeKey<String> key = new AttributeKey<>("fontName", String.class, "Arial");

        assertTrue(key.isAssignable("Times New Roman"));
    }

    @Test
    public void isAssignable_shouldReturnFalse_forWrongType() {
        AttributeKey<String> key = new AttributeKey<>("fontName", String.class, "Arial");

        assertFalse(key.isAssignable(123));
    }

    @Test
    public void isAssignable_shouldRespectNullPolicy() {
        AttributeKey<String> nullableKey = new AttributeKey<>("fontName", String.class, "Arial", true);
        AttributeKey<String> nonNullableKey = new AttributeKey<>("fontName", String.class, "Arial", false);

        assertTrue(nullableKey.isAssignable(null));
        assertFalse(nonNullableKey.isAssignable(null));
    }

    @Test
    public void equalsAndHashCode_shouldDependOnKeyString() {
        AttributeKey<String> key1 = new AttributeKey<>("fontName", String.class, "Arial");
        AttributeKey<String> key2 = new AttributeKey<>("fontName", String.class, "Helvetica");
        AttributeKey<String> key3 = new AttributeKey<>("fontSize", String.class, "12");

        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
        assertNotEquals(key1, key3);
    }

    @Test
    public void toString_shouldReturnKeyString() {
        AttributeKey<String> key = new AttributeKey<>("fontName", String.class, "Arial");

        assertEquals("fontName", key.toString());
    }

    @Test
    public void putClone_shouldStoreCloneInsteadOfOriginalObject() {
        AttributeKey<ArrayList<String>> key =
                new AttributeKey<>("list", (Class<ArrayList<String>>) (Class<?>) ArrayList.class, null);

        Map<AttributeKey<?>, Object> map = new HashMap<>();

        ArrayList<String> original = new ArrayList<>();
        original.add("abc");

        key.putClone(map, original);

        ArrayList<String> stored = key.get(map);

        assertNotNull(stored);
        assertEquals(1, stored.size());
        assertEquals("abc", stored.get(0));
        assertNotSame(original, stored);
    }

    @Test
    public void putClone_shouldStoreNull_whenValueIsNullAndNullIsAllowed() {
        AttributeKey<ArrayList<String>> key =
                new AttributeKey<>("list", (Class<ArrayList<String>>) (Class<?>) ArrayList.class, null, true);

        Map<AttributeKey<?>, Object> map = new HashMap<>();

        key.putClone(map, null);

        assertNull(key.get(map));
    }
}