package me.testaccount666.serversystem.moderation.ban;

import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.moderation.BanModeration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractSqlBanManager extends AbstractModerationManager {
    protected final Connection connection;

    public AbstractSqlBanManager(UUID ownerUuid, Connection connection) {
        super(ownerUuid);
        this.connection = connection;
    }

    @Override
    public void addModeration(AbstractModeration moderation) {
        try {
            var query = "INSERT INTO Moderation (TargetUUID, SenderUUID, IssueTime, ExpireTime, Reason, Type) VALUES (?, ?, ?, ?, ?, ?)";
            var statement = connection.prepareStatement(query);
            statement.setString(1, moderation.targetUuid().toString());
            statement.setString(2, moderation.senderUuid().toString());
            statement.setLong(3, moderation.issueTime());
            statement.setLong(4, moderation.expireTime());
            statement.setString(5, moderation.reason());
            statement.setString(6, "BAN");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            throw new RuntimeException("Error adding ban moderation for target '${moderation.targetUuid()}'", exception);
        }
    }

    @Override
    public void removeModeration(AbstractModeration moderation) {
        try {
            var query = "DELETE FROM Moderation WHERE TargetUUID = ? AND SenderUUID = ? AND IssueTime = ? AND Type = 'BAN'";
            var statement = connection.prepareStatement(query);
            statement.setString(1, moderation.targetUuid().toString());
            statement.setString(2, moderation.senderUuid().toString());
            statement.setLong(3, moderation.issueTime());

            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            throw new RuntimeException("Error removing ban moderation for target '${moderation.targetUuid()}'", exception);
        }
    }

    @Override
    public List<AbstractModeration> getModerations() {
        try {
            var query = "SELECT * FROM Moderation WHERE TargetUUID = ? AND Type = 'BAN'";
            var statement = connection.prepareStatement(query);
            statement.setString(1, ownerUuid.toString());
            var resultSet = statement.executeQuery();

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

            resultSet.close();
            statement.close();

            return moderations;
        } catch (SQLException exception) {
            throw new RuntimeException("Error getting ban moderations for '${ownerUuid}'", exception);
        }
    }

    @Override
    public Optional<AbstractModeration> getActiveModeration() {
        return super.getActiveModeration();
    }
}
