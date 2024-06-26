package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class UnSignCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public UnSignCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "unsign")) {
            var permission = this._plugin.GetPermissions().GetPermission("unsign");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        player.getInventory().getItemInMainHand();
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "UnSign.NoItem"));
            return true;
        }

        var meta = player.getInventory().getItemInMainHand().getItemMeta();
        assert meta != null;
        if (!meta.hasLore()) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "UnSign.NotSigned"));
            return true;
        }

        meta.setLore(null);

        player.getInventory().getItemInMainHand().setItemMeta(meta);
        player.updateInventory();

        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "UnSign.Success"));
        return true;
    }
}
