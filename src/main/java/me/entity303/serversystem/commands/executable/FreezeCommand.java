package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand extends CommandUtils implements CommandExecutorOverload {
    private final NamespacedKey namespacedKey;

    public FreezeCommand(ServerSystem plugin) {
        super(plugin);

        this.namespacedKey = new NamespacedKey(plugin, "freeze");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "freeze")) {
            var permission = this.plugin.getPermissions().getPermission("freeze");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            var command1 = command.getName();
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command1, commandSender, null, "Freeze"));
            return true;
        }

        var target = Bukkit.getPlayer(arguments[0]);

        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (this.isFrozen(target)) {
            this.unFreeze(target);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Freeze.UnFreeze"));
            return true;
        }

        this.freeze(target);

        commandSender.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Freeze.Freeze"));

        return true;
    }

    private boolean isFrozen(Player player) {
        return isFrozen(player, this.namespacedKey);
    }

    private void unFreeze(Player player) {
        player.getPersistentDataContainer().set((org.bukkit.NamespacedKey) this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 0);
    }

    private void freeze(Player player) {
        player.getPersistentDataContainer().set((org.bukkit.NamespacedKey) this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean isFrozen(Player player, NamespacedKey namespacedKey) {
        if (!player.getPersistentDataContainer().has(namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE))
            return false;

        var frozen = player.getPersistentDataContainer().get(namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE);

        if (frozen == null)
            return false;

        return frozen >= 1;
    }
}
