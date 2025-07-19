package me.testaccount666.serversystem.commands.executables.enderchest.offline;

import de.tr7zw.nbtapi.NBT;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.utils.BiDirectionalHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class EnderChestLoader {
    public final BiDirectionalHashMap<UUID, Inventory> inventoryMap = new BiDirectionalHashMap<>();

    public Optional<Inventory> loadOfflineInventory(OfflinePlayer offlinePlayer) {
        if (!offlinePlayer.hasPlayedBefore()) return Optional.empty();

        var playerUUID = offlinePlayer.getUniqueId();
        if (inventoryMap.containsKey(playerUUID)) return inventoryMap.getValue(playerUUID);

        var playerDataFile = Path.of(Bukkit.getWorlds().getFirst().getWorldFolder().getPath(), "playerdata", "${playerUUID}.dat").toFile();

        if (!playerDataFile.exists()) {
            ServerSystem.getLog().warning("Player data file not found for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})");
            return Optional.empty();
        }

        try {
            var fileHandle = NBT.getFileHandle(playerDataFile);
            var enderItemsTag = fileHandle.getCompoundList("EnderItems");
            if (enderItemsTag == null) return Optional.empty();

            var inventory = Bukkit.createInventory(null, 27, "${offlinePlayer.getName()}'s Ender Chest");

            for (var index = 0; index < enderItemsTag.size(); index++) {
                var itemTag = enderItemsTag.get(index);
                if (itemTag == null) continue;

                var slot = itemTag.getByte("Slot");
                var itemStack = NBT.itemStackFromNBT(itemTag);

                inventory.setItem(slot, itemStack);
            }

            inventoryMap.put(playerUUID, inventory);
            return Optional.of(inventory);
        } catch (IOException exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Failed to load ender chest for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})", exception);
            return Optional.empty();
        }
    }

    public void saveOfflineInventory(UUID playerUUID, Inventory inventory) {
        var offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        var playerDataFile = Path.of(Bukkit.getWorlds().getFirst().getWorldFolder().getPath(), "playerdata", "${playerUUID}.dat").toFile();

        if (!playerDataFile.exists()) {
            ServerSystem.getLog().warning("Player data file not found for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})");
            return;
        }

        try {
            var fileHandle = NBT.getFileHandle(playerDataFile);
            var enderItemsTag = fileHandle.getCompoundList("EnderItems");
            if (enderItemsTag == null) return;

            enderItemsTag.clear();
            for (var slot = 0; slot < 27; slot++) {
                var item = inventory.getItem(slot);
                if (item == null || item.getType().isAir()) continue;

                var itemTag = NBT.itemStackToNBT(item);
                itemTag.setByte("Slot", (byte) slot);
                enderItemsTag.addCompound(itemTag);
            }

            fileHandle.save();

            ServerSystem.getLog().info("Saved ender chest for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})");
        } catch (IOException exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Failed to save ender chest for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})", exception);
        }
    }
}
