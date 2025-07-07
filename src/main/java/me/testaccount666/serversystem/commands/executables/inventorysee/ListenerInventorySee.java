package me.testaccount666.serversystem.commands.executables.inventorysee;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredCommands(requiredCommands = CommandInventorySee.class)
public class ListenerInventorySee implements Listener {

    private CommandInventorySee _commandInventorySee;

    public boolean canRegister(Set<ServerSystemCommandExecutor> commands) {
        var canRegister = new AtomicBoolean(false);

        commands.forEach(command -> {
            if (!(command instanceof CommandInventorySee foundCommand)) return;

            _commandInventorySee = foundCommand;
            canRegister.set(true);
        });

        return canRegister.get();
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

        if (!PermissionManager.hasPermission(viewerPlayer, "Commands.InventorySee.Modify", false)
                || event.getSlot() > 44) event.setCancelled(true);

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
}

