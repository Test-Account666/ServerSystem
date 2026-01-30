package me.testaccount666.serversystem.commands.executables.moderation

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.moderation.AbstractModeration
import me.testaccount666.serversystem.userdata.UserManager

object ModerationUtils {
    fun findSenderName(banModeration: AbstractModeration): String? {
        val sender = ServerSystem.instance.registry.getService<UserManager>().getUserOrNull(banModeration.senderUuid) ?: return null
        return sender.offlineUser.getNameOrNull()
    }
}
