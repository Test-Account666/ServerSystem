package me.testaccount666.serversystem.commands.executables.offlineteleport

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.*
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

        commandSender.commandMsg("OfflineTeleport.Success") { target(targetName) }
    }

    private fun handleOfflineTeleportHere(commandSender: User, label: String, vararg arguments: String) {
        val cachedUser = getTargetUser(commandSender, label, arguments[0]) ?: return
        val targetName = cachedUser.offlineUser.getNameOrNull() ?: arguments[0]

        cachedUser.offlineUser.logoutPosition = commandSender.getPlayer()!!.location
        cachedUser.offlineUser.save()

        commandSender.commandMsg("OfflineTeleportHere.Success") { target(targetName) }
    }

    private fun getTargetUser(commandSender: User, label: String, name: String): CachedUser? {
        val cachedUser = getService<UserManager>().getUserOrNull(name) ?: run {
            commandSender.generalMsg("ErrorOccurred") {
                label(label)
                target(name)
            }
            return null
        }

        val targetName = cachedUser.offlineUser.getNameOrNull() ?: name

        if (cachedUser.isOnlineUser) {
            commandSender.generalMsg("Offline.NotOffline") { target(targetName) }
            return null
        }

        return cachedUser
    }
}
