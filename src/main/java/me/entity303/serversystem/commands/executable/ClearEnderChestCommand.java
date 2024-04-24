package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ClearEnderChestCommand extends CommandUtils implements ICommandExecutorOverload {

    public ClearEnderChestCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
                return true;
            }

            if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.clearenderchest.self.required"))
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "clearenderchest.self.permission")) {
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                 .GetNoPermission(this._plugin.GetPermissions()
                                                                                                                             .GetPermission(
                                                                                                                                     "clearenderchest.self.permission")));
                    return true;
                }

            this.ClearEnderChest(commandSender, (Player) commandSender, command, commandLabel);
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "clearenderchest.others")) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("clearenderchest.others")));
            return true;
        }
        var target = this.GetPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        this.ClearEnderChest(commandSender, target, command, commandLabel);
        return true;
    }

    private void ClearEnderChest(CommandSender commandSender, Player target, Command command, String commandLabel) {
        var counter = 0;
        var enderChestInventory = target.getEnderChest();

        for (var index = 0; index < enderChestInventory.getSize(); index++) {
            var itemStack = enderChestInventory.getItem(index);

            if (itemStack == null)
                continue;

            counter = counter + itemStack.getAmount();
        }

        enderChestInventory.clear();

        if (commandSender == target) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                     "ClearEnderChest.Self")
                                                                                         .replace("<AMOUNT>", String.valueOf(counter)));
            return;
        }

        target.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                              .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                          "ClearEnderChest.Others.Target")
                                                                              .replace("<AMOUNT>", String.valueOf(counter)));
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                 "ClearEnderChest.Others.Sender")
                                                                                     .replace("<AMOUNT>", String.valueOf(counter)));
    }
}
