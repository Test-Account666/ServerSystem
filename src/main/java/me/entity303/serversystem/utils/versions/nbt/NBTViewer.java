package me.entity303.serversystem.utils.versions.nbt;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class NBTViewer implements INBTViewer {
    final HashMap<String, NamespacedKey> _keys = new HashMap<>();

    private NamespacedKey GetOrCreateKey(String tag) {
        return this._keys.entrySet()
                         .stream()
                         .filter(stringNamespacedKeyEntry -> stringNamespacedKeyEntry.getKey().equalsIgnoreCase(tag))
                         .map(Map.Entry::getValue)
                         .findFirst()
                         .orElse(new NamespacedKey(ServerSystem.getPlugin(ServerSystem.class), tag));
    }

    @Override
    public boolean IsTagSet(String tag, ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return false;

        if (!(itemStack.getItemMeta() instanceof PersistentDataHolder))
            return false;

        var meta = itemStack.getItemMeta();
        assert meta != null;

        var container = meta.getPersistentDataContainer();

        var namespacedKey = this.GetOrCreateKey(tag);
        return container.has(namespacedKey, PersistentDataType.BYTE);
    }

    @Override
    public ItemStack RemoveTag(String tag, ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return itemStack;

        if (!(itemStack.getItemMeta() instanceof PersistentDataHolder))
            return itemStack;

        if (!this.IsTagSet(tag, itemStack))
            return itemStack;

        var meta = itemStack.getItemMeta();
        assert meta != null;

        var container = meta.getPersistentDataContainer();

        var namespacedKey = this.GetOrCreateKey(tag);

        if (!container.has(namespacedKey, PersistentDataType.BYTE))
            return itemStack;

        container.remove(namespacedKey);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public ItemStack SetTag(String tag, ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            throw new NullPointerException("ItemStack doesn't have ItemMeta, cannot set tag!");

        if (!(itemStack.getItemMeta() instanceof PersistentDataHolder))
            throw new IllegalStateException("ItemStack ItemMeta doesn't extend PersistentDataHolder, cannot set tag!");

        if (this.IsTagSet(tag, itemStack))
            return itemStack;

        var meta = itemStack.getItemMeta();
        assert meta != null;

        var container = meta.getPersistentDataContainer();

        var namespacedKey = this.GetOrCreateKey(tag);

        container.set(namespacedKey, PersistentDataType.BYTE, (byte) 1);

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
