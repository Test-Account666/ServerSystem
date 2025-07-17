package me.testaccount666.serversystem.commands.executables.fly;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

/**
 * Command executor for the fly command.
 * This command allows players to toggle flight mode for themselves or other players.
 */
@ServerSystemCommand(name = "fly")
public class CommandFly extends AbstractServerSystemCommand {

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
        if (!checkBasePermission(commandSender, "Fly.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);

        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Fly.Other", targetPlayer.getName())) return;

        var isFlying = !targetPlayer.getAllowFlight();
        var messagePath = isSelf? "Fly.Success" : "Fly.SuccessOther";
        messagePath = isFlying? "${messagePath}.Enabled" : "${messagePath}.Disabled";

        var isOnGround = targetPlayer.getLocation().add(0, -.3, 0).getBlock().getType().isSolid();

        targetPlayer.setAllowFlight(isFlying);
        if (!isOnGround) targetPlayer.setFlying(isFlying);

        command(messagePath, targetUser).target(targetPlayer.getName()).build();

        if (isSelf) return;
        command("Fly.Success" + (isFlying? "Enabled" : "Disabled"), commandSender).target(targetPlayer.getName()).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Fly";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Fly.Use", false);
    }
}
