package me.testaccount666.serversystem.commands.executables.commandspy;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

@ServerSystemCommand(name = "commandspy")
public class CommandCommandSpy extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "CommandSpy.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "CommandSpy.Other", targetPlayer.getName(), label)) return;

        var isEnabled = !targetUser.isCommandSpyEnabled();

        var messagePath = isSelf? "CommandSpy.Success" : "CommandSpy.SuccessOther";

        messagePath += isEnabled? ".Enabled" : ".Disabled";

        targetUser.setCommandSpyEnabled(isEnabled);
        targetUser.save();

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "CommandSpy.Success." + (isEnabled? "Enabled" : "Disabled"), null, label, null);
    }
}
