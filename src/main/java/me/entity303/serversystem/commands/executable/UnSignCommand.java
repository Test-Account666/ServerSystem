package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class UnSignCommand extends CommandUtils implements CommandExecutorOverload {

    public UnSignCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "unsign")) {
            var permission = this.plugin.getPermissions().getPermission("unsign");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        player.getInventory().getItemInMainHand();
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "UnSign.NoItem"));
            return true;
        }

        var meta = player.getInventory().getItemInMainHand().getItemMeta();
        assert meta != null;
        if (!meta.hasLore()) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "UnSign.NotSigned"));
            return true;
        }

        meta.setLore(null);

        player.getInventory().getItemInMainHand().setItemMeta(meta);
        player.updateInventory();

        commandSender.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "UnSign.Success"));
        return true;
    }
}
