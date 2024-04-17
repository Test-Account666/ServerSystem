package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class HealCommand extends CommandUtils implements CommandExecutorOverload {

    public HealCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Heal"));
                return true;
            }

            if (!this.plugin.getPermissions().hasPermission(commandSender, "heal.self")) {
                var permission = this.plugin.getPermissions().getPermission("heal.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

            this.HealPlayer(commandSender, player, command, commandLabel);
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "heal.others")) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Heal"));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        this.HealPlayer(commandSender, target, command, commandLabel);
        return true;
    }

    private void HealPlayer(CommandSender commandSender, Player target, Command command, String commandLabel) {
        var maxHealth = Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

        if (maxHealth < 1.0) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Heal.NotHealable"));
            return;
        }

        target.setHealth(maxHealth);
        target.setFoodLevel(20);
        target.setExhaustion(0);
        target.setSaturation(20);

        target.setFireTicks(0);
        for (var effect : new ArrayList<>(target.getActivePotionEffects()))
            target.removePotionEffect(effect.getType());

        if (commandSender == target) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Heal.Self"));

            return;
        }

        target.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Heal.Others.Target"));

        commandSender.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Heal.Others.Sender"));
    }
}

