package me.testaccount666.serversystem.utils;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.messages.MappingsData;
import me.testaccount666.serversystem.userdata.OfflineUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DurationParserTest {

    private OfflineUser mockUser;
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        mockUser = mock(OfflineUser.class);
        mockLogger = mock(Logger.class);
    }

    @Test
    void parseDuration_shouldParseSingleUnits() {
        assertEquals(1000L, DurationParser.parseDuration("1s"));
        assertEquals(60000L, DurationParser.parseDuration("1m"));
        assertEquals(3600000L, DurationParser.parseDuration("1h"));
        assertEquals(86400000L, DurationParser.parseDuration("1d"));
        assertEquals(604800000L, DurationParser.parseDuration("1w"));
        assertEquals(2419200000L, DurationParser.parseDuration("1mo"));
        assertEquals(31536000000L, DurationParser.parseDuration("1y"));
    }

    @Test
    void parseDuration_shouldParseMultipleUnits() {
        // 1 day + 2 hours + 30 minutes = 86400 + 7200 + 1800 = 95400 seconds = 95400000 ms
        assertEquals(95400000L, DurationParser.parseDuration("1d2h30m"));

        // 1 year + 1 month + 1 week = 31536000 + 2419200 + 604800 = 34560000 seconds = 34560000000 ms
        assertEquals(34560000000L, DurationParser.parseDuration("1y1mo1w"));
    }

    @Test
    void parseDuration_shouldHandlePermanent() {
        assertEquals(-1L, DurationParser.parseDuration("permanent"));
        assertEquals(-1L, DurationParser.parseDuration("PERMANENT"));
        assertEquals(-1L, DurationParser.parseDuration("Permanent"));
    }

    @Test
    void parseDuration_shouldHandleInvalidInput() {
        // Invalid input that doesn't match the regex pattern returns 0
        assertEquals(0L, DurationParser.parseDuration("invalid"));
        assertEquals(0L, DurationParser.parseDuration("abc"));
        assertEquals(0L, DurationParser.parseDuration(""));
    }

    @Test
    void parseDuration_shouldHandleZeroValues() {
        assertEquals(0L, DurationParser.parseDuration("0s"));
        assertEquals(0L, DurationParser.parseDuration("0m"));
        assertEquals(0L, DurationParser.parseDuration("0h"));
    }

    @Test
    void parseDuration_shouldHandleLargeValues() {
        assertEquals(999000L, DurationParser.parseDuration("999s"));
        assertEquals(59940000L, DurationParser.parseDuration("999m"));
    }

    @Test
    void parseDuration_shouldHandleNegativeInput() {
        // The regex matches digits only, so "-1s" matches "1s" and ignores the minus sign
        assertEquals(1000L, DurationParser.parseDuration("-1s"));
        assertEquals(300000L, DurationParser.parseDuration("-5m"));
    }

    @Test
    void parseDate_shouldHandlePermanent() {
        try (var mappingsDataMock = Mockito.mockStatic(MappingsData.class);
             var serverSystemMock = Mockito.mockStatic(ServerSystem.class)) {

            var mockModeration = mock(MappingsData.Moderation.class);
            mappingsDataMock.when(() -> MappingsData.moderation(mockUser)).thenReturn(mockModeration);
            when(mockModeration.getName("permanent")).thenReturn(Optional.of("Permanent"));

            var result = DurationParser.parseDate(-1L, mockUser);
            assertEquals("Permanent", result);
        }
    }

    @Test
    void parseDate_shouldHandlePermanentWithMissingMapping() {
        try (var mappingsDataMock = Mockito.mockStatic(MappingsData.class);
             var serverSystemMock = Mockito.mockStatic(ServerSystem.class)) {

            var mockModeration = mock(MappingsData.Moderation.class);
            mappingsDataMock.when(() -> MappingsData.moderation(mockUser)).thenReturn(mockModeration);
            when(mockModeration.getName("permanent")).thenReturn(Optional.empty());
            serverSystemMock.when(ServerSystem::getLog).thenReturn(mockLogger);

            var result = DurationParser.parseDate(-1L, mockUser);
            assertEquals("Never", result);

            verify(mockLogger).warning("Permanent name could not be found! This should not happen!");
        }
    }

    @Test
    void parseDate_shouldFormatRegularDate() {
        // Test with a specific timestamp: January 1, 2024, 12:00:00
        var timestamp = 1704110400000L; // 2024-01-01 12:00:00 UTC (may vary by timezone)

        var result = DurationParser.parseDate(timestamp, mockUser);

        // Verify the format is correct (dd.MM.yyyy HH:mm:ss)
        var dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        var expected = dateFormatter.format(timestamp);
        assertEquals(expected, result);
    }

    @Test
    void parseDate_shouldHandleZeroTimestamp() {
        // Test with timestamp 0 (January 1, 1970)
        var result = DurationParser.parseDate(0L, mockUser);

        var dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        var expected = dateFormatter.format(0L);
        assertEquals(expected, result);
    }

    @Test
    void parseDate_shouldHandlePermanentTimestamp() {
        try (var mappingsDataMock = Mockito.mockStatic(MappingsData.class);
             var serverSystemMock = Mockito.mockStatic(ServerSystem.class)) {
            var mockModeration = mock(MappingsData.Moderation.class);
            mappingsDataMock.when(() -> MappingsData.moderation(mockUser)).thenReturn(mockModeration);
            serverSystemMock.when(ServerSystem::getLog).thenReturn(mockLogger);

            var result = DurationParser.parseDate(-1L, mockUser);

            var expected = "Never";
            assertEquals(expected, result);
        }
    }
}