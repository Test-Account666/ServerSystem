package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_clearchat implements CommandExecutor {
    private final ss plugin;

    public COMMAND_clearchat(ss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPerm(cs, "clearchat")) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("clearchat")));
            return true;
        }
        Bukkit.getOnlinePlayers().forEach(this::clear);
        Bukkit.broadcastMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "ClearChat"));
        return true;
    }

    private void clear(Player player) {
        for (int i = 0; i < 500; i++) player.sendMessage(String.valueOf((char) (5000 - 10)));
        for (int i = 0; i < 500; i++) player.sendMessage(" ");
    }
}
