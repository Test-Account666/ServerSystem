package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Message;
import me.entity303.serversystem.utils.PermissionsChecker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public SocialSpyCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Message messages = this._plugin.GetMessages();
        PermissionsChecker permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return true;
        }

        if (!permissions.HasPermission(player, "socialspy")) {
            var permission = permissions.GetPermission("socialspy");
            player.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            return true;
        }

        if (this._plugin.GetSocialSpy().contains(player)) {
            this._plugin.GetSocialSpy().remove(player);
            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "SocialSpyToggle.Deactivated"));
            return true;
        }

        this._plugin.GetSocialSpy().add(player);

        player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                           this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "SocialSpyToggle.Activated"));
        return true;
    }
}
