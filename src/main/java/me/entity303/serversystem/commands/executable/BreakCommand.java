package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BreakCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public BreakCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "break.use")) {
            var permission = this._plugin.GetPermissions().GetPermission("break.use");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var block = player.getTargetBlock(null, 10);
        if (block.getType() == Material.AIR) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Break.NoBlockFound"));
            return true;
        }

        if (block.getType() == Material.BEDROCK)
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "break.bedrock")) {
                var permission = this._plugin.GetPermissions().GetPermission("break.bedrock");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

        if (block.getType() == Material.BARRIER) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "break.barrier")) {
                var permission = this._plugin.GetPermissions().GetPermission("break.barrier");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
            return true;
        }

        if (block.getType() == Material.COMMAND_BLOCK || block.getType() == Material.CHAIN_COMMAND_BLOCK || block.getType() == Material.REPEATING_COMMAND_BLOCK) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "break.commandblock")) {
                var permission = this._plugin.GetPermissions().GetPermission("break.commandblock");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
            return true;
        }

        if (block.getType() == Material.STRUCTURE_BLOCK) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "break.structureblock")) {
                var permission = this._plugin.GetPermissions().GetPermission("break.structureblock");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
            return true;
        }

        block.setType(Material.AIR);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, null, "Break.BlockBroke")
                                                                                     .replace("<X>", String.valueOf(block.getX()))
                                                                                     .replace("<Y>", String.valueOf(block.getY()))
                                                                                     .replace("<Z>", String.valueOf(block.getZ())));
        return true;
    }
}
