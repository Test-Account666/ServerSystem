package me.testaccount666.serversystem.utils

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

/**
 * Unit tests for the BiDirectionalHashMap class.
 *
 * Tests bidirectional key-value mapping, containment checks, and removal operations.
 */
class BiDirectionalHashMapTest {

    private lateinit var map: BiDirectionalHashMap<String, Int>

    @BeforeEach
    fun setup() {
        map = BiDirectionalHashMap()
    }

    @Test
    fun `should be empty when initialized`() {
        assertEquals(0, map.size())
        assertTrue(map.keySet().isEmpty())
        assertTrue(map.valueSet().isEmpty())
    }

    @Test
    fun `should put and retrieve key-value pair`() {
        map.put("one", 1)
        assertEquals(1, map.getValue("one"))
        assertEquals("one", map.getKey(1))
    }

    @Test
    fun `should contain key and value after put`() {
        map.put("one", 1)
        assertTrue(map.containsKey("one"))
        assertTrue(map.containsValue(1))
    }

    @Test
    fun `should not contain non-existent key and value`() {
        assertFalse(map.containsKey("two"))
        assertFalse(map.containsValue(2))
    }

    @Test
    fun `should return null for non-existent key and value`() {
        assertNull(map.getValue("nonexistent"))
        assertNull(map.getKey(999))
    }

    @Test
    fun `should update existing key`() {
        map.put("one", 1)
        map.put("one", 100)
        assertEquals(100, map.getValue("one"))
    }

    @Test
    fun `should handle multiple entries`() {
        map.put("one", 1)
        map.put("two", 2)
        map.put("three", 3)

        assertEquals(3, map.size())
        assertEquals(1, map.getValue("one"))
        assertEquals(2, map.getValue("two"))
        assertEquals(3, map.getValue("three"))
    }

    @Test
    fun `should remove by key`() {
        map.put("one", 1)
        map.put("two", 2)

        map.removeByKey("one")

        assertFalse(map.containsKey("one"))
        assertFalse(map.containsValue(1))
        assertEquals(1, map.size())
        assertTrue(map.containsKey("two"))
    }

    @Test
    fun `should remove by value`() {
        map.put("one", 1)
        map.put("two", 2)

        map.removeByValue(1)

        assertFalse(map.containsKey("one"))
        assertFalse(map.containsValue(1))
        assertEquals(1, map.size())
        assertTrue(map.containsKey("two"))
    }

    @Test
    fun `should handle remove of non-existent key gracefully`() {
        map.put("one", 1)
        map.removeByKey("nonexistent")
        assertEquals(1, map.size())
    }

    @Test
    fun `should handle remove of non-existent value gracefully`() {
        map.put("one", 1)
        map.removeByValue(999)
        assertEquals(1, map.size())
    }

    @Test
    fun `should return all keys`() {
        map.put("one", 1)
        map.put("two", 2)
        map.put("three", 3)

        val keys = map.keySet()
        assertEquals(3, keys.size)
        assertTrue(keys.contains("one"))
        assertTrue(keys.contains("two"))
        assertTrue(keys.contains("three"))
    }

    @Test
    fun `should return all values`() {
        map.put("one", 1)
        map.put("two", 2)
        map.put("three", 3)

        val values = map.valueSet()
        assertEquals(3, values.size)
        assertTrue(values.contains(1))
        assertTrue(values.contains(2))
        assertTrue(values.contains(3))
    }

    @Test
    fun `should clear all entries`() {
        map.put("one", 1)
        map.put("two", 2)

        map.clear()

        assertEquals(0, map.size())
        assertTrue(map.keySet().isEmpty())
        assertTrue(map.valueSet().isEmpty())
    }

    @Test
    fun `should maintain bidirectional consistency after multiple operations`() {
        map.put("a", 1)
        map.put("b", 2)
        map.put("c", 3)

        map.removeByKey("b")
        map.put("d", 4)

        assertEquals(3, map.size())
        assertEquals(1, map.getValue("a"))
        assertEquals(3, map.getValue("c"))
        assertEquals(4, map.getValue("d"))
        assertEquals("a", map.getKey(1))
        assertEquals("c", map.getKey(3))
        assertEquals("d", map.getKey(4))
    }

    @Test
    fun `should handle different generic types`() {
        val intStringMap = BiDirectionalHashMap<Int, String>()
        intStringMap.put(1, "one")
        intStringMap.put(2, "two")

        assertEquals("one", intStringMap.getValue(1))
        assertEquals(2, intStringMap.getKey("two"))
    }
}

