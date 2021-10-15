package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_gms extends MessageUtils implements CommandExecutor {

    public COMMAND_gms(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) if (args.length == 0) if (this.isAllowed(cs, "gamemode.self.survival")) {
            Player p = (Player) cs;
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Self", label, cmd.getName(), p.getName(), null).replace("<MODE>", this.getMode(p.getGameMode())));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("gamemode.self.survival")));
        else if (this.isAllowed(cs, "gamemode.others.survival")) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) {
                target.setGameMode(GameMode.SURVIVAL);
                target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
            } else cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("gamemode.others.survival")));
        else if (args.length > 0) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) {
                target.setGameMode(GameMode.SURVIVAL);
                target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
            } else cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
        } else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Gms", label, cmd.getName(), cs, null));
        return true;
    }

    private String getMode(GameMode gamemode) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.GameModes." + gamemode.name());
    }
}
