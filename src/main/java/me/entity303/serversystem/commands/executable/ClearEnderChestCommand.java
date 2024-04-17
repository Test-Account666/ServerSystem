package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ClearEnderChestCommand extends CommandUtils implements CommandExecutorOverload {

    public ClearEnderChestCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
                return true;
            }

            if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.clearenderchest.self.required"))
                if (!this.plugin.getPermissions().hasPermission(commandSender, "clearenderchest.self.permission")) {
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                                 .getNoPermission(this.plugin.getPermissions()
                                                                                                                             .getPermission(
                                                                                                                                     "clearenderchest.self.permission")));
                    return true;
                }

            this.ClearEnderChest(commandSender, (Player) commandSender, command, commandLabel);
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "clearenderchest.others")) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("clearenderchest.others")));
            return true;
        }
        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        this.ClearEnderChest(commandSender, target, command, commandLabel);
        return true;
    }

    private void ClearEnderChest(CommandSender commandSender, Player target, Command command, String commandLabel) {
        var counter = 0;
        var enderChestInventory = target.getEnderChest();

        for (var i = 0; i < enderChestInventory.getSize(); i++) {
            var itemStack = enderChestInventory.getItem(i);

            if (itemStack == null)
                continue;

            counter = counter + itemStack.getAmount();
        }

        enderChestInventory.clear();

        if (commandSender == target) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                     "ClearEnderChest.Self")
                                                                                         .replace("<AMOUNT>", String.valueOf(counter)));
            return;
        }

        target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                          "ClearEnderChest.Others.Target")
                                                                              .replace("<AMOUNT>", String.valueOf(counter)));
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                 "ClearEnderChest.Others.Sender")
                                                                                     .replace("<AMOUNT>", String.valueOf(counter)));
    }
}
