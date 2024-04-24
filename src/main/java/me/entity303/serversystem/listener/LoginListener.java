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
    public void OnLogin(PlayerLoginEvent event) {
        if (this._plugin.IsStarting())
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                       ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.ServerStillStarting")));

        if (this._plugin.IsMaintenance())
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "maintenance.join", true)) {
                var sender = event.getPlayer().getName();
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, this._plugin.GetMessages().GetMessage("Join", "Join", sender, null, "Maintenance.NoJoin"));
                return;
            }

        var banManager = this._plugin.GetBanManager();
        if (banManager.IsBanned(event.getPlayer().getUniqueId())) {
            var ban = banManager.GetBanByPlayer(event.getPlayer());
            if (ban.GetExpireTime() > -1)
                if (ban.GetExpireTime() <= System.currentTimeMillis()) {
                    this._plugin.GetBanManager().UnBan(event.getPlayer().getUniqueId());
                    return;
                }
            var name = "Unknown";

            if (ban.GetSenderUuid().equalsIgnoreCase(this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ConsoleName")))
                name = this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ConsoleName");
            else
                try {
                    var player = Bukkit.getOfflinePlayer(UUID.fromString(ban.GetSenderUuid()));
                    name = player.getName();
                } catch (IllegalArgumentException ignored) {
                }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, this._plugin.GetMessages()
                                                                       .GetMessage("login", "login", name, event.getPlayer(), "Ban.Kick")
                                                                       .replace("<REASON>", ChatColor.TranslateAlternateColorCodes('&', ban.GetReason()))
                                                                       .replace("<DATE>", ban.GetExpireDate().replace("&", "§")));
        } else if (Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size())
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "joinfullserver.premium", true) &&
                !this._plugin.GetPermissions().HasPermission(event.getPlayer(), "joinfullserver.admin", true))
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, this._plugin.GetMessages()
                                                                         .GetMiscMessage("ServerJoin", "ServerJoin", event.getPlayer(), null, "ServerFull")
                                                                         .replace("<PERMISSION>", this._plugin.GetPermissions().GetPermission("joinfullserver")));
            else {
                event.allow();
                if (this._plugin.GetPermissions().HasPermission(event.getPlayer(), "joinfullserver.admin", true)) {
                    var kickedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> player != event.getPlayer()).filter(player -> !this._plugin.GetPermissions().HasPermission(player, "joinfullserver.admin", true)).findFirst().orElse(null);
                    var name = kickedPlayer.getName();
                    kickedPlayer.kickPlayer(
                            this._plugin.GetMessages().GetMessageWithStringTarget("joinKick", "joinKick", event.getPlayer(), name, "KickedByHigher.Admin"));
                } else if (this._plugin.GetPermissions().HasPermission(event.getPlayer(), "joinfullserver.premium", true)) {
                    var kickedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> player != event.getPlayer()).filter(player -> {
                        if (this._plugin.GetPermissions().HasPermission(player, "joinfullserver.premium", true))
                            return false;
                        return !this._plugin.GetPermissions().HasPermission(player, "joinfullserver.admin", true);
                    }).findFirst().orElse(null);
                    var name = kickedPlayer.getName();
                    kickedPlayer.kickPlayer(
                            this._plugin.GetMessages().GetMessageWithStringTarget("joinKick", "joinKick", event.getPlayer(), name, "KickedByHigher.Premium"));
                }
            }
    }
}
