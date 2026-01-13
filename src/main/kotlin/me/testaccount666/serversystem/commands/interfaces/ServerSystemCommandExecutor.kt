package me.testaccount666.serversystem.commands.interfaces

import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

/**
 * Custom Interface for command executors.
 * Classes implementing this interface can be registered as command executors
 * with the CommandManager.
 */
fun interface ServerSystemCommandExecutor {
    /**
     * Executes a command for the given sender.
     * 
     * @param commandSender The user who executed the command
     * @param command       The command that was executed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command
     */
    fun execute(commandSender: User, command: Command, label: String, vararg arguments: String)
}
