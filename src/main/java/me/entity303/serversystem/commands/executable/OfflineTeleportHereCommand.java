package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ITabExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

import static me.entity303.serversystem.commands.executable.OfflineEnderChestCommand.GetOfflinePlayers;

public class OfflineTeleportHereCommand implements ITabExecutorOverload {

    protected final ServerSystem _plugin;

    public OfflineTeleportHereCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "offlineteleporthere")) {
            var permission = this._plugin.GetPermissions().GetPermission("offlineteleporthere");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "OfflineTeleportHere"));
            return true;
        }

        var offlineTarget = Bukkit.getOfflinePlayer(arguments[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            var name = offlineTarget.getName();
            if (name == null)
                name = arguments[0];
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                       commandSender, name,
                                                                                                                       "OfflineTeleportHere.NeverPlayed"));
            return true;
        }

        if (offlineTarget.isOnline()) {
            if (CommandUtils.GetPlayer(this._plugin, commandSender, offlineTarget.getUniqueId()) == null) {

                var player = offlineTarget.getPlayer();
                player.teleport(((Entity) commandSender).getLocation());

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender,
                                                                                                           offlineTarget.getPlayer(),
                                                                                                           "OfflineTeleportHere.Success"));
                return true;
            }
            CommandSender target = offlineTarget.getPlayer();
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, target,
                                                                                                       "OfflineTeleportHere.PlayerIsOnline"));
            return true;
        }

        var player = CommandUtils.GetHookedPlayer(this._plugin, offlineTarget);

        this._plugin.GetVersionStuff().GetTeleport().Teleport(player, ((Player) commandSender).getLocation());

        player.saveData();
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "OfflineTeleportHere.Success"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "offlineteleporthere", true))
            return Collections.singletonList("");

        return GetOfflinePlayers(arguments);
    }
}
