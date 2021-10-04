package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_gmc extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_gmc(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) if (args.length == 0) if (this.isAllowed(cs, "gamemode.self.creative")) {
            Player p = (Player) cs;
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Self", label, cmd.getName(), p.getName(), null).replace("<MODE>", this.getMode(p.getGameMode())));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("gamemode.self.creative")));
        else if (this.isAllowed(cs, "gamemode.others.creative")) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) {
                target.setGameMode(GameMode.CREATIVE);
                target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
            } else cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("gamemode.others.creative")));
        else if (args.length > 0) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) {
                target.setGameMode(GameMode.CREATIVE);
                target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
            } else cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
        } else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Gmc", label, cmd.getName(), cs, null));
        return true;
    }

    private String getMode(GameMode gamemode) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.GameModes." + gamemode.name());
    }
}
