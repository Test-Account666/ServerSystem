package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

@ServerSystemCommand(name = "KickAll")
public class KickAllCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public KickAllCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        return serverSystem.GetConfigReader().GetBoolean("banSystem.enabled");
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "kickall")) {
            var permission = this._plugin.GetPermissions().GetPermission("kickall");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var command1 = command.getName();
        var reason = this._plugin.GetMessages().GetMessage(commandLabel, command1, commandSender, null, "KickAll.DefaultReason");

        if (arguments.length > 0) reason = Arrays.stream(arguments).map(arg -> arg + " ").collect(Collectors.joining());

        for (var all : Bukkit.getOnlinePlayers()) {
            if (all == commandSender) continue;

            all.kickPlayer(this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "KickAll.Kick").replace("<REASON>", reason));
        }


        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "KickAll.Success"));
        return true;
    }
}
