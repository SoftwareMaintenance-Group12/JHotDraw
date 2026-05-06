package org.jhotdraw.draw.acceptance;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.jhotdraw.draw.AttributeKey;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class AttributeKeyAcceptanceTest
        extends ScenarioTest<
        AttributeKeyAcceptanceTest.GivenAttributeKey,
        AttributeKeyAcceptanceTest.WhenAttributeKey,
        AttributeKeyAcceptanceTest.ThenAttributeKey> {

    @Test
    public void non_nullable_attribute_key_rejects_null_value() {
        given().a_non_nullable_attribute_key();

        when().null_is_put_as_value();

        then().a_null_pointer_exception_should_be_thrown();
    }

    public static class GivenAttributeKey extends Stage<GivenAttributeKey> {

        @ScenarioState
        AttributeKey<String> key;

        @ScenarioState
        Map<AttributeKey<?>, Object> attributes;

        public GivenAttributeKey a_non_nullable_attribute_key() {
            key = new AttributeKey<>("testKey", String.class, "default", false);
            attributes = new HashMap<>();
            return self();
        }
    }

    public static class WhenAttributeKey extends Stage<WhenAttributeKey> {

        @ScenarioState
        AttributeKey<String> key;

        @ScenarioState
        Map<AttributeKey<?>, Object> attributes;

        @ScenarioState
        Exception thrownException;

        public WhenAttributeKey null_is_put_as_value() {
            try {
                key.put(attributes, null);
            } catch (Exception ex) {
                thrownException = ex;
            }
            return self();
        }
    }

    public static class ThenAttributeKey extends Stage<ThenAttributeKey> {

        @ScenarioState
        Exception thrownException;

        public ThenAttributeKey a_null_pointer_exception_should_be_thrown() {
            assertTrue(thrownException instanceof NullPointerException);
            return self();
        }
    }
}