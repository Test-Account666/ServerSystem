package me.testaccount666.serversystem.commands.executables.kit

import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.commands.interfaces.getService
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command

class TabCompleterKit : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        val commandName = command.name.lowercase()
        if (!hasPermission(commandSender, commandName)) return listOf()

        return when (commandName) {
            "kit" -> handleKitCommand(*arguments)
            "createkit" -> handleCreateKitCommand(*arguments)
            "deletekit" -> handleDeleteKitCommand(*arguments)
            else -> null
        }
    }

    private fun handleKitCommand(vararg arguments: String): List<String>? {
        return when (arguments.size) {
            1 -> handleKitNameCompletion(arguments[0])
            2 -> handlePlayerNameCompletion(arguments[1])
            else -> null
        }
    }

    private fun handleCreateKitCommand(vararg arguments: String): List<String>? {
        return when (arguments.size) {
            1 -> listOf()
            2 -> handleTimeCompletion(arguments[1])
            else -> null
        }
    }

    private fun handleDeleteKitCommand(vararg arguments: String): List<String> {
        if (arguments.size != 1) return listOf()

        return handleKitNameCompletion(arguments[0])
    }

    private fun hasPermission(commandSender: User, commandName: String): Boolean {
        val permissionNode = when (commandName) {
            "kit" -> "Kit.Use"
            "createkit" -> "Kit.Create"
            "deletekit" -> "Kit.Delete"
            else -> null
        }
        return hasCommandPermission(commandSender, permissionNode, false)
    }

    private fun handleKitNameCompletion(argument: String): List<String> {
        val kitManager = getService<KitManager>()

        val possibleCompletions = kitManager.allKitNames
        if (argument.isEmpty()) return possibleCompletions

        return possibleCompletions.filter { it.startsWith(argument, true) }
    }

    private fun handlePlayerNameCompletion(argument: String): List<String> {
        val possibleCompletions = Bukkit.getOnlinePlayers().map { it.name }
        if (argument.isEmpty()) return possibleCompletions

        return possibleCompletions.filter { it.startsWith(argument, true) }
    }

    private fun handleTimeCompletion(argument: String): List<String> {
        if (argument.isEmpty()) return defaultTimeSuggestions

        return if (argument.matches(".*\\d$".toRegex()) || argument.matches("\\d+".toRegex())) {
            getTimeSuggestions(argument)
        } else {
            getMatchingTimeUnits(argument)
        }
    }

    private val defaultTimeSuggestions
        get() = _NUMBERS.toList()

    private fun getTimeSuggestions(argument: String) = _NUMBERS.map { argument + it } + _TIME_UNITS.map { argument + it }
    private fun getMatchingTimeUnits(argument: String) = _TIME_UNITS.filter { it.startsWith(argument, true) }

    companion object {
        private val _TIME_UNITS = listOf("s", "m", "h", "d", "w", "mo", "y")
        private val _NUMBERS = (0 until 10).map { it.toString() }
    }
}
