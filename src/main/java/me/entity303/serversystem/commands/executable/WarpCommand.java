package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.WarpTabCompleter;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

@ServerSystemCommand(name = "Warp", tabCompleter = WarpTabCompleter.class)
public class WarpCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public WarpCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.warp.required")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "warp.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("warp.permission");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Warp"));
            return true;
        }

        var name = arguments[0].toLowerCase();
        var warpManager = this._plugin.GetWarpManager();
        if (!warpManager.DoesWarpExist(name)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null,
                                                                                                       "Warp.WarpDoesntExists")
                                                                                           .replace("<WARP>", name.toUpperCase()));
            return true;
        }

        if (!(commandSender instanceof Player) || arguments.length >= 2) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "warp.others")) {
                var permission = this._plugin.GetPermissions().GetPermission("warp.others");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            if (arguments.length < 2) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Warp"));
                return true;
            }

            var targetPlayer = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[1]);
            if (targetPlayer == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[1]));
                return true;
            }

            var location = warpManager.GetWarp(name);

            targetPlayer.teleport(location);

            targetPlayer.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                          .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                      "Warp.Others.Teleporting.Target")
                                                                                          .replace("<WARP>", name.toUpperCase()));

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, targetPlayer,
                                                                                                       "Warp.Others.Teleporting.Sender")
                                                                                           .replace("<WARP>", name.toUpperCase()));
            return true;
        }

        if (this._plugin.GetConfigReader().GetBoolean("teleportation.warp.enableDelay")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "warp.bypassdelay", true)) {
                this._plugin.GetTeleportMap().put(((Player) commandSender), Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                    OfflinePlayer player = ((OfflinePlayer) commandSender).getPlayer();
                    assert player != null;
                    if (player.isOnline()) {
                        var location = warpManager.GetWarp(name);

                        var player1 = Objects.requireNonNull(player.getPlayer());
                        player1.teleport(location);

                        commandSender.sendMessage(WarpCommand.this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&',
                                                                                                                                              WarpCommand.this._plugin.GetMessages()
                                                                                                                                                                      .GetConfiguration()
                                                                                                                                                                      .GetString(
                                                                                                                                                                              "Messages.Misc.Teleportation.Success")));
                        WarpCommand.this._plugin.GetTeleportMap().remove(player);
                    }
                }, 20L * this._plugin.GetConfigReader().GetInt("teleportation.warp.delay")));

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, null, "Warp.Teleporting")
                                                                                               .replace("<WARP>", name.toUpperCase()));
                return true;
            }
        }

        var location = warpManager.GetWarp(name);

        ((Player) commandSender).teleport(location);


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessage(commandLabel, command, commandSender, null, "Warp.InstantTeleporting")
                                                                                       .replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
