package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BreakCommand extends MessageUtils implements CommandExecutor {

    public BreakCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (!this.isAllowed(cs, "break.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("break.use")));
            return true;
        }

        Player player = (Player) cs;
        Block block = player.getTargetBlock(null, 10);
        if (block.getType() == Material.AIR) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Break.NoBlockFound", label, cmd.getName(), cs, null));
            return true;
        }

        if (block.getType() == Material.BEDROCK) if (!this.isAllowed(cs, "break.bedrock")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("break.bedrock")));
            return true;
        }

        if (block.getType() == Material.BARRIER) {
            if (!this.isAllowed(cs, "break.barrier")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("break.barrier")));
                return true;
            }
            return true;
        }

        if (block.getType() == Material.COMMAND_BLOCK || block.getType() == Material.CHAIN_COMMAND_BLOCK || block.getType() == Material.REPEATING_COMMAND_BLOCK) {
            if (!this.isAllowed(cs, "break.commandblock")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("break.commandblock")));
                return true;
            }
            return true;
        }

        if (block.getType() == Material.STRUCTURE_BLOCK) {
            if (!this.isAllowed(cs, "break.structureblock")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("break.structureblock")));
                return true;
            }
            return true;
        }

        block.setType(Material.AIR);
        cs.sendMessage(this.getPrefix() + this.getMessage("Break.BlockBroke", label, cmd.getName(), cs, null).replace("<X>", String.valueOf(block.getX())).replace("<Y>", String.valueOf(block.getY())).replace("<Z>", String.valueOf(block.getZ())));
        return true;
    }
}
