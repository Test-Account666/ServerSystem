package me.entity303.serversystem.silentinventory;

import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class SilentInventory_v1_9_R2 implements ITileInventory {
    public final ITileInventory inv;

    public SilentInventory_v1_9_R2(ITileInventory inv) {
        this.inv = inv;
    }

    public boolean r_() {
        return this.inv.x_();
    }

    @Override
    public boolean x_() {
        return false;
    }

    @Override
    public void a(ChestLock chestLock) {
        this.inv.a(chestLock);
    }

    @Override
    public ChestLock y_() {
        return null;
    }

    public ChestLock i() {
        return this.inv.y_();
    }

    @Override
    public int getSize() {
        return this.inv.getSize();
    }

    @Override
    public ItemStack getItem(int i) {
        return this.inv.getItem(i);
    }

    @Override
    public ItemStack splitStack(int i, int i1) {
        return this.inv.splitStack(i, i1);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        return this.inv.splitWithoutUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.inv.setItem(i, itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return this.inv.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int i) {
        this.inv.setMaxStackSize(i);
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void update() {
        this.inv.update();
    }

    @Override
    public boolean a(EntityHuman entityHuman) {
        return this.inv.a(entityHuman);
    }

    @Override
    public void startOpen(EntityHuman entityHuman) {
    }

    @Override
    public void closeContainer(EntityHuman entityHuman) {
    }

    @Override
    public boolean b(int i, ItemStack itemStack) {
        return this.inv.b(i, itemStack);
    }

    @Override
    public int getProperty(int i) {
        return this.inv.getProperty(i);
    }

    @Override
    public void setProperty(int i, int i1) {

    }

    public void b(int i, int i1) {
        Item itemStack = Item.getById(i1);
        ItemStack item = new ItemStack(itemStack);
        this.inv.b(i, item);
    }

    @Override
    public int g() {
        return this.inv.g();
    }

    @Override
    public void l() {
        this.inv.l();
    }

    @Override
    public ItemStack[] getContents() {
        return this.inv.getContents();
    }

    @Override
    public void onOpen(CraftHumanEntity craftHumanEntity) {
        this.inv.onOpen(craftHumanEntity);
    }

    @Override
    public void onClose(CraftHumanEntity craftHumanEntity) {
        this.inv.onClose(craftHumanEntity);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return this.inv.getViewers();
    }

    @Override
    public InventoryHolder getOwner() {
        return this.inv.getOwner();
    }

    @Override
    public String getName() {
        return this.inv.getName();
    }

    @Override
    public boolean hasCustomName() {
        return this.inv.hasCustomName();
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return this.inv.getScoreboardDisplayName();
    }

    @Override
    public Container createContainer(PlayerInventory playerInventory, EntityHuman entityHuman) {
        return new ContainerChest(playerInventory, this, entityHuman);
    }

    @Override
    public String getContainerName() {
        return this.inv.getContainerName();
    }
}
