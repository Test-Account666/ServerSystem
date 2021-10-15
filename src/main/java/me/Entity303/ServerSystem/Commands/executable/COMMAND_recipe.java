package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.*;

public class COMMAND_recipe extends MessageUtils implements CommandExecutor {
    private final static List<Player> recipeList = new ArrayList<>();

    public COMMAND_recipe(ss plugin) {
        super(plugin);
    }

    public static List<Player> getRecipeList() {
        return COMMAND_recipe.recipeList;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.recipe.required"))
            if (!this.isAllowed(cs, "recipe.permission")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("recipe.permission")));
                return true;
            }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        Player player = (Player) cs;
        if (args.length <= 0) {
            ItemStack stack = player.getInventory().getItemInHand();
            if (stack.getType() == Material.AIR) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Recipe.NoItem", label, cmd.getName(), cs, null));
                return true;
            }
            List<Recipe> recipeList = Bukkit.getRecipesFor(stack);
            if (recipeList.isEmpty()) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Recipe.NoRecipe", label, cmd.getName(), cs, null).replace("<MATERIAL>", stack.getType().name()));
                return true;
            }
            COMMAND_recipe.recipeList.add((Player) cs);
            Recipe recipe = recipeList.get(0);
            if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
                InventoryView inventoryView = player.openWorkbench(player.getLocation(), true);
                Inventory craftingInventory = inventoryView.getTopInventory();
                ItemStack[] contents = new ItemStack[10];
                for (int i = 0; i < 10; i++) contents[i] = null;

                for (int i = 1, i1 = 0; i < Math.min(shapelessRecipe.getIngredientList().size() + 1, 11); i++, i1++)
                    contents[i] = this.normalize(shapelessRecipe.getIngredientList().get(i1));

                craftingInventory.setContents(contents);
                return true;
            }

            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                InventoryView inventoryView = player.openWorkbench(player.getLocation(), true);
                Inventory craftingInventory = inventoryView.getTopInventory();
                ItemStack[] contents = new ItemStack[10];
                for (int i = 0; i < 10; i++) contents[i] = null;

                Map<Character, ItemStack> ingredient = new LinkedHashMap<>();

                String[] rows = shapedRecipe.getShape();

                List<String> alphabet = new ArrayList<>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".split("")));

                for (String row : rows) for (String character : row.split("")) alphabet.remove(character);

                for (String row : rows) {
                    StringBuilder rowEdit = new StringBuilder(row);
                    int rowLength = row.split("").length;
                    for (String character : row.split(""))
                        if (character.equalsIgnoreCase(" ")) ingredient.put(character.charAt(0), null);
                        else
                            ingredient.put(character.charAt(0), shapedRecipe.getIngredientMap().get(character.charAt(0)));

                    String usedCharacter = "";

                    for (int i = rowLength; i < 3; i++)
                        for (String character : alphabet)
                            if (!rowEdit.toString().contains(character)) {
                                rowEdit.append(character);
                                ingredient.put(character.charAt(0), null);
                                usedCharacter = character;
                                break;
                            }

                    for (String character : usedCharacter.split("")) alphabet.remove(character);
                }

                int i = 1;
                for (ItemStack itemStack : ingredient.values()) {
                    if (i >= 10) break;
                    contents[i] = this.normalize(itemStack);
                    i += 1;
                }

                craftingInventory.setContents(contents);
                return true;
            }

            cs.sendMessage(this.getPrefix() + this.getMessage("Recipe.NoRecipe", label, cmd.getName(), cs, null).replace("<MATERIAL>", stack.getType().name()));
            return true;
        }

        String name = args[0].toUpperCase().split(":")[0];
        int subId = 0;
        if (name.contains(":")) try {
            subId = Integer.parseInt(args[0].split(":")[1]);
        } catch (Exception ignored) {
        }
        Material material = Material.getMaterial(name);
        if (material == null) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Recipe.InvalidMaterial", label, cmd.getName(), cs, null).replace("<MATERIAL>", name));
            return true;
        }

        ItemStack stack = new ItemStack(material);

        if (stack.getType() == Material.AIR) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Recipe.InvalidMaterial", label, cmd.getName(), cs, null).replace("<MATERIAL>", name));
            return true;
        }

        if (!this.plugin.getVersionManager().isV119()) stack.setDurability((short) subId);

        List<Recipe> recipeList = Bukkit.getRecipesFor(stack);
        if (recipeList.isEmpty()) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Recipe.NoRecipe", label, cmd.getName(), cs, null).replace("<MATERIAL>", name));
            return true;
        }
        COMMAND_recipe.recipeList.add((Player) cs);
        Recipe recipe = recipeList.get(0);
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
            InventoryView inventoryView = player.openWorkbench(player.getLocation(), true);
            Inventory craftingInventory = inventoryView.getTopInventory();
            ItemStack[] contents = new ItemStack[10];
            for (int i = 0; i < 10; i++) contents[i] = null;

            for (int i = 1, i1 = 0; i < Math.min(shapelessRecipe.getIngredientList().size() + 1, 11); i++, i1++)
                contents[i] = this.normalize(shapelessRecipe.getIngredientList().get(i1));

            craftingInventory.setContents(contents);
            return true;
        }

        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            InventoryView inventoryView = player.openWorkbench(player.getLocation(), true);
            Inventory craftingInventory = inventoryView.getTopInventory();
            ItemStack[] contents = new ItemStack[10];
            for (int i = 0; i < 10; i++) contents[i] = null;

            Map<Character, ItemStack> ingredient = new LinkedHashMap<>();

            String[] rows = shapedRecipe.getShape();

            List<String> alphabet = new ArrayList<>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".split("")));

            for (String row : rows) for (String character : row.split("")) alphabet.remove(character);

            for (String row : rows) {
                StringBuilder rowEdit = new StringBuilder(row);
                int rowLength = row.split("").length;
                for (String character : row.split(""))
                    if (character.equalsIgnoreCase(" ")) ingredient.put(character.charAt(0), null);
                    else
                        ingredient.put(character.charAt(0), shapedRecipe.getIngredientMap().get(character.charAt(0)));

                String usedCharacter = "";

                for (int i = rowLength; i < 3; i++)
                    for (String character : alphabet)
                        if (!rowEdit.toString().contains(character)) {
                            rowEdit.append(character);
                            ingredient.put(character.charAt(0), null);
                            usedCharacter = character;
                            break;
                        }

                for (String character : usedCharacter.split("")) alphabet.remove(character);
            }

            int i = 1;
            for (ItemStack itemStack : ingredient.values()) {
                if (i >= 10) break;
                contents[i] = this.normalize(itemStack);
                i += 1;
            }

            craftingInventory.setContents(contents);
            return true;
        }

        cs.sendMessage(this.getPrefix() + this.getMessage("Recipe.NoRecipe", label, cmd.getName(), cs, null).replace("<MATERIAL>", name));

        return true;
    }

    private ItemStack normalize(ItemStack itemStack) {
        if (this.plugin.getVersionManager().isV119()) return itemStack;
        if (itemStack == null) return itemStack;
        if (itemStack.getDurability() > 15) itemStack.setDurability((short) 0);
        return itemStack;
    }
}
