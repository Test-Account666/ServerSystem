package me.testaccount666.serversystem.commands.executables.smelt;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "smelt")
public class CommandSmelt extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Smelt.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var itemInHand = commandSender.getPlayer().getInventory().getItemInMainHand();
        if (itemInHand.getType().isAir()) {
            command("Smelt.NoItemInHand", commandSender).build();
            return;
        }

        var smeltedItemOptional = getSmeltedItem(itemInHand);
        if (smeltedItemOptional.isEmpty()) {
            command("Smelt.NoSmeltedItem", commandSender).build();
            return;
        }

        var smeltedItem = smeltedItemOptional.get();
        smeltedItem.setAmount(itemInHand.getAmount());
        commandSender.getPlayer().getInventory().setItemInMainHand(smeltedItem);

        command("Smelt.Success", commandSender).
                postModifier(message -> message.replace("<INPUT>", itemInHand.getType().name())
                        .replace("<OUTPUT>", smeltedItem.getType().name())).build();
    }

    private Optional<ItemStack> getSmeltedItem(ItemStack itemStack) {
        var recipeIterator = Bukkit.recipeIterator();

        while (recipeIterator.hasNext()) {
            var recipe = recipeIterator.next();
            if (!(recipe instanceof FurnaceRecipe furnaceRecipe)) continue;
            if (!furnaceRecipe.getInputChoice().test(itemStack)) continue;

            return Optional.of(furnaceRecipe.getResult());
        }

        return Optional.empty();
    }

    @Override
    public String getSyntaxPath(Command command) {
        throw new UnsupportedOperationException("Smelt command doesn't have an available syntax!");
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Smelt.Use", false);
    }
}
