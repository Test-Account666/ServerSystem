package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LightningCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public LightningCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "lightning")) {
            var permission = this._plugin.GetPermissions().GetPermission("lightning");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        var block = player.getTargetBlock(null, 60);
        Objects.requireNonNull(block.getLocation().getWorld()).strikeLightning(block.getLocation());
        return true;
    }
}
