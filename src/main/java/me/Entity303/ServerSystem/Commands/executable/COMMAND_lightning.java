package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_lightning extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_lightning(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "lightning")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("lightning")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getOnlyPlayer());
            return true;
        }
        Player player = (Player) cs;
        Block block = player.getTargetBlock(null, 60);
        block.getLocation().getWorld().strikeLightning(block.getLocation());
        return true;
    }
}
