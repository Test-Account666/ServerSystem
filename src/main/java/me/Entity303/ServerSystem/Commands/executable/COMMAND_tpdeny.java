package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.TpaData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_tpdeny implements CommandExecutor {
    private final ss plugin;

    public COMMAND_tpdeny(ss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.tpdeny.required"))
            if (!this.plugin.getPermissions().hasPerm(cs, "tpdeny.permission")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("tpdeny.permission")));
                return true;
            }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getTpaDataMap().containsKey(cs)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "TpDeny.NoTpa"));
            return true;
        }

        TpaData tpaData = this.plugin.getTpaDataMap().get(cs);

        if (tpaData.getEnd() <= System.currentTimeMillis()) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "TpDeny.NoTpa"));
            return true;
        }

        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpDeny.Sender"));
        if (tpaData.getSender().isOnline())
            tpaData.getSender().getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(label, cmd.getName(), cs, tpaData.getSender().getName(), "TpDeny.Target"));
        this.plugin.getTpaDataMap().remove(cs);
        return true;
    }
}
