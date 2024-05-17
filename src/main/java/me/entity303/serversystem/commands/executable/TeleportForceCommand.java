package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportForceCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public TeleportForceCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (this._plugin.GetPermissions().HasPermission(commandSender, "tpo.self", true) || this._plugin.GetPermissions().HasPermission(commandSender, "tpo.others", true)) {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Tpo"));
                return true;
            }
            this._plugin.Info(ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.NoPermissionInfo"))
                                      .replace("<SENDER>", commandSender.getName()));
            var permission = this._plugin.GetPermissions().GetPermission("tpo.self") + " || " + this._plugin.GetPermissions().GetPermission("tpo.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (arguments.length == 1) {
            if ((!(commandSender instanceof Player))) {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Tpo"));
                return true;
            }
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tpo.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("tpo.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
            var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
                return true;
            }

            var location = target.getLocation();
            ((Player) commandSender).teleport(location);


            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Tpo.Self"));
            return true;
        }
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "tpo.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("tpo.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        var target1 = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target1 == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }
        var target2 = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[1]);
        if (target2 == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[1]));
            return true;
        }

        target1.teleport(target2.getLocation());


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target1, "Tpo.Others").replace("<TARGET2>", target2.getName()));
        return true;
    }
}
