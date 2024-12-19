package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class VanishListener implements Listener {
    private final ServerSystem _plugin;

    public VanishListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnChat(AsyncPlayerChatEvent event) {
        if (!this._plugin.GetVanish().IsVanish(event.getPlayer())) return;
        if (this._plugin.GetVanish().GetAllowChat().contains(event.getPlayer()) || !this._plugin.GetCommandManager().IsChatActive()) return;
        event.setCancelled(true);
        event.getPlayer()
             .sendMessage(this._plugin.GetMessages().GetPrefix() +
                          this._plugin.GetMessages().GetMessage("vanish", "vanish", event.getPlayer().getName(), null, "Vanish.Misc.NoChat"));

    }

    @EventHandler
    public void OnInteract(PlayerInteractEvent event) {
        if (!this._plugin.GetVanish().IsVanish(event.getPlayer())) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) return;
        if (this._plugin.GetVanish().GetAllowInteract().contains(event.getPlayer()) || !this._plugin.GetCommandManager().IsInteractActive()) return;
        event.setCancelled(true);
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.getPlayer()
                 .sendMessage(this._plugin.GetMessages().GetPrefix() +
                              this._plugin.GetMessages().GetMessage("vanish", "vanish", event.getPlayer().getName(), null, "Vanish.Misc.NoInteract"));
        }
    }

    @EventHandler
    public void OnDrop(PlayerDropItemEvent event) {
        if (!this._plugin.GetVanish().IsVanish(event.getPlayer())) return;
        if (this._plugin.GetVanish().GetAllowDrop().contains(event.getPlayer()) || !this._plugin.GetCommandManager().IsDropActive()) return;
        event.setCancelled(true);
        event.getPlayer()
             .sendMessage(this._plugin.GetMessages().GetPrefix() +
                          this._plugin.GetMessages().GetMessage("vanish", "vanish", event.getPlayer().getName(), null, "Vanish.Misc.NoDrop"));
    }

    @EventHandler
    public void OnPickup(PlayerPickupItemEvent event) {
        if (!this._plugin.GetVanish().IsVanish(event.getPlayer())) return;
        if (this._plugin.GetVanish().GetAllowPickup().contains(event.getPlayer()) || !this._plugin.GetCommandManager().IsPickupActive()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void OnEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() == null) return;
        if (!(event.getTarget() instanceof Player player)) return;
        if (this._plugin.GetVanish().IsVanish(player)) event.setTarget(null);
    }
}
