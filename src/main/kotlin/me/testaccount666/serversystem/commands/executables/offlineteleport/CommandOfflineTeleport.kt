package me.testaccount666.serversystem.commands.executables.offlineteleport

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.CachedUser
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("offlineteleport", ["offlineteleporthere"], TabCompleterOfflineTeleport::class)
class CommandOfflineTeleport : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (command.name.equals("offlineteleport", true)) {
            handleOfflineTeleport(commandSender, label, *arguments)
            return
        }

        if (command.name.equals("offlineteleporthere", true)) handleOfflineTeleportHere(commandSender, label, *arguments)
    }

    private fun handleOfflineTeleport(commandSender: User, label: String, vararg arguments: String) {
        val cachedUser = getTargetUser(commandSender, label, arguments[0]) ?: return

        val targetName = cachedUser.offlineUser.getNameOrNull() ?: arguments[0]

        commandSender.getPlayer()!!.teleport(cachedUser.offlineUser.logoutPosition!!)

        command("OfflineTeleport.Success", commandSender) { target(targetName) }.build()
    }

    private fun handleOfflineTeleportHere(commandSender: User, label: String, vararg arguments: String) {
        val cachedUser = getTargetUser(commandSender, label, arguments[0]) ?: return

        val targetName = cachedUser.offlineUser.getNameOrNull() ?: arguments[0]

        cachedUser.offlineUser.logoutPosition = commandSender.getPlayer()!!.location
        cachedUser.offlineUser.save()

        command("OfflineTeleportHere.Success", commandSender) { target(targetName) }.build()
    }

    private fun getTargetUser(commandSender: User, label: String, name: String): CachedUser? {
        val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(name)

        if (cachedUser == null) {
            general("ErrorOccurred", commandSender) {
                label(label)
                target(name)
            }.build()
            return null
        }

        val targetName = cachedUser.offlineUser.getNameOrNull() ?: name

        if (cachedUser.isOnlineUser) {
            general("Offline.NotOffline", commandSender) { target(targetName) }.build()
            return null
        }

        return cachedUser
    }

    override fun getSyntaxPath(command: Command?): String {
        return "OfflineTeleport"
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        if (command.name.equals("offlineteleport", true)) return hasCommandPermission(player, "OfflineTeleport.Use", false)

        return hasCommandPermission(player, "OfflineTeleportHere.Use", false)
    }
}
