package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportRequestAcceptCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public TeleportRequestAcceptCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.tpaccept.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tpaccept.permission")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("tpaccept.permission")));
                return true;
            }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getTpaDataMap().containsKey(commandSender)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "TpAccept.NoTpa"));
            return true;
        }

        var tpaData = this.plugin.getTpaDataMap().get(commandSender);

        if (tpaData.getEnd() <= System.currentTimeMillis()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "TpAccept.NoTpa"));
            return true;
        }

        if (!tpaData.getSender().isOnline()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command.getName(),
                                                                                                                     commandSender, tpaData.getSender().getName(),
                                                                                                                     "TpAccept.AlreadyOffline"));
            return true;
        }

        if (!tpaData.isTpahere()) {
            if (!this.plugin.getConfigReader().getBoolean("teleportation.tpa.enableDelay") ||
                this.plugin.getPermissions().hasPermission(tpaData.getSender().getPlayer(), "tpaccept.bypassdelay", true)) {
                var player = tpaData.getSender().getPlayer();
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpAccept.Sender"));
                tpaData.getSender()
                       .getPlayer()
                       .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                       .getMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                   tpaData.getSender().getName(),
                                                                                                                   "TpAccept.Target"));

                Teleport.teleport(player, (Player) commandSender);

                player.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&',
                                                                                                                  TeleportRequestAcceptCommand.this.plugin.getMessages()
                                                                                                                                                          .getCfg()
                                                                                                                                                          .getString(
                                                                                                                                                                  "Messages.Misc.Teleportation.Success")));
                this.plugin.getTpaDataMap().remove(commandSender);
                return true;
            }
            this.plugin.getTeleportMap().put(tpaData.getSender().getPlayer(), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                OfflinePlayer player = tpaData.getSender().getPlayer();
                if (player.isOnline() && ((OfflinePlayer) commandSender).isOnline()) {

                    Teleport.teleport(player.getPlayer(), (Player) commandSender);

                    player.getPlayer()
                          .sendMessage(TeleportRequestAcceptCommand.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&',
                                                                                                                                                   TeleportRequestAcceptCommand.this.plugin.getMessages()
                                                                                                                                                                                           .getCfg()
                                                                                                                                                                                           .getString(
                                                                                                                                                                                                   "Messages.Misc.Teleportation.Success")));
                    TeleportRequestAcceptCommand.this.plugin.getTeleportMap().remove(player);
                    TeleportRequestAcceptCommand.this.plugin.getTpaDataMap().remove(commandSender);
                }
            }, 20L * this.plugin.getConfigReader().getInt("teleportation.tpa.delay")));
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpAccept.Sender"));
            tpaData.getSender()
                   .getPlayer()
                   .sendMessage(this.plugin.getMessages().getPrefix() +
                                this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpAccept.Target"));
            tpaData.getSender()
                   .getPlayer()
                   .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                   .getMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                               tpaData.getSender().getName(),
                                                                                                               "TpAccept.Teleporting"));
        } else {
            if (!this.plugin.getConfigReader().getBoolean("teleportation.tpa.enableDelay") ||
                this.plugin.getPermissions().hasPermission(commandSender, "tpaccept.bypassdelay", true)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpAccept.Sender"));
                tpaData.getSender()
                       .getPlayer()
                       .sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                       .getMessageWithStringTarget(commandLabel, command.getName(), commandSender,
                                                                                                                   tpaData.getSender().getName(),
                                                                                                                   "TpAccept.Target"));
                var player = ((Player) commandSender);

                Teleport.teleport(player.getPlayer(), tpaData.getSender().getPlayer());

                player.getPlayer()
                      .sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&',
                                                                                                                  TeleportRequestAcceptCommand.this.plugin.getMessages()
                                                                                                                                                          .getCfg()
                                                                                                                                                          .getString(
                                                                                                                                                                  "Messages.Misc.Teleportation.Success")));
                this.plugin.getTpaDataMap().remove(commandSender);
                return true;
            }
            this.plugin.getTeleportMap().put(((Player) commandSender), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                OfflinePlayer player = ((OfflinePlayer) commandSender).getPlayer();
                if (player.isOnline() && tpaData.getSender().isOnline()) {

                    Teleport.teleport(player.getPlayer(), tpaData.getSender().getPlayer());

                    player.getPlayer()
                          .sendMessage(TeleportRequestAcceptCommand.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&',
                                                                                                                                                   TeleportRequestAcceptCommand.this.plugin.getMessages()
                                                                                                                                                                                           .getCfg()
                                                                                                                                                                                           .getString(
                                                                                                                                                                                                   "Messages.Misc.Teleportation.Success")));
                    TeleportRequestAcceptCommand.this.plugin.getTeleportMap().remove(player);
                    TeleportRequestAcceptCommand.this.plugin.getTpaDataMap().remove(commandSender);
                }
            }, 20L * this.plugin.getConfigReader().getInt("teleportation.tpa.delay")));
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpAccept.Sender"));
            tpaData.getSender()
                   .getPlayer()
                   .sendMessage(this.plugin.getMessages().getPrefix() +
                                this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpAccept.Target"));
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpAccept.Teleporting"));
        }
        return true;
    }
}
