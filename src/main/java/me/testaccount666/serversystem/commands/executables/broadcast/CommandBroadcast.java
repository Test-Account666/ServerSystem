package me.testaccount666.serversystem.commands.executables.broadcast;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "broadcast")
public class CommandBroadcast extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Broadcast.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var broadcast = ChatColor.translateColor(String.join(" ", arguments).trim());
        var messageFormatOptional = command("Broadcast.Format", commandSender)
                .target("*").prefix(false).send(false)
                .modifier(message -> message.replace("<BROADCAST>", broadcast)).build();

        if (messageFormatOptional.isEmpty()) {
            general("ErrorOccurred", commandSender).label(label).target("*").build();
            return;
        }
        var messageFormat = messageFormatOptional.get();

        Bukkit.broadcastMessage(messageFormat);
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Broadcast.Use", false);
    }
}
