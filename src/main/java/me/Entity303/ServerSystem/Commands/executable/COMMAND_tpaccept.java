package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.TpaData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_tpaccept implements CommandExecutor {
    private final ss plugin;

    public COMMAND_tpaccept(ss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.tpaccept.required"))
            if (!this.plugin.getPermissions().hasPerm(cs, "tpaccept.permission")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("tpaccept.permission")));
                return true;
            }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getTpaDataMap().containsKey(cs)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "TpAccept.NoTpa"));
            return true;
        }

        TpaData tpaData = this.plugin.getTpaDataMap().get(cs);

        if (tpaData.getEnd() <= System.currentTimeMillis()) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "TpAccept.NoTpa"));
            return true;
        }

        if (!tpaData.getSender().isOnline()) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.AlreadyOffline"));
            return true;
        }

        if (!tpaData.isTpahere()) {
            if (!this.plugin.getConfig().getBoolean("teleportation.tpa.enabledelay") || this.plugin.getPermissions().hasPerm(tpaData.getSender().getPlayer(), "tpaccept.bypassdelay", true)) {
                Player player = tpaData.getSender().getPlayer();
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Sender"));
                tpaData.getSender().getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Target"));
                player.teleport(((Player) cs));
                player.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', COMMAND_tpaccept.this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Success")));
                this.plugin.getTpaDataMap().remove(cs);
                return true;
            }
            this.plugin.getTeleportMap().put(tpaData.getSender().getPlayer(), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                OfflinePlayer player = tpaData.getSender().getPlayer();
                if (player.isOnline() && ((OfflinePlayer) cs).isOnline()) {
                    player.getPlayer().teleport(((Player) cs));
                    player.getPlayer().sendMessage(COMMAND_tpaccept.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', COMMAND_tpaccept.this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Success")));
                    COMMAND_tpaccept.this.plugin.getTeleportMap().remove(player);
                    COMMAND_tpaccept.this.plugin.getTpaDataMap().remove(cs);
                }
            }, 20L * this.plugin.getConfig().getInt("teleportation.tpa.delay")));
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Sender"));
            tpaData.getSender().getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Target"));
            tpaData.getSender().getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Teleporting"));
        } else {
            if (!this.plugin.getConfig().getBoolean("teleportation.tpa.enabledelay") || this.plugin.getPermissions().hasPerm(cs, "tpaccept.bypassdelay", true)) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Sender"));
                tpaData.getSender().getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Target"));
                Player player = ((Player) cs);
                player.getPlayer().teleport(tpaData.getSender().getPlayer());
                player.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', COMMAND_tpaccept.this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Success")));
                this.plugin.getTpaDataMap().remove(cs);
                return true;
            }
            this.plugin.getTeleportMap().put(((Player) cs), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                OfflinePlayer player = ((OfflinePlayer) cs).getPlayer();
                if (player.isOnline() && tpaData.getSender().isOnline()) {
                    player.getPlayer().teleport(tpaData.getSender().getPlayer());
                    player.getPlayer().sendMessage(COMMAND_tpaccept.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', COMMAND_tpaccept.this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Success")));
                    COMMAND_tpaccept.this.plugin.getTeleportMap().remove(player);
                    COMMAND_tpaccept.this.plugin.getTpaDataMap().remove(cs);
                }
            }, 20L * this.plugin.getConfig().getInt("teleportation.tpa.delay")));
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Sender"));
            tpaData.getSender().getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Target"));
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpAccept.Teleporting"));
        }
        return true;
    }
}
