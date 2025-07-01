package me.testaccount666.serversystem.commands.executables.fly;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Command executor for the fly command.
 * This command allows players to toggle flight mode for themselves or other players.
 */
@ServerSystemCommand(name = "fly")
public class CommandFly implements ServerSystemCommandExecutor {

    /**
     * Executes the fly command.
     * This method toggles flight mode for the target player if the sender has the required permissions.
     * If no target is specified, the sender is used as the target.
     *
     * @param commandSender The user who executed the command
     * @param command       The command that was executed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command, where the first argument can be a target player name
     */
    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "Fly.Use")) {
            MessageManager.getNoPermissionMessage(commandSender, "Commands.Fly.Use", null, label).ifPresent(commandSender::sendMessage);
            return;
        }

        if (arguments.length == 0 && commandSender instanceof ConsoleUser) {
            MessageManager.getFormattedMessage(commandSender, "General.NotPlayer", null, label).ifPresent(commandSender::sendMessage);
            return;
        }

        var targetPlayerOptional = getTargetPlayer(commandSender, arguments);

        if (targetPlayerOptional.isEmpty()) {
            MessageManager.getFormattedMessage(commandSender, "General.PlayerNotFound", arguments[0], label).ifPresent(commandSender::sendMessage);
            return;
        }

        var targetPlayer = targetPlayerOptional.get();
        var targetUserOptional = ServerSystem.Instance.getUserManager().getUser(targetPlayer);

        if (targetUserOptional.isEmpty()) {
            Bukkit.getLogger().warning("(CommandFly) User '${targetPlayer.getName()}' is not cached! This should not happen!");
            MessageManager.getFormattedMessage(commandSender, "General.ErrorOccurred", arguments[0], label).ifPresent(commandSender::sendMessage);
            return;
        }

        // Target should be online, so casting, without additional checks, should be safe
        var targetUser = (User) targetUserOptional.get().getOfflineUser();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !PermissionManager.hasCommandPermission(commandSender, "Fly.Other")) {
            MessageManager.getNoPermissionMessage(commandSender, "Commands.Fly.Other", targetPlayer.getName(), label).ifPresent(commandSender::sendMessage);
            return;
        }

        var messagePath = isSelf? "Fly.Success" : "Fly.SuccessOther";
        var isFlying = !targetPlayer.getAllowFlight();

        messagePath = isFlying? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        var isOnGround = targetPlayer.getLocation().add(0, -.3, 0).getBlock().getType().isSolid();

        targetPlayer.setAllowFlight(isFlying);
        if (!isOnGround) targetPlayer.setFlying(isFlying);

        MessageManager.getCommandMessage(commandSender, messagePath, targetPlayer.getName(), label).ifPresent(commandSender::sendMessage);

        if (isSelf) return;
        MessageManager.getCommandMessage(targetPlayer, "Fly.Success." + (isFlying? "Enabled" : "Disabled"), null, label).ifPresent(targetPlayer::sendMessage);
    }

    //TODO: Nice Javadocs, but maybe throw this into a Utilities class

    /**
     * Gets the target player for the fly command.
     * If arguments are provided, tries to find a player with the name specified in the first argument.
     * If no arguments are provided, uses the command sender as the target.
     *
     * @param commandSender The user who executed the command
     * @param arguments     The arguments passed to the command
     * @return An Optional containing the target player, or empty if the target player is not found
     */
    private Optional<Player> getTargetPlayer(User commandSender, String... arguments) {
        if (arguments.length > 0) return Optional.ofNullable(Bukkit.getPlayer(arguments[0]));

        return Optional.of(commandSender.getPlayer());
    }
}
