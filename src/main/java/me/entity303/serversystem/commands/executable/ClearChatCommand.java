package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public ClearChatCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "clearchat")) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("clearchat")));
            return true;
        }

        Bukkit.getOnlinePlayers().forEach(this::clear);
        Bukkit.broadcastMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "ClearChat"));

        return true;
    }

    private void clear(Player player) {
        for (var i = 0; i < 500; i++)
            player.sendMessage(String.valueOf((char) (5000 - 10)).repeat(i));
        for (var i = 0; i < 500; i++)
            player.sendMessage(" ");
    }
}
