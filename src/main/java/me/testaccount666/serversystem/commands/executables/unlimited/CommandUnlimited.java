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

@ServerSystemCommand(name = "unlimited")
public class CommandUnlimited extends AbstractServerSystemCommand {
    protected final NamespacedKey unlimitedKey;

    public CommandUnlimited() {
        unlimitedKey = new NamespacedKey(ServerSystem.Instance, "unlimited");
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Unlimited.Use", label)) return;
        if (commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        var itemInHand = commandSender.getPlayer().getInventory().getItemInMainHand();

        if (itemInHand.getType().isAir()) {
            sendGeneralMessage(commandSender, "NoItemInHand", null, label, null);
            return;
        }

        var itemMeta = itemInHand.getItemMeta();
        var dataContainer = itemMeta.getPersistentDataContainer();

        var setUnlimited = !dataContainer.has(unlimitedKey, PersistentDataType.BYTE);

        if (setUnlimited) dataContainer.set(unlimitedKey, PersistentDataType.BYTE, (byte) 1);
        else dataContainer.remove(unlimitedKey);

        itemInHand.setItemMeta(itemMeta);

        var messagePath = setUnlimited? "Unlimited.Success.Enabled" : "Unlimited.Success.Disabled";
        sendCommandMessage(commandSender, messagePath, null, label, null);
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Unlimited.Use", false);
    }
}
