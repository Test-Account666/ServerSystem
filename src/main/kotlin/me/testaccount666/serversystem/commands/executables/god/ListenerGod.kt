package me.testaccount666.serversystem.commands.executables.god

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent

/**
 * Listener for handling events related to god mode functionality.
 * This class cancels damage, death, combustion, and hunger events for players in god mode.
 */
class ListenerGod : Listener {
    @EventHandler
    fun onUserDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return

        handleCancellableEvent(event, player)
    }

    @EventHandler
    fun onUserDeath(event: PlayerDeathEvent) {
        handleCancellableEvent(event, event.getEntity())
    }

    @EventHandler
    fun onUserCombust(event: EntityCombustEvent) {
        val player = event.entity as? Player ?: return

        handleCancellableEvent(event, player)
    }

    @EventHandler
    fun onUserFoodLevelChange(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return

        handleCancellableEvent(event, player)
    }

    /**
     * Common handler for events affecting players in god mode.
     * Checks if the player is in god mode and cancels the event if they are.
     * Also ensures the player's food level and saturation remain at maximum.
     * 
     * @param event  The cancellable event to handle
     * @param player The player affected by the event
     */
    private fun handleCancellableEvent(event: Cancellable, player: Player) {
        val user = instance.registry.getService<UserManager>().getUserOrNull(player)

        if (user == null) {
            log.warning("(ListenerGod) User '${player.name}' is not cached! This should not happen!")
            return
        }

        // Player should be online, so casting, without additional checks, should be safe
        val onlineUser = user.offlineUser as User
        if (!onlineUser.isGodMode) return

        event.isCancelled = true
        player.foodLevel = 20
        player.saturation = 20f
    }
}