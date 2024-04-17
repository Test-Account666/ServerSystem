package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.DummyCommandSender;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

import static me.entity303.serversystem.commands.executable.OfflineEnderChestCommand.GetOfflinePlayers;

public class OfflineTeleportCommand extends CommandUtils implements CommandExecutorOverload, TabCompleter {

    public OfflineTeleportCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "offlineteleport")) {
            var permission = this.plugin.getPermissions().getPermission("offlineteleport");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "OfflineTeleport"));
            return true;
        }

        var offlineTarget = Bukkit.getOfflinePlayer(arguments[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            var name = offlineTarget.getName();
            if (name == null)
                name = arguments[0];
            CommandSender target = new DummyCommandSender(name);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "OfflineTeleport.NeverPlayed"));
            return true;
        }

        if (offlineTarget.isOnline()) {
            if (this.getPlayer(commandSender, offlineTarget.getUniqueId()) == null) {

                Teleport.teleport((Player) commandSender, offlineTarget.getPlayer());

                CommandSender target = offlineTarget.getPlayer();
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "OfflineTeleport.Success"));
                return true;
            }
            CommandSender target = offlineTarget.getPlayer();
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "OfflineTeleport.PlayerIsOnline"));
            return true;
        }

        var player = this.getHookedPlayer(offlineTarget);

        var location = player.getLocation();

        Teleport.teleport((Player) commandSender, location);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, player, "OfflineTeleport.Success"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "offlineteleport", true))
            return Collections.singletonList("");

        return GetOfflinePlayers(arguments);
    }
}
