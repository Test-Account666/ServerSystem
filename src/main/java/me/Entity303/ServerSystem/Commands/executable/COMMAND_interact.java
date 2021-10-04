package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_interact extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_interact(ss plugin) {
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
