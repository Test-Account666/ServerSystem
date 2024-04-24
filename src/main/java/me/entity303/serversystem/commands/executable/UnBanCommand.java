package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.util.CommandManager;
import me.entity303.serversystem.events.AsyncUnbanEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnBanCommand extends CommandUtils implements ICommandExecutorOverload {

    public UnBanCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, CommandManager.UNBAN)) {
            var permission = this._plugin.GetPermissions().GetPermission(CommandManager.UNBAN);
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "UnBan"));
            return true;
        }

        var target = UnBanCommand.GetPlayer(arguments[0]);
        if (!this._plugin.GetBanManager().IsBanned(target.getUniqueId())) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "UnBan.NotBanned"));
            return true;
        }

        this._plugin.GetBanManager().UnBan(target.getUniqueId());

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "UnBan.Success"));
        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            var asyncUnbanEvent = new AsyncUnbanEvent(commandSender, target);
            Bukkit.getPluginManager().callEvent(asyncUnbanEvent);
        });
        return true;
    }

    private static OfflinePlayer GetPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private ServerSystem GetPlugin() {
        return this._plugin;
    }
}
