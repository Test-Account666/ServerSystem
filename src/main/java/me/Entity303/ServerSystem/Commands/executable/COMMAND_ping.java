package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class COMMAND_ping extends ServerSystemCommand implements CommandExecutor {
    private Method getHandleMethod;
    private Field pingField;

    public COMMAND_ping(ss plugin) {
        super(plugin);
    }

    private void sendPing(Player player, String label) {
        try {
            if (this.getHandleMethod == null) {
                this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                this.getHandleMethod.setAccessible(true);
            }
            Object entityPlayer = this.getHandleMethod.invoke(player);
            if (this.pingField == null) {
                this.pingField = entityPlayer.getClass().getDeclaredField("ping");
                this.pingField.setAccessible(true);
            }
            int ping = this.pingField.getInt(entityPlayer);

            player.sendMessage(this.getPrefix() + this.getMessage("Ping.Self", label, "ping", player.getName(), null).replace("<PING>", String.valueOf(Math.max(ping, 0))));
        } catch (Exception e) {
            player.sendMessage(this.getPrefix() + this.getMessage("Ping.Self", label, "ping", player.getName(), null).replace("<PING>", String.valueOf(666)));
        }
    }

    private int getPing(Player player) {
        try {
            if (this.getHandleMethod == null) {
                this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                this.getHandleMethod.setAccessible(true);
            }
            Object entityPlayer = this.getHandleMethod.invoke(player);
            if (this.pingField == null) {
                this.pingField = entityPlayer.getClass().getDeclaredField("ping");
                this.pingField.setAccessible(true);
            }
            int ping = this.pingField.getInt(entityPlayer);

            return Math.max(ping, 0);
        } catch (Exception e) {
            return 666;
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Ping", label, cmd.getName(), cs, null));
                return true;
            }
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.ping.self.required"))
                if (!this.isAllowed(cs, "ping.self.permission")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("ping.self.permission")));
                    return true;
                }
            this.sendPing((Player) cs, label);
            return true;
        }
        if (!this.isAllowed(cs, "ping.others", true)) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("ping.others")));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        int ping = this.getPing(target);
        cs.sendMessage(this.getPrefix() + this.getMessage("Ping.Others", label, cmd.getName(), cs, target).replace("<PING>", String.valueOf(ping)));
        return true;
    }
}
