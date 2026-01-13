package me.testaccount666.serversystem.commands

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class DefaultTabCompleter : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? = null
}
