package me.testaccount666.serversystem.commands.executables

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

abstract class AbstractServerSystemCommand : ServerSystemCommandExecutor {
    /**
     * Gets the target user for the command with full control over index and fallback behavior.
     * If arguments are provided, tries to find a player with the name specified at the given index.
     * If no arguments are provided or the index is out of bounds and returnSender is true,
     * uses the command sender as the target.
     *
     * @param commandSender The user who executed the command
     * @param index         The index in the arguments array to look for the player name
     * @param returnSender  Whether to return the command sender when no target is specified
     * @param arguments     The arguments passed to the command
     * @return The target user, or null if the target user is not found
     */
    protected fun getTargetUser(commandSender: User, index: Int = 0, returnSender: Boolean = true, vararg arguments: String): User? {
        if (arguments.size > index) {
            val registry = ServerSystem.instance.registry.getService<UserManager>()
            val cachedUser = registry.getUserOrNull(arguments[index], true)
            return cachedUser?.offlineUser as? User
        }
        return if (returnSender) commandSender else null
    }

    /**
     * Handles the case when the console executes a command without specifying a target player at the given index.
     * Sends a message to the console indicating that a player target is required.
     *
     * @param commandSender  The user who executed the command
     * @param expectedLength The array length expected with target argument -1
     * @param arguments      The arguments passed to the command
     * @return true if the command was executed by console without a target, false otherwise
     */
    protected fun handleConsoleWithNoTarget(
        commandSender: User,
        syntaxPath: String,
        label: String,
        expectedLength: Int = 0,
        vararg arguments: String
    ): Boolean {
        if (arguments.size <= expectedLength && commandSender is ConsoleUser) {
            general("InvalidArguments", commandSender) {
                syntax(syntaxPath)
                label(label)
            }.build()
            return true
        }
        return false
    }

    /**
     * Checks if the command sender has the base permission required to execute the command.
     *
     * @param commandSender The user who executed the command
     * @param permission    The permission to check
     * @return true if the user has the required permission, false otherwise
     */
    protected fun checkBasePermission(commandSender: User, permission: String): Boolean = checkOtherPermission(commandSender, permission)

    /**
     * Checks if the command sender has the permission required to execute the command on a target.
     * If the user doesn't have the required permission, sends a no permission message.
     *
     * @param commandSender The user who executed the command
     * @param permission    The permission to check
     * @param targetName    The name of the target player, or null if there is no target
     * @return true if the user has the required permission, false otherwise
     */
    protected fun checkOtherPermission(commandSender: User, permission: String, targetName: String? = null): Boolean {
        if (!PermissionManager.hasCommandPermission(commandSender, permission)) {
            sendNoPermissionMessage(commandSender, "Commands.${permission}", targetName)
            return false
        }
        return true
    }

    /**
     * Sends a no permission message to the recipient, including the specific permission that was missing.
     *
     * @param recipient  The user who will receive the message
     * @param permission The permission that was checked and failed
     * @param targetName The name of the target player, or null if there is no target
     */
    protected fun sendNoPermissionMessage(recipient: User, permission: String, targetName: String?) {
        general("NoPermission", recipient) {
            target(targetName)
            postModifier { it.replace("<PERMISSION>", PermissionManager.getPermission(permission)!!) }
        }.build()
    }

    abstract fun getSyntaxPath(command: Command?): String

    abstract fun hasCommandAccess(player: Player, command: Command): Boolean
}