package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.events.AsyncUnbanEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnBanCommand extends CommandUtils implements CommandExecutorOverload {

    public UnBanCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "unban")) {
            var permission = this.plugin.getPermissions().getPermission("unban");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "UnBan"));
            return true;
        }

        var target = UnBanCommand.getPlayer(arguments[0]);
        if (!this.getPlugin().getBanManager().isBanned(target.getUniqueId())) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "UnBan.NotBanned"));
            return true;
        }

        this.getPlugin().getBanManager().unBan(target.getUniqueId());

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "UnBan.Success"));
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            var asyncUnbanEvent = new AsyncUnbanEvent(commandSender, target);
            Bukkit.getPluginManager().callEvent(asyncUnbanEvent);
        });
        return true;
    }

    private static OfflinePlayer getPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }
}
