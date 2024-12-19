package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "TeleportAccept")
public class TeleportRequestAcceptCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public TeleportRequestAcceptCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.tpaccept.required")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tpaccept.permission")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("tpaccept.permission")));
                return true;
            }
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetTpaDataMap().containsKey(commandSender)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "TpAccept.NoTpa"));
            return true;
        }

        var tpaData = this._plugin.GetTpaDataMap().get(commandSender);

        if (tpaData.GetEnd() <= System.currentTimeMillis()) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "TpAccept.NoTpa"));
            return true;
        }

        if (!tpaData.GetSender().isOnline()) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                       tpaData.GetSender().getName(),
                                                                                                                       "TpAccept.AlreadyOffline"));
            return true;
        }

        if (!tpaData.IsTpahere()) {
            if (!this._plugin.GetConfigReader().GetBoolean("teleportation.tpa.enableDelay") ||
                this._plugin.GetPermissions().HasPermission(tpaData.GetSender().getPlayer(), "tpaccept.bypassdelay", true)) {
                var player = tpaData.GetSender().getPlayer();
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                           tpaData.GetSender().getName(),
                                                                                                                           "TpAccept.Sender"));
                tpaData.GetSender()
                       .getPlayer()
                       .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                     tpaData.GetSender().getName(), "TpAccept.Target"));

                player.teleport(((Entity) commandSender).getLocation());

                player.sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&',
                                                                                                                   TeleportRequestAcceptCommand.this._plugin.GetMessages()
                                                                                                                                                            .GetConfiguration()
                                                                                                                                                            .GetString(
                                                                                                                                                                    "Messages.Misc.Teleportation.Success")));
                this._plugin.GetTpaDataMap().remove(commandSender);
                return true;
            }
            this._plugin.GetTeleportMap().put(tpaData.GetSender().getPlayer(), Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                OfflinePlayer player = tpaData.GetSender().getPlayer();
                if (player.isOnline() && ((OfflinePlayer) commandSender).isOnline()) {

                    var player1 = player.getPlayer();
                    player1.teleport(((Entity) commandSender).getLocation());

                    player.getPlayer()
                          .sendMessage(TeleportRequestAcceptCommand.this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&',
                                                                                                                                                    TeleportRequestAcceptCommand.this._plugin.GetMessages()
                                                                                                                                                                                             .GetConfiguration()
                                                                                                                                                                                             .GetString(
                                                                                                                                                                                                     "Messages.Misc.Teleportation.Success")));
                    TeleportRequestAcceptCommand.this._plugin.GetTeleportMap().remove(player);
                    TeleportRequestAcceptCommand.this._plugin.GetTpaDataMap().remove(commandSender);
                }
            }, 20L * this._plugin.GetConfigReader().GetInt("teleportation.tpa.delay")));
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                       tpaData.GetSender().getName(), "TpAccept.Sender"));
            tpaData.GetSender()
                   .getPlayer()
                   .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                 tpaData.GetSender().getName(), "TpAccept.Target"));
            tpaData.GetSender()
                   .getPlayer()
                   .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                 tpaData.GetSender().getName(), "TpAccept.Teleporting"));
        } else {
            if (!this._plugin.GetConfigReader().GetBoolean("teleportation.tpa.enableDelay") ||
                this._plugin.GetPermissions().HasPermission(commandSender, "tpaccept.bypassdelay", true)) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                           tpaData.GetSender().getName(),
                                                                                                                           "TpAccept.Sender"));
                tpaData.GetSender()
                       .getPlayer()
                       .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                     tpaData.GetSender().getName(), "TpAccept.Target"));
                var player = ((Player) commandSender);

                var player1 = player.getPlayer();
                Entity entity = tpaData.GetSender().getPlayer();
                player1.teleport(entity.getLocation());

                player.getPlayer()
                      .sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&',
                                                                                                                   TeleportRequestAcceptCommand.this._plugin.GetMessages()
                                                                                                                                                            .GetConfiguration()
                                                                                                                                                            .GetString(
                                                                                                                                                                    "Messages.Misc.Teleportation.Success")));
                this._plugin.GetTpaDataMap().remove(commandSender);
                return true;
            }
            this._plugin.GetTeleportMap().put(((Player) commandSender), Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                OfflinePlayer player = ((OfflinePlayer) commandSender).getPlayer();
                if (player.isOnline() && tpaData.GetSender().isOnline()) {

                    var player1 = player.getPlayer();
                    Entity entity = tpaData.GetSender().getPlayer();
                    player1.teleport(entity.getLocation());

                    player.getPlayer()
                          .sendMessage(TeleportRequestAcceptCommand.this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&',
                                                                                                                                                    TeleportRequestAcceptCommand.this._plugin.GetMessages()
                                                                                                                                                                                             .GetConfiguration()
                                                                                                                                                                                             .GetString(
                                                                                                                                                                                                     "Messages.Misc.Teleportation.Success")));
                    TeleportRequestAcceptCommand.this._plugin.GetTeleportMap().remove(player);
                    TeleportRequestAcceptCommand.this._plugin.GetTpaDataMap().remove(commandSender);
                }
            }, 20L * this._plugin.GetConfigReader().GetInt("teleportation.tpa.delay")));
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                       tpaData.GetSender().getName(), "TpAccept.Sender"));
            tpaData.GetSender()
                   .getPlayer()
                   .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                 tpaData.GetSender().getName(), "TpAccept.Target"));
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                       tpaData.GetSender().getName(),
                                                                                                                       "TpAccept.Teleporting"));
        }
        return true;
    }
}
