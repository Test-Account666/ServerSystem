package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class COMMAND_suicide extends MessageUtils implements CommandExecutor {

    public COMMAND_suicide(ss plugin) {
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
