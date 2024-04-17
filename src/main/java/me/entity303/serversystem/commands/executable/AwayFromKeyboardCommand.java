package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AwayFromKeyboardCommand extends CommandUtils implements CommandExecutorOverload {

    public AwayFromKeyboardCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.afk.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "afk.permission")) {
                var permission = this.plugin.getPermissions().getPermission("afk.permission");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

        var awayFromKeyboard = false;

        if (player.hasMetadata("afk"))
            awayFromKeyboard = isAwayFromKeyboard(player);

        if (!awayFromKeyboard) {
            player.removeMetadata("afk", this.plugin);
            player.setMetadata("afk", this.plugin.getMetaValue().getMetaValue(true));


            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Afk.Enabled"));
            return true;
        }

        player.removeMetadata("afk", this.plugin);
        player.setMetadata("afk", this.plugin.getMetaValue().getMetaValue(false));

        commandSender.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Afk.Disabled"));
        return true;
    }
}
