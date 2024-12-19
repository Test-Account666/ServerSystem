package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;

import static me.entity303.serversystem.commands.executable.UnlimitedCommand.IsUnlimited;

public class UnlimitedListener implements Listener {
    private final HashSet<HumanEntity> _openInventory = new HashSet<>();

    @EventHandler
    public void OnOpen(InventoryOpenEvent event) {
        this._openInventory.add(event.getPlayer());
    }

    @EventHandler
    public void OnClose(InventoryCloseEvent event) {
        this._openInventory.remove(event.getPlayer());
    }

    @EventHandler
    public void OnInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof PlayerInventory playerInventory) {
            if (playerInventory.getHolder() == event.getWhoClicked()) this._openInventory.add(event.getWhoClicked());
        }
    }

    @EventHandler
    public void OnProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {

            if (player.getGameMode() == GameMode.CREATIVE) return;

            if (player.getInventory().getItemInMainHand() == null) return;

            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return;

            if (player.getInventory().getItemInMainHand().getType() == Material.BOW) return;

            if (player.getInventory().getItemInMainHand().getType().name().equalsIgnoreCase("CROSSBOW")) return;

            var itemStack = player.getInventory().getItemInMainHand();

            if (IsUnlimited(itemStack)) {
                itemStack.setAmount(itemStack.getAmount() + 1);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void OnConsume(PlayerItemConsumeEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (IsUnlimited(event.getItem())) {
            var itemStack = event.getPlayer().getInventory().getItemInMainHand();
            itemStack.setAmount(itemStack.getAmount() + 1);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnBucketPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (event.getItem() == null) return;

        if (event.getItem().getType() == Material.AIR) return;

        if (event.getItem().getType() == Material.BUCKET) {
            if (!IsUnlimited(event.getItem())) return;

            if (event.isCancelled()) return;

            var slot = event.getPlayer().getInventory().getHeldItemSlot();
            var itemStack = event.getItem().clone();
            for (var index = 0; index < 20; index++)
                Bukkit.getScheduler()
                      .runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().getInventory().setItem(slot, itemStack), 1L + index);
            return;
        }

        if (event.getItem().getType() != Material.LAVA_BUCKET && event.getItem().getType() != Material.WATER_BUCKET) return;

        if (!IsUnlimited(event.getItem())) return;

        if (event.isCancelled()) return;

        var itemStack = event.getItem().clone();
        var slot = event.getPlayer().getInventory().getHeldItemSlot();

        Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().getInventory().setItem(slot, itemStack), 1L);
        Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().getInventory().setItem(slot, itemStack), 2L);
        Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().getInventory().setItem(slot, itemStack), 3L);
    }

    @EventHandler
    public void OnDrop(PlayerDropItemEvent event) {
        if (!IsUnlimited(event.getItemDrop().getItemStack())) return;

        if (this._openInventory.contains(event.getPlayer())) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                var itemStack = event.getItemDrop().getItemStack().clone();
                itemStack.setAmount(itemStack.getMaxStackSize());
                event.getPlayer().setItemOnCursor(itemStack);

                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().updateInventory(), 1L);
                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().updateInventory(), 2L);
                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().updateInventory(), 3L);
                return;
            }

            if (event.getPlayer().getOpenInventory().getCursor() == null || event.getPlayer().getOpenInventory().getCursor().getType() == Material.AIR) {
                var itemStack = event.getItemDrop().getItemStack();
                if (itemStack.getAmount() > itemStack.getType().getMaxStackSize()) itemStack.setAmount(itemStack.getMaxStackSize());

                event.getPlayer().getOpenInventory().setCursor(itemStack);

                Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().updateInventory(), 5L);
                return;
            }

            var itemStack = event.getPlayer().getOpenInventory().getCursor();
            itemStack.setAmount(itemStack.getAmount() + 1);
            Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getPlayer().updateInventory(), 5L);
            return;
        }

        if (event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR ||
            event.getPlayer().getInventory().getItemInMainHand().getAmount() <= 0) {
            event.getPlayer().getInventory().setItemInHand(event.getItemDrop().getItemStack());
            event.getPlayer().updateInventory();
            return;
        }

        event.getPlayer()
             .getInventory()
             .getItemInMainHand()
             .setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() + event.getItemDrop().getItemStack().getAmount());
        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void OnDispense(BlockDispenseEvent event) {
        if (IsUnlimited(event.getItem())) {
            var inventory = ((InventoryHolder) event.getBlock().getState()).getInventory();
            Bukkit.getScheduler().runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> inventory.addItem(event.getItem()), 1L);
        }
    }

    @EventHandler
    public void OnItemDamage(PlayerItemDamageEvent event) {
        if (IsUnlimited(event.getItem())) event.setCancelled(true);
    }

    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            if (IsUnlimited(event.getItemInHand())) {
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() + 1);
                Bukkit.getScheduler()
                      .runTaskLater(ServerSystem.getPlugin(ServerSystem.class), () -> event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1), 1L);
                event.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler
    public void OnEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item item) {
            if (IsUnlimited(item.getItemStack())) ServerSystem.getPlugin(ServerSystem.class).GetVersionStuff().GetNbtViewer().RemoveTag("unlimited", item.getItemStack());
        }
    }
}
