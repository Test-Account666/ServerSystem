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
    private final ServerSystem plugin;

    public VanishListener(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!this.plugin.getVanish().isVanish(e.getPlayer())) return;
        if (this.plugin.getVanish().getAllowChat().contains(e.getPlayer()) || !this.plugin.getCommandManager().isChatActive())
            return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("vanish", "vanish", e.getPlayer().getName(), null, "Vanish.Misc.NoChat"));

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!this.plugin.getVanish().isVanish(e.getPlayer())) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) return;
        if (this.plugin.getVanish().getAllowInteract().contains(e.getPlayer()) || !this.plugin.getCommandManager().isInteractActive())
            return;
        e.setCancelled(true);
        if (e.getAction() == Action.LEFT_CLICK_BLOCK)
            e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("vanish", "vanish", e.getPlayer().getName(), null, "Vanish.Misc.NoInteract"));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!this.plugin.getVanish().isVanish(e.getPlayer())) return;
        if (this.plugin.getVanish().getAllowDrop().contains(e.getPlayer()) || !this.plugin.getCommandManager().isDropActive())
            return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("vanish", "vanish", e.getPlayer().getName(), null, "Vanish.Misc.NoDrop"));
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (!this.plugin.getVanish().isVanish(e.getPlayer())) return;
        if (this.plugin.getVanish().getAllowPickup().contains(e.getPlayer()) || !this.plugin.getCommandManager().isPickupActive())
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() == null) return;
        if (!(e.getTarget() instanceof Player)) return;
        Player player = (Player) e.getTarget();
        if (this.plugin.getVanish().isVanish(player)) e.setTarget(null);
    }
}
