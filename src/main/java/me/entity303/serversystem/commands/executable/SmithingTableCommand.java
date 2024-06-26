package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmithingTableCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public SmithingTableCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "smithingtable")) {
            var permission = this._plugin.GetPermissions().GetPermission("smithingtable");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        this._plugin.GetVersionStuff().GetVirtualSmithing().OpenSmithingTable((Player) commandSender);
        return true;
    }
}
