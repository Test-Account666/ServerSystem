package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnlimitedCommand extends CommandUtils implements CommandExecutorOverload {

    public UnlimitedCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "unlimited")) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission("unlimited"));
            return true;
        }

        if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Unlimited.NoItem"));
            return true;
        }

        var itemStack = player.getInventory().getItemInMainHand();

        if (UnlimitedCommand.isUnlimited(itemStack)) {
            this.plugin.getVersionStuff().getNbtViewer().removeTag("unlimited", itemStack);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Unlimited.LimitedNow"));
            return true;
        }

        this.plugin.getVersionStuff().getNbtViewer().setTag("unlimited", itemStack);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Unlimited.UnlimitedNow"));
        return true;
    }

    public static boolean isUnlimited(ItemStack itemStack) {
        return ServerSystem.getPlugin(ServerSystem.class).getVersionStuff().getNbtViewer().isTagSet("unlimited", itemStack);
    }
}
