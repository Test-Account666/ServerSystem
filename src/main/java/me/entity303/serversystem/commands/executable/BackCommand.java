package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public BackCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getBackreason().containsKey(commandSender)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Back.NoBack"));
            return true;
        }

        var reason = this.plugin.getBackreason().get(commandSender);
        if ("Teleport".equalsIgnoreCase(reason)) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "back.teleport")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("back.teleport")));
                return true;
            }

            Teleport.teleport(((Player) commandSender), this.plugin.getBackloc().get(commandSender));

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Back.Success.Teleport"));
            return true;
        }

        if ("Death".equalsIgnoreCase(reason)) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "back.death")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("back.death")));
                return true;
            }

            Teleport.teleport(((Player) commandSender), this.plugin.getBackloc().get(commandSender));

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Back.Success.Death"));
            return true;
        }
        return true;
    }
}
