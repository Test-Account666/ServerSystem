package me.testaccount666.serversystem.managers.messages;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.PlaceholderManager;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageManagerTest {

    private User mockUser;
    private LanguageLoader mockLanguageLoader;
    private PlaceholderManager mockPlaceholderManager;
    private ConfigReader mockConfigReader;
    private Logger mockLogger;

    @BeforeEach
    void setUp() throws Exception {
        mockUser = mock(User.class);
        mockLanguageLoader = mock(LanguageLoader.class);
        mockPlaceholderManager = mock(PlaceholderManager.class);
        mockConfigReader = mock(ConfigReader.class);
        mockLogger = mock(Logger.class);

        var languageLoaderField = MessageManager.class.getDeclaredField("_LanguageLoader");
        languageLoaderField.setAccessible(true);
        languageLoaderField.set(null, null);

        var placeholderManagerField = MessageManager.class.getDeclaredField("_PlaceholderManager");
        placeholderManagerField.setAccessible(true);
        placeholderManagerField.set(null, null);
    }

    @Test
    void initialize_shouldSetupDependencies() throws FileNotFoundException {
        try (var placeholderManagerMock = Mockito.mockConstruction(PlaceholderManager.class);
             var languageLoaderMock = Mockito.mockConstruction(LanguageLoader.class)) {

            MessageManager.initialize();

            assertNotNull(MessageManager.getLanguageLoader());
            assertEquals(1, placeholderManagerMock.constructed().size());
            assertEquals(1, languageLoaderMock.constructed().size());
        }
    }

    @Test
    void formatMessage_shouldReturnEmptyStringForNullMessage() throws Exception {
        setupMockDependencies();

        var result = MessageManager.formatMessage(null, mockUser, "target", "label", false);

        assertEquals("", result);
    }

    @Test
    void formatMessage_shouldAddPrefixWhenRequested() throws Exception {
        setupMockDependencies();
        when(mockUser.getPlayerLanguage()).thenReturn("english");
        when(mockLanguageLoader.getMessageReader("english")).thenReturn(Optional.of(mockConfigReader));
        when(mockConfigReader.getString("Messages.General.Prefix", null)).thenReturn("[PREFIX] ");
        when(mockPlaceholderManager.applyPlaceholders(anyString(), any(), any(), any())).thenReturn("test message");

        try (var chatColorMock = Mockito.mockStatic(ChatColor.class)) {
            chatColorMock.when(() -> ChatColor.translateColor(anyString())).thenReturn("colored message");

            var result = MessageManager.formatMessage("test", mockUser, null, null, true);

            verify(mockConfigReader).getString("Messages.General.Prefix", null);
            chatColorMock.verify(() -> ChatColor.translateColor(anyString()));
            assertEquals("colored message", result);
        }
    }

    @Test
    void formatMessage_shouldNotAddPrefixWhenNotRequested() throws Exception {
        setupMockDependencies();
        when(mockPlaceholderManager.applyPlaceholders(anyString(), any(), any(), any())).thenReturn("test message");

        try (var chatColorMock = Mockito.mockStatic(ChatColor.class)) {
            chatColorMock.when(() -> ChatColor.translateColor(anyString())).thenReturn("colored message");

            var result = MessageManager.formatMessage("test", mockUser, null, null, false);

            verify(mockConfigReader, never()).getString("Messages.General.Prefix", null);
            assertEquals("colored message", result);
        }
    }

    @Test
    void applyPlaceholders_shouldCallPlaceholderManager() throws Exception {
        setupMockDependencies();
        when(mockPlaceholderManager.applyPlaceholders("test", mockUser, "target", "label")).thenReturn("processed");

        var result = MessageManager.applyPlaceholders("test", mockUser, "target", "label");

        assertEquals("processed", result);
        verify(mockPlaceholderManager).applyPlaceholders("test", mockUser, "target", "label");
    }

    @Test
    void applyPlaceholders_shouldHandleNullLabel() throws Exception {
        setupMockDependencies();
        when(mockPlaceholderManager.applyPlaceholders("test", mockUser, "target", "")).thenReturn("processed");

        var result = MessageManager.applyPlaceholders("test", mockUser, "target", null);

        assertEquals("processed", result);
        verify(mockPlaceholderManager).applyPlaceholders("test", mockUser, "target", "");
    }

    @Test
    void formatMessageAsComponent_shouldReturnEmptyComponentForNullMessage() throws Exception {
        setupMockDependencies();

        var result = MessageManager.formatMessageAsComponent(null, mockUser, "target", "label", false);

        assertEquals(Component.empty(), result);
    }

    @Test
    void formatMessageAsComponent_shouldReturnFormattedComponent() throws Exception {
        setupMockDependencies();
        when(mockPlaceholderManager.applyPlaceholders(anyString(), any(), any(), any())).thenReturn("processed message");
        var expectedComponent = Component.text("test component");

        try (var componentColorMock = Mockito.mockStatic(ComponentColor.class)) {
            componentColorMock.when(() -> ComponentColor.translateToComponent("processed message")).thenReturn(expectedComponent);

            var result = MessageManager.formatMessageAsComponent("test", mockUser, null, null, false);

            assertEquals(expectedComponent, result);
            componentColorMock.verify(() -> ComponentColor.translateToComponent("processed message"));
        }
    }

    @Test
    void getMessage_shouldThrowExceptionWhenNotInitialized() {
        var exception = assertThrows(IllegalStateException.class, () ->
                MessageManager.getMessage(mockUser, "test.path"));

        assertEquals("MessageManager was not yet initialized. Call initialize first.", exception.getMessage());
    }

    @Test
    void getMessage_shouldReturnMessageForUserLanguage() throws Exception {
        setupMockDependencies();
        when(mockUser.getPlayerLanguage()).thenReturn("german");
        when(mockLanguageLoader.getMessageReader("german")).thenReturn(Optional.of(mockConfigReader));
        when(mockConfigReader.getString("Messages.test.path", null)).thenReturn("German message");

        var result = MessageManager.getMessage(mockUser, "test.path");

        assertTrue(result.isPresent());
        assertEquals("German message", result.get());
    }

    @Test
    void getMessage_shouldFallbackToEnglishWhenLanguageNotFound() throws Exception {
        setupMockDependencies();
        when(mockUser.getPlayerLanguage()).thenReturn("nonexistent");
        when(mockLanguageLoader.getMessageReader("nonexistent")).thenReturn(Optional.empty());
        when(mockLanguageLoader.getMessageReader("english")).thenReturn(Optional.of(mockConfigReader));
        when(mockConfigReader.getString("Messages.test.path", null)).thenReturn("English message");

        var result = MessageManager.getMessage(mockUser, "test.path");

        assertTrue(result.isPresent());
        assertEquals("English message", result.get());
        verify(mockLanguageLoader).getMessageReader("nonexistent");
        verify(mockLanguageLoader).getMessageReader("english");
    }

    @Test
    void getMessage_shouldReturnEmptyWhenMessageNotFound() throws Exception {
        setupMockDependencies();
        when(mockUser.getPlayerLanguage()).thenReturn("english");
        when(mockLanguageLoader.getMessageReader("english")).thenReturn(Optional.of(mockConfigReader));
        when(mockConfigReader.getString("Messages.test.path", null)).thenReturn(null);

        try (var serverSystemMock = Mockito.mockStatic(ServerSystem.class)) {
            serverSystemMock.when(ServerSystem::getLog).thenReturn(mockLogger);

            var result = MessageManager.getMessage(mockUser, "test.path");

            assertFalse(result.isPresent());
            verify(mockLogger).warning(anyString());
        }
    }

    @Test
    void getMessage_shouldHandleNullUser() throws Exception {
        setupMockDependencies();
        when(mockLanguageLoader.getMessageReader("english")).thenReturn(Optional.of(mockConfigReader));
        when(mockConfigReader.getString("Messages.test.path", null)).thenReturn("Default message");

        var result = MessageManager.getMessage(null, "test.path");

        assertTrue(result.isPresent());
        assertEquals("Default message", result.get());
    }

    @Test
    void getMessage_shouldHandleEmptyEnglishFallback() throws Exception {
        setupMockDependencies();
        when(mockUser.getPlayerLanguage()).thenReturn("nonexistent");
        when(mockLanguageLoader.getMessageReader("nonexistent")).thenReturn(Optional.empty());
        when(mockLanguageLoader.getMessageReader("english")).thenReturn(Optional.empty());

        // This should throw NoSuchElementException due to .get() call on empty Optional
        assertThrows(NoSuchElementException.class, () -> MessageManager.getMessage(mockUser, "test.path"));

        verify(mockLanguageLoader).getMessageReader("nonexistent");
        verify(mockLanguageLoader).getMessageReader("english");
    }

    private void setupMockDependencies() throws Exception {
        var languageLoaderField = MessageManager.class.getDeclaredField("_LanguageLoader");
        languageLoaderField.setAccessible(true);
        languageLoaderField.set(null, mockLanguageLoader);

        var placeholderManagerField = MessageManager.class.getDeclaredField("_PlaceholderManager");
        placeholderManagerField.setAccessible(true);
        placeholderManagerField.set(null, mockPlaceholderManager);
    }
}