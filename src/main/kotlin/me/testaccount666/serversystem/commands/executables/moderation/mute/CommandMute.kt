package me.testaccount666.serversystem.commands.executables.moderation.mute

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.moderation.AbstractModerationCommand
import me.testaccount666.serversystem.commands.executables.moderation.TabCompleterModeration
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand("mute", ["unmute", "shadowmute"], TabCompleterModeration::class)
class CommandMute : AbstractModerationCommand<MuteModeration>() {
    override fun minRequiredArguments(command: Command): Int {
        if (isRemoveModeration(command)) return 1
        return 2
    }

    override fun getUsagePermission(command: Command): String {
        return when (command.name.lowercase()) {
            "mute" -> "Moderation.Mute.Use"
            "shadowmute" -> "Moderation.Mute.Shadow"
            "unmute" -> "Moderation.Mute.Remove"
            else -> error("(CommandMute;getUsagePermission) Unknown command name: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Mute"

        return when (val commandName = command.name.lowercase()) {
            "mute", "shadowmute" -> "Mute"
            "unmute" -> "Unmute"
            else -> error("(CommandMute) Unknown command name: ${commandName}")
        }
    }

    override fun createModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String): MuteModeration {
        val shadowMute = command.name.equals("shadowmute", true)

        return MuteModeration.builder()
            .isShadowMute(shadowMute).expireTime(expireTime)
            .reason(reason).senderUuid(commandSender.uuid)
            .targetUuid(targetUser.uuid).build()
    }

    override fun getModerationManager(targetUser: OfflineUser) = targetUser.muteManager

    override fun type(command: Command?): String {
        if (command == null) return "Mute"

        if (command.name.equals("shadowmute", true)) return "ShadowMute"
        return "Mute"
    }

    override fun handlePostRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser) {
        // Nothing to do
    }

    override fun handlePostModeration(command: Command, commandSender: User, targetUser: OfflineUser, moderation: MuteModeration) {
        // Nothing to do
    }
}
