package me.testaccount666.serversystem.commands.executables.enderchest.offline;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.executables.enderchest.CommandEnderChest;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredCommands(requiredCommands = CommandEnderChest.class)
public class ListenerOfflineEnderChest implements Listener {
    private EnderChestLoader _enderChestLoader;
    private CommandEnderChest _enderChest;

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);

        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandEnderChest commandEnderChest)) return;
            var enderChestLoader = commandEnderChest.offlineEnderChest.enderChestLoader;
            if (enderChestLoader == null) return;

            _enderChest = commandEnderChest;
            _enderChestLoader = enderChestLoader;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    @EventHandler
    public void onViewedJoin(PlayerLoginEvent event) {
        var viewedPlayer = event.getPlayer();

        var inventoryOptional = _enderChestLoader.inventoryMap.getValue(viewedPlayer.getUniqueId());
        if (inventoryOptional.isEmpty()) {
            Bukkit.getLogger().info("OfflineEnderChest: No offline inventory found for player ${viewedPlayer.getName()}");
            return;
        }
        var inventory = inventoryOptional.get();

        Bukkit.getLogger().info("OfflineEnderChest: Offline inventory was found for player ${viewedPlayer.getName()}");

        var viewers = new ArrayList<>(inventory.getViewers());
        inventory.close();

        _enderChestLoader.inventoryMap.removeByKey(viewedPlayer.getUniqueId());
        _enderChestLoader.saveOfflineInventory(viewedPlayer.getUniqueId(), inventory);

        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> viewers.forEach(viewer -> {
            if (!(viewer instanceof Player player)) return;

            var optionalUser = ServerSystem.Instance.getUserManager().getUser(player);
            if (optionalUser.isEmpty()) return;

            var cachedUser = optionalUser.get();
            if (cachedUser.isOfflineUser()) return;
            var user = (User) cachedUser.getOfflineUser();

            _enderChest.executeEnderChestCommand(user, viewedPlayer.getName());
        }), 10L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        var inventory = event.getInventory();

        if (!_enderChestLoader.inventoryMap.containsValue(inventory)) return;

        var uuid = _enderChestLoader.inventoryMap.getKey(inventory);
        if (uuid.isEmpty()) return;

        _enderChestLoader.inventoryMap.removeByValue(inventory);
        _enderChestLoader.saveOfflineInventory(uuid.get(), inventory);
    }
}
