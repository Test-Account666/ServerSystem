package me.testaccount666.serversystem.commands.executables.serversystem

import me.testaccount666.migration.plugins.MigratorRegistry
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.commands.interfaces.getService
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterServerSystem : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (arguments.size <= 1) {
            val possibleCompletions = listOf("version", "reload", "migrate")
            if (arguments.isEmpty()) return possibleCompletions

            return possibleCompletions.filter { it.startsWith(arguments[0], true) }
        }

        val subCommand = arguments[0].lowercase()
        if (subCommand == "migrate") {
            val newArguments = arguments.drop(1).toTypedArray()

            return migrate(*newArguments)
        }
        return listOf()
    }

    fun migrate(vararg arguments: String): List<String> {
        if (arguments.size <= 1) {
            val possibleCompletions = listOf("to", "from")
            if (arguments.isEmpty()) return possibleCompletions

            return possibleCompletions.filter { it.startsWith(arguments[0], true) }
        }

        if (arguments.size > 2) return listOf()

        val migratorRegistry = getService<MigratorRegistry>()
        val possibleCompletions = migratorRegistry.migrators

        return possibleCompletions.filter { it.startsWith(arguments[1], true) }
    }
}
