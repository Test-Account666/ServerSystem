package me.testaccount666.serversystem.commands.executables.repair;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "repair", tabCompleter = TabCompleterRepair.class)
public class CommandRepair extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Repair.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var repairType = arguments.length == 0? "hand" : arguments[0].toLowerCase();
        var player = commandSender.getPlayer();

        switch (repairType) {
            case "all", "*" -> {
                var repaired = 0;
                repaired += repairInventory(player.getInventory().getContents());
                repaired += repairInventory(player.getInventory().getArmorContents());
                repaired += repairInventory(player.getInventory().getExtraContents());

                sendSuccessMessage(commandSender, repaired);
            }
            case "hand" -> {
                var item = player.getInventory().getItemInMainHand();
                if (repairItem(item)) sendSuccessMessage(commandSender, 1);
                else command("Repair.NotRepairable", commandSender).build();
            }
            case "offhand" -> {
                var item = player.getInventory().getItemInOffHand();
                if (repairItem(item)) sendSuccessMessage(commandSender, 1);
                else command("Repair.NotRepairable", commandSender).build();
            }
            case "armor" -> {
                var repaired = repairInventory(player.getInventory().getArmorContents());
                sendSuccessMessage(commandSender, repaired);
            }
            case "inventory" -> {
                var repaired = repairInventory(player.getInventory().getContents());
                sendSuccessMessage(commandSender, repaired);
            }
            default -> general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
        }
    }

    private void sendSuccessMessage(User commandSender, int count) {
        command("Repair.Success", commandSender).postModifier(message -> message.replace("<COUNT>", String.valueOf(count))).build();
    }

    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        var meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;
        if (!damageable.hasDamage()) return false;

        damageable.setDamage(0);
        item.setItemMeta(meta);
        return true;
    }

    private int repairInventory(ItemStack[] items) {
        var repairedCount = 0;
        for (var item : items) if (repairItem(item)) repairedCount++;
        return repairedCount;
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Repair";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Repair.Use", false);
    }
}