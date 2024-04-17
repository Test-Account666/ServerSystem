package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class LoginListener extends CommandUtils implements Listener {

    public LoginListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (this.plugin.isStarting())
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                       ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.ServerStillStarting")));

        if (this.plugin.isMaintenance())
            if (!this.plugin.getPermissions().hasPermission(e.getPlayer(), "maintenance.join", true)) {
                var sender = e.getPlayer().getName();
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, this.plugin.getMessages().getMessage("Join", "Join", sender, null, "Maintenance.NoJoin"));
                return;
            }

        var banManager = this.plugin.getBanManager();
        if (banManager.isBanned(e.getPlayer().getUniqueId())) {
            var ban = banManager.getBanByPlayer(e.getPlayer());
            if (ban.UNBAN_TIME() > -1)
                if (ban.UNBAN_TIME() <= System.currentTimeMillis()) {
                    this.plugin.getBanManager().unBan(e.getPlayer().getUniqueId());
                    return;
                }
            var name = "Unknown";

            if (ban.BAN_SENDER_UUID().equalsIgnoreCase(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ConsoleName")))
                name = this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ConsoleName");
            else
                try {
                    var player = Bukkit.getOfflinePlayer(UUID.fromString(ban.BAN_SENDER_UUID()));
                    name = player.getName();
                } catch (IllegalArgumentException ignored) {
                }
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.plugin.getMessages()
                                                                       .getMessage("login", "login", name, e.getPlayer(), "Ban.Kick")
                                                                       .replace("<REASON>", ChatColor.translateAlternateColorCodes('&', ban.BAN_REASON()))
                                                                       .replace("<DATE>", ban.UNBAN_DATE().replace("&", "§")));
        } else if (Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size())
            if (!this.plugin.getPermissions().hasPermission(e.getPlayer(), "joinfullserver.premium", true) &&
                !this.plugin.getPermissions().hasPermission(e.getPlayer(), "joinfullserver.admin", true))
                e.disallow(PlayerLoginEvent.Result.KICK_FULL, this.plugin.getMessages()
                                                                         .getMiscMessage("ServerJoin", "ServerJoin", e.getPlayer(), null, "ServerFull")
                                                                         .replace("<PERMISSION>", this.plugin.getPermissions().getPermission("joinfullserver")));
            else {
                e.allow();
                if (this.plugin.getPermissions().hasPermission(e.getPlayer(), "joinfullserver.admin", true)) {
                    var kickedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> player != e.getPlayer()).filter(player -> !this.plugin.getPermissions().hasPermission(player, "joinfullserver.admin", true)).findFirst().orElse(null);
                    var name = kickedPlayer.getName();
                    kickedPlayer.kickPlayer(
                            this.plugin.getMessages().getMessageWithStringTarget("joinKick", "joinKick", e.getPlayer(), name, "KickedByHigher.Admin"));
                } else if (this.plugin.getPermissions().hasPermission(e.getPlayer(), "joinfullserver.premium", true)) {
                    var kickedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> player != e.getPlayer()).filter(player -> {
                        if (this.plugin.getPermissions().hasPermission(player, "joinfullserver.premium", true))
                            return false;
                        return !this.plugin.getPermissions().hasPermission(player, "joinfullserver.admin", true);
                    }).findFirst().orElse(null);
                    var name = kickedPlayer.getName();
                    kickedPlayer.kickPlayer(
                            this.plugin.getMessages().getMessageWithStringTarget("joinKick", "joinKick", e.getPlayer(), name, "KickedByHigher.Premium"));
                }
            }
    }
}
