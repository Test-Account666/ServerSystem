package me.testaccount666.serversystem.commands.executables.kit.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Kit {
    private final String _name;
    private final long _coolDown;
    private final ItemStack _offHandItem;
    private final ItemStack[] _armorContents;
    private final ItemStack[] _inventoryContents;
    private String _displayName = null;

    public String getDisplayName() {
        if (_displayName != null) return _displayName;
        _displayName = _name;
        var charArray = _name.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);
        _displayName = new String(charArray);

        return _displayName;
    }

    public void giveKit(Player player) {
        var inventory = player.getInventory();
        var overflowItems = new ArrayList<ItemStack>();

        handleOffHandItem(inventory, overflowItems);
        handleArmorItems(inventory, overflowItems);
        handleInventoryItems(inventory, overflowItems);
        dropOverflowItems(player, overflowItems);
    }

    private void handleOffHandItem(PlayerInventory inventory, List<ItemStack> overflowItems) {
        if (_offHandItem == null) return;
        var currentOffHandItem = inventory.getItemInOffHand();

        if (!currentOffHandItem.getType().isAir()) {
            overflowItems.add(_offHandItem);
            return;
        }

        inventory.setItemInOffHand(_offHandItem);
    }

    private void handleArmorItems(PlayerInventory inventory, List<ItemStack> overflowItems) {
        if (_armorContents == null) return;
        var slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (var index = 0; index < _armorContents.length; index++) {
            var currentItem = inventory.getItem(slots[index]);
            var newArmorItem = _armorContents[index];

            if (newArmorItem == null) continue;

            if (!currentItem.getType().isAir()) {
                overflowItems.add(newArmorItem);
                continue;
            }

            inventory.setItem(slots[index], newArmorItem);
        }
    }

    private void handleInventoryItems(Inventory inventory, List<ItemStack> overflowItems) {
        if (_inventoryContents == null) return;
        for (var index = 0; index < _inventoryContents.length; index++) {
            var currentItem = inventory.getItem(index);
            var newInventoryItem = _inventoryContents[index];

            if (newInventoryItem == null) continue;

            if (currentItem != null && !currentItem.getType().isAir()) {
                overflowItems.add(newInventoryItem);
                continue;
            }

            inventory.setItem(index, newInventoryItem);
        }
    }

    private void dropOverflowItems(Entity player, List<ItemStack> overflowItems) {
        if (overflowItems.isEmpty()) return;

        for (var item : overflowItems)
            player.getWorld().dropItem(player.getLocation(), item, (entity) -> entity.setOwner(player.getUniqueId()));
    }
}
