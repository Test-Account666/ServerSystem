package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class AwayFromKeyboardCommand extends MessageUtils implements CommandExecutor {

    public AwayFromKeyboardCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.afk.required"))
            if (!this.isAllowed(cs, "afk.permission")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("afk.permission")));
                return true;
            }

        boolean awayFromKeyboard = false;

        if (((Player) cs).hasMetadata("afk")) {
            for (MetadataValue metadataValue : ((Player) cs).getMetadata("afk")) {
                if (metadataValue == null)
                    continue;

                if (metadataValue.getOwningPlugin() == null)
                    continue;

                if (!metadataValue.getOwningPlugin().getName().equalsIgnoreCase("ServerSystem"))
                    continue;

                awayFromKeyboard = metadataValue.asBoolean();
                break;
            }

            ((Player) cs).removeMetadata("afk", this.plugin);
        }

        if (!awayFromKeyboard) {
            ((Player) cs).setMetadata("afk", this.plugin.getMetaValue().getMetaValue(true));

            cs.sendMessage(this.getPrefix() + this.getMessage("Afk.Enabled", label, cmd.getName(), cs, null));
            return true;
        }

        ((Player) cs).setMetadata("afk", this.plugin.getMetaValue().getMetaValue(false));

        cs.sendMessage(this.getPrefix() + this.getMessage("Afk.Disabled", label, cmd.getName(), cs, null));
        return true;
    }
}
