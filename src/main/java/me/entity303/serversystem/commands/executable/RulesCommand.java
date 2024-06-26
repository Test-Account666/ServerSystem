package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public RulesCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.rules.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "rules.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("rules.permission");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

        commandSender.sendMessage(this._plugin.GetMessages()
                                             .GetMessage(commandLabel, command, commandSender, null, "Rules")
                                             .replace("<RULES>", this.GetRules(commandLabel, command.getName(), commandSender, null)));
        return true;
    }

    public String GetRules(String commandLabel, String command, CommandSender sender, CommandSender target) {
        var rules = this._plugin.GetRulesConfig().getString("Rules");
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
            senderName = this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }

        try {
            return ChatColor.TranslateAlternateColorCodes('&', rules.replace("<LABEL>", commandLabel)
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
