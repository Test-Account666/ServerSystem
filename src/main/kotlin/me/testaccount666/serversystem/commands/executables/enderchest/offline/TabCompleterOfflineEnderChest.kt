package me.testaccount666.serversystem.commands.executables.enderchest.offline

import me.testaccount666.serversystem.commands.common.tabcompleters.OfflinePlayerTabCompletion.getOfflinePlayerNames
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterOfflineEnderChest : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        return if (arguments.size <= 1) getOfflinePlayerNames(*arguments) else listOf()
    }
}