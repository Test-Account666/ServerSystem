package me.testaccount666.serversystem.commands.wrappers

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CommandExecutorWrapper(val commandExecutor: ServerSystemCommandExecutor) : AbstractCommandWrapper(), CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, label: String, arguments: Array<String>): Boolean {
        val commandUser = resolveCommandUser(commandSender)

        // This should technically never happen...
        if (commandUser == null) {
            log.severe("Error executing command '${command.name}'. CommandSender '${commandSender.name}' is not a valid user?!")
            return false
        }

        commandExecutor.execute(commandUser, command, label, *arguments)
        return true
    }
}
