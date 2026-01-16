package me.testaccount666.serversystem.moderation.mute

import me.testaccount666.serversystem.moderation.AbstractSqlModerationManager
import me.testaccount666.serversystem.moderation.MuteModeration
import java.sql.ResultSet
import java.util.*

abstract class AbstractSqlMuteManager(ownerUuid: UUID) : AbstractSqlModerationManager<MuteModeration>(ownerUuid) {

    override val moderationTypes = listOf("MUTE", "SHADOW_MUTE")

    override fun getModerationType(moderation: MuteModeration) = if (moderation.isShadowMute) "SHADOW_MUTE" else "MUTE"

    override fun mapResultSet(resultSet: ResultSet): MuteModeration {
        val type = resultSet.getString("Type")
        return MuteModeration.builder()
            .issueTime(resultSet.getLong("IssueTime"))
            .expireTime(resultSet.getLong("ExpireTime"))
            .reason(resultSet.getString("Reason"))
            .senderUuid(UUID.fromString(resultSet.getString("SenderUUID")))
            .targetUuid(UUID.fromString(resultSet.getString("TargetUUID")))
            .isShadowMute("SHADOW_MUTE" == type)
            .build()
    }

    val isPlayerMuted
        get() = hasActiveModeration()

    val isPlayerShadowMuted
        get() = activeModerations.any { it.isShadowMute }
}
