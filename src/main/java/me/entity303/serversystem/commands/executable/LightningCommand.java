package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LightningCommand extends CommandUtils implements CommandExecutorOverload {

    public LightningCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "lightning")) {
            var permission = this.plugin.getPermissions().getPermission("lightning");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        var block = player.getTargetBlock(null, 60);
        Objects.requireNonNull(block.getLocation().getWorld()).strikeLightning(block.getLocation());
        return true;
    }
}
