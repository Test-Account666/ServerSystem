package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public WorkbenchCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "workbench")) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("workbench")));
            return true;
        }

        ((Player) commandSender).openWorkbench(((Player) commandSender).getLocation(), true);
        return true;
    }
}
