package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.databasemanager.WarpManager;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand extends MessageUtils implements CommandExecutor {

    public SetWarpCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (!this.isAllowed(cs, "setwarp")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("setwarp")));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("SetWarp", label, cmd.getName(), cs, null));
            return true;
        }
        String name = args[0].toLowerCase();
        WarpManager warpManager = this.plugin.getWarpManager();
        if (warpManager.doesWarpExist(name)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("SetWarp.WarpAlreadyExists", label, cmd.getName(), cs, null).replace("<WARP>", name.toUpperCase()));
            return true;
        }
        warpManager.addWarp(name, ((Player) cs).getLocation());
        cs.sendMessage(this.getPrefix() + this.getMessage("SetWarp.Success", label, cmd.getName(), cs, null).replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
