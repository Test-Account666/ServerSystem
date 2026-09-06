package me.testaccount666.serversystem.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Unit tests for the DurationParser utility object.
 *
 * Tests duration parsing from string format to milliseconds.
 * Supports formats: y (years), mo (months), w (weeks), d (days), h (hours), m (minutes), s (seconds)
 */
@Suppress("KotlinMisorderedAssertEqualsArguments")
class DurationParserTest {

    @ParameterizedTest
    @ValueSource(strings = ["permanent", "Permanent", "PERMANENT"])
    fun `should parse permanent duration case-insensitively as -1`(permanent: String) {
        assertEquals(-1L, DurationParser.parseDuration(permanent))
    }

    @Test
    fun `should parse seconds`() {
        assertEquals(30.seconds.inWholeMilliseconds, DurationParser.parseDuration("30s"))
    }

    @Test
    fun `should parse minutes`() {
        assertEquals(5.minutes.inWholeMilliseconds, DurationParser.parseDuration("5m"))
    }

    @Test
    fun `should parse hours`() {
        assertEquals(2.hours.inWholeMilliseconds, DurationParser.parseDuration("2h"))
    }

    @Test
    fun `should parse days`() {
        assertEquals(1.days.inWholeMilliseconds, DurationParser.parseDuration("1d"))
    }

    @Test
    fun `should parse weeks`() {
        assertEquals(7.days.inWholeMilliseconds, DurationParser.parseDuration("1w"))
    }

    @Test
    fun `should parse months (30 days)`() {
        assertEquals(30.days.inWholeMilliseconds, DurationParser.parseDuration("1mo"))
    }

    @Test
    fun `should parse years (365 days)`() {
        assertEquals(365.days.inWholeMilliseconds, DurationParser.parseDuration("1y"))
    }

    @Test
    fun `should combine multiple time units`() {
        val result = DurationParser.parseDuration("1d12h30m")
        val expected = 1.days.inWholeMilliseconds + 12.hours.inWholeMilliseconds + 30.minutes.inWholeMilliseconds
        assertEquals(expected, result)
    }

    @Test
    fun `should handle complex mixed duration`() {
        val result = DurationParser.parseDuration("1w2d3h4m5s")
        val expected = 9.days.inWholeMilliseconds + 3.hours.inWholeMilliseconds + 4.minutes.inWholeMilliseconds + 5.seconds.inWholeMilliseconds
        assertEquals(expected, result)
    }

    @Test
    fun `should parse single large number`() {
        val result = DurationParser.parseDuration("100d")
        val expected = 100.days.inWholeMilliseconds
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @ValueSource(strings = [" ", "\t", "\n", "invalid", "", "abc", "5x"])
    fun `should return negative two for invalid format`(input: String) {
        assertEquals(-2L, DurationParser.parseDuration(input))
    }

    @Test
    fun `should ignore extra numbers without valid unit`() {
        val result = DurationParser.parseDuration("5m10")
        val expected = 5.minutes.inWholeMilliseconds
        assertEquals(expected, result)
    }

    @Test
    fun `should handle leading zeros`() {
        val result = DurationParser.parseDuration("05d")
        val expected = 5.days.inWholeMilliseconds
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["0s", "0m", "0d"])
    fun `should parse zero values as -2`(duration: String) {
        assertEquals(-2L, DurationParser.parseDuration(duration))
    }

    @Test
    fun `should handle months before days in mixed format`() {
        val result = DurationParser.parseDuration("2mo1d")
        val expected = 30.days.times(2).inWholeMilliseconds + 1.days.inWholeMilliseconds
        assertEquals(expected, result)
    }
}

