package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "SmithingTable")
public class SmithingTableCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public SmithingTableCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        var shouldRegister = serverSystem.isRunningPaper();

        if (!shouldRegister) serverSystem.Warn("Looks like you're running Spigot! Due to recent changes, the smithing command requires Paper to be running!");

        return shouldRegister && serverSystem.GetVersionStuff().GetVirtualSmithing() != null;
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
