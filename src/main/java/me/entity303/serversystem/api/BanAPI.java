package me.entity303.serversystem.api;

import me.entity303.serversystem.bansystem.moderation.BanModeration;
import me.entity303.serversystem.bansystem.BanManager_Disabled;
import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("NewMethodNamingConvention") public class BanAPI {
    private final ServerSystem _plugin;

    public BanAPI(ServerSystem plugin) {
        this._plugin = plugin;
    }

    /**
     @param player = Player which is banned or not banned

     @return = Gives out if the player is banned
     */
    public boolean isBanned(OfflinePlayer player) {
        var uniqueId = player.getUniqueId();
        return this._plugin.GetBanManager().IsBanned(uniqueId);
    }

    /**
     @param banned = UUID of banned player
     @param senderUUID = UUID of the banSender (See Messages.Misc.BanSystem.ConsoleName for the right entry of the console UUID!)
     @param reason = The reason why the player is banned
     @param howLong = How long the player should be banned
     @param timeUnit = The TimeUnit (e.g. seconds, minutes, ...) of how long the player should be banned

     @return = Returns the Ban Object
     */
    public BanModeration createBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return this._plugin.GetBanManager().CreateBan(banned, senderUUID, reason, howLong, timeUnit);
    }

    /**
     @param player = The player that should get unbanned
     */
    public void deleteBan(OfflinePlayer player) {
        var uniqueId = player.getUniqueId();
        this._plugin.GetBanManager().UnBan(uniqueId);
    }

    /**
     @param player = The banned player

     @return = Returns the Ban object of the banned player
     */
    public BanModeration getBan(OfflinePlayer player) {
        return this._plugin.GetBanManager().GetBanByPlayer(player);
    }

    /**
     @return = Gives you a list with all banned player names
     */
    public List<String> getBannedPlayerNames() {
        return this._plugin.GetBanManager().GetBannedPlayerNames();
    }

    public boolean isBanEnabled() {
        return !(this._plugin.GetBanManager() instanceof BanManager_Disabled);
    }
}
