package me.entity303.serversystem.listener.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
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

public class InteractListener implements Listener {
    private final List<HumanEntity> _open = new ArrayList<>();
    private final List<HumanEntity> _nerfed = new ArrayList<>();
    private final ServerSystem _plugin;

    public InteractListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event) {
        GameMode mode;
        boolean allowFlying;
        boolean flying;
        if (!this._open.contains(event.getPlayer()))
            return;
        this._open.remove(event.getPlayer());
        mode = event.getPlayer().getGameMode();
        allowFlying = ((Player) event.getPlayer()).getAllowFlight();
        flying = ((Player) event.getPlayer()).isFlying();
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ServerSystem"), () -> {
            event.getPlayer().setGameMode(mode);
            ((Player) event.getPlayer()).setAllowFlight(allowFlying);
            if (flying)
                ((Player) event.getPlayer()).setFlying(true);
        }, 1L);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ServerSystem"), () -> this._nerfed.remove(event.getPlayer()), 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnInteract(PlayerInteractEvent event) {
        if (this._plugin.GetVanish() == null)
            return;
        if (!this._plugin.GetVanish().IsVanish(event.getPlayer()))
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getClickedBlock() == null)
            return;
        if (event.getClickedBlock().getType() != CHEST && event.getClickedBlock().getType() != TRAPPED_CHEST && !this.IsShulker(event.getClickedBlock())) {
            if (!this._plugin.GetVanish().GetAllowInteract().contains(event.getPlayer()) && this._plugin.GetCommandManager().IsInteractActive()) {
                event.getPlayer()
                 .sendMessage(this._plugin.GetMessages().GetPrefix() +
                              this._plugin.GetMessages().GetMessage("vanish", "vanish", event.getPlayer().getName(), null, "Vanish.Misc.NoInteract"));
                event.setCancelled(true);
            }
            return;
        }
        if (this._nerfed.contains(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        this._nerfed.add(event.getPlayer());
        event.setCancelled(false);
        GameMode mode;
        boolean allowFlying;
        boolean flying;
        mode = event.getPlayer().getGameMode();
        allowFlying = event.getPlayer().getAllowFlight();
        flying = event.getPlayer().isFlying();
        this._open.add(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ServerSystem"), () -> {
            event.getPlayer().setGameMode(mode);
            event.getPlayer().setAllowFlight(allowFlying);
            if (flying)
                event.getPlayer().setFlying(true);
        }, 1L);
    }

    public ServerSystem GetPlugin() {
        return this._plugin;
    }

    private boolean IsShulker(Block block) {
        var type = block.getType();
        return this.IsShulker(type);
    }

    private boolean IsShulker(Material type) {
        return type == Material.getMaterial("SHULKER_BOX") || type == Material.getMaterial("BLACK_SHULKER_BOX") ||
               type == Material.getMaterial("BLUE_SHULKER_BOX") || type == Material.getMaterial("BROWN_SHULKER_BOX") ||
               type == Material.getMaterial("CYAN_SHULKER_BOX") || type == Material.getMaterial("GRAY_SHULKER_BOX") ||
               type == Material.getMaterial("GREEN_SHULKER_BOX") || type == Material.getMaterial("LIGHT_BLUE_SHULKER_BOX") ||
               type == Material.getMaterial("LIGHT_GRAY_SHULKER_BOX") || type == Material.getMaterial("LIME_SHULKER_BOX") ||
               type == Material.getMaterial("MAGENTA_SHULKER_BOX") || type == Material.getMaterial("ORANGE_SHULKER_BOX") ||
               type == Material.getMaterial("PINK_SHULKER_BOX") || type == Material.getMaterial("PURPLE_SHULKER_BOX") ||
               type == Material.getMaterial("RED_SHULKER_BOX") || type == Material.getMaterial("WHITE_SHULKER_BOX") ||
               type == Material.getMaterial("YELLOW_SHULKER_BOX");
    }
}
