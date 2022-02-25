package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public WorkbenchCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix()
                    + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (!this.plugin.getPermissions().hasPerm(cs, "workbench")) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("workbench")));
            return true;
        }
        ((Player) cs).openWorkbench(((Player) cs).getLocation(), true);
        return true;
    }
}
