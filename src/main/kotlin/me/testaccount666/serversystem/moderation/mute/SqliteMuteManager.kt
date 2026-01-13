package me.testaccount666.serversystem.moderation.mute

import java.util.*

/**
 * Implementation of AbstractSqlMuteManager for SQLite databases.
 */
class SqliteMuteManager(ownerUuid: UUID) : AbstractSqlMuteManager(ownerUuid)