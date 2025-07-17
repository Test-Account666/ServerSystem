package me.testaccount666.serversystem.commands.executables.commandspy;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "commandspy")
public class CommandCommandSpy extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "CommandSpy.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "CommandSpy.Other", targetPlayer.getName())) return;

        var isEnabled = !targetUser.isCommandSpyEnabled();

        var messagePath = isSelf? "CommandSpy.Success" : "CommandSpy.SuccessOther";

        messagePath += isEnabled? ".Enabled" : ".Disabled";

        targetUser.setCommandSpyEnabled(isEnabled);
        targetUser.save();

        command(messagePath, commandSender).target(targetPlayer.getName()).build();

        if (isSelf) return;
        command("CommandSpy.Success" + (isEnabled? "Enabled" : "Disabled"), targetUser).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "CommandSpy";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "CommandSpy.Use", false);
    }
}
