package me.entity303.serversystem.utils.versions.nbt;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.util.HashMap;
import java.util.Map;

public class NBTViewer_v1_13_R1 implements NBTViewer {
    HashMap<String, NamespacedKey> keys = new HashMap<>();

    private NamespacedKey getOrCreateKey(String tag) {
        return this.keys.entrySet().stream().filter(stringNamespacedKeyEntry -> stringNamespacedKeyEntry.getKey().equalsIgnoreCase(tag)).map(Map.Entry::getValue).findFirst().orElse(new NamespacedKey(ServerSystem.getPlugin(ServerSystem.class), tag));
    }

    @Override
    public boolean isTagSet(String tag, ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return false;

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        NamespacedKey namespacedKey = this.getOrCreateKey(tag);

        return meta.getCustomTagContainer().hasCustomTag(namespacedKey, ItemTagType.BYTE);
    }

    @Override
    public ItemStack removeTag(String tag, ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            return itemStack;

        if (!isTagSet(tag, itemStack))
            return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        NamespacedKey namespacedKey = this.getOrCreateKey(tag);

        meta.getCustomTagContainer().removeCustomTag(namespacedKey);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public ItemStack setTag(String tag, ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            throw new NullPointerException("ItemStack doesn't have ItemMeta, cannot set tag!");

        if (isTagSet(tag, itemStack))
            return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        NamespacedKey namespacedKey = this.getOrCreateKey(tag);

        meta.getCustomTagContainer().setCustomTag(namespacedKey, ItemTagType.BYTE, (byte) 1);

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
