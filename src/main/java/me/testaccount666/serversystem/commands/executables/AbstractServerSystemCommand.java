package me.testaccount666.serversystem.commands.executables;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

public abstract class AbstractServerSystemCommand implements ServerSystemCommandExecutor {

    public abstract boolean hasCommandAccess(Player player, Command command);

    /**
     * Gets the target user for a command.
     * If arguments are provided, tries to find a player with the name specified in the first argument.
     * If no arguments are provided, uses the command sender as the target.
     *
     * @param commandSender The user who executed the command
     * @param arguments     The arguments passed to the command
     * @return An Optional containing the target user, or empty if the target user is not found
     */
    protected Optional<User> getTargetUser(User commandSender, String... arguments) {
        return getTargetUser(commandSender, true, arguments);
    }

    /**
     * Gets the target user for the command, with control over returning the sender as fallback.
     * If arguments are provided, tries to find a player with the name specified in the first argument.
     * If no arguments are provided and returnSender is true, uses the command sender as the target.
     *
     * @param commandSender The user who executed the command
     * @param returnSender  Whether to return the command sender when no target is specified
     * @param arguments     The arguments passed to the command
     * @return An Optional containing the target user, or empty if the target user is not found
     */
    protected Optional<User> getTargetUser(User commandSender, boolean returnSender, String... arguments) {
        return getTargetUser(commandSender, 0, returnSender, arguments);
    }

    /**
     * Gets the target user for the command using a specific argument index.
     * If arguments are provided, tries to find a player with the name specified at the given index.
     * If no arguments are provided or the index is out of bounds, uses the command sender as the target.
     *
     * @param commandSender The user who executed the command
     * @param index         The index in the arguments array to look for the player name
     * @param arguments     The arguments passed to the command
     * @return An Optional containing the target user, or empty if the target user is not found
     */
    protected Optional<User> getTargetUser(User commandSender, int index, String... arguments) {
        return getTargetUser(commandSender, index, true, arguments);
    }

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
     * @return An Optional containing the target user, or empty if the target user is not found
     */
    protected Optional<User> getTargetUser(User commandSender, int index, boolean returnSender, String... arguments) {
        if (arguments.length > index) {
            var userOptional = ServerSystem.Instance.getUserManager().getUser(arguments[index], true);
            return userOptional.map(user -> (User) user.getOfflineUser());
        }
        return returnSender? Optional.of(commandSender) : Optional.empty();
    }

    /**
     * Handles the case when the console executes a command without specifying a target player.
     * Sends a message to the console indicating that a player target is required.
     *
     * @param commandSender The user who executed the command
     * @param arguments     The arguments passed to the command
     * @return true if the command was executed by console without a target, false otherwise
     */
    protected boolean handleConsoleWithNoTarget(User commandSender, String... arguments) {
        return handleConsoleWithNoTarget(commandSender, 0, arguments);
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
    protected boolean handleConsoleWithNoTarget(User commandSender, int expectedLength, String... arguments) {
        if (arguments.length <= expectedLength && commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return true;
        }
        return false;
    }

    /**
     * Checks if the command sender has the base permission required to execute the command.
     *
     * @param commandSender The user who executed the command
     * @param permission    The permission to check
     * @return true if the user has the required permission, false otherwise
     */
    protected boolean checkBasePermission(User commandSender, String permission) {
        return checkOtherPermission(commandSender, permission, null);
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
    protected boolean checkOtherPermission(User commandSender, String permission, String targetName) {
        if (!PermissionManager.hasCommandPermission(commandSender, permission)) {
            sendNoPermissionMessage(commandSender, "Commands.${permission}", targetName);
            return false;
        }
        return true;
    }

    /**
     * Sends a no permission message to the recipient, including the specific permission that was missing.
     *
     * @param recipient  The user who will receive the message
     * @param permission The permission that was checked and failed
     * @param targetName The name of the target player, or null if there is no target
     */
    protected void sendNoPermissionMessage(User recipient, String permission, String targetName) {
        general("NoPermission", recipient)
                .modifier(message -> message.replace("<PERMISSION>", PermissionManager.getPermission(permission)))
                .target(targetName).build();
    }
}
