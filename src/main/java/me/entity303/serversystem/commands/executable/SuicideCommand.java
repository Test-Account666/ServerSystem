package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SuicideCommand extends MessageUtils implements CommandExecutor {

    public SuicideCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.suicide.required"))
            if (!this.isAllowed(cs, "suicide.permission")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("suicide.permission")));
                return true;
            }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        ((Player) cs).setHealth(0);
        return true;
    }
}
