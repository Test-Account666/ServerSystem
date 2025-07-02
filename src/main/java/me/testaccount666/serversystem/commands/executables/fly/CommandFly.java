package me.testaccount666.serversystem.commands.executables.fly;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractPlayerTargetingCommand;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

/**
 * Command executor for the fly command.
 * This command allows players to toggle flight mode for themselves or other players.
 */
@ServerSystemCommand(name = "fly")
public class CommandFly extends AbstractPlayerTargetingCommand {

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
        if (!checkBasePermission(commandSender, "Fly.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetPlayerOptional = getTargetPlayer(commandSender, arguments);

        if (targetPlayerOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetPlayer = targetPlayerOptional.get();
        var targetUserOptional = ServerSystem.Instance.getUserManager().getUser(targetPlayer);

        if (targetUserOptional.isEmpty()) {
            Bukkit.getLogger().warning("(CommandFly) User '${targetPlayer.getName()}' is not cached! This should not happen!");
            sendGeneralMessage(commandSender, "ErrorOccurred", targetPlayer.getName(), label, null);
            return;
        }

        // Target should be online, so casting, without additional checks, should be safe
        var targetUser = (User) targetUserOptional.get().getOfflineUser();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Fly.Other", targetPlayer.getName(), label)) return;

        var messagePath = isSelf? "Fly.Success" : "Fly.SuccessOther";
        var isFlying = !targetPlayer.getAllowFlight();

        messagePath = isFlying? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        var isOnGround = targetPlayer.getLocation().add(0, -.3, 0).getBlock().getType().isSolid();

        targetPlayer.setAllowFlight(isFlying);
        if (!isOnGround) targetPlayer.setFlying(isFlying);

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "Fly.Success." + (isFlying? "Enabled" : "Disabled"), null, label, null);
    }
}
