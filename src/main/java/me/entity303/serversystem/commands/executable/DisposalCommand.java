package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DisposalCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public DisposalCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (this.plugin.getMessages().getCfg().getBoolean("Permissions.disposal.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "disposal.permission")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("disposal.permission")));
                return true;
            }

        var disposal = Bukkit.getServer()
                             .createInventory(null, 54,
                                              this.plugin.getMessages().getMiscMessage(commandLabel, command.getName(), commandSender, null, "DisposalName"));

        player.openInventory(disposal);
        return true;
    }
}
