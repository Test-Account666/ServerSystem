package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class MaintenanceCommand extends CommandUtils implements CommandExecutorOverload {

    public MaintenanceCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "maintenance.toggle")) {
            var permission = this.plugin.getPermissions().getPermission("maintenance.toggle");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        this.plugin.setMaintenance(!this.plugin.isMaintenance());

        if (!this.plugin.isMaintenance()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Maintenance.Deactivated"));
            return true;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Maintenance.Activated"));

        Bukkit.getOnlinePlayers()
              .stream()
              .filter(player -> !this.plugin.getPermissions().hasPermission(player, "maintenance.join", true))
              .forEach(player -> player.kickPlayer(this.plugin.getMessages().getMessage(commandLabel, command, commandSender, player, "Maintenance.Kick")));

        return true;
    }
}
