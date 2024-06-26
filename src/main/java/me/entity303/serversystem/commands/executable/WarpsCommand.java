package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarpsCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public WarpsCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.warps.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "warps.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("warps.permission");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

        var warpBuilder = new StringBuilder();
        var separator = this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Warps.Format.Separator");
        var warpFormat = this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Warps.Format.Format");

        if (this._plugin.GetWarpManager().GetWarps().isEmpty()) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
            return true;
        }

        var warps = this._plugin.GetWarpManager().GetWarps();

        for (var warp : warps)
            warpBuilder.append(warpFormat.replace("<SEPERATOR>", separator).replace("<Warp>", warp));

        if (warpBuilder.toString().toLowerCase().startsWith(separator))
            warpBuilder.delete(0, separator.length());

        var warpMessage = this._plugin.GetMessages()
                                     .GetMessage(commandLabel, command.getName(), commandSender, null, "Warps.Format.Message")
                                     .replace("<AMOUNT>", String.valueOf(warps.size()))
                                     .replace("<WARPS>", warpBuilder.toString());

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + warpMessage);
        return true;
    }
}
