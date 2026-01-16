package me.testaccount666.serversystem.commands.wrappers

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleterWrapper(val tabCompleter: ServerSystemTabCompleter) : AbstractCommandWrapper(), TabCompleter {
    override fun onTabComplete(commandSender: CommandSender, command: Command, label: String, arguments: Array<String>): List<String>? {
        val commandUser = resolveCommandUser(commandSender)

        // This should technically never happen...
        if (commandUser == null) {
            log.severe("Error tab completing command '${command.name}'. CommandSender '${commandSender.name}' is not a valid user?!")
            return listOf()
        }

        return tabCompleter.tabComplete(commandUser, command, label, *arguments)
    }
}
