package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KitCommand extends CommandUtils implements CommandExecutorOverload {

    public KitCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Kit"));
            return true;
        }

        if (arguments.length == 1) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Kit"));
                return true;
            }

            this.GiveKit(player, arguments[0], commandSender, command, commandLabel, commandSender.getName());
            return true;
        }

        var kitName = arguments[0];
        var target = this.getPlayer(commandSender, arguments[1]);

        this.GiveKit(target, kitName, commandSender, command, commandLabel, arguments[1]);
        return true;
    }

    private void GiveKit(Player target, String kitName, CommandSender commandSender, Command command, String commandLabel, String targetName) {
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(targetName));
            return;
        }

        if (!this.plugin.getKitsManager().doesKitExist(kitName)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, target,
                                                                                                     "Kit.DoesntExist")
                                                                                         .replace("<KIT>", kitName.toUpperCase()));
            return;
        }

        var others = target != commandSender;

        if (!this.plugin.getKitsManager().isKitAllowed(commandSender, kitName, others))
            return;

        if (this.plugin.getKitsManager().isKitDelayed(target, kitName))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "kit.bypassdelay", true)) {
                var delay = this.plugin.getKitsManager().getPlayerLastDelay(target.getUniqueId().toString(), kitName) +
                            this.plugin.getKitsManager().getKitDelay(kitName);

                var dateFormat =
                        new SimpleDateFormat(this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Kit.TimeFormat"));
                var date = new Date(delay);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, target,
                                                                                                         "Kit.OnDelay")
                                                                                             .replace("<KIT>", kitName.toUpperCase())
                                                                                             .replace("<DATE>", dateFormat.format(date)));
                return;
            }

        this.plugin.getKitsManager().giveKit(target, kitName);

        if (!others) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, target,
                                                                                                     "Kit.Success.Self")
                                                                                         .replace("<KIT>", kitName.toUpperCase()));
            return;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, target,
                                                                                                 "Kit.Success.Others.Sender")
                                                                                     .replace("<KIT>", kitName.toUpperCase()));

        target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, command, commandSender, target,
                                                                                          "Kit.Success.Others.Target")
                                                                              .replace("<KIT>", kitName.toUpperCase()));
    }
}


