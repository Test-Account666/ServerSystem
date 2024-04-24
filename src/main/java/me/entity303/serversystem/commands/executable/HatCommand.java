package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HatCommand extends CommandUtils implements ICommandExecutorOverload {

    public HatCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.hat.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "hat.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("hat.permission");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            if (player.getInventory().getHelmet() != null)
                player.getInventory().addItem(player.getInventory().getHelmet());

            player.getInventory().setHelmet(player.getInventory().getItemInMainHand());
            player.getInventory().removeItem(player.getInventory().getItemInMainHand());

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command, commandSender, null,
                                                                                                     "Hat.Success.NewHat")
                                                                                         .replace("<TYPE>",
                                                                                                  player.getInventory().getHelmet().getType().toString()));
            return true;
        }

        if (player.getInventory().getHelmet() == null) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Hat.NoItem"));
            return true;
        }

        player.getInventory().addItem(player.getInventory().getHelmet());

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, null,
                                                                                                 "Hat.Success.HatRemoved")
                                                                                     .replace("<TYPE>", player.getInventory().getHelmet().getType().toString()));

        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        return true;
    }
}
