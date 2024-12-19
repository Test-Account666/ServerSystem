package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "StoneCutter")
public class StoneCutterCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public StoneCutterCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        return serverSystem.GetVersionStuff().GetVirtualStoneCutter() != null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "stonecutter")) {
            var permission = this._plugin.GetPermissions().GetPermission("stonecutter");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        this._plugin.GetVersionStuff().GetVirtualStoneCutter().OpenStoneCutter((Player) commandSender);
        return true;
    }
}
