package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class COMMAND_unnick extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_unnick(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Unnick", label, cmd.getName(), cs, null));
                return true;
            }
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.unnick.required"))
                if (!this.isAllowed(cs, "unnick.permission")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("unnick.permission")));
                    return true;
                }

            return true;
        }

        if (!this.isAllowed(cs, "unnick.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("unnick.others")));
            return true;
        }

        Player targetPlayer = this.getPlayer(cs, args[0]);

        if (targetPlayer == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        return true;
    }

    private void changeName(String name, Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);

            Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
            Field ff = profile.getClass().getDeclaredField("name");
            ff.setAccessible(true);
            ff.set(profile, name);
            player.setCustomName(name);
            player.setDisplayName(name);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(player);
                p.showPlayer(player);
            }
            if (this.plugin.getVanish().isVanish(player)) this.plugin.getVanish().setVanish(true, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
