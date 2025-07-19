package me.testaccount666.serversystem.moderation.mute;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager;
import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.moderation.MuteModeration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractSqlMuteManager extends AbstractModerationManager {
    protected final AbstractModerationDatabaseManager databaseManager;

    public AbstractSqlMuteManager(UUID ownerUuid) {
        super(ownerUuid);
        databaseManager = ServerSystem.Instance.getModerationDatabaseManager();
    }

    @Override
    public void addModeration(AbstractModeration moderation) {
        if (!(moderation instanceof MuteModeration muteModeration)) throw new IllegalArgumentException("Moderation must be a MuteModeration");

        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("INSERT INTO Moderation (TargetUUID, SenderUUID, IssueTime, ExpireTime, Reason, Type) VALUES (?, ?, ?, ?, ?, ?)")) {

            ServerSystem.getLog().info("Adding mute moderation for target '${moderation.targetUuid()}'");
            statement.setString(1, moderation.targetUuid().toString());
            statement.setString(2, moderation.senderUuid().toString());
            statement.setLong(3, moderation.issueTime());
            statement.setLong(4, moderation.expireTime());
            statement.setString(5, moderation.reason());
            statement.setString(6, muteModeration.isShadowMute()? "SHADOW_MUTE" : "MUTE");

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Error adding mute moderation for target '${moderation.targetUuid()}'", exception);
        }
    }

    @Override
    public void removeModeration(AbstractModeration moderation) {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("DELETE FROM Moderation WHERE TargetUUID = ? AND SenderUUID = ? AND IssueTime = ? AND (Type = 'MUTE' OR Type = 'SHADOW_MUTE')")) {

            statement.setString(1, moderation.targetUuid().toString());
            statement.setString(2, moderation.senderUuid().toString());
            statement.setLong(3, moderation.issueTime());

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Error removing mute moderation for target '${moderation.targetUuid()}'", exception);
        }
    }

    @Override
    public List<AbstractModeration> getModerations() {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM Moderation WHERE TargetUUID = ? AND (Type = 'MUTE' OR Type = 'SHADOW_MUTE')")) {

            statement.setString(1, ownerUuid.toString());

            try (var resultSet = statement.executeQuery()) {
                var moderations = new ArrayList<AbstractModeration>();

                while (resultSet.next()) {
                    var issueTime = resultSet.getLong("IssueTime");
                    var expireTime = resultSet.getLong("ExpireTime");
                    var reason = resultSet.getString("Reason");
                    var senderUuid = UUID.fromString(resultSet.getString("SenderUUID"));
                    var targetUuid = UUID.fromString(resultSet.getString("TargetUUID"));
                    var type = resultSet.getString("Type");
                    var isShadowMute = "SHADOW_MUTE".equals(type);

                    moderations.add(MuteModeration.builder()
                            .issueTime(issueTime)
                            .expireTime(expireTime)
                            .reason(reason)
                            .senderUuid(senderUuid)
                            .targetUuid(targetUuid)
                            .isShadowMute(isShadowMute)
                            .build());
                }

                return moderations;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error getting mute moderations for '${ownerUuid}'", exception);
        }
    }

    @Override
    public Optional<AbstractModeration> getActiveModeration() {
        return getActiveMuteModerations().stream().map(AbstractModeration.class::cast).findFirst();
    }


    /**
     * Gets all active mute moderations for the specified target UUID.
     *
     * @return A list of active mute moderations
     */
    public List<MuteModeration> getActiveMuteModerations() {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM Moderation WHERE TargetUUID = ? AND (Type = 'MUTE' OR Type = 'SHADOW_MUTE') AND (ExpireTime > ? OR ExpireTime = -1)")) {

            statement.setString(1, ownerUuid.toString());
            statement.setLong(2, System.currentTimeMillis());

            try (var resultSet = statement.executeQuery()) {
                var moderations = new ArrayList<MuteModeration>();

                while (resultSet.next()) {
                    var issueTime = resultSet.getLong("IssueTime");
                    var expireTime = resultSet.getLong("ExpireTime");
                    var reason = resultSet.getString("Reason");
                    var senderUuid = UUID.fromString(resultSet.getString("SenderUUID"));
                    var type = resultSet.getString("Type");
                    var isShadowMute = "SHADOW_MUTE".equals(type);

                    moderations.add(MuteModeration.builder()
                            .issueTime(issueTime)
                            .expireTime(expireTime)
                            .reason(reason)
                            .senderUuid(senderUuid)
                            .targetUuid(ownerUuid)
                            .isShadowMute(isShadowMute)
                            .build());
                }

                return moderations;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error getting active mute moderations for '${ownerUuid}'", exception);
        }
    }

    /**
     * Checks if a player is currently muted.
     *
     * @return True if the player is muted, false otherwise
     */
    public boolean isPlayerMuted() {
        return !getActiveMuteModerations().isEmpty();
    }

    /**
     * Checks if a player is currently shadow muted.
     *
     * @return True if the player is shadow muted, false otherwise
     */
    public boolean isPlayerShadowMuted() {
        return getActiveMuteModerations().stream().anyMatch(MuteModeration::isShadowMute);
    }
}
