package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_rules extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_rules(ss plugin) {
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
