package me.testaccount666.serversystem.commands.executables.stack;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "stack")
public class CommandStack extends AbstractServerSystemCommand {

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Stack.Use", false);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Stack.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var itemInHand = commandSender.getPlayer().getInventory().getItemInMainHand();
        if (itemInHand.getType().isAir()) {
            command("Stack.NoItemInHand", commandSender).build();
            return;
        }

        itemInHand.setAmount(itemInHand.getMaxStackSize());
        command("Stack.Success", commandSender).build();
    }
}
