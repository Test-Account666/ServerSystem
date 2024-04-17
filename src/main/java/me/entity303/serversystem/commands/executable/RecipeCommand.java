package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class RecipeCommand extends CommandUtils implements CommandExecutorOverload {
    private final static List<Player> recipeList = new ArrayList<>();

    public RecipeCommand(ServerSystem plugin) {
        super(plugin);
    }

    public static List<Player> getRecipeList() {
        return RecipeCommand.recipeList;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.recipe.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "recipe.permission")) {
                var permission = this.plugin.getPermissions().getPermission("recipe.permission");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (arguments.length == 0) {
            var stack = player.getInventory().getItemInMainHand();
            if (stack.getType() == Material.AIR) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Recipe.NoItem"));
                return true;
            }
            var recipeList = Bukkit.getRecipesFor(stack);
            if (recipeList.isEmpty()) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe").replace("<MATERIAL>", stack.getType().name()));
                return true;
            }
            RecipeCommand.recipeList.add((Player) commandSender);
            var recipe = recipeList.get(0);
            if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                var inventoryView = player.openWorkbench(player.getLocation(), true);
                var craftingInventory = inventoryView.getTopInventory();
                var contents = new ItemStack[10];
                for (var i = 0; i < 10; i++)
                    contents[i] = null;

                for (int i = 1, i1 = 0; i < Math.min(shapelessRecipe.getIngredientList().size() + 1, 11); i++, i1++)
                    contents[i] = this.normalize(shapelessRecipe.getIngredientList().get(i1));

                craftingInventory.setContents(contents);
                return true;
            }

            if (recipe instanceof ShapedRecipe shapedRecipe) {
                var inventoryView = player.openWorkbench(player.getLocation(), true);
                var craftingInventory = inventoryView.getTopInventory();
                var contents = new ItemStack[10];
                for (var i = 0; i < 10; i++)
                    contents[i] = null;

                Map<Character, ItemStack> ingredient = new LinkedHashMap<>();

                var rows = shapedRecipe.getShape();

                List<String> alphabet = new ArrayList<>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".split("")));

                for (var row : rows)
                    for (String character : row.split(""))
                        alphabet.remove(character);

                for (var row : rows) {
                    var rowEdit = new StringBuilder(row);
                    var rowLength = row.split("").length;
                    for (var character : row.split(""))
                        if (character.equalsIgnoreCase(" "))
                            ingredient.put(character.charAt(0), null);
                        else
                            ingredient.put(character.charAt(0), shapedRecipe.getIngredientMap().get(character.charAt(0)));

                    var usedCharacter = "";

                    for (var i = rowLength; i < 3; i++)
                        for (var character : alphabet)
                            if (!rowEdit.toString().contains(character)) {
                                rowEdit.append(character);
                                ingredient.put(character.charAt(0), null);
                                usedCharacter = character;
                                break;
                            }

                    for (var character : usedCharacter.split(""))
                        alphabet.remove(character);
                }

                var i = 1;
                for (var itemStack : ingredient.values()) {
                    if (i >= 10)
                        break;
                    contents[i] = this.normalize(itemStack);
                    i += 1;
                }

                craftingInventory.setContents(contents);
                return true;
            }

            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe").replace("<MATERIAL>", stack.getType().name()));
            return true;
        }

        var name = arguments[0].toUpperCase().split(":")[0];
        var subId = 0;
        if (name.contains(":"))
            try {
            } catch (Exception ignored) {
            }
        var material = Material.getMaterial(name);
        if (material == null) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Recipe.InvalidMaterial").replace("<MATERIAL>", name));
            return true;
        }

        var stack = new ItemStack(material);

        if (stack.getType() == Material.AIR) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Recipe.InvalidMaterial").replace("<MATERIAL>", name));
            return true;
        }

        var recipeList = Bukkit.getRecipesFor(stack);
        if (recipeList.isEmpty()) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe").replace("<MATERIAL>", name));
            return true;
        }
        RecipeCommand.recipeList.add((Player) commandSender);
        var recipe = recipeList.get(0);
        if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            var inventoryView = player.openWorkbench(player.getLocation(), true);
            var craftingInventory = inventoryView.getTopInventory();
            var contents = new ItemStack[10];
            for (var i = 0; i < 10; i++)
                contents[i] = null;

            for (int i = 1, i1 = 0; i < Math.min(shapelessRecipe.getIngredientList().size() + 1, 11); i++, i1++)
                contents[i] = this.normalize(shapelessRecipe.getIngredientList().get(i1));

            craftingInventory.setContents(contents);
            return true;
        }

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            var inventoryView = player.openWorkbench(player.getLocation(), true);
            var craftingInventory = inventoryView.getTopInventory();
            var contents = new ItemStack[10];
            for (var i = 0; i < 10; i++)
                contents[i] = null;

            Map<Character, ItemStack> ingredient = new LinkedHashMap<>();

            var rows = shapedRecipe.getShape();

            List<String> alphabet = new ArrayList<>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".split("")));

            for (var row : rows)
                for (String character : row.split(""))
                    alphabet.remove(character);

            for (var row : rows) {
                var rowEdit = new StringBuilder(row);
                var rowLength = row.split("").length;
                for (var character : row.split(""))
                    if (character.equalsIgnoreCase(" "))
                        ingredient.put(character.charAt(0), null);
                    else
                        ingredient.put(character.charAt(0), shapedRecipe.getIngredientMap().get(character.charAt(0)));

                var usedCharacter = "";

                for (var i = rowLength; i < 3; i++)
                    for (var character : alphabet)
                        if (!rowEdit.toString().contains(character)) {
                            rowEdit.append(character);
                            ingredient.put(character.charAt(0), null);
                            usedCharacter = character;
                            break;
                        }

                for (var character : usedCharacter.split(""))
                    alphabet.remove(character);
            }

            var i = 1;
            for (var itemStack : ingredient.values()) {
                if (i >= 10)
                    break;
                contents[i] = this.normalize(itemStack);
                i += 1;
            }

            craftingInventory.setContents(contents);
            return true;
        }

        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe").replace("<MATERIAL>", name));

        return true;
    }

    private ItemStack normalize(ItemStack itemStack) {
        return itemStack;
    }
}
