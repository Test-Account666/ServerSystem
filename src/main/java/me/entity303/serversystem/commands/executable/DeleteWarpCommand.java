package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.databasemanager.WarpManager;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteWarpCommand extends MessageUtils implements CommandExecutor {

    public DeleteWarpCommand(ServerSystem plugin) {
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
