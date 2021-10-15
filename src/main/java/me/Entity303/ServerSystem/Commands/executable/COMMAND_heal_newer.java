package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class COMMAND_heal_newer extends MessageUtils implements CommandExecutor {

    public COMMAND_heal_newer(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) if (cs instanceof Player) if (this.isAllowed(cs, "heal.self")) {
            if (((Player) cs).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() < 1.0) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Heal.NotHealable", label, cmd.getName(), cs, null));
                return true;
            }
            ((Player) cs).setHealth(((Player) cs).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            ((Player) cs).setFoodLevel(20);
            ((Player) cs).setExhaustion(0);
            ((Player) cs).setFireTicks(0);
            for (PotionEffect effect : new ArrayList<>(((Player) cs).getActivePotionEffects()))
                ((Player) cs).removePotionEffect(effect.getType());
            cs.sendMessage(this.getPrefix() + this.getMessage("Heal.Self", label, cmd.getName(), cs, null));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("heal.self")));
        else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Heal", label, cmd.getName(), cs, null));
        else if (this.isAllowed(cs, "heal.others")) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) {
                if (target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() < 1.0) {
                    cs.sendMessage(this.getPrefix() + this.getMessage("Heal.NotHealable", label, cmd.getName(), cs, target));
                    return true;
                }
                target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                target.setFoodLevel(20);
                target.setExhaustion(0);
                target.setFireTicks(0);
                for (PotionEffect effect : new ArrayList<>(target.getActivePotionEffects()))
                    target.removePotionEffect(effect.getType());
                target.sendMessage(this.getPrefix() + this.getMessage("Heal.Others.Target", label, cmd.getName(), cs, target));
                cs.sendMessage(this.getPrefix() + this.getMessage("Heal.Others.Sender", label, cmd.getName(), cs, target));
            } else cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
        } else if (cs instanceof Player) if (this.isAllowed(cs, "heal.self")) {
            if (((Player) cs).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() < 1.0) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Heal.NotHealable", label, cmd.getName(), cs, null));
                return true;
            }
            ((Player) cs).setHealth(((Player) cs).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            ((Player) cs).setFoodLevel(20);
            ((Player) cs).setExhaustion(0);
            cs.sendMessage(this.getPrefix() + this.getMessage("Heal.Self", label, cmd.getName(), cs, null));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("heal.others")));
        else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Heal", label, cmd.getName(), cs, null));
        return true;
    }
}

