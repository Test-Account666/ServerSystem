package me.testaccount666.serversystem.commands.executables.god;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listener for handling events related to god mode functionality.
 * This class cancels damage, death, combustion, and hunger events for players in god mode.
 */
public class ListenerGod implements Listener {

    @EventHandler
    public void onUserDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        handleCancellableEvent(event, player);
    }

    @EventHandler
    public void onUserDeath(PlayerDeathEvent event) {
        handleCancellableEvent(event, event.getEntity());
    }

    @EventHandler
    public void onUserCombust(EntityCombustEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        handleCancellableEvent(event, player);
    }

    @EventHandler
    public void onUserFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        handleCancellableEvent(event, player);
    }

    /**
     * Common handler for events affecting players in god mode.
     * Checks if the player is in god mode and cancels the event if they are.
     * Also ensures the player's food level and saturation remain at maximum.
     *
     * @param event  The cancellable event to handle
     * @param player The player affected by the event
     */
    private void handleCancellableEvent(Cancellable event, Player player) {
        var userOptional = ServerSystem.Instance.getRegistry().getService(UserManager.class).getUser(player);

        if (userOptional.isEmpty()) {
            ServerSystem.getLog().warning("(ListenerGod) User '${player.getName()}' is not cached! This should not happen!");
            return;
        }

        var cachedUser = userOptional.get();

        // Player should be online, so casting, without additional checks, should be safe
        var user = (User) cachedUser.getOfflineUser();

        if (!user.isGodMode()) return;

        event.setCancelled(true);
        player.setFoodLevel(20);
        player.setSaturation(20);
    }
}