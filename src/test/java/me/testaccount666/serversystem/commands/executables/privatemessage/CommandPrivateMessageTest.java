package me.testaccount666.serversystem.commands.executables.privatemessage;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class CommandPrivateMessageTest {

    @Mock
    private Command mockCommand;

    @Mock
    private Player mockPlayer;

    private CommandPrivateMessage commandPrivateMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandPrivateMessage = new CommandPrivateMessage();
    }

    @Test
    void commandPrivateMessage_shouldInstantiate() {
        assertNotNull(commandPrivateMessage);
    }

    @Test
    void getSyntaxPath_shouldReturnCorrectPath() {
        when(mockCommand.getName()).thenReturn("privatemessage");
        assertEquals("PrivateMessage", commandPrivateMessage.getSyntaxPath(mockCommand));

        when(mockCommand.getName()).thenReturn("reply");
        assertEquals("Reply", commandPrivateMessage.getSyntaxPath(mockCommand));

        when(mockCommand.getName()).thenReturn("messagetoggle");
        assertEquals("MessageToggle", commandPrivateMessage.getSyntaxPath(mockCommand));
    }
}