package me.testaccount666.serversystem.commands.executables.god;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

/**
 * Command executor for the god command.
 * This command allows players to toggle god mode (invulnerability) for themselves or other players.
 */
@ServerSystemCommand(name = "god")
public class CommandGod extends AbstractServerSystemCommand {
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
        if (!checkBasePermission(commandSender, "God.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "God.Other", targetPlayer.getName())) return;

        var isGod = !targetUser.isGodMode();
        var messagePath = isSelf? "God.Success" : "God.SuccessOther";
        messagePath = isGod? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        targetUser.setGodMode(isGod);
        targetUser.save();

        command(messagePath, commandSender).target(targetPlayer.getName()).build();

        if (isSelf) return;
        command("God.Success." + (isGod? "Enabled" : "Disabled"), targetUser)
                .sender(commandSender.getName().get()).target(targetPlayer.getName()).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "God";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "God.Use", false);
    }
}
