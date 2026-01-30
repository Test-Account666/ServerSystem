package me.testaccount666.serversystem.commands.common.tabcompleters

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class SimpleTabCompleter(private val completions: Map<Int, List<String>?>) : ServerSystemTabCompleter {

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (!completions.containsKey(arguments.size)) return listOf()
        return completions[arguments.size]?.filter { it.startsWith(arguments.last(), true) }
    }
}
