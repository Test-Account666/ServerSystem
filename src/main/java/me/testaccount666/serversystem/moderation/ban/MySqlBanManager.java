package me.testaccount666.serversystem.moderation.ban;

import java.sql.Connection;
import java.util.UUID;

/**
 * Implementation of AbstractSqlBanManager for MySQL databases.
 */
public class MySqlBanManager extends AbstractSqlBanManager {
    public MySqlBanManager(UUID ownerUuid, Connection connection) {
        super(ownerUuid, connection);
    }
}