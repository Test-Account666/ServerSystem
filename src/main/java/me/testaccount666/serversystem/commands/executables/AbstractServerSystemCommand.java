package me.testaccount666.serversystem.commands.executables;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.UnaryOperator;

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
     * @param label         The command label that was used
     * @param arguments     The arguments passed to the command
     * @return true if the command was executed by console without a target, false otherwise
     */
    protected boolean handleConsoleWithNoTarget(User commandSender, String label, String... arguments) {
        return handleConsoleWithNoTarget(commandSender, label, 0, arguments);
    }

    /**
     * Handles the case when the console executes a command without specifying a target player at the given index.
     * Sends a message to the console indicating that a player target is required.
     *
     * @param commandSender  The user who executed the command
     * @param label          The command label that was used
     * @param expectedLength The array length expected with target argument -1
     * @param arguments      The arguments passed to the command
     * @return true if the command was executed by console without a target, false otherwise
     */
    protected boolean handleConsoleWithNoTarget(User commandSender, String label, int expectedLength, String... arguments) {
        if (arguments.length <= expectedLength && commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return true;
        }
        return false;
    }

    /**
     * Sends a message to the command sender indicating that the specified player was not found.
     *
     * @param commandSender The user who executed the command
     * @param label         The command label that was used
     * @param playerName    The name of the player that was not found
     * @return true, always returns true to indicate the message was sent
     */
    protected boolean sendMissingPlayerMessage(User commandSender, String label, String playerName) {
        sendGeneralMessage(commandSender, "PlayerNotFound", playerName, label, null);
        return true;
    }

    /**
     * Checks if the command sender has the base permission required to execute the command.
     *
     * @param commandSender The user who executed the command
     * @param permission    The permission to check
     * @param label         The command label that was used
     * @return true if the user has the required permission, false otherwise
     */
    protected boolean checkBasePermission(User commandSender, String permission, String label) {
        return checkOtherPermission(commandSender, permission, null, label);
    }

    /**
     * Checks if the command sender has the permission required to execute the command on a target.
     * If the user doesn't have the required permission, sends a no permission message.
     *
     * @param commandSender The user who executed the command
     * @param permission    The permission to check
     * @param targetName    The name of the target player, or null if there is no target
     * @param label         The command label that was used
     * @return true if the user has the required permission, false otherwise
     */
    protected boolean checkOtherPermission(User commandSender, String permission, String targetName, String label) {
        if (!PermissionManager.hasCommandPermission(commandSender, permission)) {
            sendNoPermissionMessage(commandSender, "Commands.${permission}", targetName, label, null);
            return false;
        }
        return true;
    }

    /**
     * Sends a formatted message to the recipient from the General section of the messages file.
     *
     * @param recipient       The user who will receive the message
     * @param path            The path to the message in the General section of the messages file
     * @param targetName      The name of the target player, or null if there is no target
     * @param label           The command label that was used
     * @param messageModifier A function to modify the message before sending, or null for no modification
     */
    protected void sendGeneralMessage(User recipient, String path, String targetName, String label, UnaryOperator<String> messageModifier) {
        sendGeneralMessage(recipient, path, targetName, label, messageModifier, true);
    }

    /**
     * Sends a formatted message to the recipient from the General section of the messages file.
     *
     * @param recipient       The user who will receive the message
     * @param path            The path to the message in the General section of the messages file
     * @param targetName      The name of the target player, or null if there is no target
     * @param label           The command label that was used
     * @param messageModifier A function to modify the message before sending, or null for no modification
     * @param addPrefix       Whether to add the prefix to the message
     */
    protected void sendGeneralMessage(User recipient, String path, String targetName, String label, UnaryOperator<String> messageModifier, boolean addPrefix) {
        MessageManager.getMessage("General.${path}")
                .map(message -> messageModifier != null? messageModifier.apply(message) : message)
                .map(message -> MessageManager.formatMessage(message, recipient, targetName, label, addPrefix))
                .ifPresent(recipient::sendMessage);
    }

    /**
     * Sends a no permission message to the recipient, including the specific permission that was missing.
     *
     * @param recipient       The user who will receive the message
     * @param permission      The permission that was checked and failed
     * @param targetName      The name of the target player, or null if there is no target
     * @param label           The command label that was used
     * @param messageModifier A function to modify the message before sending, or null for no modification
     */
    protected void sendNoPermissionMessage(User recipient, String permission, String targetName, String label, UnaryOperator<String> messageModifier) {
        sendNoPermissionMessage(recipient, permission, targetName, label, messageModifier, true);
    }

    /**
     * Sends a no permission message to the recipient, including the specific permission that was missing.
     *
     * @param recipient       The user who will receive the message
     * @param permission      The permission that was checked and failed
     * @param targetName      The name of the target player, or null if there is no target
     * @param label           The command label that was used
     * @param messageModifier A function to modify the message before sending, or null for no modification
     * @param addPrefix       Whether to add the prefix to the message
     */
    protected void sendNoPermissionMessage(User recipient, String permission, String targetName, String label, UnaryOperator<String> messageModifier, boolean addPrefix) {
        MessageManager.getMessage("General.NoPermission")
                .map(message -> message.replace("<PERMISSION>", PermissionManager.getPermission(permission)))
                .map(message -> messageModifier != null? messageModifier.apply(message) : message)
                .map(message -> MessageManager.formatMessage(message, recipient, targetName, label, addPrefix))
                .ifPresent(recipient::sendMessage);
    }

    /**
     * Sends a formatted message to the recipient from the Commands section of the messages file.
     *
     * @param recipient       The user who will receive the message
     * @param path            The path to the message in the Commands section of the messages file
     * @param targetName      The name of the target player, or null if there is no target
     * @param label           The command label that was used
     * @param messageModifier A function to modify the message before sending, or null for no modification
     */
    protected void sendCommandMessage(User recipient, String path, String targetName, String label, UnaryOperator<String> messageModifier) {
        sendCommandMessage(recipient, path, targetName, label, messageModifier, true);
    }

    /**
     * Sends a formatted message to the recipient from the Commands section of the messages file.
     *
     * @param recipient       The user who will receive the message
     * @param path            The path to the message in the Commands section of the messages file
     * @param targetName      The name of the target player, or null if there is no target
     * @param label           The command label that was used
     * @param messageModifier A function to modify the message before sending, or null for no modification
     * @param addPrefix       Whether to add the prefix to the message
     */
    protected void sendCommandMessage(User recipient, String path, String targetName, String label, UnaryOperator<String> messageModifier, boolean addPrefix) {
        MessageManager.getMessage("Commands.${path}")
                .map(message -> messageModifier != null? messageModifier.apply(message) : message)
                .map(message -> MessageManager.formatMessage(message, recipient, targetName, label, addPrefix))
                .ifPresent(recipient::sendMessage);
    }
}
