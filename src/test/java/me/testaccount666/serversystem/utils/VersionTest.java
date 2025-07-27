package me.testaccount666.serversystem.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    void testVersionComparison() {
        var version1 = new Version("3.1.0");
        var version2 = new Version("3.2.0");
        var version3 = new Version("3.1.0");

        assertTrue(version2.compareTo(version1) > 0, "3.2.0 should be greater than 3.1.0");
        assertTrue(version1.compareTo(version2) < 0, "3.1.0 should be less than 3.2.0");
        assertEquals(0, version1.compareTo(version3), "3.1.0 should equal 3.1.0");
    }

    @Test
    void testVersionEquality() {
        var version1 = new Version("3.1.0");
        var version2 = new Version("3.1.0");
        var version3 = new Version("3.2.0");

        assertEquals(version1, version2, "Same versions should be equal");
        assertNotEquals(version1, version3, "Different versions should not be equal");
        assertEquals(version1.hashCode(), version2.hashCode(), "Same versions should have same hash code");
    }

    @Test
    void testVersionToString() {
        var version = new Version("3.1.0");
        assertEquals("3.1.0", version.toString(), "toString should return the version string");
        assertEquals("3.1.0", version.getVersion(), "getVersionString should return the version string");
    }

    @Test
    void testInvalidVersions() {
        assertThrows(IllegalArgumentException.class, () -> new Version(null), "Null version should throw exception");
        assertThrows(IllegalArgumentException.class, () -> new Version(""), "Empty version should throw exception");
        assertThrows(IllegalArgumentException.class, () -> new Version("   "), "Whitespace version should throw exception");
        assertThrows(IllegalArgumentException.class, () -> new Version("3.1.a"), "Non-numeric version should throw exception");
        assertThrows(IllegalArgumentException.class, () -> new Version("3..1"), "Double dots should throw exception");
    }

    @Test
    void testVersionComparisonWithDifferentLengths() {
        var version1 = new Version("3.1");
        var version2 = new Version("3.1.0");

        // 3.1 should be considered less than 3.1.0 (shorter versions are considered smaller)
        assertTrue(version1.compareTo(version2) < 0, "3.1 should be less than 3.1.0");
        assertTrue(version2.compareTo(version1) > 0, "3.1.0 should be greater than 3.1");
    }

    @Test
    void testNullComparison() {
        var version = new Version("3.1.0");
        assertThrows(NullPointerException.class, () -> version.compareTo(null), "Comparing to null should throw exception");
    }
}