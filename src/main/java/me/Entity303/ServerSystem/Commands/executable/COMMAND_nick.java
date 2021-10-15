package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class COMMAND_nick extends MessageUtils implements CommandExecutor {

    public COMMAND_nick(ss plugin) {
        super(plugin);
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

    private void changeName(String name, Player player, boolean colored) {
        if (!colored) this.changeName(name, player);
        else try {
            name = ChatColor.translateAlternateColorCodes('&', name);
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

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Nick", label, cmd.getName(), cs, null));
            return true;
        }

        if (args.length == 1) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Nick", label, cmd.getName(), cs, null));
                return true;
            }

            if (!this.isAllowed(cs, "nick.self.use")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("nick.self.use")));
                return true;
            }

            if (args[0].length() > 16) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Nick.NickTooLong", label, cmd.getName(), cs, null).replace("<NICK>", args[0]));
                return true;
            }

            this.changeName(args[0], ((Player) cs), this.isAllowed(cs, "nick.self.colored", true));
            cs.sendMessage(this.getPrefix() + this.getMessage("Nick.Success.Self", label, cmd.getName(), cs, null).replace("<NICK>", args[0]));
            return true;
        }

        if (!this.isAllowed(cs, "nick.others.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("nick.others.use")));
            return true;
        }

        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        if (args[1].length() > 16) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Nick.NickTooLong", label, cmd.getName(), cs, null).replace("<NICK>", args[1]));
            return true;
        }

        cs.sendMessage(this.getPrefix() + this.getMessage("Nick.Success.Others", label, cmd.getName(), cs, target).replace("<NICK>", args[1]));
        this.changeName(args[1], target, this.isAllowed(cs, "nick.others.colored", true));
        return true;
    }
}
