package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public TeleportCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tp.self", true))
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "tp.others", true)) {
                    this._plugin.Info(ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages()
                                                                                            .GetConfiguration()
                                                                                            .GetString("Messages.Misc.NoPermissionInfo"))
                                              .replace("<SENDER>", commandSender.getName()));

                    var selfTeleportPermission = this._plugin.GetPermissions().GetPermission("tp.self");

                    var othersTeleportPermission = this._plugin.GetPermissions().GetPermission("tp.others");

                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                 .GetNoPermission(
                                                                                                         selfTeleportPermission + " || " +
                                                                                                         othersTeleportPermission));
                    return true;
                }

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Tp"));
            return true;
        }

        if (arguments.length == 1) {
            if ((!(commandSender instanceof Player))) {

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Tp"));
                return true;
            }

            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tp.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("tp.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
                return true;
            }

            if (target != commandSender)
                if (!this._plugin.GetWantsTeleport().DoesPlayerWantTeleport(target)) {

                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                 .GetMessage(commandLabel, command,
                                                                                                             commandSender, target,
                                                                                                             "Tp.NoTeleportations"));
                    return true;
                }

            var location = target.getLocation();
            ((Player) commandSender).teleport(location);


            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Tp.Self"));
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "tp.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("tp.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var target1 = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target1 == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (target1 != commandSender)
            if (!this._plugin.GetWantsTeleport().DoesPlayerWantTeleport(target1)) {

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessage(commandLabel, command,
                                                                                                         commandSender, target1,
                                                                                                         "Tp.NoTeleportations"));
                return true;
            }

        var target2 = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[1]);
        if (target2 == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[1]));
            return true;
        }

        if (target2 != commandSender)
            if (!this._plugin.GetWantsTeleport().DoesPlayerWantTeleport(target2)) {

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessage(commandLabel, command,
                                                                                                         commandSender, target2,
                                                                                                         "Tp.NoTeleportations"));
                return true;
            }

        target1.teleport(target2.getLocation());


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender,
                                                                                                 target1, "Tp.Others")
                                                                                     .replace("<TARGET2>", target2.getName())
                                                                                     .replace("<TARGET2DISPLAY>",
                                                                                              target2.getDisplayName()));
        return true;
    }
}
