package me.entity303.serversystem.utils.versions.nbt;

import org.bukkit.inventory.ItemStack;

public interface INBTViewer {

    boolean IsTagSet(String tag, ItemStack itemStack);

    ItemStack RemoveTag(String tag, ItemStack itemStack);

    ItemStack SetTag(String tag, ItemStack itemStack);
}
