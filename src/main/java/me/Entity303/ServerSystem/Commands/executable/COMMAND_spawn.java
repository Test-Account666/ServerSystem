package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class COMMAND_spawn extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_spawn(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.spawn.required"))
            if (!this.isAllowed(cs, "spawn.permission")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("spawn.permission")));
                return true;
            }
        File spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        if (!spawnFile.exists()) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Spawn.NoSpawn", label, cmd.getName(), cs, null));
            return true;
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
        Location location = ((Player) cs).getLocation().clone();
        location.setX(cfg.getDouble("Spawn.X"));
        location.setY(cfg.getDouble("Spawn.Y"));
        location.setZ(cfg.getDouble("Spawn.Z"));
        location.setYaw((float) cfg.getDouble("Spawn.Yaw"));
        location.setPitch((float) cfg.getDouble("Spawn.Pitch"));
        location.setWorld(Bukkit.getWorld(cfg.getString("Spawn.World")));
        if (!this.plugin.getConfig().getBoolean("teleportation.spawn.enabledelay") || this.isAllowed(cs, "spawn.bypassdelay", true)) {
            ((Player) cs).teleport(location);
            cs.sendMessage(this.getPrefix() + this.getMessage("Spawn.InstantTeleporting", label, cmd.getName(), cs, null));
            return true;
        }
        this.plugin.getTeleportMap().put(((Player) cs), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            OfflinePlayer player = ((OfflinePlayer) cs).getPlayer();
            if (player.isOnline()) {
                player.getPlayer().teleport(location);
                cs.sendMessage(COMMAND_spawn.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', COMMAND_spawn.this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Success")));
                COMMAND_spawn.this.plugin.getTeleportMap().remove(player);
            }
        }, 20L * this.plugin.getConfig().getInt("teleportation.spawn.delay")));
        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.Teleporting"));
        return true;
    }
}
