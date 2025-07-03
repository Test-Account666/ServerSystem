package me.testaccount666.serversystem.commands.executables.god;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractPlayerTargetingCommand;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

/**
 * Command executor for the god command.
 * This command allows players to toggle god mode (invulnerability) for themselves or other players.
 */
@ServerSystemCommand(name = "god")
public class CommandGod extends AbstractPlayerTargetingCommand {
    /**
     * Executes the god command.
     * This method toggles god mode (invulnerability) for the target player if the sender has the required permissions.
     * If no target is specified, the sender is used as the target.
     *
     * @param commandSender The user who executed the command
     * @param command       The command that was executed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command, where the first argument can be a target player name
     */
    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "God.Use", label)) return;

        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetPlayerOptional = getTargetPlayer(commandSender, arguments);

        if (targetPlayerOptional.isEmpty()) {
            if (arguments.length > 0) sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetPlayer = targetPlayerOptional.get();

        var targetUserOptional = validateAndGetUser(commandSender, targetPlayer, label, "God");
        if (targetUserOptional.isEmpty()) return;

        var targetUser = targetUserOptional.get();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "God.Other", targetPlayer.getName(), label)) return;

        var messagePath = isSelf? "God.Success" : "God.SuccessOther";
        var isGod = !targetUser.isGodMode();

        messagePath = isGod? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        targetUser.setGodMode(isGod);
        targetUser.save();

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "God.Success." + (isGod? "Enabled" : "Disabled"), null, label, null);
    }
}
