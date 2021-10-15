package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_delkit extends MessageUtils implements CommandExecutor {

    public COMMAND_delkit(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "deletekit")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("deletekit")));
            return true;
        }

        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("DeleteKit", label, cmd.getName(), cs, null));
            return true;
        }

        if (!this.plugin.getKitsManager().doesKitExist(args[0])) {
            cs.sendMessage(this.getPrefix() + this.getMessage("DeleteKit.DoesntExist", label, cmd.getName(), cs, null));
            return true;
        }

        this.plugin.getKitsManager().deleteKit(args[0]);
        cs.sendMessage(this.getPrefix() + this.getMessage("DeleteKit.Success", label, cmd.getName(), cs, null).replace("<KIT>", args[0].toUpperCase()));
        return true;
    }
}
