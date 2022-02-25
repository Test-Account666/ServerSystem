package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LightningCommand extends MessageUtils implements CommandExecutor {

    public LightningCommand(ServerSystem plugin) {
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
