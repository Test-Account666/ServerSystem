package me.entity303.serversystem.utils.versions.nbt;

import org.bukkit.inventory.ItemStack;

public interface NBTViewer {

    boolean isTagSet(String tag, ItemStack itemStack);

    ItemStack removeTag(String tag, ItemStack itemStack);

    ItemStack setTag(String tag, ItemStack itemStack);
}
