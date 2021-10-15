package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.DatabaseManager.WarpManager;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_delwarp extends MessageUtils implements CommandExecutor {

    public COMMAND_delwarp(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "delwarp")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("delwarp")));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("DelWarp", label, cmd.getName(), cs, null));
            return true;
        }
        String name = args[0].toLowerCase();
        WarpManager warpManager = this.plugin.getWarpManager();
        if (!warpManager.doesWarpExist(name)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("DelWarp.WarpDoesntExists", label, cmd.getName(), cs, null).replace("<WARP>", name.toUpperCase()));
            return true;
        }
        warpManager.deleteWarp(name);
        cs.sendMessage(this.getPrefix() + this.getMessage("DelWarp.Success", label, cmd.getName(), cs, null).replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
