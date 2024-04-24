package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnlimitedCommand extends CommandUtils implements ICommandExecutorOverload {

    public UnlimitedCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "unlimited")) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("unlimited")));
            return true;
        }

        if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Unlimited.NoItem"));
            return true;
        }

        var itemStack = player.getInventory().getItemInMainHand();

        if (UnlimitedCommand.IsUnlimited(itemStack)) {
            this._plugin.GetVersionStuff().GetNbtViewer().RemoveTag("unlimited", itemStack);
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Unlimited.LimitedNow"));
            return true;
        }

        this._plugin.GetVersionStuff().GetNbtViewer().SetTag("unlimited", itemStack);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Unlimited.UnlimitedNow"));
        return true;
    }

    public static boolean IsUnlimited(ItemStack itemStack) {
        return ServerSystem.getPlugin(ServerSystem.class).GetVersionStuff().GetNbtViewer().IsTagSet("unlimited", itemStack);
    }
}
