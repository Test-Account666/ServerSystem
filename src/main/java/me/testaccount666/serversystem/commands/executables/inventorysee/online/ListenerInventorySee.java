package me.testaccount666.serversystem.commands.executables.inventorysee.online;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.AbstractInventorySeeListener;
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;

import java.util.Set;

@RequiredCommands(requiredCommands = CommandInventorySee.class)
public class ListenerInventorySee extends AbstractInventorySeeListener implements Listener {

    /**
     * Checks if the listener can be registered by finding the required CommandInventorySee instance.
     *
     * @param commands Set of available commands
     * @return true if the listener can be registered, false otherwise
     */
    public boolean canRegister(Set<ServerSystemCommandExecutor> commands) {
        return internalCanRegister(commands);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onViewerClick(InventoryClickEvent event) {
        var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        var topInventory = event.getView().getTopInventory();
        var bottomInventory = event.getView().getBottomInventory();
        var viewerPlayer = (Player) event.getWhoClicked();
        var owner = findInventoryOwner(topInventory, bottomInventory);

        if (owner == null || owner == viewerPlayer) return;

        var cachedInventory = _commandInventorySee.inventoryCache.get(owner);
        if (cachedInventory == null || (cachedInventory != topInventory && cachedInventory != bottomInventory)) return;

        if (!PermissionManager.hasCommandPermission(viewerPlayer, "InventorySee.Modify", false)
                || (event.getRawSlot() > 44 && event.getRawSlot() < 54)) event.setCancelled(true);

        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> _commandInventorySee.applyChangesToOwner(owner, cachedInventory), 1L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onOwnerInventoryChange(PlayerDropItemEvent event) {
        updateCachedInventory(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onOwnerInventoryChange(PlayerPickupItemEvent event) {
        updateCachedInventory(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onOwnerInventoryChange(PlayerSwapHandItemsEvent event) {
        updateCachedInventory(event.getPlayer());
    }

    @EventHandler
    public void onGeneralClick(InventoryClickEvent event) {
        var viewerPlayer = (Player) event.getWhoClicked();
        updateCachedInventory(viewerPlayer);
    }

    private Player findInventoryOwner(Inventory topInventory, Inventory bottomInventory) {
        var holder = topInventory.getHolder();
        if (holder instanceof Player player) return player;

        holder = bottomInventory.getHolder();
        if (holder instanceof Player player) return player;

        return null;
    }

    private void updateCachedInventory(Player player) {
        var cachedInventory = _commandInventorySee.inventoryCache.get(player);
        if (cachedInventory == null) return;

        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> _commandInventorySee.refreshInventoryContents(player, cachedInventory), 1L);
    }

    @EventHandler
    public void onViewedQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        if (!_commandInventorySee.inventoryCache.containsKey(player)) return;

        var inventory = _commandInventorySee.inventoryCache.get(player);
        _commandInventorySee.inventoryCache.remove(player);

        InventorySeeUtils.handleInventoryViewers(inventory, player, 10L,
                (user, playerName) -> _commandInventorySee.offlineInventorySee.processOfflineInventorySee(user, "", playerName));
    }
}
