package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.events.AsyncUnbanEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.UnBanTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@ServerSystemCommand(name = "UnBan", tabCompleter = UnBanTabCompleter.class)
public class UnBanCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public UnBanCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        return serverSystem.GetConfigReader().GetBoolean("banSystem.enabled");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "unban")) {
            var permission = this._plugin.GetPermissions().GetPermission("unban");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "UnBan"));
            return true;
        }

        var target = UnBanCommand.GetPlayer(arguments[0]);
        if (!this._plugin.GetBanManager().IsBanned(target.getUniqueId())) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "UnBan.NotBanned"));
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
