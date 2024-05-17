package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgToggleCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public MsgToggleCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var messages = this._plugin.GetMessages();
        var permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return true;
        }

        if (!permissions.HasPermission(player, "msgtoggle")) {
            var permission = permissions.GetPermission("msgtoggle");
            player.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            return true;
        }

        if (this._plugin.GetMsgOff().contains(player)) {
            this._plugin.GetMsgOff().remove(player);

            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "MsgToggle.Activated"));
            return true;
        }

        this._plugin.GetMsgOff().add(player);

        player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                           this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "MsgToggle.Deactivated"));
        return true;
    }
}
