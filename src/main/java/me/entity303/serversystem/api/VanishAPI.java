package me.entity303.serversystem.api;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("NewMethodNamingConvention")
public class VanishAPI {
    private final ServerSystem _plugin;

    public VanishAPI(ServerSystem plugin) {
        this._plugin = plugin;
    }

    /**
     @return = Gives you a list of all players which are able to interact in vanish
     */
    public List<Player> getAllowInteract() {
        return this._plugin.GetVanish().GetAllowInteract();
    }

    /**
     @return = Gives you a list of all players which are able to chat in vanish
     */
    public List<Player> getAllowChat() {
        return this._plugin.GetVanish().GetAllowChat();
    }

    /**
     @return = Gives you a list of all players which are able to drop items in vanish
     */
    public List<Player> getAllowDrop() {
        return this._plugin.GetVanish().GetAllowDrop();
    }

    /**
     @return = Gives you a list of all players which are able to pick up items in vanish
     */
    public List<Player> getAllowPickup() {
        return this._plugin.GetVanish().GetAllowPickup();
    }

    /**
     @return = Gives you a list of all players (uuids) which are  in vanish
     */
    public List<UUID> getVanishList() {
        return this._plugin.GetVanish().GetVanishList();
    }

    /**
     @param player = The player which is in vanish or not

     @return = Gives out if the player is in vanish
     */
    public boolean isVanish(OfflinePlayer player) {
        return this._plugin.GetVanish().IsVanish(player);
    }

    /**
     @param player = The player which's vanish status should be changed
     @param vanish = The vanish status (true = vanished, false = not vanished)
     */
    public void setVanish(Player player, boolean vanish) {
        this._plugin.GetVanish().SetVanishData(player, vanish);
        this._plugin.GetVanish().SetVanish(vanish, player);
    }

    /**
     @param player = The player which's vanish status should be changed
     @param vanish = The vanish status (true = vanished, false = not vanished)
     */
    public void setVanish(OfflinePlayer player, boolean vanish) {
        this._plugin.GetVanish().SetVanish(vanish, player.getUniqueId());
    }
}
