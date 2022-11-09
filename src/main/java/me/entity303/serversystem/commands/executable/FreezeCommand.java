package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Objects;

public class FreezeCommand extends MessageUtils implements CommandExecutor {
    private boolean persistent = false;
    private boolean checked = false;
    private final NamespacedKey namespacedKey;

    public FreezeCommand(ServerSystem plugin) {
        super(plugin);
        this.namespacedKey = new NamespacedKey(plugin, "freeze");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!this.checked) {
            this.checked = true;

            try {
                Class.forName("org.bukkit.persistence.PersistentDataHolder");
                this.persistent = true;
            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
            }
        }

        if (!this.isAllowed(commandSender, "freeze")) {
            commandSender.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("freeze")));
            return true;
        }

        if (args.length <= 0) {
            commandSender.sendMessage(this.getPrefix() + this.getSyntax("Freeze", label, command.getName(), commandSender, null));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            commandSender.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        if (this.isFrozen(target)) {
            this.unFreeze(target);
            commandSender.sendMessage(this.getPrefix() + this.getMessage("Freeze.UnFreeze", label, command.getName(), commandSender, target));
            return true;
        }

        this.freeze(target);
        commandSender.sendMessage(this.getPrefix() + this.getMessage("Freeze.Freeze", label, command.getName(), commandSender, target));

        return true;
    }

    private boolean isFrozen(Player player) {
        if (this.persistent) {
            org.bukkit.persistence.PersistentDataHolder dataHolder = (org.bukkit.persistence.PersistentDataHolder) player;

            if (!dataHolder.getPersistentDataContainer().has(this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE))
                return false;

            byte frozen = dataHolder.getPersistentDataContainer().get(this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE);
            return frozen >= 1;
        }

        if (!player.hasMetadata("freeze"))
            return false;

        return Objects.requireNonNull(player.getMetadata("freeze").stream().findFirst().orElse(null)).asBoolean();
    }

    private void freeze(Player player) {
        if (this.persistent) {
            org.bukkit.persistence.PersistentDataHolder dataHolder = (org.bukkit.persistence.PersistentDataHolder) player;

            dataHolder.getPersistentDataContainer().set(this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
            return;
        }

        MetadataValue metadataValue = new FixedMetadataValue(this.plugin, true);
        player.setMetadata("freeze", metadataValue);
    }

    private void unFreeze(Player player) {
        if (this.persistent) {
            org.bukkit.persistence.PersistentDataHolder dataHolder = (org.bukkit.persistence.PersistentDataHolder) player;

            dataHolder.getPersistentDataContainer().set(this.namespacedKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 0);
            return;
        }

        MetadataValue metadataValue = new FixedMetadataValue(this.plugin, true);
        player.removeMetadata("freeze", this.plugin);
        player.setMetadata("freeze", metadataValue);
    }
}
