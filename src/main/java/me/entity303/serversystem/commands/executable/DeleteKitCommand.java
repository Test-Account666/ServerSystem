package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteKitCommand extends MessageUtils implements CommandExecutor {

    public DeleteKitCommand(ServerSystem plugin) {
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
