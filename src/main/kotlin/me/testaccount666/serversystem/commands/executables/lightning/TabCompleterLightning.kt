package me.testaccount666.serversystem.commands.executables.lightning

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

class TabCompleterLightning : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (arguments.isEmpty() || arguments.size > 2) return listOf()
        if (arguments.size == 2) return listOf("--visual", "-v").filter { it.startsWith(arguments[1], true) }

        val players = Bukkit.getOnlinePlayers().filter { commandSender.getPlayer()?.canSee(it) ?: false }.map { it.name }.toMutableList()
        players.add("-v")
        players.add("--visual")
        return players.filter { it.startsWith(arguments[0], true) }
    }
}
