package me.Entity303.ServerSystem.Listener;

import me.Entity303.ServerSystem.Commands.executable.COMMAND_recipe;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryClickListener extends MessageUtils implements Listener {

    public InventoryClickListener(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        this.plugin.getEnderchest().remove(e.getPlayer());
        if (COMMAND_recipe.getRecipeList().contains(e.getPlayer())) {
            ItemStack[] contents = new ItemStack[10];
            for (int i = 0; i < 10; i++) contents[i] = null;
            if (e.getInventory() instanceof CraftingInventory) e.getInventory().setContents(contents);
        }
        COMMAND_recipe.getRecipeList().remove(e.getPlayer());
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (COMMAND_recipe.getRecipeList().contains(e.getWhoClicked())) {
            if (e.getClickedInventory() instanceof PlayerInventory) return;
            e.setCancelled(true);
            return;
        }

        if (this.plugin.getEnderchest().containsKey(e.getWhoClicked())) {
            Player owner = this.plugin.getEnderchest().get(e.getWhoClicked());
            if (owner == e.getWhoClicked()) return;
            if (!this.isAllowed(owner, "enderchest.exempt", true)) return;
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory().getHolder() == null) return;
        if (e.getClickedInventory().getHolder() == e.getWhoClicked()) return;
        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof HumanEntity)) return;
        HumanEntity targetEntity = (HumanEntity) holder;
        if (e.getInventory().getType() == InventoryType.ENDER_CHEST) {
            if (!this.isAllowed(targetEntity, "enderchest.exempt", true)) return;
            e.setCancelled(true);
            return;
        }
        if (!this.isAllowed(targetEntity, "invsee.exempt", true)) return;
        e.setCancelled(true);
    }
}
