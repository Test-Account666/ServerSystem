package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_vanish extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_vanish(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!this.plugin.getPermissions().hasPerm(cs, "vanish.self")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("vanish.self")));
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, cmd.getName(), cs, null, "Vanish"));
                return true;
            }
            if (this.plugin.getVanish().isVanish(((Player) cs))) {
                this.plugin.getVanish().setVanishData(((Player) cs), false);
                this.plugin.getVanish().setVanish(false, ((Player) cs));
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Vanish.Self.DeActivated"));
                Bukkit.getOnlinePlayers().forEach(all -> all.showPlayer(((Player) cs)));
            } else {
                this.plugin.getVanish().setVanishData(((Player) cs), true);
                this.plugin.getVanish().setVanish(true, ((Player) cs));
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Vanish.Self.Activated"));
                Bukkit.getOnlinePlayers().stream().filter(player -> !this.plugin.getPermissions().hasPerm(player, "vanish.see", true)).forEachOrdered(player -> player.hidePlayer(((Player) cs)));
            }
            return true;
        }
        if (!this.plugin.getPermissions().hasPerm(cs, "vanish.others")) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("vanish.others")));
            return true;
        }
        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(args[0]));
            return true;
        }
        if (this.plugin.getVanish().isVanish(targetPlayer)) {
            this.plugin.getVanish().setVanishData(targetPlayer, false);
            this.plugin.getVanish().setVanish(false, targetPlayer);
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "Vanish.Others.DeActivated.Sender"));
            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "Vanish.Others.DeActivated.Target"));
            Bukkit.getOnlinePlayers().forEach(all -> all.showPlayer(targetPlayer));
        } else {
            this.plugin.getVanish().setVanishData(targetPlayer, true);
            this.plugin.getVanish().setVanish(true, targetPlayer);
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "Vanish.Others.Activated.Sender"));
            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "Vanish.Others.Activated.Target"));
            Bukkit.getOnlinePlayers().stream().filter(player -> !this.plugin.getPermissions().hasPerm(player, "vanish.see", true)).forEachOrdered(player -> player.hidePlayer(targetPlayer));
        }
        return true;
    }
}
