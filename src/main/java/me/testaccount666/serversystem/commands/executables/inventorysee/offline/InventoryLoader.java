package me.testaccount666.serversystem.commands.executables.inventorysee.offline;

import de.tr7zw.nbtapi.NBT;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee;
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils;
import me.testaccount666.serversystem.utils.BiDirectionalHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class InventoryLoader {
    public final BiDirectionalHashMap<UUID, Inventory> inventoryMap = new BiDirectionalHashMap<>();
    private final CommandInventorySee _commandInventorySee;

    public InventoryLoader(CommandInventorySee commandInventorySee) {
        _commandInventorySee = commandInventorySee;
    }

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

            var inventoryTag = fileHandle.getCompoundList("Inventory");
            if (inventoryTag == null) return Optional.empty();
            var inventory = Bukkit.createInventory(null, 54, "${offlinePlayer.getName()}'s Inventory");

            for (var index = 0; index < inventoryTag.size(); index++) {
                var itemTag = inventoryTag.get(index);
                if (itemTag == null || itemTag.toString().equalsIgnoreCase("{}")) continue;

                var slot = itemTag.getByte("Slot");

                var itemStack = NBT.itemStackFromNBT(itemTag);
                inventory.setItem(slot, itemStack);
            }

            var equipmentTag = fileHandle.getCompound("equipment");
            var equipmentSlotList = new String[]{"head", "chest", "legs", "feet", "offhand"};

            if (equipmentTag != null) for (var index = 0; index < equipmentSlotList.length; index++) {
                var equipmentSlot = equipmentSlotList[index];
                var itemTag = equipmentTag.getCompound(equipmentSlot);
                if (itemTag == null || itemTag.toString().equalsIgnoreCase("{}")) continue;

                var itemStack = NBT.itemStackFromNBT(itemTag);

                inventory.setItem(36 + index, itemStack);
            }

            addSectionDecorators(inventory);

            inventoryMap.put(playerUUID, inventory);
            return Optional.of(inventory);
        } catch (IOException exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Failed to load inventory for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})", exception);
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

            var inventoryTag = fileHandle.getCompoundList("Inventory");
            inventoryTag.clear();

            for (var slot = 0; slot < 41; slot++) {
                var item = inventory.getItem(slot);
                if (item == null || item.getType().isAir()) continue;

                var itemTag = NBT.itemStackToNBT(item);
                itemTag.setByte("Slot", (byte) slot);
                inventoryTag.addCompound(itemTag);
            }


            var equipmentTag = fileHandle.getCompound("equipment");
            var equipmentSlotList = new String[]{"head", "chest", "legs", "feet", "offhand"};

            if (equipmentTag != null) for (var i = 0; i < equipmentSlotList.length; i++) {
                var equipmentSlot = equipmentSlotList[i];
                var slotIndex = 36 + i;
                var item = inventory.getItem(slotIndex);
                if (item == null || item.getType().isAir()) {
                    equipmentTag.removeKey(equipmentSlot);
                    continue;
                }

                equipmentTag.setItemStack(equipmentSlot, item);
            }

            fileHandle.save();

            ServerSystem.getLog().info("Saved inventory for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})");
        } catch (IOException exception) {
            ServerSystem.getLog().log(Level.SEVERE, "Failed to save inventory for ${offlinePlayer.getName()} (${offlinePlayer.getUniqueId()})", exception);
        }
    }

    public void addSectionDecorators(Inventory displayInventory) {
        InventorySeeUtils.addSectionDecorators(displayInventory, true);
    }
}
