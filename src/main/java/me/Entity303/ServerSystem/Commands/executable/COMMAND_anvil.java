package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_anvil extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_anvil(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (!this.isAllowed(cs, "anvil")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("anvil")));
            return true;
        }

        this.plugin.getVersionStuff().getVirtualAnvil().openAnvil((Player) cs);
        return true;
    }
}
