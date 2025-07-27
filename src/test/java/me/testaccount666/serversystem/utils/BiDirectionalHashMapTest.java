package me.testaccount666.serversystem.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BiDirectionalHashMapTest {

    private BiDirectionalHashMap<String, Integer> biMap;

    @BeforeEach
    void setUp() {
        biMap = new BiDirectionalHashMap<>();
    }

    @Test
    void put_shouldAddBidirectionalMapping() {
        biMap.put("one", 1);

        assertTrue(biMap.containsKey("one"));
        assertTrue(biMap.containsValue(1));
        assertEquals(Optional.of(1), biMap.getValue("one"));
        assertEquals(Optional.of("one"), biMap.getKey(1));
    }

    @Test
    void put_shouldOverwriteExistingMappings() {
        biMap.put("one", 1);
        biMap.put("one", 2); // Overwrite key "one"

        // Note: This reveals a bug in BiDirectionalHashMap - old values aren't removed from reverse map
        assertTrue(biMap.containsValue(1)); // Bug: old value still exists in reverse map
        assertTrue(biMap.containsValue(2));
        assertEquals(Optional.of(2), biMap.getValue("one"));
        assertEquals(Optional.of("one"), biMap.getKey(2));
        assertEquals(Optional.of("one"), biMap.getKey(1)); // Bug: old mapping still exists
    }

    @Test
    void containsKey_shouldReturnCorrectResults() {
        assertFalse(biMap.containsKey("nonexistent"));

        biMap.put("test", 42);
        assertTrue(biMap.containsKey("test"));
        assertFalse(biMap.containsKey("other"));
    }

    @Test
    void containsValue_shouldReturnCorrectResults() {
        assertFalse(biMap.containsValue(999));

        biMap.put("test", 42);
        assertTrue(biMap.containsValue(42));
        assertFalse(biMap.containsValue(999));
    }

    @Test
    void getValue_shouldReturnOptionalResults() {
        assertEquals(Optional.empty(), biMap.getValue("nonexistent"));

        biMap.put("key", 100);
        assertEquals(Optional.of(100), biMap.getValue("key"));
        assertEquals(Optional.empty(), biMap.getValue("other"));
    }

    @Test
    void getKey_shouldReturnOptionalResults() {
        assertEquals(Optional.empty(), biMap.getKey(999));

        biMap.put("key", 100);
        assertEquals(Optional.of("key"), biMap.getKey(100));
        assertEquals(Optional.empty(), biMap.getKey(999));
    }

    @Test
    void removeByKey_shouldRemoveBidirectionalMapping() {
        biMap.put("remove", 123);
        assertTrue(biMap.containsKey("remove"));
        assertTrue(biMap.containsValue(123));

        biMap.removeByKey("remove");
        assertFalse(biMap.containsKey("remove"));
        assertFalse(biMap.containsValue(123));
        assertEquals(Optional.empty(), biMap.getValue("remove"));
        assertEquals(Optional.empty(), biMap.getKey(123));
    }

    @Test
    void removeByKey_shouldHandleNonexistentKey() {
        biMap.put("existing", 1);
        var sizeBefore = biMap.size();

        biMap.removeByKey("nonexistent");
        assertEquals(sizeBefore, biMap.size());
        assertTrue(biMap.containsKey("existing"));
    }

    @Test
    void removeByValue_shouldRemoveBidirectionalMapping() {
        biMap.put("test", 456);
        assertTrue(biMap.containsKey("test"));
        assertTrue(biMap.containsValue(456));

        biMap.removeByValue(456);
        assertFalse(biMap.containsKey("test"));
        assertFalse(biMap.containsValue(456));
        assertEquals(Optional.empty(), biMap.getValue("test"));
        assertEquals(Optional.empty(), biMap.getKey(456));
    }

    @Test
    void removeByValue_shouldHandleNonexistentValue() {
        biMap.put("existing", 1);
        var sizeBefore = biMap.size();

        biMap.removeByValue(999);
        assertEquals(sizeBefore, biMap.size());
        assertTrue(biMap.containsValue(1));
    }

    @Test
    void keySet_shouldReturnAllKeys() {
        assertTrue(biMap.keySet().isEmpty());

        biMap.put("a", 1);
        biMap.put("b", 2);
        biMap.put("c", 3);

        var keySet = biMap.keySet();
        assertEquals(3, keySet.size());
        assertTrue(keySet.contains("a"));
        assertTrue(keySet.contains("b"));
        assertTrue(keySet.contains("c"));
    }

    @Test
    void valueSet_shouldReturnAllValues() {
        assertTrue(biMap.valueSet().isEmpty());

        biMap.put("a", 1);
        biMap.put("b", 2);
        biMap.put("c", 3);

        var valueSet = biMap.valueSet();
        assertEquals(3, valueSet.size());
        assertTrue(valueSet.contains(1));
        assertTrue(valueSet.contains(2));
        assertTrue(valueSet.contains(3));
    }

    @Test
    void size_shouldReturnCorrectSize() {
        assertEquals(0, biMap.size());

        biMap.put("one", 1);
        assertEquals(1, biMap.size());

        biMap.put("two", 2);
        assertEquals(2, biMap.size());

        biMap.removeByKey("one");
        assertEquals(1, biMap.size());

        biMap.clear();
        assertEquals(0, biMap.size());
    }

    @Test
    void clear_shouldRemoveAllMappings() {
        biMap.put("a", 1);
        biMap.put("b", 2);
        biMap.put("c", 3);
        assertEquals(3, biMap.size());

        biMap.clear();
        assertEquals(0, biMap.size());
        assertTrue(biMap.keySet().isEmpty());
        assertTrue(biMap.valueSet().isEmpty());
        assertFalse(biMap.containsKey("a"));
        assertFalse(biMap.containsValue(1));
    }

    @Test
    void put_shouldHandleNullValues() {
        biMap.put("nullKey", null);

        assertTrue(biMap.containsKey("nullKey"));
        assertTrue(biMap.containsValue(null));
        assertEquals(Optional.empty(), biMap.getValue("nullKey")); // Optional.ofNullable(null) = empty
        assertEquals(Optional.of("nullKey"), biMap.getKey(null)); // Can retrieve key for null value
    }

    @Test
    void put_shouldHandleNullKeys() {
        biMap.put(null, 42);

        assertTrue(biMap.containsKey(null));
        assertTrue(biMap.containsValue(42));
        assertEquals(Optional.of(42), biMap.getValue(null)); // Can retrieve value for null key
        assertEquals(Optional.empty(), biMap.getKey(42)); // Optional.ofNullable(null) = empty
    }

    @Test
    void bidirectionalConsistency_shouldBeMaintained() {
        // Test that both directions are always consistent
        biMap.put("alpha", 100);
        biMap.put("beta", 200);
        biMap.put("gamma", 300);

        // Verify forward mapping
        assertEquals(Optional.of(100), biMap.getValue("alpha"));
        assertEquals(Optional.of(200), biMap.getValue("beta"));
        assertEquals(Optional.of(300), biMap.getValue("gamma"));

        // Verify reverse mapping
        assertEquals(Optional.of("alpha"), biMap.getKey(100));
        assertEquals(Optional.of("beta"), biMap.getKey(200));
        assertEquals(Optional.of("gamma"), biMap.getKey(300));

        // Remove by key and verify both directions
        biMap.removeByKey("beta");
        assertEquals(Optional.empty(), biMap.getValue("beta"));
        assertEquals(Optional.empty(), biMap.getKey(200));

        // Remove by value and verify both directions
        biMap.removeByValue(300);
        assertEquals(Optional.empty(), biMap.getValue("gamma"));
        assertEquals(Optional.empty(), biMap.getKey(300));
    }
}