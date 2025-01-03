package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@ServerSystemCommand(name = "Maintenance")
public class MaintenanceCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public MaintenanceCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "maintenance.toggle")) {
            var permission = this._plugin.GetPermissions().GetPermission("maintenance.toggle");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        this._plugin.SetMaintenance(!this._plugin.IsMaintenance());

        if (!this._plugin.IsMaintenance()) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Maintenance.Deactivated"));
            return true;
        }

        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Maintenance.Activated"));

        Bukkit.getOnlinePlayers()
              .stream()
              .filter(player -> !this._plugin.GetPermissions().HasPermission(player, "maintenance.join", true))
              .forEach(player -> player.kickPlayer(this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "Maintenance.Kick")));

        return true;
    }
}
