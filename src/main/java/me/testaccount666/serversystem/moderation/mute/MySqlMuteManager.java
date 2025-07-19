package me.testaccount666.serversystem.moderation.mute;

import java.util.UUID;

/**
 * Implementation of AbstractSqlMuteManager for MySQL databases.
 */
public class MySqlMuteManager extends AbstractSqlMuteManager {
    public MySqlMuteManager(UUID ownerUuid) {
        super(ownerUuid);
    }
}