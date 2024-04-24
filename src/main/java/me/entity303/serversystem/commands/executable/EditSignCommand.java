package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditSignCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public EditSignCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetVersionStuff().GetSignEdit() == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "EditSign.NotAvailable"));
            return true;
        }
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(player, "editschild.players")) {
            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("editschild.players")));
            return true;
        }
        var block = player.getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign sign)) {
            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "EditSign.SignNeeded"));
            return true;
        }

        this._plugin.GetVersionStuff().GetSignEdit().EditSign(player, sign);
        return true;
    }
}
