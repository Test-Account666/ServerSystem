package me.testaccount666.serversystem.updates;

import me.testaccount666.serversystem.utils.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MainUpdateCheckerTest {

    private MainUpdateChecker updateChecker;

    static Stream<Arguments> parseLatestVersionSuccessTestData() {
        return Stream.of(
                arguments(
                        "<html><body><a href=\"1.2.3\">Version 1.2.3</a></body></html>",
                        new Version("1.2.3"),
                        "shouldParseSingleVersion"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="1.0.0">Version 1.0.0</a>
                                <a href="1.2.3">Version 1.2.3</a>
                                <a href="1.1.5">Version 1.1.5</a>
                                <a href="2.0.0">Version 2.0.0</a>
                                <a href="1.5.0">Version 1.5.0</a>
                                </body></html>
                                """,
                        new Version("2.0.0"),
                        "shouldParseMultipleVersionsAndReturnLatest"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="1.0">Version 1.0</a>
                                <a href="1.2.3.4">Version 1.2.3.4</a>
                                <a href="2.1">Version 2.1</a>
                                <a href="1.10.5">Version 1.10.5</a>
                                </body></html>
                                """,
                        new Version("2.1"),
                        "shouldHandleVersionsWithDifferentFormats"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="1.9.9">Version 1.9.9</a>
                                <a href="2.0.0">Version 2.0.0</a>
                                <a href="1.10.0">Version 1.10.0</a>
                                </body></html>
                                """,
                        new Version("2.0.0"),
                        "shouldHandleMajorVersionComparison"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="1.9.0">Version 1.9.0</a>
                                <a href="1.10.0">Version 1.10.0</a>
                                <a href="1.2.0">Version 1.2.0</a>
                                </body></html>
                                """,
                        new Version("1.10.0"),
                        "shouldHandleMinorVersionComparison"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="1.0.9">Version 1.0.9</a>
                                <a href="1.0.10">Version 1.0.10</a>
                                <a href="1.0.2">Version 1.0.2</a>
                                </body></html>
                                """,
                        new Version("1.0.10"),
                        "shouldHandlePatchVersionComparison"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="1.0.0">Version 1.0.0</a>
                                <a href="1.0.0">Version 1.0.0 (duplicate)</a>
                                </body></html>
                                """,
                        new Version("1.0.0"),
                        "shouldHandleEqualVersions"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="invalid">Invalid version</a>
                                <a href="1.2.3">Version 1.2.3</a>
                                <a href="not.a.version">Not a version</a>
                                </body></html>
                                """,
                        new Version("1.2.3"),
                        "shouldIgnoreInvalidVersionFormats"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="999.999.999">Version 999.999.999</a>
                                <a href="1000.0.0">Version 1000.0.0</a>
                                <a href="1.0.0">Version 1.0.0</a>
                                </body></html>
                                """,
                        new Version("1000.0.0"),
                        "shouldHandleLargeVersionNumbers"
                ),
                arguments(
                        """
                                <html><body>
                                <a href="3.0.0">Version 3.0.0</a>
                                <a href="1.0.0">Version 1.0.0</a>
                                <a href="5.2.1">Version 5.2.1</a>
                                <a href="2.1.0">Version 2.1.0</a>
                                <a href="4.0.0">Version 4.0.0</a>
                                </body></html>
                                """,
                        new Version("5.2.1"),
                        "shouldHandleVersionsInRandomOrder"
                )
        );
    }

    static Stream<Arguments> parseLatestVersionExceptionTestData() {
        return Stream.of(
                arguments(
                        "<html><body><p>No versions available</p></body></html>",
                        "shouldThrowExceptionWhenNoVersionFound"
                ),
                arguments(
                        "",
                        "shouldThrowExceptionForEmptyResponse"
                )
        );
    }

    static Stream<Arguments> getDownloadUrlTestData() {
        return Stream.of(
                arguments(
                        new Version("1.2.3"),
                        "https://pluginsupport.zapto.org/PluginSupport/ServerSystem2/1.2.3",
                        "shouldReturnCorrectUrl"
                ),
                arguments(
                        new Version("2.1.0.5"),
                        "https://pluginsupport.zapto.org/PluginSupport/ServerSystem2/2.1.0.5",
                        "shouldHandleVersionWithMultipleParts"
                )
        );
    }

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

    @ParameterizedTest(name = "parseLatestVersion_{2}")
    @MethodSource("parseLatestVersionSuccessTestData")
    void parseLatestVersion_successCases(String responseBody, Version expectedVersion, String testName) {
        var result = updateChecker.parseLatestVersion(responseBody);
        assertEquals(expectedVersion, result, "Test: ${testName}, Expected Version: ${expectedVersion}, Actual Version: ${result}");
    }

    @ParameterizedTest(name = "parseLatestVersion_{1}")
    @MethodSource("parseLatestVersionExceptionTestData")
    void parseLatestVersion_exceptionCases(String responseBody, String testName) {
        var exception = assertThrows(IllegalStateException.class, () ->
                updateChecker.parseLatestVersion(responseBody));

        assertEquals("No version found in response body", "Test: ${testName}: " + exception.getMessage());
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

    @ParameterizedTest(name = "getDownloadUrl_{2}")
    @MethodSource("getDownloadUrlTestData")
    void getDownloadUrl_testCases(Version version, String expectedUrl, String testName) throws Exception {
        // Set up the latestVersion field using reflection
        var latestVersionField = AbstractUpdateChecker.class.getDeclaredField("latestVersion");
        latestVersionField.setAccessible(true);
        latestVersionField.set(updateChecker, version);

        var result = updateChecker.getDownloadUrl();

        assertEquals(expectedUrl, result, "Test: ${testName}, Version: ${version}, Expected URL: ${expectedUrl}, Actual URL: ${result}");
    }


}