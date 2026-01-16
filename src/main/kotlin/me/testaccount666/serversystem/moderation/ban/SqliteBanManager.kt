package me.testaccount666.serversystem.moderation.ban

import java.util.*

/**
 * Implementation of AbstractSqlBanManager for SQLite databases.
 */
class SqliteBanManager(ownerUuid: UUID) : AbstractSqlBanManager(ownerUuid)