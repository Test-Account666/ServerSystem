package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public BackCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {

            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getBackreason().containsKey(cs)) {
            String reason = this.plugin.getBackreason().get(cs);
            if ("Teleport".equalsIgnoreCase(reason)) {
                if (!this.plugin.getPermissions().hasPerm(cs, "back.teleport")) {
                    cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("back.teleport")));
                    return true;
                }

                Teleport.teleport(((Player) cs), this.plugin.getBackloc().get(cs));

                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Back.Success.Teleport"));
                return true;
            } else if ("Death".equalsIgnoreCase(reason)) {
                if (!this.plugin.getPermissions().hasPerm(cs, "back.death")) {
                    cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("back.death")));
                    return true;
                }

                Teleport.teleport(((Player) cs), this.plugin.getBackloc().get(cs));

                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Back.Success.Death"));
                return true;
            }
        } else
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Back.NoBack"));
        return true;
    }
}
