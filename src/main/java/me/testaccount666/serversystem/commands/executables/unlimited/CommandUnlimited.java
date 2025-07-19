package me.testaccount666.serversystem.commands.executables.unlimited;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "unlimited")
public class CommandUnlimited extends AbstractServerSystemCommand {
    protected final NamespacedKey unlimitedKey;

    public CommandUnlimited() {
        unlimitedKey = new NamespacedKey(ServerSystem.Instance, "unlimited");
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Unlimited.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var itemInHand = commandSender.getPlayer().getInventory().getItemInMainHand();

        if (itemInHand.getType().isAir()) {
            command("Unlimited.NoItemInHand", commandSender).build();
            return;
        }

        var itemMeta = itemInHand.getItemMeta();
        var dataContainer = itemMeta.getPersistentDataContainer();

        var setUnlimited = !dataContainer.has(unlimitedKey, PersistentDataType.BYTE);

        if (setUnlimited) dataContainer.set(unlimitedKey, PersistentDataType.BYTE, (byte) 1);
        else dataContainer.remove(unlimitedKey);

        itemInHand.setItemMeta(itemMeta);

        var messagePath = setUnlimited? "Unlimited.Success.Enabled" : "Unlimited.Success.Disabled";
        command(messagePath, commandSender).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        throw new UnsupportedOperationException("Unlimited command doesn't have an available syntax!");
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Unlimited.Use", false);
    }
}
