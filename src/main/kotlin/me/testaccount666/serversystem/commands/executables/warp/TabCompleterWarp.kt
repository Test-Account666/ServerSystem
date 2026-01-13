package me.testaccount666.serversystem.commands.executables.warp

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterWarp : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        val commandName = command.name
        if (!commandName.equals("warp", true)
            && !commandName.equals("deletewarp", true)) return null

        val permissionPath = when (commandName) {
            "warp" -> "Warp.Use"
            "deletewarp" -> "Warp.Delete"
            "setwarp" -> "Warp.Set"
            else -> null
        }

        if (!hasCommandPermission(commandSender, permissionPath, false)) return listOf()

        val potentialCompletions = instance.registry.getService<WarpManager>().warps.map { it.name }
        return potentialCompletions.filter { it.startsWith(arguments[0], true) }
    }
}
