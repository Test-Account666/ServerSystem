package me.testaccount666.serversystem.commands.executables.broadcast;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

@ServerSystemCommand(name = "broadcast")
public class CommandBroadcast extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Broadcast.Use", label)) return;

        if (arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var broadcast = new StringBuilder();

        for (var argument : arguments) broadcast.append(argument).append(" ");

        var messageFormatOptional = MessageManager.getCommandMessage(commandSender, "Broadcast.Format", "*", label, false);

        if (messageFormatOptional.isEmpty()) {
            sendGeneralMessage(commandSender, "ErrorOccurred", "*", label, null);
            return;
        }

        var messageFormat = messageFormatOptional.get();

        messageFormat = messageFormat.replace("<BROADCAST>", ChatColor.translateColorCodes(broadcast.toString().trim()));

        Bukkit.broadcastMessage(messageFormat);
    }
}
