package me.testaccount666.serversystem.commands.executables.inventorysee.online

import me.testaccount666.serversystem.commands.executables.inventorysee.offline.TabCompleterOfflineInventorySee
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterInventorySee : ServerSystemTabCompleter {
    private val _offlineInventorySee: TabCompleterOfflineInventorySee = TabCompleterOfflineInventorySee()

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (command.name.startsWith("offline", true)) return _offlineInventorySee.tabComplete(
            commandSender, command, label, *arguments
        )

        if (!hasCommandPermission(commandSender, "InventorySee.Use", false)) return listOf()
        if (arguments.size <= 1) return null

        return listOf()
    }
}
