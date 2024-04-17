package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InteractCommand extends CommandUtils implements CommandExecutorOverload {

    public InteractCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (this.plugin.getVanish().getAllowInteract().contains(commandSender)) {

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Interact.DeActivated"));
            this.plugin.getVanish().getAllowInteract().remove(commandSender);
        } else {

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Interact.Activated"));
            this.plugin.getVanish().getAllowInteract().add(((Player) commandSender));
        }

        return true;
    }
}
