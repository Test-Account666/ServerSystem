package me.Entity303.ServerSystem.Listener.Vanish;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.SilentInventory.EnumDirectionList_v1_8_R3;
import me.Entity303.ServerSystem.SilentInventory.SilentInventory_v1_8_R3;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener_v1_8_R3 implements Listener {
    private final ss plugin;

    public InteractListener_v1_8_R3(ss plugin) {
        this.plugin = plugin;
    }

    public ss getPlugin() {
        return this.plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChestOpen(PlayerInteractEvent e) {

        if (!this.getPlugin().getVanish().isVanish(e.getPlayer())) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
            if (e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                e.setCancelled(true);
                this.activateChest(e.getPlayer(), e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ());
            } else if (!this.plugin.getVanish().getAllowInteract().contains(e.getPlayer()) && this.plugin.getCommandManager().isInteractActive()) {
                e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("vanish", "vanish", e.getPlayer().getName(), null, "Vanish.Misc.NoInteract"));
                e.setCancelled(true);
            }

    }

    private void activateChest(Player p, int x, int y, int z) {
        BlockPosition position = new BlockPosition(x, y, z);
        EntityPlayer player = ((CraftPlayer) p).getHandle();
        World world = player.world;
        if (world.isClientSide) return;
        BlockChest chest = (BlockChest) (((BlockChest) world.getType(position).getBlock()).b == 1 ? Block.getByName("trapped_chest") : Block.getByName("chest"));

        TileEntity tileEntity = world.getTileEntity(position);
        if (!(tileEntity instanceof TileEntityChest)) return;
        ITileInventory tileInventory = (ITileInventory) tileEntity;
        for (EnumDirection direction : EnumDirectionList_v1_8_R3.HORIZONTAL) {
            BlockPosition side = position.shift(direction);
            Block block = world.getType(side).getBlock();
            if (block == chest) {
                TileEntity sideTileEntity = world.getTileEntity(side);
                if ((sideTileEntity instanceof TileEntityChest))
                    if ((direction != EnumDirection.WEST) && (direction != EnumDirection.NORTH))
                        tileInventory = new InventoryLargeChest("container.chestDouble", tileInventory, (TileEntityChest) sideTileEntity);
                    else
                        tileInventory = new InventoryLargeChest("container.chestDouble", (TileEntityChest) sideTileEntity, tileInventory);
            }
        }
        tileInventory = new SilentInventory_v1_8_R3(tileInventory);
        player.openContainer(tileInventory);
    }
}
