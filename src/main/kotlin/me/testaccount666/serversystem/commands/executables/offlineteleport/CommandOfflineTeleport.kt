package me.testaccount666.serversystem.commands.executables.offlineteleport

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.CachedUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command

@ServerSystemCommand("offlineteleport", ["offlineteleporthere"], TabCompleterOfflineTeleport::class)
class CommandOfflineTeleport : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command): String {
        if (command.name.equals("offlineteleport", true)) return "OfflineTeleport.Use"
        return "OfflineTeleportHere.Use"
    }

    override fun getSyntaxPath(command: Command?) = "OfflineTeleport"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        if (command.name.equals("offlineteleport", true)) {
            handleOfflineTeleport(commandSender, label, *arguments)
            return
        }

        handleOfflineTeleportHere(commandSender, label, *arguments)
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
        val cachedUser = getService<UserManager>().getUserOrNull(name) ?: run {
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
}
