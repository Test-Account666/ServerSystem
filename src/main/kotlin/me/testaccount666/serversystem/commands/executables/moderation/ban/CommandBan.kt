package me.testaccount666.serversystem.commands.executables.moderation.ban

import com.destroystokyo.paper.profile.PlayerProfile
import io.papermc.paper.ban.BanListType
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.moderation.AbstractModerationCommand
import me.testaccount666.serversystem.commands.executables.moderation.TabCompleterModeration
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.moderation.BanModeration
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.BanEntry
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.time.Instant
import java.util.Locale.getDefault

@ServerSystemCommand("ban", ["unban"], TabCompleterModeration::class)
class CommandBan : AbstractModerationCommand<BanModeration>() {
    override fun handlePostModeration(command: Command, commandSender: User, targetUser: OfflineUser, moderation: BanModeration) {
        if (targetUser !is User) return

        val player = targetUser.getPlayer()!!
        val unbanDate = parseDate(moderation.expireTime, targetUser)
        val kickOptional = command("Moderation.Ban.Kick", targetUser) {
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

        if (kickOptional.isEmpty()) {
            log.severe("(CommandBan) Kick message is empty! This should not happen!")
            general("ErrorOccurred", commandSender).build()
            return
        }

        var expireTime = moderation.expireTime
        if (expireTime == 0L) expireTime = Instant.now().toEpochMilli() + 1
        val unbanTime = if (expireTime > 0) Instant.ofEpochMilli(expireTime) else null

        player.ban<BanEntry<in PlayerProfile>>(kickOptional, unbanTime, moderation.senderUuid.toString(), true)
    }

    override fun handlePostRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser) {
        Bukkit.getServer().getBanList(BanListType.PROFILE).pardon(targetUser.player!!.playerProfile)
    }

    override fun createModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String): BanModeration {
        return BanModeration.builder()
            .senderUuid(commandSender.uuid).targetUuid(targetUser.uuid)
            .reason(reason).expireTime(expireTime).build()
    }

    override fun checkBasePermission(commandSender: User, command: Command): Boolean {
        val permissionPath = when (command.name.lowercase(getDefault())) {
            "ban" -> "Moderation.Ban.Use"
            "unban" -> "Moderation.Ban.Remove"
            else -> null
        }
        return checkBasePermission(commandSender, permissionPath!!)
    }

    override fun getModerationManager(targetUser: OfflineUser) = targetUser.banManager

    override fun type(command: Command?): String = "Ban"

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "Ban"

        return when (val commandName = command.name.lowercase(getDefault())) {
            "ban" -> "Ban"
            "unban" -> "Unban"
            else -> throw IllegalArgumentException("(CommandBan) Unknown command name: ${commandName}")
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionPath = when (command.name.lowercase(getDefault())) {
            "ban" -> "Moderation.Ban.Use"
            "unban" -> "Moderation.Ban.Remove"
            else -> null
        }
        return PermissionManager.hasCommandPermission(player, permissionPath!!, false)
    }
}
