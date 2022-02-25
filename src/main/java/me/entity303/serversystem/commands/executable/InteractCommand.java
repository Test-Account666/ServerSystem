package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InteractCommand extends MessageUtils implements CommandExecutor {

    public InteractCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getVanish().getAllowInteract().contains(cs)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.getMessage("Interact.DeActivated", label, cmd.getName(), cs, null));
            this.plugin.getVanish().getAllowInteract().remove(cs);
        } else {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.getMessage("Interact.Activated", label, cmd.getName(), cs, null));
            this.plugin.getVanish().getAllowInteract().add(((Player) cs));
        }
        return true;
    }
}
