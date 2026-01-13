package me.testaccount666.serversystem.commands.executables.lightning

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterLightning : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (arguments.size == 1) {
            if (!"visual".startsWith(arguments[0], true)) return listOf()

            return listOf("visual")
        }

        return null
    }
}
