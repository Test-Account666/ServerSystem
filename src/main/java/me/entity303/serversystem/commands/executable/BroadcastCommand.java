package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public BroadcastCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "broadcast")) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("broadcast")));
            return true;
        }
        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "BroadCast"));
            return true;
        }

        var builder = new StringBuilder();
        for (var argument : arguments)
            builder.append(argument).append(" ");

        Bukkit.broadcastMessage(this._plugin.GetMessages()
                                           .GetMessage(commandLabel, command.getName(), commandSender, null, "Broadcast")
                                           .replace("<MESSAGE>", ChatColor.TranslateAlternateColorCodes('&', builder.toString())));
        return true;
    }
}
