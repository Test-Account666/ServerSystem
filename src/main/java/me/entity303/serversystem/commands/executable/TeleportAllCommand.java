package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "TeleportAll")
public class TeleportAllCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public TeleportAllCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "TpAll"));
                return true;
            }
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tpall.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("tpall.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            Bukkit.getOnlinePlayers().forEach(all -> all.teleport(((Entity) commandSender).getLocation()));

            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "TpAll.Self"));
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "tpall.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("tpall.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        Bukkit.getOnlinePlayers().forEach(all -> all.teleport(target.getLocation()));
        var command1 = command.getName();
        target.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command1, commandSender, target, "TpAll.Self"));

        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "TpAll.Others"));
        return true;
    }
}
