package me.testaccount666.serversystem.moderation.ban;

import java.sql.Connection;
import java.util.UUID;

/**
 * Implementation of AbstractSqlBanManager for SQLite databases.
 */
public class SqliteBanManager extends AbstractSqlBanManager {
    public SqliteBanManager(UUID ownerUuid, Connection connection) {
        super(ownerUuid, connection);
    }
}