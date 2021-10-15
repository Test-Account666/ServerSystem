package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_checkgamemode extends MessageUtils implements CommandExecutor {

    public COMMAND_checkgamemode(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPerm(cs, "checkgamemode")) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("checkgamemode")));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, command.getName(), cs, null, "CheckGameMode"));
            return true;
        }

        Player target = this.getPlayer(cs, args[0]);
        if (target != null)
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, command.getName(), cs, target, "CheckGamemode").replace("<MODE>", this.getMode(target.getGameMode())));
        else if (args[0].equalsIgnoreCase("Konsole") || args[0].equalsIgnoreCase("Console"))
            cs.sendMessage(args[0].equalsIgnoreCase("Konsole") ? this.plugin.getMessages().getPrefix() + "Die Konsole ist allmächtig und wird uns alle TÖTEN!" : this.plugin.getMessages().getPrefix() + "The console is almighty and will KILL us all!");
        else
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(args[0]));
        return true;
    }

    private String getMode(GameMode gamemode) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.GameModes." + gamemode.name());
    }
}
