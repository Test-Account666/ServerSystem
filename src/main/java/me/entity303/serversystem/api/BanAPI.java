package me.entity303.serversystem.api;

import me.entity303.serversystem.bansystem.Ban;
import me.entity303.serversystem.bansystem.BanManager_Disabled;
import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public class BanAPI {
    private final ServerSystem plugin;

    public BanAPI(ServerSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * @param player = Player which is banned or not banned
     * @return = Gives out if the player is banned
     */
    public boolean isBanned(OfflinePlayer player) {
        return this.plugin.getBanManager().isBanned(player.getUniqueId());
    }

    /**
     * @param banned     = UUID of banned player
     * @param senderUUID = UUID of the banSender (See Messages.Misc.BanSystem.ConsoleName for the right entry of the console UUID!)
     * @param reason     = The reason why the player is banned
     * @param howLong    = How long the player should be banned
     * @param timeUnit   = The TimeUnit (e.g. seconds, minutes, ...) of how long the player should be banned
     * @return = Returns the Ban Object
     */
    public Ban createBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return this.plugin.getBanManager().createBan(banned, senderUUID, reason, howLong, timeUnit);
    }

    /**
     * @param player = The player that should get unbanned
     */
    public void deleteBan(OfflinePlayer player) {
        this.plugin.getBanManager().unBan(player.getUniqueId());
    }

    /**
     * @param player = The banned player
     * @return = Returns the Ban object of the banned player
     */
    public Ban getBan(OfflinePlayer player) {
        return this.plugin.getBanManager().getBanByPlayer(player);
    }

    /**
     * @return = Gives you a list with all banned player names
     */
    public List<String> getBannedPlayerNames() {
        return this.plugin.getBanManager().getBannedPlayerNames();
    }

    public boolean isBanEnabled() {
        return !(this.plugin.getBanManager() instanceof BanManager_Disabled);
    }
}
