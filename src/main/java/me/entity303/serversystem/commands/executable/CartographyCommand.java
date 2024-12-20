package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "CartographyTable")
public class CartographyCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public CartographyCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        var shouldRegister = serverSystem.isRunningPaper();

        if (!shouldRegister) serverSystem.Warn("Looks like you're running Spigot! Due to recent changes, the cartography command requires Paper to be running!");

        return shouldRegister && serverSystem.GetVersionStuff().GetVirtualCartography() != null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "cartography")) {
            var permission = this._plugin.GetPermissions().GetPermission("cartography");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        this._plugin.GetVersionStuff().GetVirtualCartography().OpenCartography((Player) commandSender);
        return true;
    }
}
