package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.events.AsyncUnmuteEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.UnMuteTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@ServerSystemCommand(name = "UnMute", tabCompleter = UnMuteTabCompleter.class)
public class UnMuteCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public UnMuteCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        return serverSystem.GetConfigReader().GetBoolean("banSystem.enabled");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "unmute")) {
            var permission = this._plugin.GetPermissions().GetPermission("unmute");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "UnMute"));
            return true;
        }
        var target = this.GetPlayer(arguments[0]);

        if (!this._plugin.GetMuteManager().IsMuted(target)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "UnMute.NotMuted"));
            return true;
        }
        this._plugin.GetMuteManager().RemoveMute(target.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            var asyncUnmuteEvent = new AsyncUnmuteEvent(commandSender, target);
            Bukkit.getPluginManager().callEvent(asyncUnmuteEvent);
        });


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "UnMute.Success"));
        return true;
    }

    private OfflinePlayer GetPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (player == null) player = Bukkit.getOfflinePlayer(name);
        return player;
    }
}
