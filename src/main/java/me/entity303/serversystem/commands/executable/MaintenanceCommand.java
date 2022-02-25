package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaintenanceCommand extends MessageUtils implements CommandExecutor {

    public MaintenanceCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "maintenance.toggle")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("maintenance.toggle")));
            return true;
        }
        this.plugin.setMaintenance(!this.plugin.isMaintenance());
        if (this.plugin.isMaintenance())
            cs.sendMessage(this.getPrefix() + this.getMessage("Maintenance.Activated", label, cmd.getName(), cs, null));
        else
            cs.sendMessage(this.getPrefix() + this.getMessage("Maintenance.Deactivated", label, cmd.getName(), cs, null));

        for (Player player : Bukkit.getOnlinePlayers())
            if (!this.isAllowed(player, "maintenance.join", true))
                player.kickPlayer(this.getMessage("Maintenance.Kick", label, cmd.getName(), cs, player));
        return true;
    }
}
