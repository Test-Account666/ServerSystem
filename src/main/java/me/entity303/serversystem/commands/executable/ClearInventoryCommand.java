package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "ClearInventory")
public class ClearInventoryCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public ClearInventoryCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
                return true;
            }

            if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.clearinventory.self.required")) {
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "clearinventory.self.permission")) {
                    var permission = this._plugin.GetPermissions().GetPermission("clearinventory.self.permission");

                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                    return true;
                }
            }

            this.ClearInventory(commandSender, (Player) commandSender, command, commandLabel);
            return true;
        }
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "clearinventory.others")) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("clearinventory.others")));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        this.ClearInventory(commandSender, target, command, commandLabel);
        return true;
    }

    private void ClearInventory(CommandSender commandSender, Player target, Command command, String commandLabel) {
        var counter = 0;

        for (var index = 0; index < (27 + 9); index++) {
            var itemStack = target.getInventory().getItem(index);

            if (itemStack == null || itemStack.getType() == Material.AIR) continue;

            counter = counter + itemStack.getAmount();
        }

        var helmet = target.getInventory().getHelmet();
        var chestPlate = target.getInventory().getChestplate();
        var leggings = target.getInventory().getLeggings();
        var boots = target.getInventory().getBoots();

        if (helmet != null && helmet.getType() != Material.AIR) counter = counter + helmet.getAmount();
        if (chestPlate != null && chestPlate.getType() != Material.AIR) counter = counter + chestPlate.getAmount();
        if (leggings != null && leggings.getType() != Material.AIR) counter = counter + leggings.getAmount();
        if (boots != null && boots.getType() != Material.AIR) counter = counter + boots.getAmount();

        target.getInventory().setHelmet(null);
        target.getInventory().setChestplate(null);
        target.getInventory().setLeggings(null);
        target.getInventory().setBoots(null);
        target.getInventory().clear();

        if (target == commandSender) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                       "ClearInventory.Self")
                                                                                           .replace("<AMOUNT>", String.valueOf(counter)));
            return;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                   "ClearInventory.Others.Sender")
                                                                                       .replace("<AMOUNT>", String.valueOf(counter)));

        target.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                            "ClearInventory.Others.Target")
                                                                                .replace("<AMOUNT>", String.valueOf(counter)));
    }
}
