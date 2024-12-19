package me.entity303.serversystem.api;

import me.entity303.serversystem.bansystem.MuteManager_Disabled;
import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.bansystem.moderation.MuteModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("NewMethodNamingConvention")
public class MuteAPI {
    private final ServerSystem _plugin;

    public MuteAPI(ServerSystem plugin) {
        this._plugin = plugin;
    }

    /**
     @param player = The muted player

     @return = Gives you the Mute object of the muted player
     */
    public MuteModeration getMute(OfflinePlayer player) {
        return this._plugin.GetMuteManager().GetMute(player);
    }

    /**
     @param player = The muted player
     */
    public void deleteMute(OfflinePlayer player) {
        this._plugin.GetMuteManager().RemoveMute(player.getUniqueId());
    }

    /**
     @param mutedUUID = The uuid of the player that should get muted
     @param senderUUID = The sender of the mute (See Messages.Misc.BanSystem.ConsoleName for the right entry of the console UUID!)
     @param reason = The reason why the player was muted
     @param howLong = How long the player should be muted
     @param timeUnit = The TimeUnit (e.g. seconds, minutes, ...) of how long the player should be muted

     @return = Returns the Mute Object
     */
    public MuteModeration createMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return this._plugin.GetMuteManager().CreateMute(mutedUUID, senderUUID, reason, howLong, timeUnit);
    }

    /**
     @param player = Player which is muted or not muted

     @return = Gives out if the player is muted
     */
    public boolean isMuted(OfflinePlayer player) {
        return this._plugin.GetMuteManager().IsMuted(player);
    }

    /**
     @return = Gives you a list with all muted player names
     */
    public List<String> getMutedPlayerNames() {
        return this._plugin.GetMuteManager().GetMutedPlayerNames();
    }

    public boolean isMuteEnabled() {
        return !(this._plugin.GetMuteManager() instanceof MuteManager_Disabled);
    }
}
