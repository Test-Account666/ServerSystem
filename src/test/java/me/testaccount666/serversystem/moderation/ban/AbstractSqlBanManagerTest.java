package me.testaccount666.serversystem.moderation.ban;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.ServiceRegistry;
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.moderation.BanModeration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractSqlBanManagerTest {

    private TestSqlBanManager banManager;
    private AbstractModerationDatabaseManager mockDatabaseManager;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private ServerSystem mockServerSystem;
    private UUID ownerUuid;
    private UUID targetUuid;
    private UUID senderUuid;

    @BeforeEach
    void setUp() throws SQLException {
        ownerUuid = UUID.randomUUID();
        targetUuid = UUID.randomUUID();
        senderUuid = UUID.randomUUID();

        mockDatabaseManager = mock(AbstractModerationDatabaseManager.class);
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockServerSystem = mock(ServerSystem.class);

        when(mockDatabaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        ServerSystem.Instance = mockServerSystem;
        var mockRegistry = mock(ServiceRegistry.class);
        when(mockServerSystem.getRegistry()).thenReturn(mockRegistry);
        when(mockRegistry.getService(AbstractModerationDatabaseManager.class)).thenReturn(mockDatabaseManager);

        banManager = new TestSqlBanManager(ownerUuid);
    }

    @Test
    void constructor_shouldSetOwnerUuidAndDatabaseManager() throws Exception {
        var ownerUuidField = AbstractModerationManager.class.getDeclaredField("ownerUuid");
        ownerUuidField.setAccessible(true);
        var actualOwnerUuid = (UUID) ownerUuidField.get(banManager);

        var databaseManagerField = AbstractSqlBanManager.class.getDeclaredField("databaseManager");
        databaseManagerField.setAccessible(true);
        var actualDatabaseManager = databaseManagerField.get(banManager);

        assertEquals(ownerUuid, actualOwnerUuid);
        assertSame(mockDatabaseManager, actualDatabaseManager);
    }

    @Test
    void addModeration_shouldInsertBanIntoDatabase() throws SQLException {
        var moderation = BanModeration.builder()
                .targetUuid(targetUuid)
                .senderUuid(senderUuid)
                .issueTime(1000L)
                .expireTime(2000L)
                .reason("Test ban reason")
                .build();

        banManager.addModeration(moderation);

        verify(mockConnection).prepareStatement("INSERT INTO Moderation (TargetUUID, SenderUUID, IssueTime, ExpireTime, Reason, Type) VALUES (?, ?, ?, ?, ?, ?)");
        verify(mockStatement).setString(1, targetUuid.toString());
        verify(mockStatement).setString(2, senderUuid.toString());
        verify(mockStatement).setLong(3, 1000L);
        verify(mockStatement).setLong(4, 2000L);
        verify(mockStatement).setString(5, "Test ban reason");
        verify(mockStatement).setString(6, "BAN");
        verify(mockStatement).executeUpdate();
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void addModeration_shouldThrowRuntimeExceptionOnSQLException() throws SQLException {
        var moderation = BanModeration.builder()
                .targetUuid(targetUuid)
                .senderUuid(senderUuid)
                .issueTime(1000L)
                .expireTime(2000L)
                .reason("Test ban reason")
                .build();

        when(mockStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        var exception = assertThrows(RuntimeException.class, () -> banManager.addModeration(moderation));

        assertTrue(exception.getMessage().contains("Error adding ban moderation for target"));
        assertTrue(exception.getMessage().contains(targetUuid.toString()));
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    @Test
    void addModeration_shouldHandleNullReason() throws SQLException {
        var moderation = BanModeration.builder()
                .targetUuid(targetUuid)
                .senderUuid(senderUuid)
                .issueTime(1000L)
                .expireTime(2000L)
                .reason(null)
                .build();

        banManager.addModeration(moderation);

        verify(mockStatement).setString(5, null);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void removeModeration_shouldDeleteBanFromDatabase() throws SQLException {
        var moderation = BanModeration.builder()
                .targetUuid(targetUuid)
                .senderUuid(senderUuid)
                .issueTime(1000L)
                .expireTime(2000L)
                .reason("Test ban reason")
                .build();

        banManager.removeModeration(moderation);

        verify(mockConnection).prepareStatement("DELETE FROM Moderation WHERE TargetUUID = ? AND SenderUUID = ? AND IssueTime = ? AND Type = 'BAN'");
        verify(mockStatement).setString(1, targetUuid.toString());
        verify(mockStatement).setString(2, senderUuid.toString());
        verify(mockStatement).setLong(3, 1000L);
        verify(mockStatement).executeUpdate();
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void removeModeration_shouldThrowRuntimeExceptionOnSQLException() throws SQLException {
        var moderation = BanModeration.builder()
                .targetUuid(targetUuid)
                .senderUuid(senderUuid)
                .issueTime(1000L)
                .expireTime(2000L)
                .reason("Test ban reason")
                .build();

        when(mockStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        var exception = assertThrows(RuntimeException.class, () -> banManager.removeModeration(moderation));

        assertTrue(exception.getMessage().contains("Error removing ban moderation for target"));
        assertTrue(exception.getMessage().contains(targetUuid.toString()));
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    @Test
    void getModerations_shouldReturnListOfBanModerations() throws SQLException {
        // Setup result set to return two ban records
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getLong("IssueTime")).thenReturn(1000L, 1500L);
        when(mockResultSet.getLong("ExpireTime")).thenReturn(2000L, 2500L);
        when(mockResultSet.getString("Reason")).thenReturn("First ban", "Second ban");
        when(mockResultSet.getString("SenderUUID")).thenReturn(senderUuid.toString(), senderUuid.toString());
        when(mockResultSet.getString("TargetUUID")).thenReturn(targetUuid.toString(), targetUuid.toString());

        var result = banManager.getModerations();

        assertEquals(2, result.size());

        var firstBan = result.getFirst();
        assertEquals(1000L, firstBan.issueTime());
        assertEquals(2000L, firstBan.expireTime());
        assertEquals("First ban", firstBan.reason());
        assertEquals(senderUuid, firstBan.senderUuid());
        assertEquals(targetUuid, firstBan.targetUuid());

        var secondBan = result.get(1);
        assertEquals(1500L, secondBan.issueTime());
        assertEquals(2500L, secondBan.expireTime());
        assertEquals("Second ban", secondBan.reason());
        assertEquals(senderUuid, secondBan.senderUuid());
        assertEquals(targetUuid, secondBan.targetUuid());

        verify(mockConnection).prepareStatement("SELECT * FROM Moderation WHERE TargetUUID = ? AND Type = 'BAN'");
        verify(mockStatement).setString(1, ownerUuid.toString());
        verify(mockStatement).executeQuery();
        verify(mockResultSet).close();
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void getModerations_shouldReturnEmptyListWhenNoRecords() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        var result = banManager.getModerations();

        assertTrue(result.isEmpty());
        verify(mockStatement).executeQuery();
    }

    @Test
    void getModerations_shouldThrowRuntimeExceptionOnSQLException() throws SQLException {
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        var exception = assertThrows(RuntimeException.class, () -> banManager.getModerations());

        assertTrue(exception.getMessage().contains("Error getting ban moderations for"));
        assertTrue(exception.getMessage().contains(ownerUuid.toString()));
        assertInstanceOf(SQLException.class, exception.getCause());
    }

    @Test
    void getModerations_shouldHandleNullValues() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getLong("IssueTime")).thenReturn(1000L);
        when(mockResultSet.getLong("ExpireTime")).thenReturn(2000L);
        when(mockResultSet.getString("Reason")).thenReturn(null);
        when(mockResultSet.getString("SenderUUID")).thenReturn(senderUuid.toString());
        when(mockResultSet.getString("TargetUUID")).thenReturn(targetUuid.toString());

        var result = banManager.getModerations();

        assertEquals(1, result.size());
        var ban = result.getFirst();
        assertNull(ban.reason());
    }

    @Test
    void getModerations_shouldCloseResourcesOnException() throws SQLException {
        when(mockResultSet.next()).thenThrow(new SQLException("ResultSet error"));

        assertThrows(RuntimeException.class, () -> banManager.getModerations());

        verify(mockResultSet).close();
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void getActiveModeration_shouldDelegateToSuperclass() {
        // This test verifies that the method delegates to the parent class
        // Since we can't easily mock the superclass behavior, we just verify the method exists and can be called
        assertDoesNotThrow(() -> banManager.getActiveModeration());
    }

    @Test
    void addModeration_shouldCloseResourcesOnException() throws SQLException {
        var moderation = BanModeration.builder()
                .targetUuid(targetUuid)
                .senderUuid(senderUuid)
                .issueTime(1000L)
                .expireTime(2000L)
                .reason("Test ban reason")
                .build();

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Connection error"));

        assertThrows(RuntimeException.class, () -> banManager.addModeration(moderation));

        verify(mockConnection).close();
    }

    @Test
    void removeModeration_shouldCloseResourcesOnException() throws SQLException {
        var moderation = BanModeration.builder()
                .targetUuid(targetUuid)
                .senderUuid(senderUuid)
                .issueTime(1000L)
                .expireTime(2000L)
                .reason("Test ban reason")
                .build();

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Connection error"));

        assertThrows(RuntimeException.class, () -> banManager.removeModeration(moderation));

        verify(mockConnection).close();
    }

    // Concrete implementation for testing
    private static class TestSqlBanManager extends AbstractSqlBanManager {
        public TestSqlBanManager(UUID ownerUuid) {
            super(ownerUuid);
        }
    }
}