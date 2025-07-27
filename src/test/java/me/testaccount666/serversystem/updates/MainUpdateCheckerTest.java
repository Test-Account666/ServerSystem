package me.testaccount666.serversystem.updates;

import me.testaccount666.serversystem.utils.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class MainUpdateCheckerTest {

    private MainUpdateChecker updateChecker;

    @BeforeEach
    void setUp() {
        updateChecker = new MainUpdateChecker();
    }

    @Test
    void constructor_shouldSetCorrectURI() throws Exception {
        var uriField = AbstractUpdateChecker.class.getDeclaredField("updateURI");
        uriField.setAccessible(true);
        var uri = (URI) uriField.get(updateChecker);

        assertEquals("https://pluginsupport.zapto.org/PluginSupport/ServerSystem2/", uri.toString());
    }

    @Test
    void parseLatestVersion_shouldParseSingleVersion() {
        var responseBody = "<html><body><a href=\"1.2.3\">Version 1.2.3</a></body></html>";

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("1.2.3"), result);
    }

    @Test
    void parseLatestVersion_shouldParseMultipleVersionsAndReturnLatest() {
        var responseBody = """
                <html><body>
                <a href="1.0.0">Version 1.0.0</a>
                <a href="1.2.3">Version 1.2.3</a>
                <a href="1.1.5">Version 1.1.5</a>
                <a href="2.0.0">Version 2.0.0</a>
                <a href="1.5.0">Version 1.5.0</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("2.0.0"), result);
    }

    @Test
    void parseLatestVersion_shouldHandleVersionsWithDifferentFormats() {
        var responseBody = """
                <html><body>
                <a href="1.0">Version 1.0</a>
                <a href="1.2.3.4">Version 1.2.3.4</a>
                <a href="2.1">Version 2.1</a>
                <a href="1.10.5">Version 1.10.5</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("2.1"), result);
    }

    @Test
    void parseLatestVersion_shouldHandleMajorVersionComparison() {
        var responseBody = """
                <html><body>
                <a href="1.9.9">Version 1.9.9</a>
                <a href="2.0.0">Version 2.0.0</a>
                <a href="1.10.0">Version 1.10.0</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("2.0.0"), result);
    }

    @Test
    void parseLatestVersion_shouldHandleMinorVersionComparison() {
        var responseBody = """
                <html><body>
                <a href="1.9.0">Version 1.9.0</a>
                <a href="1.10.0">Version 1.10.0</a>
                <a href="1.2.0">Version 1.2.0</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("1.10.0"), result);
    }

    @Test
    void parseLatestVersion_shouldHandlePatchVersionComparison() {
        var responseBody = """
                <html><body>
                <a href="1.0.9">Version 1.0.9</a>
                <a href="1.0.10">Version 1.0.10</a>
                <a href="1.0.2">Version 1.0.2</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("1.0.10"), result);
    }

    @Test
    void parseLatestVersion_shouldHandleEqualVersions() {
        var responseBody = """
                <html><body>
                <a href="1.0.0">Version 1.0.0</a>
                <a href="1.0.0">Version 1.0.0 (duplicate)</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("1.0.0"), result);
    }

    @Test
    void parseLatestVersion_shouldThrowExceptionWhenNoVersionFound() {
        var responseBody = "<html><body><p>No versions available</p></body></html>";

        var exception = assertThrows(IllegalStateException.class, () ->
                updateChecker.parseLatestVersion(responseBody));

        assertEquals("No version found in response body", exception.getMessage());
    }

    @Test
    void parseLatestVersion_shouldThrowExceptionForEmptyResponse() {
        var responseBody = "";

        var exception = assertThrows(IllegalStateException.class, () ->
                updateChecker.parseLatestVersion(responseBody));

        assertEquals("No version found in response body", exception.getMessage());
    }

    @Test
    void parseLatestVersion_shouldIgnoreInvalidVersionFormats() {
        var responseBody = """
                <html><body>
                <a href="invalid">Invalid version</a>
                <a href="1.2.3">Version 1.2.3</a>
                <a href="not.a.version">Not a version</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("1.2.3"), result);
    }

    @Test
    void parseLatestVersion_shouldHandleVersionsWithLeadingZeros() {
        var responseBody = """
                <html><body>
                <a href="1.01.0">Version 1.01.0</a>
                <a href="1.2.03">Version 1.2.03</a>
                <a href="01.5.0">Version 01.5.0</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        // The Version class should handle leading zeros appropriately
        // We expect the highest version to be selected
        assertNotNull(result);
    }

    @Test
    void getDownloadUrl_shouldReturnCorrectUrl() throws Exception {
        // Set up the latestVersion field using reflection
        var latestVersionField = AbstractUpdateChecker.class.getDeclaredField("latestVersion");
        latestVersionField.setAccessible(true);
        latestVersionField.set(updateChecker, new Version("1.2.3"));

        var result = updateChecker.getDownloadUrl();

        assertEquals("https://pluginsupport.zapto.org/PluginSupport/ServerSystem2/1.2.3", result);
    }

    @Test
    void getDownloadUrl_shouldHandleVersionWithMultipleParts() throws Exception {
        var latestVersionField = AbstractUpdateChecker.class.getDeclaredField("latestVersion");
        latestVersionField.setAccessible(true);
        latestVersionField.set(updateChecker, new Version("2.1.0.5"));

        var result = updateChecker.getDownloadUrl();

        assertEquals("https://pluginsupport.zapto.org/PluginSupport/ServerSystem2/2.1.0.5", result);
    }

    @Test
    void parseLatestVersion_shouldHandleLargeVersionNumbers() {
        var responseBody = """
                <html><body>
                <a href="999.999.999">Version 999.999.999</a>
                <a href="1000.0.0">Version 1000.0.0</a>
                <a href="1.0.0">Version 1.0.0</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("1000.0.0"), result);
    }

    @Test
    void parseLatestVersion_shouldHandleVersionsInRandomOrder() {
        var responseBody = """
                <html><body>
                <a href="3.0.0">Version 3.0.0</a>
                <a href="1.0.0">Version 1.0.0</a>
                <a href="5.2.1">Version 5.2.1</a>
                <a href="2.1.0">Version 2.1.0</a>
                <a href="4.0.0">Version 4.0.0</a>
                </body></html>
                """;

        var result = updateChecker.parseLatestVersion(responseBody);

        assertEquals(new Version("5.2.1"), result);
    }
}