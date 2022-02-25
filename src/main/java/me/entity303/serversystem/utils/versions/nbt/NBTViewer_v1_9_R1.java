package me.entity303.serversystem.utils.versions.nbt;

import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;

public class NBTViewer_v1_9_R1 implements NBTViewer {

    @Override
    public boolean isTagSet(String tag, org.bukkit.inventory.ItemStack itemStack) {
        ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
        if (!itemStack1.hasTag())
            itemStack1.setTag(new NBTTagCompound());

        NBTTagCompound nbtTagCompound = itemStack1.getTag();
        return nbtTagCompound.hasKey(tag);
    }

    @Override
    public org.bukkit.inventory.ItemStack removeTag(String tag, org.bukkit.inventory.ItemStack itemStack) {
        ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
        if (!itemStack1.hasTag())
            return itemStack;

        if (!this.isTagSet(tag, itemStack))
            return itemStack;

        NBTTagCompound nbtTagCompound = itemStack1.getTag();
        nbtTagCompound.remove(tag);
        itemStack1.setTag(nbtTagCompound);

        itemStack.setItemMeta(CraftItemStack.getItemMeta(itemStack1));
        return itemStack;
    }

    @Override
    public org.bukkit.inventory.ItemStack setTag(String tag, org.bukkit.inventory.ItemStack itemStack) {
        ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
        if (!itemStack1.hasTag())
            itemStack1.setTag(new NBTTagCompound());

        if (this.isTagSet(tag, itemStack))
            return itemStack;

        NBTTagCompound nbtTagCompound = itemStack1.getTag();
        nbtTagCompound.setByte(tag, (byte) 1);
        itemStack1.setTag(nbtTagCompound);

        itemStack.setItemMeta(CraftItemStack.getItemMeta(itemStack1));
        return itemStack;
    }
}
