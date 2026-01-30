package me.testaccount666.serversystem.commands.executables.moderation.ban

import io.papermc.paper.ban.BanListType
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.moderation.AbstractModerationCommand
import me.testaccount666.serversystem.commands.executables.moderation.TabCompleterModeration
import me.testaccount666.serversystem.moderation.BanModeration
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.ComponentColor
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.command.Command
import java.time.Instant

@ServerSystemCommand("ban", ["unban"], TabCompleterModeration::class)
class CommandBan : AbstractModerationCommand<BanModeration>() {
    override fun minRequiredArguments(command: Command): Int {
        if (isRemoveModeration(command)) return 1
        return 2
    }

    override fun getUsagePermission(command: Command): String {
        return when (command.name.lowercase()) {
            "ban" -> "Moderation.Ban.Use"
            "unban" -> "Moderation.Ban.Remove"
            else -> error("(CommandBan;getUsagePermission) Unknown command name: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?): String {
        command ?: return "Ban"

        return when (val commandName = command.name.lowercase()) {
            "ban" -> "Ban"
            "unban" -> "Unban"
            else -> error("(CommandBan;getSyntaxPath) Unknown command name: ${commandName}")
        }
    }

    override fun handlePostModeration(command: Command, commandSender: User, targetUser: OfflineUser, moderation: BanModeration) {
        val player = targetUser.player!!
        val unbanDate = parseDate(moderation.expireTime, targetUser)
        val user = targetUser as? User ?: UserManager.consoleUser

        val kickMessage = command("Moderation.Ban.Kick", user) {
            sender(commandSender.getNameSafe())
            send(false)
            target(targetUser.getNameOrNull())
            prefix(false)
            postModifier {
                it.replace("<DATE>", unbanDate)
                    .replace("<REASON>", moderation.reason)
            }
            blankError(true)
        }.build()

        if (kickMessage.isEmpty()) {
            log.severe("(CommandBan) Kick message is empty! This should not happen!")
            general("ErrorOccurred", commandSender).build()
            return
        }

        var expireTime = moderation.expireTime
        if (expireTime == 0L) expireTime = Instant.now().toEpochMilli() + 1
        val unbanTime = if (expireTime > 0) Instant.ofEpochMilli(expireTime) else null

        val banlist = Bukkit.getServer().getBanList(BanListType.PROFILE)
        banlist.addBan(player.playerProfile, kickMessage, unbanTime, moderation.senderUuid.toString())?.save()

        player.player?.kick(ComponentColor.translateToComponent(kickMessage))
    }

    override fun handlePostRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser) {
        Bukkit.getServer().getBanList(BanListType.PROFILE).pardon(targetUser.player!!.playerProfile)
    }

    override fun createModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String): BanModeration {
        return BanModeration.builder()
            .senderUuid(commandSender.uuid).targetUuid(targetUser.uuid)
            .reason(reason).expireTime(expireTime).build()
    }

    override fun getModerationManager(targetUser: OfflineUser) = targetUser.banManager

    override fun type(command: Command?): String = "Ban"
}
