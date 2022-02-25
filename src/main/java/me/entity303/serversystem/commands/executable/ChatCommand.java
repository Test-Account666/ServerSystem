package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public ChatCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getVanish().getAllowChat().contains(cs)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Chat.DeActivated"));
            this.plugin.getVanish().getAllowChat().remove(cs);
        } else {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Chat.Activated"));
            this.plugin.getVanish().getAllowChat().add(((Player) cs));
        }
        return true;
    }
}
