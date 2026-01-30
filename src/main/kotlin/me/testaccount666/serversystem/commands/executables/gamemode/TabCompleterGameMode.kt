package me.testaccount666.serversystem.commands.executables.gamemode

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.GameMode
import org.bukkit.command.Command

class TabCompleterGameMode : ServerSystemTabCompleter {
    private val modeNames by lazy {
        GameMode.entries.map { it.name.lowercase() }.toTypedArray()
    }

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (command.name.equals("gamemode", true)) return handleGameModeCommand(*arguments)

        return if (arguments.size == 1) null else listOf()
    }

    private fun handleGameModeCommand(vararg arguments: String): List<String>? {
        if (arguments.size == 1) return listOf("0", "1", "2", "3", *modeNames).filter { it.startsWith(arguments[0], true) }
        if (arguments.size == 2) return null
        return listOf()
    }
}
