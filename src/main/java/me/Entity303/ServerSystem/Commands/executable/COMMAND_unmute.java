package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Events.AsyncUnmuteEvent;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_unmute extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_unmute(ss plugin) {
        super(plugin);
    }

    private OfflinePlayer getPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (!this.plugin.getMuteManager().isMuted(player)) player = null;
        if (player == null) player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "unmute")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("unmute")));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("UnMute", label, cmd.getName(), cs, null));
            return true;
        }
        OfflinePlayer target = this.getPlayer(args[0]);
        if (!this.plugin.getMuteManager().isMuted(target)) {
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("UnMute.NotMuted", label, cmd.getName(), cs, target.getName()));
            return true;
        }
        this.plugin.getMuteManager().removeMute(target.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            AsyncUnmuteEvent asyncUnmuteEvent = new AsyncUnmuteEvent(cs, target);
            Bukkit.getPluginManager().callEvent(asyncUnmuteEvent);
        });

        cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("UnMute.Success", label, cmd.getName(), cs, target.getName()));
        return true;
    }
}
