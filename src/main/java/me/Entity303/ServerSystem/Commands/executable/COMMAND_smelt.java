package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;


public class COMMAND_smelt extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_smelt(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (!this.isAllowed(cs, "smelt")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("smelt")));
            return true;
        }

        ((Player) cs).getInventory().getItemInHand();

        try {
            if (((Player) cs).getInventory().getItemInHand().getType() == Material.AIR) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Smelt.NoItem", label, cmd.getName(), cs, null));
                return true;
            }
        } catch (Exception ignored) {
        }

        boolean recipeFound = false;
        ItemStack end = null;

        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof FurnaceRecipe) {
                FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;
                if (furnaceRecipe.getInput().getType() == ((Player) cs).getInventory().getItemInHand().getType()) {
                    end = furnaceRecipe.getResult();
                    recipeFound = true;
                    break;
                }
            }
        }

        if (recipeFound) {
            ((Player) cs).getInventory().getItemInHand().setType(end.getType());
            ((Player) cs).getInventory().getItemInHand().setDurability(end.getDurability());
            cs.sendMessage(this.getPrefix() + this.getMessage("Smelt.Success", label, cmd.getName(), cs, null).replace("<ITEM>", end.getType() + ":" + end.getDurability()));
            return true;
        }


        ItemStack result = this.plugin.getFurnace().getResult(((Player) cs).getInventory().getItemInHand());

        if (result != null) {
            end = result;
            ((Player) cs).getInventory().getItemInHand().setType(end.getType());
            ((Player) cs).getInventory().getItemInHand().setDurability(end.getDurability());
            cs.sendMessage(this.getPrefix() + this.getMessage("Smelt.Success", label, cmd.getName(), cs, null).replace("<ITEM>", end.getType() + ":" + end.getDurability()));
            return true;
        }

        cs.sendMessage(this.getPrefix() + this.getMessage("Smelt.NotSmeltable", label, cmd.getName(), cs, null));
        return true;
    }

}
