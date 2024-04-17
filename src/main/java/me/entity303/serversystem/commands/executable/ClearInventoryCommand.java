package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInventoryCommand extends CommandUtils implements CommandExecutorOverload {

    public ClearInventoryCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
                return true;
            }

            if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.clearinventory.self.required"))
                if (!this.plugin.getPermissions().hasPermission(commandSender, "clearinventory.self.permission")) {
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                                 .getNoPermission(this.plugin.getPermissions()
                                                                                                                             .getPermission(
                                                                                                                                     "clearinventory.self.permission")));
                    return true;
                }

            this.ClearInventory(commandSender, (Player) commandSender, command, commandLabel);
            return true;
        }
        if (!this.plugin.getPermissions().hasPermission(commandSender, "clearinventory.others")) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("clearinventory.others")));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        this.ClearInventory(commandSender, target, command, commandLabel);
        return true;
    }

    private void ClearInventory(CommandSender commandSender, Player target, Command command, String commandLabel) {
        var counter = 0;

        for (var i = 0; i < (27 + 9); i++) {
            var itemStack = target.getInventory().getItem(i);

            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            counter = counter + itemStack.getAmount();
        }

        var helmet = target.getInventory().getHelmet();
        var chestPlate = target.getInventory().getChestplate();
        var leggings = target.getInventory().getLeggings();
        var boots = target.getInventory().getBoots();

        if (helmet != null && helmet.getType() != Material.AIR)
            counter = counter + helmet.getAmount();
        if (chestPlate != null && chestPlate.getType() != Material.AIR)
            counter = counter + chestPlate.getAmount();
        if (leggings != null && leggings.getType() != Material.AIR)
            counter = counter + leggings.getAmount();
        if (boots != null && boots.getType() != Material.AIR)
            counter = counter + boots.getAmount();

        target.getInventory().setHelmet(null);
        target.getInventory().setChestplate(null);
        target.getInventory().setLeggings(null);
        target.getInventory().setBoots(null);
        target.getInventory().clear();

        if (target == commandSender) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                     "ClearInventory.Self")
                                                                                         .replace("<AMOUNT>", String.valueOf(counter)));
            return;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                 "ClearInventory.Others.Sender")
                                                                                     .replace("<AMOUNT>", String.valueOf(counter)));

        target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                          "ClearInventory.Others.Target")
                                                                              .replace("<AMOUNT>", String.valueOf(counter)));
    }
}
