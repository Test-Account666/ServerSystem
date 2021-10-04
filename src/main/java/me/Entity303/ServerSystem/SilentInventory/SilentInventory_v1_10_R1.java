package me.Entity303.ServerSystem.SilentInventory;

import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class SilentInventory_v1_10_R1 implements ITileInventory {
    public final ITileInventory inv;

    public SilentInventory_v1_10_R1(ITileInventory inv) {
        this.inv = inv;
    }

    public boolean r_() {
        return inv.x_();
    }

    @Override
    public boolean x_() {
        return false;
    }

    @Override
    public void a(ChestLock chestLock) {
        inv.a(chestLock);
    }

    @Override
    public ChestLock y_() {
        return null;
    }

    public ChestLock i() {
        return inv.y_();
    }

    @Override
    public int getSize() {
        return inv.getSize();
    }

    @Override
    public ItemStack getItem(int i) {
        return inv.getItem(i);
    }

    @Override
    public ItemStack splitStack(int i, int i1) {
        return inv.splitStack(i, i1);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        return inv.splitWithoutUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        inv.setItem(i, itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return inv.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int i) {
        inv.setMaxStackSize(i);
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void update() {
        inv.update();
    }

    @Override
    public boolean a(EntityHuman entityHuman) {
        return inv.a(entityHuman);
    }

    @Override
    public void startOpen(EntityHuman entityHuman) {
    }

    @Override
    public void closeContainer(EntityHuman entityHuman) {
    }

    @Override
    public boolean b(int i, ItemStack itemStack) {
        return inv.b(i, itemStack);
    }

    @Override
    public int getProperty(int i) {
        return inv.getProperty(i);
    }

    @Override
    public void setProperty(int i, int i1) {

    }

    public void b(int i, int i1) {
        Item itemStack = Item.getById(i1);
        ItemStack item = new ItemStack(itemStack);
        inv.b(i, item);
    }

    @Override
    public int g() {
        return inv.g();
    }

    @Override
    public void l() {
        inv.l();
    }

    @Override
    public ItemStack[] getContents() {
        return inv.getContents();
    }

    @Override
    public void onOpen(CraftHumanEntity craftHumanEntity) {
        inv.onOpen(craftHumanEntity);
    }

    @Override
    public void onClose(CraftHumanEntity craftHumanEntity) {
        inv.onClose(craftHumanEntity);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return inv.getViewers();
    }

    @Override
    public InventoryHolder getOwner() {
        return inv.getOwner();
    }

    @Override
    public String getName() {
        return inv.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inv.hasCustomName();
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return inv.getScoreboardDisplayName();
    }

    @Override
    public Container createContainer(PlayerInventory playerInventory, EntityHuman entityHuman) {
        return new ContainerChest(playerInventory, this, entityHuman);
    }

    @Override
    public String getContainerName() {
        return inv.getContainerName();
    }
}
