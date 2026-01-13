package me.testaccount666.serversystem.commands.executables.kit

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.command.Command
import java.util.Locale.getDefault

class TabCompleterKit : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        val commandName = command.name.lowercase(getDefault())
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
        val kitManager = instance.registry.getService<KitManager>()

        val possibleCompletions = kitManager.allKitNames
        if (argument.isEmpty()) return possibleCompletions

        return possibleCompletions.filter { name -> name.startsWith(argument, true) }
    }

    private fun handlePlayerNameCompletion(argument: String): List<String> {
        val possibleCompletions = Bukkit.getOnlinePlayers().map { it.name }
        if (argument.isEmpty()) return possibleCompletions

        return possibleCompletions.filter { it.startsWith(argument, true) }
    }

    private fun handleTimeCompletion(argument: String): List<String> {
        if (argument.isEmpty()) return defaultTimeSuggestions

        val suggestions = ArrayList<String>()

        if (argument.matches(".*\\d$".toRegex())) suggestions.addAll(getTimeSuggestions(argument))
        else if (argument.matches("\\d+".toRegex())) suggestions.addAll(getTimeSuggestions(argument))
        else suggestions.addAll(getMatchingTimeUnits(argument))

        return suggestions
    }

    private val defaultTimeSuggestions: List<String>
        get() = ArrayList(_NUMBERS)

    private fun getTimeSuggestions(argument: String): List<String> {
        val suggestions = ArrayList<String>()
        _NUMBERS.forEach { suggestions.add(argument + it) }
        _TIME_UNITS.forEach { suggestions.add(argument + it) }
        return suggestions
    }

    private fun getMatchingTimeUnits(argument: String): List<String> {
        return _TIME_UNITS.filter { unit -> unit.startsWith(argument, true) }
    }

    companion object {
        private val _TIME_UNITS = listOf("s", "m", "h", "d", "w", "mo", "y")
        private val _NUMBERS = (0 until 10).map { it.toString() }
    }
}
