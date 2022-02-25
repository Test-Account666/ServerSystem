package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckhealthCommand extends MessageUtils implements CommandExecutor {

    public CheckhealthCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "checkhealth")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("checkhealth")));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("CheckHealth", label, cmd.getName(), cs, null));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        cs.sendMessage(this.getPrefix() + this.getMessage("CheckHealth", label, cmd.getName(), cs, target).replace("<HEALTH>", String.valueOf(target.getHealth())).replace("<FOOD>", String.valueOf(target.getFoodLevel())).replace("<MAXHEALTH>", String.valueOf(this.getMaxHealth(target))));
        return true;
    }
}
