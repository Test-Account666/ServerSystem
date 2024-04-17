package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public BroadcastCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "broadcast")) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("broadcast")));
            return true;
        }
        if (arguments.length == 0) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "BroadCast"));
            return true;
        }

        var builder = new StringBuilder();
        for (var argument : arguments)
            builder.append(argument).append(" ");

        Bukkit.broadcastMessage(this.plugin.getMessages()
                                           .getMessage(commandLabel, command.getName(), commandSender, null, "Broadcast")
                                           .replace("<MESSAGE>", ChatColor.translateAlternateColorCodes('&', builder.toString())));
        return true;
    }
}
