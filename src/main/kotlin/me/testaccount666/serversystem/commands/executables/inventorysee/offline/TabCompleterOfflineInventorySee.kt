package me.testaccount666.serversystem.commands.executables.inventorysee.offline

import me.testaccount666.serversystem.commands.common.tabcompleters.OfflinePlayerTabCompletion.getOfflinePlayerNames
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterOfflineInventorySee : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (!hasCommandPermission(commandSender, "OfflineInventorySee.Use", false)) return listOf()

        if (arguments.size <= 1) return getOfflinePlayerNames(*arguments)

        return listOf()
    }
}
