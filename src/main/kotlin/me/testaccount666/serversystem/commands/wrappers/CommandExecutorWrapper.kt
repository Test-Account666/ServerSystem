package me.testaccount666.serversystem.commands.wrappers

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CommandExecutorWrapper(val commandExecutor: ServerSystemCommandExecutor) : AbstractCommandWrapper(), CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, label: String, arguments: Array<String>): Boolean {
        val commandUser = resolveCommandUser(commandSender) ?: run {
            // This should technically never happen...
            log.severe("Error executing command '${command.name}'. CommandSender '${commandSender.name}' is not a valid user?!")
            return false
        }

        if (commandExecutor !is AbstractServerSystemCommand) {
            log.severe("Command '${command.name}' does not extend AbstractServerSystemCommand!")
            general("ErrorOccurred", commandUser) { label(label) }.build()
            return false
        }

        val permission = commandExecutor.getUsagePermission(command)
        if (!commandExecutor.checkPermission(commandUser, permission)) return true

        if (arguments.size < commandExecutor.minRequiredArguments(command)) {
            general("InvalidArguments", commandUser) {
                syntax(commandExecutor.getSyntaxPath(command))
                label(label)
            }.build()
            return true
        }

        commandExecutor.execute(commandUser, command, label, *arguments)
        return true
    }
}
