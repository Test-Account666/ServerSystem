package me.Entity303.ServerSystem.API;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class VanishAPI {
    private final ss plugin;

    public VanishAPI(ss plugin) {
        this.plugin = plugin;
    }

    /**
     * @return = Gives you a list of all players which are able to interact in vanish
     */
    public List<Player> getAllowInteract() {
        return this.plugin.getVanish().getAllowInteract();
    }

    /**
     * @return = Gives you a list of all players which are able to chat in vanish
     */
    public List<Player> getAllowChat() {
        return this.plugin.getVanish().getAllowChat();
    }

    /**
     * @return = Gives you a list of all players which are able to drop items in vanish
     */
    public List<Player> getAllowDrop() {
        return this.plugin.getVanish().getAllowDrop();
    }

    /**
     * @return = Gives you a list of all players which are able to pick up items in vanish
     */
    public List<Player> getAllowPickup() {
        return this.plugin.getVanish().getAllowPickup();
    }

    /**
     * @return = Gives you a list of all players (uuids) which are  in vanish
     */
    public List<UUID> getVanishList() {
        return this.plugin.getVanish().getVanishList();
    }

    /**
     * @param player = The player which is in vanish or not
     * @return = Gives out if the player is in vanish
     */
    public boolean isVanish(OfflinePlayer player) {
        return this.plugin.getVanish().isVanish(player);
    }

    /**
     * @param player = The player which's vanish status should be changed
     * @param vanish = The vanish status (true = vanished, false = not vanished)
     */
    public void setVanish(Player player, boolean vanish) {
        this.plugin.getVanish().setVanishData(player, vanish);
        this.plugin.getVanish().setVanish(vanish, player);
    }

    /**
     * @param player = The player which's vanish status should be changed
     * @param vanish = The vanish status (true = vanished, false = not vanished)
     */
    public void setVanish(OfflinePlayer player, boolean vanish) {
        this.plugin.getVanish().setVanish(vanish, player.getUniqueId());
    }
}
