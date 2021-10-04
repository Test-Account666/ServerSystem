package me.Entity303.ServerSystem.Listener.Vanish;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.SilentInventory.SilentInventory_v1_13_R1;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.ITileInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class InteractListener_v1_13_R1 implements Listener {
    private final ss plugin;

    public InteractListener_v1_13_R1(ss plugin) {
        this.plugin = plugin;
    }

    public ss getPlugin() {
        return this.plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if (!this.getPlugin().getVanish().isVanish(e.getPlayer())) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
            e.setCancelled(true);
            this.activateChest(e.getPlayer(), e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ());
        } else if (!this.plugin.getVanish().getAllowInteract().contains(e.getPlayer()) && this.plugin.getCommandManager().isInteractActive()) {
            e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("vanish", "vanish", e.getPlayer().getName(), null, "Vanish.Misc.NoInteract"));
            e.setCancelled(true);
        }
    }

    private void activateChest(Player p, int x, int y, int z) {
        Location loc = new Location(p.getWorld(), x, y, z);
        org.bukkit.block.Block theChestBlock = loc.getBlock();
        org.bukkit.block.BlockState chestState = theChestBlock.getState();
        if (chestState instanceof Chest) {
            Chest ches = (Chest) chestState;
            Inventory inventory = ches.getInventory();
            if (inventory instanceof DoubleChestInventory) {
                DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
                CraftInventory craftInventory = (CraftInventory) doubleChest.getInventory();
                EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
                SilentInventory_v1_13_R1 silentInventory = new SilentInventory_v1_13_R1((ITileInventory) craftInventory.getInventory());
                silentInventory.createContainer(entityPlayer.inventory, entityPlayer);
                entityPlayer.openContainer(silentInventory);
            } else {
                CraftInventory craftInventory = (CraftInventory) inventory;
                EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
                SilentInventory_v1_13_R1 silentInventory = new SilentInventory_v1_13_R1((ITileInventory) craftInventory.getInventory());
                silentInventory.createContainer(entityPlayer.inventory, entityPlayer);
                entityPlayer.openContainer(silentInventory);
            }
        }
    }
}
