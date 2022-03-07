package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public ClearChatCommand(ServerSystem plugin) {
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
        for (int i = 0; i < 500; i++) {
            StringBuilder clear = new StringBuilder();
            for (int i1 = 0; i1 < i; i1++) {
                clear.append(String.valueOf((char) (5000 - 10)));
            }
            player.sendMessage(clear.toString());
        }
        for (int i = 0; i < 500; i++) player.sendMessage(" ");
    }
}
