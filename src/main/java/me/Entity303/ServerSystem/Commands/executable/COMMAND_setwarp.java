package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.DatabaseManager.WarpManager;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_setwarp extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_setwarp(ss plugin) {
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
