package me.testaccount666.serversystem.commands.executables.teleport;

import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @Test
    void extractLocationWithRotation_shouldParseBasicCoordinates() {
        var arguments = new String[]{"100", "64", "200"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(100.0, location.getX(), 0.001);
        assertEquals(64.0, location.getY(), 0.001);
        assertEquals(200.0, location.getZ(), 0.001);
        assertEquals(90.0f, location.getYaw(), 0.001); // Should use player's current yaw
        assertEquals(45.0f, location.getPitch(), 0.001); // Should use player's current pitch
    }

    @Test
    void extractLocationWithRotation_shouldParseCoordinatesWithYaw() {
        var arguments = new String[]{"100", "64", "200", "180"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(100.0, location.getX(), 0.001);
        assertEquals(64.0, location.getY(), 0.001);
        assertEquals(200.0, location.getZ(), 0.001);
        assertEquals(180.0f, location.getYaw(), 0.001); // Should use provided yaw
        assertEquals(45.0f, location.getPitch(), 0.001); // Should use player's current pitch
    }

    @Test
    void extractLocationWithRotation_shouldParseCoordinatesWithYawAndPitch() {
        var arguments = new String[]{"100", "64", "200", "180", "-30"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(100.0, location.getX(), 0.001);
        assertEquals(64.0, location.getY(), 0.001);
        assertEquals(200.0, location.getZ(), 0.001);
        assertEquals(180.0f, location.getYaw(), 0.001); // Should use provided yaw
        assertEquals(-30.0f, location.getPitch(), 0.001); // Should use provided pitch
    }

    @Test
    void extractLocationWithRotation_shouldParseCoordinatesWithYawPitchAndWorld() {
        var targetWorld = mock(World.class);
        when(mockServer.getWorld("nether")).thenReturn(targetWorld);

        var arguments = new String[]{"100", "64", "200", "180", "-30", "nether"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(100.0, location.getX(), 0.001);
        assertEquals(64.0, location.getY(), 0.001);
        assertEquals(200.0, location.getZ(), 0.001);
        assertEquals(180.0f, location.getYaw(), 0.001);
        assertEquals(-30.0f, location.getPitch(), 0.001);
        assertEquals(targetWorld, location.getWorld());
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativeCoordinates() {
        var arguments = new String[]{"~", "~5", "~-10"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(10.0, location.getX(), 0.001); // Player's current X
        assertEquals(75.0, location.getY(), 0.001); // Player's current Y + 5
        assertEquals(0.0, location.getZ(), 0.001); // Player's current Z - 10
    }

    @Test
    void extractLocationWithRotation_shouldReturnEmptyForInsufficientArguments() {
        var arguments = new String[]{"100", "64"}; // Missing Z coordinate

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertFalse(result.isPresent(), "Location should not be present with insufficient arguments");
    }

    @Test
    void extractLocationWithRotation_shouldReturnEmptyForInvalidCoordinates() {
        var arguments = new String[]{"invalid", "64", "200"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertFalse(result.isPresent(), "Location should not be present with invalid coordinates");
    }

    @Test
    void extractLocationWithRotation_shouldHandleInvalidYawGracefully() {
        var arguments = new String[]{"100", "64", "200", "invalid_yaw"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present even with invalid yaw");
        var location = result.get();
        assertEquals(90.0f, location.getYaw(), 0.001); // Should fall back to player's current yaw
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativeYawWithTilde() {
        var arguments = new String[]{"100", "64", "200", "~"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(90.0f, location.getYaw(), 0.001); // Should use target's (player's) current yaw
        assertEquals(45.0f, location.getPitch(), 0.001); // Should use target's current pitch
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativeYawWithAt() {
        var arguments = new String[]{"100", "64", "200", "@"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(180.0f, location.getYaw(), 0.001); // Should use sender's (execute location) yaw
        assertEquals(45.0f, location.getPitch(), 0.001); // Should use target's current pitch
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativeYawWithTildeOffset() {
        var arguments = new String[]{"100", "64", "200", "~45"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(135.0f, location.getYaw(), 0.001); // Target's yaw (90) + offset (45)
        assertEquals(45.0f, location.getPitch(), 0.001); // Should use target's current pitch
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativeYawWithAtOffset() {
        var arguments = new String[]{"100", "64", "200", "@-90"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(90.0f, location.getYaw(), 0.001); // Sender's yaw (180) + offset (-90)
        assertEquals(45.0f, location.getPitch(), 0.001); // Should use target's current pitch
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativePitchWithTilde() {
        var arguments = new String[]{"100", "64", "200", "0", "~"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(0.0f, location.getYaw(), 0.001); // Should use provided absolute yaw
        assertEquals(45.0f, location.getPitch(), 0.001); // Should use target's current pitch
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativePitchWithAt() {
        var arguments = new String[]{"100", "64", "200", "0", "@"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(0.0f, location.getYaw(), 0.001); // Should use provided absolute yaw
        assertEquals(-15.0f, location.getPitch(), 0.001); // Should use sender's pitch
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativePitchWithTildeOffset() {
        var arguments = new String[]{"100", "64", "200", "0", "~-30"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(0.0f, location.getYaw(), 0.001); // Should use provided absolute yaw
        assertEquals(15.0f, location.getPitch(), 0.001); // Target's pitch (45) + offset (-30)
    }

    @Test
    void extractLocationWithRotation_shouldHandleRelativePitchWithAtOffset() {
        var arguments = new String[]{"100", "64", "200", "0", "@30"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(0.0f, location.getYaw(), 0.001); // Should use provided absolute yaw
        assertEquals(15.0f, location.getPitch(), 0.001); // Sender's pitch (-15) + offset (30)
    }

    @Test
    void extractLocationWithRotation_shouldHandleBothRelativeYawAndPitch() {
        var arguments = new String[]{"100", "64", "200", "~90", "@15"};

        var result = commandTeleport.extractLocationWithRotation(mockExecuteLocation, mockPlayer, arguments, 0);

        assertTrue(result.isPresent(), "Location should be present");
        var location = result.get();
        assertEquals(180.0f, location.getYaw(), 0.001); // Target's yaw (90) + offset (90)
        assertEquals(0.0f, location.getPitch(), 0.001); // Sender's pitch (-15) + offset (15)
    }
}