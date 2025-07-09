package me.testaccount666.serversystem.commands.executables.inventorysee.offline;

import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee;
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.AbstractInventorySeeListener;
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Set;

@RequiredCommands(requiredCommands = CommandInventorySee.class)
public class ListenerOfflineInventorySee extends AbstractInventorySeeListener implements Listener {

    /**
     * Checks if the listener can be registered by finding the required CommandInventorySee instance.
     *
     * @param commands Set of available commands
     * @return true if the listener can be registered, false otherwise
     */
    public boolean canRegister(Set<ServerSystemCommandExecutor> commands) {
        return internalCanRegister(commands);
    }

    @Override
    protected boolean additionalRegistrationChecks() {
        return _commandInventorySee._offlineInventorySee.inventoryLoader != null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) { // Sorry paper, but I think PlayerJoinEvent is too late.
        var inventoryLoader = _commandInventorySee._offlineInventorySee.inventoryLoader;
        var player = event.getPlayer();
        var uuid = player.getUniqueId();

        @SuppressWarnings("DataFlowIssue") // Warning can be safely ignored, because inventoryLoader cannot be null, if this listener is registered.
        var inventoryMap = inventoryLoader.inventoryMap;

        var inventoryOptional = inventoryMap.getValue(uuid);
        if (inventoryOptional.isEmpty()) {
            Bukkit.getLogger().info("Player is joining, but no inventory is being viewed!");
            return;
        }

        Bukkit.getLogger().warning("Player is joining while offline inventory is being viewed!");

        var inventory = inventoryOptional.get();

        inventoryMap.removeByKey(uuid);
        inventoryLoader.saveOfflineInventory(uuid, inventory);

        InventorySeeUtils.handleInventoryViewers(inventory, player, 10L,
                (user, playerName) -> _commandInventorySee.processInventorySee(user, "", playerName));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        var inventoryLoader = _commandInventorySee._offlineInventorySee.inventoryLoader;
        var inventory = event.getInventory();

        @SuppressWarnings("DataFlowIssue") // Warning can be safely ignored, because inventoryLoader cannot be null, if this listener is registered.
        var inventoryMap = inventoryLoader.inventoryMap;
        var uuidOptional = inventoryMap.getKey(inventory);

        if (uuidOptional.isEmpty()) return;
        var uuid = uuidOptional.get();

        inventoryMap.removeByKey(uuid);
        inventoryLoader.saveOfflineInventory(uuid, inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        var inventoryLoader = _commandInventorySee._offlineInventorySee.inventoryLoader;
        var inventory = event.getInventory();

        @SuppressWarnings("DataFlowIssue") // Warning can be safely ignored, because inventoryLoader cannot be null, if this listener is registered.
        var inventoryMap = inventoryLoader.inventoryMap;
        if (!inventoryMap.containsValue(inventory)) return;

        if (event.getRawSlot() < 41 || event.getRawSlot() >= 54) return;
        event.setCancelled(true);
    }
}
