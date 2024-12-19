package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "Workbench")
public class WorkbenchCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public WorkbenchCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "workbench")) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("workbench")));
            return true;
        }

        ((Player) commandSender).openWorkbench(((Player) commandSender).getLocation(), true);
        return true;
    }
}
