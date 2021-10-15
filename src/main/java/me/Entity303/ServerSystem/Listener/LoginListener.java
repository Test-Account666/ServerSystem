package me.Entity303.ServerSystem.Listener;

import me.Entity303.ServerSystem.BanSystem.Ban;
import me.Entity303.ServerSystem.BanSystem.ManagerBan;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class LoginListener extends MessageUtils implements Listener {

    public LoginListener(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (this.plugin.isStarting())
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.ServerStillStarting")));

        if (this.plugin.isMaintenance()) if (!this.isAllowed(e.getPlayer(), "maintenance.join", true)) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, this.getMessage("Maintenance.NoJoin", "Join", "Join", e.getPlayer().getName(), null));
            return;
        }

        ManagerBan banManager = this.plugin.getBanManager();
        if (banManager.isBanned(e.getPlayer().getUniqueId())) {
            Ban ban = banManager.getBanByPlayer(e.getPlayer());
            if (ban.getUNBAN_TIME() > -1) if (ban.getUNBAN_TIME() <= System.currentTimeMillis()) {
                this.plugin.getBanManager().unBan(e.getPlayer().getUniqueId());
                return;
            }
            String name = "Unknown";

            if (ban.getBAN_SENDER_UUID().equalsIgnoreCase(this.getBanSystem("ConsoleName")))
                name = this.getBanSystem("ConsoleName");
            else try {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(ban.getBAN_SENDER_UUID()));
                name = player.getName();
            } catch (IllegalArgumentException ignored) {
            }
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.getMessage("Ban.Kick", "login", "login", name, e.getPlayer()).replace("<REASON>", ChatColor.translateAlternateColorCodes('&', ban.getBAN_REASON())).replace("<DATE>", ban.getUNBAN_DATE().replace("&", "§")));
        } else if (Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size())
            if (!this.isAllowed(e.getPlayer(), "joinfullserver.premium", true) && !this.isAllowed(e.getPlayer(), "joinfullserver.admin", true))
                e.disallow(PlayerLoginEvent.Result.KICK_FULL, this.plugin.getMessages().getMiscMessage("ServerJoin", "ServerJoin", e.getPlayer(), null, "ServerFull").replace("<PERMISSION>", this.Perm("joinfullserver")));
            else {
                e.allow();
                if (this.isAllowed(e.getPlayer(), "joinfullserver.admin", true)) {
                    Player kickedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> player != e.getPlayer()).filter(player -> !this.isAllowed(player, "joinfullserver.admin", true)).findFirst().orElse(null);
                    String name = kickedPlayer.getName();
                    kickedPlayer.kickPlayer(this.getMessageWithStringTarget("KickedByHigher.Admin", "joinKick", "joinKick", e.getPlayer(), name));
                } else if (this.isAllowed(e.getPlayer(), "joinfullserver.premium", true)) {
                    Player kickedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> player != e.getPlayer()).filter(player -> !this.isAllowed(player, "joinfullserver.premium", true) && !this.isAllowed(player, "joinfullserver.admin", true)).findFirst().orElse(null);
                    String name = kickedPlayer.getName();
                    kickedPlayer.kickPlayer(this.getMessageWithStringTarget("KickedByHigher.Premium", "joinKick", "joinKick", e.getPlayer(), name));
                }
            }
    }
}
