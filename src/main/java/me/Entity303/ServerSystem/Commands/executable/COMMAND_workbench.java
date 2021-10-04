package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_workbench implements CommandExecutor {
    private final ss plugin;

    public COMMAND_workbench(ss plugin) {
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
