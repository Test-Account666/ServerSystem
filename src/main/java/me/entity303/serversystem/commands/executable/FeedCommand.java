package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends CommandUtils implements ICommandExecutorOverload {

    public FeedCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Feed"));
                return true;
            }
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "feed.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("feed.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            player.setFoodLevel(20);
            player.setExhaustion(0);
            player.setSaturation(20);

            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Feed.Self"));
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "feed.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("feed.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var target = this.GetPlayer(commandSender, arguments[0]);
        if (target == null)
            return true;

        target.setFoodLevel(20);
        target.setExhaustion(0);
        target.setSaturation(20);

        target.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Feed.Others.Target"));

        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Feed.Others.Sender"));
        return true;
    }

}
