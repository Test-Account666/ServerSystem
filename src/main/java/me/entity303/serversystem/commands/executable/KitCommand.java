package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class KitCommand extends MessageUtils implements CommandExecutor {

    public KitCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Kit", label, cmd.getName(), cs, null));
            return true;
        }
        if (args.length == 1) {

            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Kit", label, cmd.getName(), cs, null));
                return true;
            }

            if (!this.plugin.getKitsManager().doesKitExist(args[0])) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Kit.DoesntExist", label, cmd.getName(), cs, null).replace("<KIT>", args[0].toUpperCase()));
                return true;
            }

            if (!this.plugin.getKitsManager().isKitAllowed(cs, args[0], false)) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("kit.self").replace("<KIT>", args[0].toLowerCase())));
                return true;
            }

            if (this.plugin.getKitsManager().isKitDelayed(((Player) cs), args[0]) && !this.isAllowed(cs, "kit.bypassdelay", true)) {
                long delay = this.plugin.getKitsManager().getPlayerLastDelay(((Player) cs).getUniqueId().toString(), args[0]) + this.plugin.getKitsManager().getKitDelay(args[0]);
                long calc = delay - System.currentTimeMillis();
                double minutes = ((calc / 1000D) / 60D);
                cs.sendMessage(this.getPrefix() + this.getMessage("Kit.OnDelay", label, cmd.getName(), cs, null).replace("<KIT>", args[0].toUpperCase()).replace("<MINUTES>", new DecimalFormat("#.##").format(minutes)));
                return true;
            }

            this.plugin.getKitsManager().setDelay(((Player) cs).getUniqueId().toString(), args[0], System.currentTimeMillis());

            this.plugin.getKitsManager().giveKit(((Player) cs), args[0]);
            cs.sendMessage(this.getPrefix() + this.getMessage("Kit.Success.Self", label, cmd.getName(), cs, null).replace("<KIT>", args[0].toUpperCase()));
            return true;
        }

        String kitName = args[0];

        Player target = this.getPlayer(cs, args[1]);

        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
            return true;
        }

        if (!this.plugin.getKitsManager().doesKitExist(kitName)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Kit.DoesntExist", label, cmd.getName(), cs, target).replace("<KIT>", kitName.toUpperCase()));
            return true;
        }

        if (!this.plugin.getKitsManager().isKitAllowed(cs, kitName, true)) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm(this.Perm("kit.others").replace("<KIT>", kitName.toLowerCase()))));
            return true;
        }

        this.plugin.getKitsManager().giveKit(target, kitName);
        cs.sendMessage(this.getPrefix() + this.getMessage("Kit.Success.Others.Sender", label, cmd.getName(), cs, target).replace("<KIT>", kitName.toUpperCase()));
        target.sendMessage(this.getPrefix() + this.getMessage("Kit.Success.Others.Target", label, cmd.getName(), cs, target).replace("<KIT>", kitName.toUpperCase()));
        return true;
    }
}
