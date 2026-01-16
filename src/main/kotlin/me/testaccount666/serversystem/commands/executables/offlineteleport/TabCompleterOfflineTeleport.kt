package me.testaccount666.serversystem.commands.executables.offlineteleport

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.common.tabcompleters.OfflinePlayerTabCompletion.getOfflinePlayerNames
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterOfflineTeleport : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        val permissionPath = when (command.name) {
            "offlineteleport" -> "OfflineTeleport.Use"
            "offlineteleporthere" -> "OfflineTeleportHere.Use"
            else -> {
                log.warning("Unknown OfflineTeleport command: ${command.name}")
                return listOf()
            }
        }
        if (!hasCommandPermission(commandSender, permissionPath, false)) return listOf()
        if (arguments.size != 1) return listOf()

        return getOfflinePlayerNames(*arguments)
    }
}
