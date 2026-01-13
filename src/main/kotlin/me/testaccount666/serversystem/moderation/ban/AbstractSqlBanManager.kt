package me.testaccount666.serversystem.moderation.ban

import me.testaccount666.serversystem.moderation.AbstractSqlModerationManager
import me.testaccount666.serversystem.moderation.BanModeration
import java.sql.ResultSet
import java.util.*

abstract class AbstractSqlBanManager(ownerUuid: UUID) : AbstractSqlModerationManager<BanModeration>(ownerUuid) {

    override val moderationTypes = listOf("BAN")

    override fun mapResultSet(resultSet: ResultSet): BanModeration {
        return BanModeration.builder()
            .issueTime(resultSet.getLong("IssueTime"))
            .expireTime(resultSet.getLong("ExpireTime"))
            .reason(resultSet.getString("Reason"))
            .senderUuid(UUID.fromString(resultSet.getString("SenderUUID")))
            .targetUuid(UUID.fromString(resultSet.getString("TargetUUID")))
            .build()
    }

    val isPlayerBanned
        get() = hasActiveModeration()
}
