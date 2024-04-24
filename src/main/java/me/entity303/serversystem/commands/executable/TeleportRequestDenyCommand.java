package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportRequestDenyCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public TeleportRequestDenyCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.tpdeny.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tpdeny.permission")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("tpdeny.permission")));
                return true;
            }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetTpaDataMap().containsKey(commandSender)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "TpDeny.NoTpa"));
            return true;
        }

        var tpaData = this._plugin.GetTpaDataMap().get(commandSender);

        if (tpaData.GetEnd() <= System.currentTimeMillis()) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "TpDeny.NoTpa"));
            return true;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.GetSender().getName(), "TpDeny.Sender"));
        if (tpaData.GetSender().isOnline())
            tpaData.GetSender()
                   .getPlayer()
                   .sendMessage(this._plugin.GetMessages().GetPrefix() +
                                this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.GetSender().getName(), "TpDeny.Target"));
        this._plugin.GetTpaDataMap().remove(commandSender);
        return true;
    }
}
