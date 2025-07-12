package me.testaccount666.serversystem.moderation.mute;

import java.sql.Connection;
import java.util.UUID;

/**
 * Implementation of AbstractSqlMuteManager for MySQL databases.
 */
public class MySqlMuteManager extends AbstractSqlMuteManager {
    public MySqlMuteManager(UUID ownerUuid, Connection connection) {
        super(ownerUuid, connection);
    }
}