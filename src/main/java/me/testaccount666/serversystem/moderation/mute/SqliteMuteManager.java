package me.testaccount666.serversystem.moderation.mute;

import java.util.UUID;

/**
 * Implementation of AbstractSqlMuteManager for SQLite databases.
 */
public class SqliteMuteManager extends AbstractSqlMuteManager {
    public SqliteMuteManager(UUID ownerUuid) {
        super(ownerUuid);
    }
}