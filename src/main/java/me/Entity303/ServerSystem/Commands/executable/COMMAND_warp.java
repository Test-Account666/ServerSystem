package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.DatabaseManager.WarpManager;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_warp extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_warp(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.warp.required"))
            if (!this.isAllowed(cs, "warp.permission")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("warp.permission")));
                return true;
            }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Warp", label, cmd.getName(), cs, null));
            return true;
        }
        String name = args[0].toLowerCase();
        WarpManager warpManager = this.plugin.getWarpManager();
        if (!warpManager.doesWarpExist(name)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Warp.WarpDoesntExists", label, cmd.getName(), cs, null).replace("<WARP>", name.toUpperCase()));
            return true;
        }

        if (!(cs instanceof Player) || args.length >= 2) {
            if (!this.isAllowed(cs, "warp.others")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("warp.others")));
                return true;
            }
            if (args.length < 2) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Warp", label, cmd.getName(), cs, null));
                return true;
            }
            Player targetPlayer = this.getPlayer(cs, args[1]);
            if (targetPlayer == null) {
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
                return true;
            }
            Location location = warpManager.getWarp(name);
            targetPlayer.teleport(location);
            targetPlayer.sendMessage(this.getPrefix() + this.getMessage("Warp.Others.Teleporting.Target", label, cmd.getName(), cs, targetPlayer).replace("<WARP>", name.toUpperCase()));
            cs.sendMessage(this.getPrefix() + this.getMessage("Warp.Others.Teleporting.Sender", label, cmd.getName(), cs, targetPlayer).replace("<WARP>", name.toUpperCase()));
            return true;
        }

        if (this.plugin.getConfig().getBoolean("teleportation.warp.enabledelay") && !this.isAllowed(cs, "warp.bypassdelay", true)) {
            this.plugin.getTeleportMap().put(((Player) cs), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                OfflinePlayer player = ((OfflinePlayer) cs).getPlayer();
                if (player.isOnline()) {
                    Location location = warpManager.getWarp(name);
                    player.getPlayer().teleport(location);
                    cs.sendMessage(COMMAND_warp.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', COMMAND_warp.this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Success")));
                    COMMAND_warp.this.plugin.getTeleportMap().remove(player);
                }
            }, 20L * this.plugin.getConfig().getInt("teleportation.warp.delay")));
            cs.sendMessage(this.getPrefix() + this.getMessage("Warp.Teleporting", label, cmd.getName(), cs, null).replace("<WARP>", name.toUpperCase()));
            return true;
        }
        Location location = warpManager.getWarp(name);
        ((Player) cs).teleport(location);
        cs.sendMessage(this.getPrefix() + this.getMessage("Warp.InstantTeleporting", label, cmd.getName(), cs, null).replace("<WARP>", name.toUpperCase()));
        return true;
    }
}
