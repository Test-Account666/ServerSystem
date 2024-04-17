package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PingCommand extends CommandUtils implements CommandExecutorOverload {
    private Field pingField;
    private Method getPingMethod;

    public PingCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {

                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Ping"));
                return true;
            }

            if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.ping.self.required"))
                if (!this.plugin.getPermissions().hasPermission(commandSender, "ping.self.permission")) {
                    var permission = this.plugin.getPermissions().getPermission("ping.self.permission");
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                    return true;
                }

            this.sendPing((Player) commandSender, commandLabel);
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "ping.others", true)) {
            var permission = this.plugin.getPermissions().getPermission("ping.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        var ping = this.getPing(target);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, target, "Ping.Others")
                                                                                     .replace("<PING>", String.valueOf(ping)));
        return true;
    }

    private void sendPing(Player player, String label) {
        try {
            var ping = this.getPing(player);

            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                  .getMessage(label, "ping", player, null, "Ping.Self")
                                                                                  .replace("<PING>", String.valueOf(Math.max(ping, 0))));
        } catch (Exception e) {
            player.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getMessage(label, "ping", player, null, "Ping.Self").replace("<PING>", String.valueOf(666)));
        }
    }

    private int getPing(Player player) {
        try {
            return this.getPingInternal(player);
        } catch (Exception e) {
            return 666;
        }
    }

    private int getPingInternal(Player player) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (this.plugin.getVersionStuff().getGetHandleMethod() == null)
            this.plugin.getVersionStuff().FetchGetHandleMethod();

        var entityPlayer = this.plugin.getVersionStuff().getGetHandleMethod().invoke(player);
        if (this.pingField == null && this.getPingMethod == null) {
            try {
                this.pingField = entityPlayer.getClass().getDeclaredField("ping");
            } catch (NoSuchFieldError | NoSuchFieldException e) {
                this.getPingMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer")
                                          .getDeclaredMethod("getPing");
            }
            if (this.pingField != null)
                this.pingField.setAccessible(true);
        }

        var ping = 666;

        if (this.getPingMethod != null)
            ping = (int) this.getPingMethod.invoke(player);
        else
            ping = this.pingField.getInt(entityPlayer);

        return Math.max(ping, 0);
    }
}
