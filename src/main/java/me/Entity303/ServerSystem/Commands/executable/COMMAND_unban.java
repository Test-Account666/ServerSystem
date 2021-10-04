package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Events.AsyncUnbanEvent;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_unban extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_unban(ss plugin) {
        super(plugin);
    }

    private static OfflinePlayer getPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "unban")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("unban")));
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("UnBan", label, cmd.getName(), cs, null));
            return true;
        }
        OfflinePlayer target = COMMAND_unban.getPlayer(args[0]);
        if (!this.getPlugin().getBanManager().isBanned(target.getUniqueId())) {
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("UnBan.NotBanned", label, cmd.getName(), cs, target.getName()));
            return true;
        }
        this.getPlugin().getBanManager().unBan(target.getUniqueId());
        cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("UnBan.Success", label, cmd.getName(), cs, target.getName()));
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            AsyncUnbanEvent asyncUnbanEvent = new AsyncUnbanEvent(cs, target);
            Bukkit.getPluginManager().callEvent(asyncUnbanEvent);
        });
        return true;
    }
}
