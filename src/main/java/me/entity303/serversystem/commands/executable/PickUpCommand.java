package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PickUpCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public PickUpCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (this.plugin.getVanish().getAllowPickup().contains(commandSender)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "PickUp.DeActivated"));
            this.plugin.getVanish().getAllowPickup().remove(commandSender);
        } else {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "PickUp.Activated"));
            this.plugin.getVanish().getAllowPickup().add(((Player) commandSender));
        }

        return true;
    }
}
