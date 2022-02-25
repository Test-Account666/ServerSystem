package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.events.AsyncUnbanEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnBanCommand extends MessageUtils implements CommandExecutor {

    public UnBanCommand(ServerSystem plugin) {
        super(plugin);
    }

    private static OfflinePlayer getPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private ServerSystem getPlugin() {
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
        OfflinePlayer target = UnBanCommand.getPlayer(args[0]);
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
