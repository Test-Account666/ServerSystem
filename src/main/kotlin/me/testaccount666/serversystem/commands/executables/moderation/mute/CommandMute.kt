package me.testaccount666.serversystem.commands.executables.moderation.mute

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.moderation.AbstractModerationCommand
import me.testaccount666.serversystem.commands.executables.moderation.TabCompleterModeration
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.Locale.getDefault

@ServerSystemCommand("mute", ["unmute", "shadowmute"], TabCompleterModeration::class)
class CommandMute : AbstractModerationCommand<MuteModeration>() {
    override fun createModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String): MuteModeration {
        val shadowMute = command.name.equals("shadowmute", true)

        return MuteModeration.builder()
            .isShadowMute(shadowMute).expireTime(expireTime)
            .reason(reason).senderUuid(commandSender.uuid)
            .targetUuid(targetUser.uuid).build()
    }

    override fun checkBasePermission(commandSender: User, command: Command): Boolean {
        val permissionPath = when (command.name.lowercase(getDefault())) {
            "mute" -> "Moderation.Mute.Use"
            "shadowmute" -> "Moderation.Mute.Shadow"
            "unmute" -> "Moderation.Mute.Remove"
            else -> null
        }
        return checkBasePermission(commandSender, permissionPath!!)
    }

    override fun getModerationManager(targetUser: OfflineUser) = targetUser.muteManager

    override fun type(command: Command?): String {
        if (command == null) return "Mute"

        if (command.name.equals("shadowmute", true)) return "ShadowMute"
        return "Mute"
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Mute"

        return when (val commandName = command.name.lowercase(getDefault())) {
            "mute", "shadowmute" -> "Mute"
            "unmute" -> "Unmute"
            else -> throw IllegalArgumentException("(CommandMute) Unknown command name: ${commandName}")
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionPath = when (command.name.lowercase(getDefault())) {
            "mute" -> "Moderation.Mute.Use"
            "shadowmute" -> "Moderation.Mute.Shadow"
            "unmute" -> "Moderation.Mute.Remove"
            else -> null
        }
        return PermissionManager.hasCommandPermission(player, permissionPath!!, false)
    }

    override fun handlePostRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser) {
        // Nothing to do
    }

    override fun handlePostModeration(command: Command, commandSender: User, targetUser: OfflineUser, moderation: MuteModeration) {
        // Nothing to do
    }
}
