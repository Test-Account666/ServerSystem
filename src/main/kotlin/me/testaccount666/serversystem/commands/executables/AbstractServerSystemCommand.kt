package me.testaccount666.serversystem.commands.executables

import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.*
import org.bukkit.command.Command
import org.bukkit.entity.Player

abstract class AbstractServerSystemCommand : ServerSystemCommandExecutor {
    val variantLabelMap = mutableMapOf<String, List<String>>()

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
    internal fun getTargetUser(commandSender: User, index: Int = 0, returnSender: Boolean = true, vararg arguments: String): User? {
        val name = arguments.getOrNull(index)
        return when {
            name != null -> getService<UserManager>().getUserOrNull(name, true)?.offlineUser as? User
            returnSender -> commandSender
            else -> null
        }
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
    internal fun isConsoleWithNoTarget(
        commandSender: User, syntaxPath: String, label: String,
        expectedLength: Int = 0, vararg arguments: String,
    ): Boolean {
        if (arguments.size <= expectedLength && commandSender is ConsoleUser) {
            commandSender.generalMsg("InvalidArguments") {
                syntax(syntaxPath)
                label(label)
            }
            return true
        }
        return false
    }

    /**
     * Checks if the command sender has the permission required to execute the command on a target.
     * If the user doesn't have the required permission, sends a no permission message.
     *
     * @param commandSender The user who executed the command
     * @param permission    The permission to check
     * @param targetName    The name of the target player, or null if there is no target
     * @return true if the user has the required permission, false otherwise
     */
    internal fun checkPermission(commandSender: User, permission: String, targetName: String? = null): Boolean {
        if (PermissionManager.hasCommandPermission(commandSender, permission)) return true

        sendNoPermissionMessage(commandSender, "Commands.${permission}", targetName)
        return false
    }

    /**
     * Sends a no permission message to the recipient, including the specific permission that was missing.
     *
     * @param recipient  The user who will receive the message
     * @param permission The permission that was checked and failed
     * @param targetName The name of the target player, or null if there is no target
     */
    internal fun sendNoPermissionMessage(recipient: User, permission: String, targetName: String?) {
        recipient.generalMsg("NoPermission") {
            target(targetName)
            postModifier { it.replace("<PERMISSION>", PermissionManager.getPermission(permission)!!) }
        }
    }

    internal fun isPlayer(commandSender: User, sendMessage: Boolean = true): Boolean {
        if (commandSender !is ConsoleUser) return true

        if (sendMessage) commandSender.generalMsg("NotPlayer")
        return false
    }

    abstract fun getUsagePermission(command: Command): String
    open fun minRequiredArguments(command: Command) = 0

    abstract fun getSyntaxPath(command: Command?): String

    fun hasCommandAccess(player: Player, command: Command): Boolean {
        return PermissionManager.hasCommandPermission(player, getUsagePermission(command), false)
    }
}