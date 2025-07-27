package me.testaccount666.serversystem.commands.executables.teleport;

import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandTeleportTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private World mockWorld;

    @Mock
    private Server mockServer;

    @Mock
    private Location mockExecuteLocation;

    @Mock
    private Location mockPlayerLocation;

    private CommandTeleport commandTeleport;

    // Data provider for valid location parsing tests
    static Stream<Arguments> validLocationTestCases() {
        return Stream.of(
                // Basic coordinates - should use player's current yaw/pitch
                Arguments.of("shouldParseBasicCoordinates",
                        new String[]{"100", "64", "200"},
                        100.0, 64.0, 200.0, 90.0f, 45.0f, null, false),

                // Coordinates with yaw - should use provided yaw, player's pitch
                Arguments.of("shouldParseCoordinatesWithYaw",
                        new String[]{"100", "64", "200", "180"},
                        100.0, 64.0, 200.0, 180.0f, 45.0f, null, false),

                // Coordinates with yaw and pitch - should use provided values
                Arguments.of("shouldParseCoordinatesWithYawAndPitch",
                        new String[]{"100", "64", "200", "180", "-30"},
                        100.0, 64.0, 200.0, 180.0f, -30.0f, null, false),

                // Coordinates with yaw, pitch and world - should use provided values and world
                Arguments.of("shouldParseCoordinatesWithYawPitchAndWorld",
                        new String[]{"100", "64", "200", "180", "-30", "nether"},
                        100.0, 64.0, 200.0, 180.0f, -30.0f, "nether", true),

                // Invalid yaw should fall back to player's yaw
                Arguments.of("shouldHandleInvalidYawGracefully",
                        new String[]{"100", "64", "200", "invalid_yaw"},
                        100.0, 64.0, 200.0, 90.0f, 45.0f, null, false),

                // Relative yaw with tilde - should use player's yaw
                Arguments.of("shouldHandleRelativeYawWithTilde",
                        new String[]{"100", "64", "200", "~"},
                        100.0, 64.0, 200.0, 90.0f, 45.0f, null, false),

                // Relative yaw with @ - should use execute location's yaw
                Arguments.of("shouldHandleRelativeYawWithAt",
                        new String[]{"100", "64", "200", "@"},
                        100.0, 64.0, 200.0, 180.0f, 45.0f, null, false),

                // Relative yaw with tilde offset - should add offset to player's yaw
                Arguments.of("shouldHandleRelativeYawWithTildeOffset",
                        new String[]{"100", "64", "200", "~45"},
                        100.0, 64.0, 200.0, 135.0f, 45.0f, null, false),

                // Relative yaw with @ offset - should add offset to execute location's yaw
                Arguments.of("shouldHandleRelativeYawWithAtOffset",
                        new String[]{"100", "64", "200", "@-90"},
                        100.0, 64.0, 200.0, 90.0f, 45.0f, null, false),

                // Relative pitch with tilde - should use player's pitch
                Arguments.of("shouldHandleRelativePitchWithTilde",
                        new String[]{"100", "64", "200", "0", "~"},
                        100.0, 64.0, 200.0, 0.0f, 45.0f, null, false),

                // Relative pitch with @ - should use execute location's pitch
                Arguments.of("shouldHandleRelativePitchWithAt",
                        new String[]{"100", "64", "200", "0", "@"},
                        100.0, 64.0, 200.0, 0.0f, -15.0f, null, false),

                // Relative pitch with tilde offset - should add offset to player's pitch
                Arguments.of("shouldHandleRelativePitchWithTildeOffset",
                        new String[]{"100", "64", "200", "0", "~-30"},
                        100.0, 64.0, 200.0, 0.0f, 15.0f, null, false),

                // Relative pitch with @ offset - should add offset to execute location's pitch
                Arguments.of("shouldHandleRelativePitchWithAtOffset",
                        new String[]{"100", "64", "200", "0", "@30"},
                        100.0, 64.0, 200.0, 0.0f, 15.0f, null, false),

                // Both relative yaw and pitch
                Arguments.of("shouldHandleBothRelativeYawAndPitch",
                        new String[]{"100", "64", "200", "~90", "@15"},
                        100.0, 64.0, 200.0, 180.0f, 0.0f, null, false)
        );
    }

    // Data provider for relative coordinate tests
    static Stream<Arguments> relativeCoordinateTestCases() {
        return Stream.of(
                Arguments.of("shouldHandleRelativeCoordinates",
                        new String[]{"~", "~5", "~-10"},
                        10.0, 75.0, 0.0) // Player's current position (10, 70, 10) with offsets (0, +5, -10)
        );
    }

    // Data provider for error cases
    static Stream<Arguments> errorTestCases() {
        return Stream.of(
                Arguments.of("shouldReturnEmptyForInsufficientArguments",
                        new String[]{"100", "64"}), // Missing Z coordinate

                Arguments.of("shouldReturnEmptyForInvalidCoordinates",
                        new String[]{"invalid", "64", "200"}) // Invalid X coordinate
        );
    }

    @SneakyThrows
    @BeforeEach
    void setUp() {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            commandTeleport = new CommandTeleport();

            when(mockPlayer.getWorld()).thenReturn(mockWorld);
            when(mockPlayer.getServer()).thenReturn(mockServer);
            when(mockPlayer.getLocation()).thenReturn(mockPlayerLocation);
            when(mockPlayerLocation.getYaw()).thenReturn(90.0f);
            when(mockPlayerLocation.getPitch()).thenReturn(45.0f);
            when(mockExecuteLocation.getX()).thenReturn(0.0);
            when(mockExecuteLocation.getY()).thenReturn(64.0);
            when(mockExecuteLocation.getZ()).thenReturn(0.0);
            when(mockExecuteLocation.getYaw()).thenReturn(180.0f);
            when(mockExecuteLocation.getPitch()).thenReturn(-15.0f);
            when(mockPlayerLocation.getX()).thenReturn(10.0);
            when(mockPlayerLocation.getY()).thenReturn(70.0);
            when(mockPlayerLocation.getZ()).thenReturn(10.0);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validLocationTestCases")
    void extractLocationWithRotation_validCases(String testName, String[] arguments,
                                                double expectedX, double expectedY, double expectedZ,
                                                float expectedYaw, float expectedPitch,
                                                String worldName, boolean shouldMockWorld) {
        // Setup world mock if needed
        World targetWorld = null;
        if (shouldMockWorld && worldName != null) {
            targetWorld = mock(World.class);
            when(mockServer.getWorld(worldName)).thenReturn(targetWorld);
        }

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present for " + testName);
        var location = result.get();
        assertEquals(expectedX, location.getX(), 0.001, "X coordinate mismatch for " + testName);
        assertEquals(expectedY, location.getY(), 0.001, "Y coordinate mismatch for " + testName);
        assertEquals(expectedZ, location.getZ(), 0.001, "Z coordinate mismatch for " + testName);
        assertEquals(expectedYaw, location.getYaw(), 0.001, "Yaw mismatch for " + testName);
        assertEquals(expectedPitch, location.getPitch(), 0.001, "Pitch mismatch for " + testName);

        if (targetWorld != null) assertEquals(targetWorld, location.getWorld(), "World mismatch for " + testName);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("relativeCoordinateTestCases")
    void extractLocationWithRotation_relativeCoordinates(String testName, String[] arguments,
                                                         double expectedX, double expectedY, double expectedZ) {
        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present for " + testName);
        var location = result.get();
        assertEquals(expectedX, location.getX(), 0.001, "X coordinate mismatch for " + testName);
        assertEquals(expectedY, location.getY(), 0.001, "Y coordinate mismatch for " + testName);
        assertEquals(expectedZ, location.getZ(), 0.001, "Z coordinate mismatch for " + testName);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("errorTestCases")
    void extractLocationWithRotation_errorCases(String testName, String[] arguments) {
        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertFalse(result.isPresent(), "Location should not be present for " + testName);
    }
}