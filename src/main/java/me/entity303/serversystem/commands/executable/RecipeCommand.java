package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.RecipeTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;
import java.util.regex.Pattern;

//TODO: This command probably requires a recode
@ServerSystemCommand(name = "Recipe", tabCompleter = RecipeTabCompleter.class)
public class RecipeCommand implements ICommandExecutorOverload {
    private final static List<Player> RECIPE_LIST = new ArrayList<>();
    private static final Pattern SPLIT_PATTERN = Pattern.compile("");
    protected final ServerSystem _plugin;

    public RecipeCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static List<Player> GetRecipeList() {
        return RecipeCommand.RECIPE_LIST;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.recipe.required")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "recipe.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("recipe.permission");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
        }
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        if (arguments.length == 0) {
            var stack = player.getInventory().getItemInMainHand();
            if (stack.getType() == Material.AIR) {

                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Recipe.NoItem"));
                return true;
            }
            var recipeList = Bukkit.getRecipesFor(stack);
            if (recipeList.isEmpty()) {

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe")
                                                                                               .replace("<MATERIAL>", stack.getType().name()));
                return true;
            }
            RecipeCommand.RECIPE_LIST.add((Player) commandSender);
            var recipe = recipeList.get(0);
            if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                var inventoryView = player.openWorkbench(player.getLocation(), true);
                var craftingInventory = inventoryView.getTopInventory();
                var contents = new ItemStack[10];
                for (var index = 0; index < 10; index++)
                    contents[index] = null;

                for (int index = 1, index1 = 0; index < Math.min(shapelessRecipe.getIngredientList().size() + 1, 11); index++, index1++)
                    contents[index] = this.Normalize(shapelessRecipe.getIngredientList().get(index1));

                craftingInventory.setContents(contents);
                return true;
            }

            if (recipe instanceof ShapedRecipe shapedRecipe) {
                var inventoryView = player.openWorkbench(player.getLocation(), true);
                var craftingInventory = inventoryView.getTopInventory();
                var contents = new ItemStack[10];
                for (var index = 0; index < 10; index++)
                    contents[index] = null;

                Map<Character, ItemStack> ingredient = new LinkedHashMap<>();

                var rows = shapedRecipe.getShape();

                List<String> alphabet = new ArrayList<>(Arrays.asList(SPLIT_PATTERN.split("abcdefghijklmnopqrstuvwxyz")));

                for (var row : rows)
                    for (var character : SPLIT_PATTERN.split(row))
                        alphabet.remove(character);

                for (var row : rows) {
                    var rowEdit = new StringBuilder(row);
                    var rowLength = SPLIT_PATTERN.split(row).length;
                    for (var character : SPLIT_PATTERN.split(row))
                        if (character.equalsIgnoreCase(" ")) {
                            ingredient.put(character.charAt(0), null);
                        } else {
                            ingredient.put(character.charAt(0), shapedRecipe.getIngredientMap().get(character.charAt(0)));
                        }

                    var usedCharacter = "";

                    for (var index = rowLength; index < 3; index++)
                        for (var character : alphabet)
                            if (!rowEdit.toString().contains(character)) {
                                rowEdit.append(character);
                                ingredient.put(character.charAt(0), null);
                                usedCharacter = character;
                                break;
                            }

                    for (var character : SPLIT_PATTERN.split(usedCharacter))
                        alphabet.remove(character);
                }

                var index = 1;
                for (var itemStack : ingredient.values()) {
                    if (index >= 10) break;
                    contents[index] = this.Normalize(itemStack);
                    index += 1;
                }

                craftingInventory.setContents(contents);
                return true;
            }


            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe")
                                                                                           .replace("<MATERIAL>", stack.getType().name()));
            return true;
        }

        var name = arguments[0].toUpperCase().split(":")[0];
        var subId = 0;
        if (name.contains(":")) {
            try {
            } catch (Exception ignored) {
            }
        }
        var material = Material.getMaterial(name);
        if (material == null) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null,
                                                                                                       "Recipe.InvalidMaterial")
                                                                                           .replace("<MATERIAL>", name));
            return true;
        }

        var stack = new ItemStack(material);

        if (stack.getType() == Material.AIR) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null,
                                                                                                       "Recipe.InvalidMaterial")
                                                                                           .replace("<MATERIAL>", name));
            return true;
        }

        var recipeList = Bukkit.getRecipesFor(stack);
        if (recipeList.isEmpty()) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe").replace("<MATERIAL>", name));
            return true;
        }
        RecipeCommand.RECIPE_LIST.add((Player) commandSender);
        var recipe = recipeList.get(0);
        if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            var inventoryView = player.openWorkbench(player.getLocation(), true);
            var craftingInventory = inventoryView.getTopInventory();
            var contents = new ItemStack[10];
            for (var index = 0; index < 10; index++)
                contents[index] = null;

            for (int index = 1, index1 = 0; index < Math.min(shapelessRecipe.getIngredientList().size() + 1, 11); index++, index1++)
                contents[index] = this.Normalize(shapelessRecipe.getIngredientList().get(index1));

            craftingInventory.setContents(contents);
            return true;
        }

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            var inventoryView = player.openWorkbench(player.getLocation(), true);
            var craftingInventory = inventoryView.getTopInventory();
            var contents = new ItemStack[10];
            for (var index = 0; index < 10; index++)
                contents[index] = null;

            Map<Character, ItemStack> ingredient = new LinkedHashMap<>();

            var rows = shapedRecipe.getShape();

            var alphabet = new ArrayList<>(Arrays.asList("abcdefghijklmnopqrstuvwxyz".split("")));

            for (var row : rows)
                for (var character : SPLIT_PATTERN.split(row))
                    alphabet.remove(character);

            for (var row : rows) {
                var rowEdit = new StringBuilder(row);
                var rowLength = SPLIT_PATTERN.split(row).length;
                for (var character : SPLIT_PATTERN.split(row))
                    if (character.equalsIgnoreCase(" ")) {
                        ingredient.put(character.charAt(0), null);
                    } else {
                        ingredient.put(character.charAt(0), shapedRecipe.getIngredientMap().get(character.charAt(0)));
                    }

                var usedCharacter = "";

                for (var index = rowLength; index < 3; index++)
                    for (var character : alphabet)
                        if (!rowEdit.toString().contains(character)) {
                            rowEdit.append(character);
                            ingredient.put(character.charAt(0), null);
                            usedCharacter = character;
                            break;
                        }

                for (var character : SPLIT_PATTERN.split(usedCharacter))
                    alphabet.remove(character);
            }

            var index = 1;
            for (var itemStack : ingredient.values()) {
                if (index >= 10) break;
                contents[index] = this.Normalize(itemStack);
                index += 1;
            }

            craftingInventory.setContents(contents);
            return true;
        }


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Recipe.NoRecipe").replace("<MATERIAL>", name));

        return true;
    }

    private ItemStack Normalize(ItemStack itemStack) {
        return itemStack;
    }
}
