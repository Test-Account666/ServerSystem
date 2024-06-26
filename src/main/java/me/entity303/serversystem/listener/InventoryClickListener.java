package me.entity303.serversystem.listener;

import me.entity303.serversystem.commands.executable.RecipeCommand;
import me.entity303.serversystem.main.ServerSystem;
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

public class InventoryClickListener implements Listener {

    protected final ServerSystem _plugin;

    public InventoryClickListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event) {
        this._plugin.GetEnderchest().remove(event.getPlayer());
        if (RecipeCommand.GetRecipeList().contains(event.getPlayer())) {
            var contents = new ItemStack[10];
            for (var index = 0; index < 10; index++)
                contents[index] = null;
            if (event.getInventory() instanceof CraftingInventory)
                event.getInventory().setContents(contents);
        }
        RecipeCommand.GetRecipeList().remove(event.getPlayer());
    }


    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;

        if (event.getView().getTopInventory() instanceof PlayerInventory)
            if (event.getView().getTopInventory().getSize() == 45) {
                var action = event.getAction();
                if (action.name().equalsIgnoreCase("CLONE_STACK")) {
                    if (event.getSlot() > 40)
                        event.setCancelled(true);
                    if (event.getCursor().getType() == Material.DROPPER)
                        event.setCancelled(true);
                    return;
                } else if (action.name().equalsIgnoreCase("HOTBAR_MOVE_AND_READD") || action.name().equalsIgnoreCase("HOTBAR_SWAP") ||
                           action.name().equalsIgnoreCase("MOVE_TO_OTHER_INVENTORY")) {
                    if (event.getSlot() > 40)
                        event.setCancelled(true);

                    if (event.getCursor().getType() == Material.DROPPER)
                        event.setCancelled(true);
                    return;
                } else if (action.name().equalsIgnoreCase("COLLECT_TO_CURSOR")) {
                    if (event.getCursor().getType() == Material.DROPPER)
                        event.setCancelled(true);
                    return;
                }
                return;
            }

        if (RecipeCommand.GetRecipeList().contains(event.getWhoClicked())) {
            if (event.getClickedInventory() instanceof PlayerInventory)
                return;
            event.setCancelled(true);
            return;
        }

        if (this._plugin.GetEnderchest().containsKey(event.getWhoClicked())) {
            var owner = this._plugin.GetEnderchest().get(event.getWhoClicked());
            if (owner == event.getWhoClicked())
                return;
            if (!this._plugin.GetPermissions().HasPermission(owner, "enderchest.exempt", true))
                return;
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory().getHolder() == null)
            return;
        if (event.getClickedInventory().getHolder() == event.getWhoClicked())
            return;
        var holder = event.getInventory().getHolder();
        if (!(holder instanceof HumanEntity targetEntity))
            return;
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            if (!this._plugin.GetPermissions().HasPermission(targetEntity, "enderchest.exempt", true))
                return;
            event.setCancelled(true);
            return;
        }
        if (!this._plugin.GetPermissions().HasPermission(targetEntity, "invsee.exempt", true))
            return;
        event.setCancelled(true);
    }
}
