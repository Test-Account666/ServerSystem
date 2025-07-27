package me.testaccount666.serversystem.commands.executables.rename;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "rename")
public class CommandRename extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Rename.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }
        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }
        var player = commandSender.getPlayer();
        var itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.isEmpty()) {
            command("Rename.NoItemInHand", commandSender).build();
            return;
        }

        var newName = String.join(" ", arguments).trim();

        var itemMeta = itemInHand.getItemMeta();
        itemMeta.itemName(ComponentColor.translateToComponent(newName));
        itemInHand.setItemMeta(itemMeta);
        command("Rename.Success", commandSender).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Rename";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Rename.Use", false);
    }
}
