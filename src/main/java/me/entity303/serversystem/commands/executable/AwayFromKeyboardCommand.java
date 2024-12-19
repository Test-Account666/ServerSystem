package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.listener.AwayFromKeyboardListener;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "AFK")
public class AwayFromKeyboardCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public AwayFromKeyboardCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var pluginMessages = this._plugin.GetMessages();
        var prefix = pluginMessages.GetPrefix();
        if (!(commandSender instanceof Player player)) {
            var onlyPlayerMessage = pluginMessages.GetOnlyPlayer();
            commandSender.sendMessage(prefix + onlyPlayerMessage);
            return true;
        }

        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.afk.required")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "afk.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("afk.permission");
                var noPermissionMessage = pluginMessages.GetNoPermission(permission);
                commandSender.sendMessage(prefix + noPermissionMessage);
                return true;
            }
        }

        var awayFromKeyboard = false;

        if (player.hasMetadata("afk")) awayFromKeyboard = CommandUtils.IsAwayFromKeyboard(player);

        var metaValueGenerator = this._plugin.GetMetaValue();
        if (!awayFromKeyboard) {
            player.removeMetadata("afk", this._plugin);
            var metaValue = metaValueGenerator.GetMetaValue(true);
            player.setMetadata("afk", metaValue);


            var message = pluginMessages.GetMessage(commandLabel, command, commandSender, null, AwayFromKeyboardListener.AFK_ENABLED);
            commandSender.sendMessage(prefix + message);
            return true;
        }

        player.removeMetadata("afk", this._plugin);
        var metaValue = metaValueGenerator.GetMetaValue(false);
        player.setMetadata("afk", metaValue);

        var message = pluginMessages.GetMessage(commandLabel, command, commandSender, null, AwayFromKeyboardListener.AFK_DISABLED);
        commandSender.sendMessage(prefix + message);
        return true;
    }
}
