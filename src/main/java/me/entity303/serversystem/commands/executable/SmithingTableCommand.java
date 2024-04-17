package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmithingTableCommand extends CommandUtils implements CommandExecutorOverload {

    public SmithingTableCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "smithingtable")) {
            var permission = this.plugin.getPermissions().getPermission("smithingtable");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        this.plugin.getVersionStuff().getVirtualSmithing().openSmithing((Player) commandSender);
        return true;
    }
}
