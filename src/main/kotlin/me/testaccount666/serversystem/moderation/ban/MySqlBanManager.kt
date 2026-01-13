package me.testaccount666.serversystem.moderation.ban

import java.util.*

/**
 * Implementation of AbstractSqlBanManager for MySQL databases.
 */
class MySqlBanManager(ownerUuid: UUID) : AbstractSqlBanManager(ownerUuid)