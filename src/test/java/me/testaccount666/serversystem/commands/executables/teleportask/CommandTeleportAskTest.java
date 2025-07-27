package me.testaccount666.serversystem.commands.executables.teleportask;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandTeleportAskTest {

    @Mock
    private Command mockCommand;

    @Mock
    private Player mockPlayer;

    private CommandTeleportAsk commandTeleportAsk;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandTeleportAsk = new CommandTeleportAsk();
    }

    @Test
    void commandTeleportAsk_shouldInstantiate() {
        assertNotNull(commandTeleportAsk);
    }

    @Test
    void getSyntaxPath_shouldReturnCorrectPath() {
        when(mockCommand.getName()).thenReturn("teleportask");
        assertEquals("TeleportAsk", commandTeleportAsk.getSyntaxPath(mockCommand));

        when(mockCommand.getName()).thenReturn("teleporthereask");
        assertEquals("TeleportAsk", commandTeleportAsk.getSyntaxPath(mockCommand));

        when(mockCommand.getName()).thenReturn("teleportaccept");
        assertEquals("TeleportAsk", commandTeleportAsk.getSyntaxPath(mockCommand));

        when(mockCommand.getName()).thenReturn("teleportdeny");
        assertEquals("TeleportAsk", commandTeleportAsk.getSyntaxPath(mockCommand));

        when(mockCommand.getName()).thenReturn("teleporttoggle");
        assertEquals("TeleportToggle", commandTeleportAsk.getSyntaxPath(mockCommand));
    }

    @Test
    void teleportRequest_shouldCreateCorrectly() {
        // Test TeleportRequest creation and basic functionality
        var mockSender = mock(me.testaccount666.serversystem.userdata.User.class);
        var mockTarget = mock(me.testaccount666.serversystem.userdata.User.class);
        var timeout = System.currentTimeMillis() + 120000; // 2 minutes
        var request = new TeleportRequest(mockSender, mockTarget, timeout, false);

        assertEquals(mockSender, request.getSender());
        assertEquals(mockTarget, request.getReceiver());
        assertFalse(request.isTeleportHere());
        assertFalse(request.isCancelled());
        assertFalse(request.isExpired());

        request.setCancelled(true);
        assertTrue(request.isCancelled());

        request.setTimerId(123);
        assertEquals(123, request.getTimerId());
    }

    @Test
    void teleportRequest_shouldDetectExpiration() {
        var mockSender = mock(me.testaccount666.serversystem.userdata.User.class);
        var mockTarget = mock(me.testaccount666.serversystem.userdata.User.class);
        var pastTimeout = System.currentTimeMillis() - 1000; // 1 second ago
        var request = new TeleportRequest(mockSender, mockTarget, pastTimeout, false);

        assertTrue(request.isExpired());
    }
}