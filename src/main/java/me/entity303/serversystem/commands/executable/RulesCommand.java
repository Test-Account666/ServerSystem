package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RulesCommand extends MessageUtils implements CommandExecutor {

    public RulesCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.rules.required"))
            if (!this.isAllowed(cs, "rules.permission")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("rules.permission")));
                return true;
            }
        cs.sendMessage(this.getMessage("Rules", label, cmd.getName(), cs, null).replace("<RULES>", this.getRules(label, cmd.getName(), cs, null)));
        return true;
    }
}
