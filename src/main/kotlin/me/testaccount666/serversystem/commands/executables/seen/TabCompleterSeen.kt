package me.testaccount666.serversystem.commands.executables.seen

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

class TabCompleterSeen : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (arguments.size != 1) return listOf()
        return Bukkit.getOfflinePlayers().mapNotNull { it.name }.filter { it.startsWith(arguments[0], true) }
    }
}
