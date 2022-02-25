package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.CHEST;
import static org.bukkit.Material.TRAPPED_CHEST;

public class InteractListener_Newer implements Listener {
    private final List<Player> open = new ArrayList<>();
    private final List<Player> nerfed = new ArrayList<>();
    private final ServerSystem plugin;

    public InteractListener_Newer(ServerSystem plugin) {
        this.plugin = plugin;
    }

    private boolean isShulker(Block block) {
        Material type = block.getType();
        return type == Material.getMaterial("SHULKER_BOX") || type == Material.getMaterial("BLACK_SHULKER_BOX") || type == Material.getMaterial("BLUE_SHULKER_BOX") || type == Material.getMaterial("BROWN_SHULKER_BOX") || type == Material.getMaterial("CYAN_SHULKER_BOX") || type == Material.getMaterial("GRAY_SHULKER_BOX") || type == Material.getMaterial("GREEN_SHULKER_BOX") || type == Material.getMaterial("LIGHT_BLUE_SHULKER_BOX") || type == Material.getMaterial("LIGHT_GRAY_SHULKER_BOX") || type == Material.getMaterial("LIME_SHULKER_BOX") || type == Material.getMaterial("MAGENTA_SHULKER_BOX") || type == Material.getMaterial("ORANGE_SHULKER_BOX") || type == Material.getMaterial("PINK_SHULKER_BOX") || type == Material.getMaterial("PURPLE_SHULKER_BOX") || type == Material.getMaterial("RED_SHULKER_BOX") || type == Material.getMaterial("WHITE_SHULKER_BOX") || type == Material.getMaterial("YELLOW_SHULKER_BOX");
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        GameMode mode;
        boolean allowFlying;
        boolean flying;
        if (!this.open.contains(e.getPlayer())) return;
        this.open.remove(e.getPlayer());
        mode = e.getPlayer().getGameMode();
        allowFlying = ((Player) e.getPlayer()).getAllowFlight();
        flying = ((Player) e.getPlayer()).isFlying();
        e.getPlayer().setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ServerSystem"), () -> {
            e.getPlayer().setGameMode(mode);
            ((Player) e.getPlayer()).setAllowFlight(allowFlying);
            if (flying) ((Player) e.getPlayer()).setFlying(true);
        }, 1L);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ServerSystem"), () -> this.nerfed.remove(e.getPlayer()), 5L);
    }

    public ServerSystem getPlugin() {
        return this.plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (this.getPlugin().getVanish() == null) return;
        if (!this.getPlugin().getVanish().isVanish(e.getPlayer())) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() != CHEST && e.getClickedBlock().getType() != TRAPPED_CHEST && !this.isShulker(e.getClickedBlock())) {
            if (!this.plugin.getVanish().getAllowInteract().contains(e.getPlayer()) && this.plugin.getCommandManager().isInteractActive()) {
                e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("vanish", "vanish", e.getPlayer().getName(), null, "Vanish.Misc.NoInteract"));
                e.setCancelled(true);
            }
            return;
        }
        if (this.nerfed.contains(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }
        this.nerfed.add(e.getPlayer());
        e.setCancelled(false);
        GameMode mode;
        boolean allowFlying;
        boolean flying;
        mode = e.getPlayer().getGameMode();
        allowFlying = e.getPlayer().getAllowFlight();
        flying = e.getPlayer().isFlying();
        this.open.add(e.getPlayer());
        e.getPlayer().setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ServerSystem"), () -> {
            e.getPlayer().setGameMode(mode);
            e.getPlayer().setAllowFlight(allowFlying);
            if (flying) e.getPlayer().setFlying(true);
        }, 1L);
    }
}
