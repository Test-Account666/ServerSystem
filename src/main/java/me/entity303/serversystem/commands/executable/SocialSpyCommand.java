package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Message;
import me.entity303.serversystem.utils.PermissionsChecker;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand extends CommandUtils implements ICommandExecutorOverload {

    public SocialSpyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        boolean result = false;
        Message messages = this._plugin.GetMessages();
        PermissionsChecker permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
        } else if (!permissions.HasPermission(commandSender, "socialspy")) {
            var permission = permissions.GetPermission("socialspy");
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
        } else {
            result = true;
        }

        if (result)
            return true;
        if (this._plugin.GetSocialSpy().contains(commandSender)) {
            this._plugin.GetSocialSpy().remove(commandSender);
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "SocialSpyToggle.Deactivated"));
            return true;
        }
        this._plugin.GetSocialSpy().add((Player) commandSender);
        
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "SocialSpyToggle.Activated"));
        return true;
    }
}
