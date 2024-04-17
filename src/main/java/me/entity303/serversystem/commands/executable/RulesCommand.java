package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand extends CommandUtils implements CommandExecutorOverload {

    public RulesCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.rules.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "rules.permission")) {
                var permission = this.plugin.getPermissions().getPermission("rules.permission");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

        commandSender.sendMessage(this.plugin.getMessages()
                                             .getMessage(commandLabel, command, commandSender, null, "Rules")
                                             .replace("<RULES>", this.getRules(commandLabel, command.getName(), commandSender, null)));
        return true;
    }

    public String getRules(String label, String command, CommandSender sender, CommandSender target) {
        var rules = this.plugin.getRulesConfig().getString("Rules");
        if (sender == null)
            throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null)
            target = sender;

        var senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player)
            senderDisplayName = ((Player) sender).getDisplayName();
        else
            senderDisplayName = senderName;

        var targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player)
            targetDisplayName = ((Player) target).getDisplayName();
        else
            targetDisplayName = targetName;

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }

        try {
            return ChatColor.translateAlternateColorCodes('&', rules.replace("<LABEL>", label)
                                                                    .replace("<COMMAND>", command)
                                                                    .replace("<SENDER>", senderName)
                                                                    .replace("<TARGET>", targetName)
                                                                    .replace("<SENDERDISPLAY>", senderDisplayName)
                                                                    .replace("<TARGETDISPLAY>", targetDisplayName)
                                                                    .replace("<BREAK>", "\n"));
        } catch (NullPointerException ignored) {
            return "Error! Path: Rules";
        }
    }
}
