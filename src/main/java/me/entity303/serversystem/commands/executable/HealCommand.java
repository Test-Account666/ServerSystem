package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class HealCommand extends MessageUtils implements CommandExecutor {

    public HealCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Heal", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "heal.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("heal.self")));
                return true;
            }
            if (((Player) cs).getMaxHealth() < 1) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Heal.NotHealable", label, cmd.getName(), cs, null));
                return true;
            }
            ((Player) cs).setHealth(((Player) cs).getMaxHealth());
            ((Player) cs).setFoodLevel(20);
            ((Player) cs).setExhaustion(0);
            ((Player) cs).setFireTicks(0);
            for (PotionEffect effect : new ArrayList<>(((Player) cs).getActivePotionEffects()))
                ((Player) cs).removePotionEffect(effect.getType());
            cs.sendMessage(this.getPrefix() + this.getMessage("Heal.Self", label, cmd.getName(), cs, null));
        } else if (this.isAllowed(cs, "heal.others")) {
            Player target = this.getPlayer(cs, args[0]);
            if (target == null) {
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
                return true;
            }
            if (target.getMaxHealth() < 1) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Heal.NotHealable", label, cmd.getName(), cs, target));
                return true;
            }
            target.setHealth(target.getMaxHealth());
            target.setFoodLevel(20);
            target.setExhaustion(0);
            target.setFireTicks(0);
            for (PotionEffect effect : new ArrayList<>(target.getActivePotionEffects()))
                target.removePotionEffect(effect.getType());
            target.sendMessage(this.getPrefix() + this.getMessage("Heal.Others.Target", label, cmd.getName(), cs, target));
            cs.sendMessage(this.getPrefix() + this.getMessage("Heal.Others.Sender", label, cmd.getName(), cs, target));
        } else if (cs instanceof Player) {
            if (!this.isAllowed(cs, "heal.self", true)) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("heal.others")));
                return true;
            }
            if (((Player) cs).getMaxHealth() < 1) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Heal.NotHealable", label, cmd.getName(), cs, null));
                return true;
            }
            ((Player) cs).setHealth(((Player) cs).getMaxHealth());
            ((Player) cs).setFoodLevel(20);
            ((Player) cs).setExhaustion(0);
            cs.sendMessage(this.getPrefix() + this.getMessage("Heal.Self", label, cmd.getName(), cs, null));
        } else cs.sendMessage(this.getPrefix() + this.getSyntax("Heal", label, cmd.getName(), cs, null));
        return true;
    }
}
