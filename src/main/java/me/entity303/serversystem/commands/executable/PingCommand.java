package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PingCommand implements ICommandExecutorOverload {
    protected final ServerSystem _plugin;
    private Field _pingField;
    private Method _getPingMethod;

    public PingCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {

                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Ping"));
                return true;
            }

            if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.ping.self.required"))
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "ping.self.permission")) {
                    var permission = this._plugin.GetPermissions().GetPermission("ping.self.permission");
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                    return true;
                }

            this.SendPing((Player) commandSender, commandLabel);
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "ping.others", true)) {
            var permission = this._plugin.GetPermissions().GetPermission("ping.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        var ping = this.GetPing(target);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, target, "Ping.Others")
                                                                                     .replace("<PING>", String.valueOf(ping)));
        return true;
    }

    private void SendPing(Player player, String commandLabel) {
        try {
            var ping = this.GetPing(player);

            player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                  .GetMessage(commandLabel, "ping", player, null, "Ping.Self")
                                                                                  .replace("<PING>", String.valueOf(Math.max(ping, 0))));
        } catch (Exception exception) {
            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, "ping", player, null, "Ping.Self").replace("<PING>", String.valueOf(666)));
        }
    }

    private int GetPing(Player player) {
        try {
            return this.GetPingInternal(player);
        } catch (Exception exception) {
            return 666;
        }
    }

    private int GetPingInternal(Player player) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (this._plugin.GetVersionStuff().GetGetHandleMethod() == null)
            this._plugin.GetVersionStuff().FetchGetHandleMethod(player);

        var entityPlayer = this._plugin.GetVersionStuff().GetGetHandleMethod().invoke(player);
        if (this._pingField == null && this._getPingMethod == null) {
            try {
                this._pingField = entityPlayer.getClass().getDeclaredField("ping");
            } catch (NoSuchFieldError | NoSuchFieldException exception) {
                this._getPingMethod = player.getClass().getDeclaredMethod("getPing");
            }
            if (this._pingField != null)
                this._pingField.setAccessible(true);
        }

        var ping = 666;

        if (this._getPingMethod != null)
            ping = (int) this._getPingMethod.invoke(player);
        else
            ping = this._pingField.getInt(entityPlayer);

        return Math.max(ping, 0);
    }
}
