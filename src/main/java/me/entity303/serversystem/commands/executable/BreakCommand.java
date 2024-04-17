package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BreakCommand extends CommandUtils implements CommandExecutorOverload {

    public BreakCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "break.use")) {
            var permission = this.plugin.getPermissions().getPermission("break.use");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var block = player.getTargetBlock(null, 10);
        if (block.getType() == Material.AIR) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Break.NoBlockFound"));
            return true;
        }

        if (block.getType() == Material.BEDROCK)
            if (!this.plugin.getPermissions().hasPermission(commandSender, "break.bedrock")) {
                var permission = this.plugin.getPermissions().getPermission("break.bedrock");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

        if (block.getType() == Material.BARRIER) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "break.barrier")) {
                var permission = this.plugin.getPermissions().getPermission("break.barrier");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            return true;
        }

        if (block.getType() == Material.COMMAND_BLOCK || block.getType() == Material.CHAIN_COMMAND_BLOCK || block.getType() == Material.REPEATING_COMMAND_BLOCK) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "break.commandblock")) {
                var permission = this.plugin.getPermissions().getPermission("break.commandblock");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            return true;
        }

        if (block.getType() == Material.STRUCTURE_BLOCK) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "break.structureblock")) {
                var permission = this.plugin.getPermissions().getPermission("break.structureblock");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            return true;
        }

        block.setType(Material.AIR);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, null, "Break.BlockBroke")
                                                                                     .replace("<X>", String.valueOf(block.getX()))
                                                                                     .replace("<Y>", String.valueOf(block.getY()))
                                                                                     .replace("<Z>", String.valueOf(block.getZ())));
        return true;
    }
}
