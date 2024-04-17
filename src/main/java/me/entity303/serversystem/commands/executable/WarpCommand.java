package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WarpCommand extends CommandUtils implements CommandExecutorOverload {

    public WarpCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.warp.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "warp.permission")) {
                var permission = this.plugin.getPermissions().getPermission("warp.permission");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Warp"));
            return true;
        }

        var name = arguments[0].toLowerCase();
        var warpManager = this.plugin.getWarpManager();
        if (!warpManager.doesWarpExist(name)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, null,
                                                                                                     "Warp.WarpDoesntExists")
                                                                                         .replace("<WARP>", name.toUpperCase()));
            return true;
        }

        if (!(commandSender instanceof Player) || arguments.length >= 2) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "warp.others")) {
                var permission = this.plugin.getPermissions().getPermission("warp.others");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

            if (arguments.length < 2) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Warp"));
                return true;
            }

            var targetPlayer = this.getPlayer(commandSender, arguments[1]);
            if (targetPlayer == null) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[1]));
                return true;
            }

            var location = warpManager.getWarp(name);

            Teleport.teleport(targetPlayer, location);

            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                        .getMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                    "Warp.Others.Teleporting.Target")
                                                                                        .replace("<WARP>", name.toUpperCase()));

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, targetPlayer,
                                                                                                     "Warp.Others.Teleporting.Sender")
                                                                                         .replace("<WARP>", name.toUpperCase()));
            return true;
        }

        if (this.plugin.getConfigReader().getBoolean("teleportation.warp.enableDelay"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "warp.bypassdelay", true)) {
                this.plugin.getTeleportMap().put(((Player) commandSender), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    OfflinePlayer player = ((OfflinePlayer) commandSender).getPlayer();
                    assert player != null;
                    if (player.isOnline()) {
                        var location = warpManager.getWarp(name);

                        Teleport.teleport(Objects.requireNonNull(player.getPlayer()), location);

                        commandSender.sendMessage(WarpCommand.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&',
                                                                                                                                             WarpCommand.this.plugin.getMessages()
                                                                                                                                                                    .getCfg()
                                                                                                                                                                    .getString(
                                                                                                                                                                            "Messages.Misc.Teleportation.Success")));
                        WarpCommand.this.plugin.getTeleportMap().remove(player);
                    }
                }, 20L * this.plugin.getConfigReader().getInt("teleportation.warp.delay")));

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Warp.Teleporting")
                                                                                             .replace("<WARP>", name.toUpperCase()));
                return true;
            }

        var location = warpManager.getWarp(name);

        Teleport.teleport((Player) commandSender, location);


        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, null,
                                                                                                 "Warp.InstantTeleporting")
                                                                                     .replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
