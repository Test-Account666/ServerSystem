package me.testaccount666.serversystem.moderation.ban;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager;
import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.moderation.BanModeration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractSqlBanManager extends AbstractModerationManager {
    protected final AbstractModerationDatabaseManager databaseManager;

    public AbstractSqlBanManager(UUID ownerUuid) {
        super(ownerUuid);
        databaseManager = ServerSystem.Instance.getModerationDatabaseManager();
    }

    @Override
    public void addModeration(AbstractModeration moderation) {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("INSERT INTO Moderation (TargetUUID, SenderUUID, IssueTime, ExpireTime, Reason, Type) VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setString(1, moderation.targetUuid().toString());
            statement.setString(2, moderation.senderUuid().toString());
            statement.setLong(3, moderation.issueTime());
            statement.setLong(4, moderation.expireTime());
            statement.setString(5, moderation.reason());
            statement.setString(6, "BAN");

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Error adding ban moderation for target '${moderation.targetUuid()}'", exception);
        }
    }

    @Override
    public void removeModeration(AbstractModeration moderation) {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("DELETE FROM Moderation WHERE TargetUUID = ? AND SenderUUID = ? AND IssueTime = ? AND Type = 'BAN'")) {

            statement.setString(1, moderation.targetUuid().toString());
            statement.setString(2, moderation.senderUuid().toString());
            statement.setLong(3, moderation.issueTime());

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Error removing ban moderation for target '${moderation.targetUuid()}'", exception);
        }
    }

    @Override
    public List<AbstractModeration> getModerations() {
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement("SELECT * FROM Moderation WHERE TargetUUID = ? AND Type = 'BAN'")) {

            statement.setString(1, ownerUuid.toString());

            try (var resultSet = statement.executeQuery()) {
                var moderations = new ArrayList<AbstractModeration>();

                while (resultSet.next()) {
                    var issueTime = resultSet.getLong("IssueTime");
                    var expireTime = resultSet.getLong("ExpireTime");
                    var reason = resultSet.getString("Reason");
                    var senderUuid = UUID.fromString(resultSet.getString("SenderUUID"));
                    var targetUuid = UUID.fromString(resultSet.getString("TargetUUID"));

                    moderations.add(BanModeration.builder()
                            .issueTime(issueTime)
                            .expireTime(expireTime)
                            .reason(reason)
                            .senderUuid(senderUuid)
                            .targetUuid(targetUuid)
                            .build());
                }

                return moderations;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error getting ban moderations for '${ownerUuid}'", exception);
        }
    }

    @Override
    public Optional<AbstractModeration> getActiveModeration() {
        return super.getActiveModeration();
    }
}
