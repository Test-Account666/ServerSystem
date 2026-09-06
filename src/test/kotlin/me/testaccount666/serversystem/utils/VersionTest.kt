package me.testaccount666.serversystem.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the Version class.
 *
 * Tests version parsing, comparison, and validation.
 */
class VersionTest {

    @Test
    fun `should create version with valid format`() {
        val version = Version("1.2.3")
        assertEquals("1.2.3", version.version)
    }

    @Test
    fun `should trim whitespace from version string`() {
        val version = Version("  1.2.3  ")
        assertEquals("1.2.3", version.version)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "1..2", "1.2.", ".1.2", "1.2.3a", "1.2-3"])
    fun `should reject invalid version string`(input: String) {
        assertThrows<IllegalArgumentException> { Version(input) }
    }

    @Test
    fun `should compare equal versions as equal`() {
        val version1 = Version("1.2.3")
        val version2 = Version("1.2.3")
        assertEquals(0, version1.compareTo(version2))
    }

    @Test
    fun `should identify newer major version`() {
        val version1 = Version("2.0.0")
        val version2 = Version("1.9.9")
        assertTrue(version1 > version2)
    }

    @Test
    fun `should identify older major version`() {
        val version1 = Version("1.9.9")
        val version2 = Version("2.0.0")
        assertTrue(version1 < version2)
    }

    @Test
    fun `should identify newer minor version`() {
        val version1 = Version("1.2.0")
        val version2 = Version("1.1.9")
        assertTrue(version1 > version2)
    }

    @Test
    fun `should identify newer patch version`() {
        val version1 = Version("1.2.3")
        val version2 = Version("1.2.2")
        assertTrue(version1 > version2)
    }

    @Test
    fun `should handle version with different segment counts`() {
        val version1 = Version("1.2")
        val version2 = Version("1.2.0")
        assertTrue(version1 < version2)
    }

    @Test
    fun `should handle version with single segment`() {
        val version1 = Version("2")
        val version2 = Version("1")
        assertTrue(version1 > version2)
    }

    @Test
    fun `should correctly use version in comparable scenarios`() {
        val versions = listOf(
            Version("1.2.3"),
            Version("1.0.0"),
            Version("2.1.0"),
            Version("1.2.2")
        )
        val sorted = versions.sorted()

        assertEquals("1.0.0", sorted[0].version)
        assertEquals("1.2.2", sorted[1].version)
        assertEquals("1.2.3", sorted[2].version)
        assertEquals("2.1.0", sorted[3].version)
    }

    @Test
    fun `should convert to string correctly`() {
        val version = Version("4.1.0")
        assertEquals("4.1.0", version.toString())
    }
}

