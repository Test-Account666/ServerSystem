package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_feed extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_feed(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) if (cs instanceof Player) if (this.isAllowed(cs, "feed.self")) {
            ((Player) cs).setFoodLevel(20);
            ((Player) cs).setExhaustion(0);
            cs.sendMessage(this.getPrefix() + this.getMessage("Feed.Self", label, cmd.getName(), cs, null));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("feed.self")));
        else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Feed", label, cmd.getName(), cs, null));
        else if (this.isAllowed(cs, "feed.others")) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) {
                target.setFoodLevel(20);
                target.setExhaustion(0);
                target.sendMessage(this.getPrefix() + this.getMessage("Feed.Others.Target", label, cmd.getName(), cs, target));
                cs.sendMessage(this.getPrefix() + this.getMessage("Feed.Others.Sender", label, cmd.getName(), cs, target));
            }
        } else if (cs instanceof Player) if (this.isAllowed(cs, "feed.self")) {
            ((Player) cs).setFoodLevel(20);
            ((Player) cs).setExhaustion(0);
            cs.sendMessage(this.getPrefix() + this.getMessage("Feed.Self", label, cmd.getName(), cs, null));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("feed.others")));
        else
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("feed.others")));
        return true;
    }

    @Override
    public String getPrefix() {
        return this.plugin.getMessages().getPrefix();
    }

    @Override
    public String getMessage(String action, String label, String command, CommandSender sender, CommandSender target) {
        return this.plugin.getMessages().getMessage(label, command, sender, target, action);
    }

    @Override
    public boolean isAllowed(CommandSender cs, String action) {
        return this.plugin.getPermissions().hasPerm(cs, action);
    }

    @Override
    public String Perm(String action) {
        return this.plugin.getPermissions().Perm(action);
    }

    @Override
    public String getNoPermission(String permission) {
        return this.plugin.getMessages().getNoPermission(permission);
    }

    @Override
    public String getSyntax(String action, String label, String command, CommandSender sender, CommandSender target) {
        return this.plugin.getMessages().getSyntax(label, command, sender, target, action);
    }
}
