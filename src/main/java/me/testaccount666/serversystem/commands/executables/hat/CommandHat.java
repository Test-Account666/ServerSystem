package me.testaccount666.serversystem.commands.executables.hat;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "hat")
public class CommandHat extends AbstractServerSystemCommand {
    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Hat.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var inventory = commandSender.getPlayer().getInventory();
        var itemInHand = inventory.getItemInMainHand();
        var itemOnHead = inventory.getHelmet();
        if (itemOnHead == null) itemOnHead = ItemStack.empty();

        if (itemInHand.isEmpty() && itemOnHead.isEmpty()) {
            command("Hat.NoHat", commandSender).build();
            return;
        }

        if (itemInHand.isEmpty() && !itemOnHead.isEmpty()) {
            inventory.setHelmet(ItemStack.empty());
            inventory.setItemInMainHand(itemOnHead);
            command("Hat.RemovedHat", commandSender).build();
            return;
        }

        if (!itemInHand.isEmpty() && !itemOnHead.isEmpty()) {
            command("Hat.AlreadyHasHat", commandSender).build();
            return;
        }

        inventory.setHelmet(itemInHand);
        inventory.setItemInMainHand(ItemStack.empty());
        command("Hat.AppliedHat", commandSender).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        throw new UnsupportedOperationException("Hat command doesn't have an available syntax!");
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Hat.Use", false);
    }
}
