package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;

import static me.entity303.serversystem.commands.executable.UnlimitedCommand.isUnlimited;

public class UnlimitedListener implements Listener {
    private final HashSet<Player> openInventory = new HashSet<>();

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        this.openInventory.add((Player) e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        this.openInventory.remove(e.getPlayer());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory) e.getClickedInventory();
            if (playerInventory.getHolder() == e.getWhoClicked()) this.openInventory.add((Player) e.getWhoClicked());
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();

            if (player.getGameMode() == GameMode.CREATIVE)
                return;

            if (player.getInventory().getItemInHand() == null)
                return;

            if (player.getInventory().getItemInHand().getType() == Material.AIR)
                return;

            if (player.getInventory().getItemInHand().getType() == Material.BOW)
                return;

            if (player.getInventory().getItemInHand().getType().name().equalsIgnoreCase("CROSSBOW"))
                return;

            ItemStack itemStack = player.getInventory().getItemInHand();

            if (isUnlimited(itemStack)) {
                itemStack.setAmount(itemStack.getAmount() + 1);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (isUnlimited(e.getItem())) {
            ItemStack itemStack = e.getPlayer().getInventory().getItemInHand();
            itemStack.setAmount(itemStack.getAmount() + 1);
            e.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketPlace(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (e.getItem() == null)
            return;

        if (e.getItem().getType() == Material.AIR)
            return;

        if (e.getItem().getType() == Material.BUCKET) {
            if (!isUnlimited(e.getItem()))
                return;

            if (e.isCancelled())
                return;

            int slot = e.getPlayer().getInventory().getHeldItemSlot();
            ItemStack itemStack = e.getItem().clone();
            for (int i = 0; i < 20; i++)
                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().getInventory().setItem(slot, itemStack), 1L + i);
            return;
        }

        if (e.getItem().getType() != Material.LAVA_BUCKET && e.getItem().getType() != Material.WATER_BUCKET)
            return;

        if (!isUnlimited(e.getItem()))
            return;

        if (e.isCancelled())
            return;

        ItemStack itemStack = e.getItem().clone();
        int slot = e.getPlayer().getInventory().getHeldItemSlot();

        Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().getInventory().setItem(slot, itemStack), 1L);
        Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().getInventory().setItem(slot, itemStack), 2L);
        Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().getInventory().setItem(slot, itemStack), 3L);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!isUnlimited(e.getItemDrop().getItemStack()))
            return;

        if (this.openInventory.contains(e.getPlayer())) {
            if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                ItemStack itemStack = e.getItemDrop().getItemStack().clone();
                itemStack.setAmount(itemStack.getMaxStackSize());
                e.getPlayer().setItemOnCursor(itemStack);

                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().updateInventory(), 1L);
                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().updateInventory(), 2L);
                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().updateInventory(), 3L);
                return;
            }

            if (e.getPlayer().getOpenInventory().getCursor() == null || e.getPlayer().getOpenInventory().getCursor().getType() == Material.AIR) {
                ItemStack itemStack = e.getItemDrop().getItemStack();
                if (itemStack.getAmount() > itemStack.getType().getMaxStackSize())
                    itemStack.setAmount(itemStack.getMaxStackSize());

                e.getPlayer().getOpenInventory().setCursor(itemStack);

                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().updateInventory(), 5L);
                return;
            }

            ItemStack itemStack = e.getPlayer().getOpenInventory().getCursor();
            itemStack.setAmount(itemStack.getAmount() + 1);
            Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getPlayer().updateInventory(), 5L);
            return;
        }

        if (e.getPlayer().getInventory().getItemInHand() == null || e.getPlayer().getInventory().getItemInHand().getType() == Material.AIR || e.getPlayer().getInventory().getItemInHand().getAmount() <= 0) {
            e.getPlayer().getInventory().setItemInHand(e.getItemDrop().getItemStack());
            e.getPlayer().updateInventory();
            return;
        }

        e.getPlayer().getInventory().getItemInHand().setAmount(e.getPlayer().getInventory().getItemInHand().getAmount() + e.getItemDrop().getItemStack().getAmount());
        e.getPlayer().updateInventory();
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent e) {
        if (isUnlimited(e.getItem())) {
            Inventory inventory = ((InventoryHolder) e.getBlock().getState()).getInventory();
            Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> inventory.addItem(e.getItem()), 1L);
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent e) {
        if (isUnlimited(e.getItem()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL || e.getPlayer().getGameMode() == GameMode.ADVENTURE)
            if (isUnlimited(e.getItemInHand())) {
                e.getItemInHand().setAmount(e.getItemInHand().getAmount() + 1);
                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> e.getItemInHand().setAmount(e.getItemInHand().getAmount() - 1), 1L);
                e.getPlayer().updateInventory();
            }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof Item) {
            Item item = (Item) e.getEntity();
            if (isUnlimited(item.getItemStack()))
                ServerSystem.getPlugin(ServerSystem.class).getVersionStuff().getNbtViewer().removeTag("unlimited", item.getItemStack());
        }
    }
}
