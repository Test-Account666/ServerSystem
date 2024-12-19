package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataHolder;

@ServerSystemCommand(name = "Freeze")
public class FreezeCommand implements ICommandExecutorOverload {
    protected final ServerSystem _plugin;
    private final NamespacedKey _namespacedKey;

    public FreezeCommand(ServerSystem plugin) {
        this._plugin = plugin;

        this._namespacedKey = new NamespacedKey(plugin, "freeze");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "freeze")) {
            var permission = this._plugin.GetPermissions().GetPermission("freeze");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            var command1 = command.getName();
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command1, commandSender, null, "Freeze"));
            return true;
        }

        var target = Bukkit.getPlayer(arguments[0]);

        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (this.IsFrozen(target)) {
            this.UnFreeze(target);
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Freeze.UnFreeze"));
            return true;
        }

        this.Freeze(target);

        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Freeze.Freeze"));

        return true;
    }

    private boolean IsFrozen(Player player) {
        return IsFrozen(player, this._namespacedKey);
    }

    private void UnFreeze(PersistentDataHolder player) {
        player.getPersistentDataContainer().set(this._namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 0);
    }

    private void Freeze(PersistentDataHolder player) {
        player.getPersistentDataContainer().set(this._namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean IsFrozen(Player player, NamespacedKey namespacedKey) {
        if (!player.getPersistentDataContainer().has(namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE)) return false;

        var frozen = player.getPersistentDataContainer().get(namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE);

        if (frozen == null) return false;

        return frozen >= 1;
    }
}
