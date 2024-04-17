package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.events.AsyncUnmuteEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnMuteCommand extends CommandUtils implements CommandExecutorOverload {

    public UnMuteCommand(ServerSystem plugin) {
        super(plugin);
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "unmute")) {
            var permission = this.plugin.getPermissions().getPermission("unmute");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "UnMute"));
            return true;
        }
        var target = this.getPlayer(arguments[0]);
        if (!this.plugin.getMuteManager().isMuted(target)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "UnMute.NotMuted"));
            return true;
        }
        this.plugin.getMuteManager().removeMute(target.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            var asyncUnmuteEvent = new AsyncUnmuteEvent(commandSender, target);
            Bukkit.getPluginManager().callEvent(asyncUnmuteEvent);
        });


        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "UnMute.Success"));
        return true;
    }

    private OfflinePlayer getPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (!this.plugin.getMuteManager().isMuted(player))
            player = null;
        if (player == null)
            player = Bukkit.getOfflinePlayer(name);
        return player;
    }
}
