package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditSignCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public EditSignCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getVersionStuff().getSignEdit() == null) {
            sender.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getMessage(commandLabel, command.getName(), sender, null, "EditSign.NotAvailable"));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(player, "editschild.players")) {
            player.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("editschild.players")));
            return true;
        }
        var block = player.getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign sign)) {
            player.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getMessage(commandLabel, command.getName(), sender, null, "EditSign.SignNeeded"));
            return true;
        }

        this.plugin.getVersionStuff().getSignEdit().editSign(player, sign);
        return true;
    }
}
