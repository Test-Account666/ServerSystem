package me.testaccount666.serversystem.moderation.mute

import java.util.*

/**
 * Implementation of AbstractSqlMuteManager for MySQL databases.
 */
class MySqlMuteManager(ownerUuid: UUID) : AbstractSqlMuteManager(ownerUuid)