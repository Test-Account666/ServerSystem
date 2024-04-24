package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements ICommandExecutorOverload {
    public static final String BACK_REASON_TELEPORT = "Teleport";
    public static final String BACK_REASON_DEATH = "Death";
    private final ServerSystem _plugin;

    public BackCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var pluginMessages = this._plugin.GetMessages();
        var prefix = pluginMessages.GetPrefix();
        if (!(commandSender instanceof Player)) {
            var onlyPlayerMessage = pluginMessages.GetOnlyPlayer();
            commandSender.sendMessage(prefix + onlyPlayerMessage);
            return true;
        }

        var commandName = command.getName();
        if (!this._plugin.GetBackReason().containsKey(commandSender)) {
            var message = pluginMessages.GetMessage(commandLabel, commandName, commandSender, null, "Back.NoBack");
            commandSender.sendMessage(prefix + message);
            return true;
        }

        var reason = this._plugin.GetBackReason().get(commandSender);
        if (this.Teleport(commandSender, commandLabel, pluginMessages, prefix, commandName, reason, BACK_REASON_TELEPORT, "back.teleport", "Back.Success.Teleport"))
            return true;

        this.Teleport(commandSender, commandLabel, pluginMessages, prefix, commandName, reason, BACK_REASON_DEATH, "back.death", "Back.Success.Death");
        return true;
    }

    private boolean Teleport(CommandSender commandSender, String commandLabel, Message pluginMessages, String prefix, String commandName, String reason,
                             String teleport, String permissionAction, String messageAction) {
        if (teleport.equalsIgnoreCase(reason)) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, permissionAction)) {
                var permission = this._plugin.GetPermissions().GetPermission(permissionAction);
                var noPermissionMessage = pluginMessages.GetNoPermission(permission);
                commandSender.sendMessage(prefix + noPermissionMessage);
                return true;
            }

            var backLocation = this._plugin.GetBackloc().get(commandSender);
            ((Player) commandSender).teleport(backLocation);

            var message = pluginMessages.GetMessage(commandLabel, commandName, commandSender, null, messageAction);
            commandSender.sendMessage(prefix + message);
            return true;
        }
        return false;
    }
}
