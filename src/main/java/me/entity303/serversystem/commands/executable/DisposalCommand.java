package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@ServerSystemCommand(name = "Disposal")
public class DisposalCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public DisposalCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (this._plugin.GetMessages().GetConfiguration().GetBoolean("Permissions.disposal.required")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "disposal.permission")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("disposal.permission")));
                return true;
            }
        }

        var disposal = Bukkit.getServer()
                             .createInventory(null, 54, this._plugin.GetMessages().GetMiscMessage(commandLabel, command.getName(), commandSender, null, "DisposalName"));

        player.openInventory(disposal);
        return true;
    }
}
