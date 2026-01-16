package me.testaccount666.serversystem.commands.executables.weather

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

class TabCompleterWeather : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (!hasCommandPermission(commandSender, "Weather.Use", false)) return listOf()

        if (command.name.equals("weather", true)) {
            if (arguments.size <= 1) {
                val possibleCompletions = listOf("sun", "clear", "rain", "storm", "thunder")
                return possibleCompletions.filter { it.startsWith(arguments[0], true) }
            }

            if (arguments.size == 2) return handleWorldCompletions(1, *arguments)

            return listOf()
        }

        if (arguments.size == 1) return handleWorldCompletions(0, *arguments)

        return listOf()
    }

    private fun handleWorldCompletions(index: Int, vararg arguments: String): List<String> {
        if (!hasPermission(Bukkit.getConsoleSender(), "Commands.Weather.World", false)) return listOf()

        val possibleCompletions = Bukkit.getWorlds().map { it.name }
        return possibleCompletions.filter { it.startsWith(arguments[index], true) }
    }
}
