package me.entity303.serversystem.silentinventory;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class SilentInventory_v1_8_R3 implements ITileInventory {

    public final ITileInventory inv;

    public SilentInventory_v1_8_R3(ITileInventory inv) {
        this.inv = inv;
    }

    public boolean r_() {
        return this.inv.r_();
    }

    public void a(ChestLock chestLock) {
        this.inv.a(chestLock);
    }

    public ChestLock i() {
        return this.inv.i();
    }

    public int getSize() {
        return this.inv.getSize();
    }

    public ItemStack getItem(int i) {
        return this.inv.getItem(i);
    }

    public ItemStack splitStack(int i, int i1) {
        return this.inv.splitStack(i, i1);
    }

    public ItemStack splitWithoutUpdate(int i) {
        return this.inv.splitWithoutUpdate(i);
    }

    public void setItem(int i, ItemStack itemStack) {
        this.inv.setItem(i, itemStack);
    }

    public int getMaxStackSize() {
        return this.inv.getMaxStackSize();
    }

    public void setMaxStackSize(int i) {
        this.inv.setMaxStackSize(i);
    }

    public void update() {
        this.inv.update();
    }

    public boolean a(EntityHuman entityHuman) {
        return this.inv.a(entityHuman);
    }

    public void startOpen(EntityHuman entityHuman) {
    }

    public void closeContainer(EntityHuman entityHuman) {
    }

    public boolean b(int i, ItemStack itemStack) {
        return this.inv.b(i, itemStack);
    }

    public int getProperty(int i) {
        return this.inv.getProperty(i);
    }

    public void b(int i, int i1) {
        this.inv.b(i, i1);
    }

    public int g() {
        return this.inv.g();
    }

    public void l() {
        this.inv.l();
    }

    public ItemStack[] getContents() {
        return this.inv.getContents();
    }

    public void onOpen(CraftHumanEntity craftHumanEntity) {
        this.inv.onOpen(craftHumanEntity);
    }

    public void onClose(CraftHumanEntity craftHumanEntity) {
        this.inv.onClose(craftHumanEntity);
    }

    public List<HumanEntity> getViewers() {
        return this.inv.getViewers();
    }

    public InventoryHolder getOwner() {
        return this.inv.getOwner();
    }

    public String getName() {
        return this.inv.getName();
    }

    public boolean hasCustomName() {
        return this.inv.hasCustomName();
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return this.inv.getScoreboardDisplayName();
    }

    public Container createContainer(PlayerInventory playerInventory, EntityHuman entityHuman) {
        return new ContainerChest(playerInventory, this, entityHuman);
    }

    public String getContainerName() {
        return this.inv.getContainerName();
    }
}
