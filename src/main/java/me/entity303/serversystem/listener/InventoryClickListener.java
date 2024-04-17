package me.entity303.serversystem.listener;

import me.entity303.serversystem.commands.executable.RecipeCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryClickListener extends CommandUtils implements Listener {

    public InventoryClickListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        this.plugin.getEnderchest().remove(e.getPlayer());
        if (RecipeCommand.getRecipeList().contains(e.getPlayer())) {
            var contents = new ItemStack[10];
            for (var i = 0; i < 10; i++)
                contents[i] = null;
            if (e.getInventory() instanceof CraftingInventory)
                e.getInventory().setContents(contents);
        }
        RecipeCommand.getRecipeList().remove(e.getPlayer());
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;

        if (e.getView().getTopInventory() instanceof PlayerInventory)
            if (e.getView().getTopInventory().getSize() == 45) {
                var action = e.getAction();
                if (action.name().equalsIgnoreCase("CLONE_STACK")) {
                    if (e.getSlot() > 40)
                        e.setCancelled(true);
                    if (e.getCursor().getType() == Material.DROPPER)
                        e.setCancelled(true);
                    return;
                } else if (action.name().equalsIgnoreCase("HOTBAR_MOVE_AND_READD") || action.name().equalsIgnoreCase("HOTBAR_SWAP") ||
                           action.name().equalsIgnoreCase("MOVE_TO_OTHER_INVENTORY")) {
                    if (e.getSlot() > 40)
                        e.setCancelled(true);

                    if (e.getCursor().getType() == Material.DROPPER)
                        e.setCancelled(true);
                    return;
                } else if (action.name().equalsIgnoreCase("COLLECT_TO_CURSOR")) {
                    if (e.getCursor().getType() == Material.DROPPER)
                        e.setCancelled(true);
                    return;
                }
                return;
            }

        if (RecipeCommand.getRecipeList().contains(e.getWhoClicked())) {
            if (e.getClickedInventory() instanceof PlayerInventory)
                return;
            e.setCancelled(true);
            return;
        }

        if (this.plugin.getEnderchest().containsKey(e.getWhoClicked())) {
            var owner = this.plugin.getEnderchest().get(e.getWhoClicked());
            if (owner == e.getWhoClicked())
                return;
            if (!this.plugin.getPermissions().hasPermission(owner, "enderchest.exempt", true))
                return;
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory().getHolder() == null)
            return;
        if (e.getClickedInventory().getHolder() == e.getWhoClicked())
            return;
        var holder = e.getInventory().getHolder();
        if (!(holder instanceof HumanEntity targetEntity))
            return;
        if (e.getInventory().getType() == InventoryType.ENDER_CHEST) {
            if (!this.plugin.getPermissions().hasPermission(targetEntity, "enderchest.exempt", true))
                return;
            e.setCancelled(true);
            return;
        }
        if (!this.plugin.getPermissions().hasPermission(targetEntity, "invsee.exempt", true))
            return;
        e.setCancelled(true);
    }
}
