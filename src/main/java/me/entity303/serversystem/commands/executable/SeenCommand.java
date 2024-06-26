package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ITabExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static me.entity303.serversystem.commands.executable.OfflineEnderChestCommand.GetOfflinePlayers;

public class SeenCommand implements ITabExecutorOverload {
    protected final ServerSystem _plugin;
    //TODO: Seen Command, hopp hopp, Hutch meckert

    public SeenCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "seen")) {
            var permission = this._plugin.GetPermissions().GetPermission("seen");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Seen"));
            return true;
        }

        var target = Bukkit.getOfflinePlayer(arguments[0]);
        if (target.getLastPlayed() <= 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                       commandSender, target.getName(),
                                                                                                                       "Seen.PlayerNeverPlayed"));
            return true;
        }

        var lastPlayed = target.getLastPlayed();

        if (target.isOnline())
            lastPlayed = System.currentTimeMillis();

        var dtf = DateTimeFormatter.ofPattern(
                this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Seen.TimeFormat"));

        var date = Instant.ofEpochMilli(lastPlayed).atZone(ZoneId.systemDefault()).toLocalDateTime();
        var format = dtf.format(date);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                   target.getName(), "Seen.LastSeen")
                                                                                       .replace("<TIME>", format));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "seen", true))
            return Collections.singletonList("");

        return GetOfflinePlayers(arguments);
    }
}
