package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

public class SpawnCommand extends CommandUtils implements CommandExecutorOverload {

    public SpawnCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.spawn.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "spawn.permission")) {
                var permission = this.plugin.getPermissions().getPermission("spawn.permission");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
        var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        if (!spawnFile.exists()) {

            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Spawn.NoSpawn"));
            return true;
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
        var location = GetSpawnLocation(cfg, player.getLocation());
        if (!this.plugin.getConfigReader().getBoolean("teleportation.spawn.enableDelay") ||
            this.plugin.getPermissions().hasPermission(commandSender, "spawn.bypassdelay", true)) {

            Teleport.teleport((Player) commandSender, location);


            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Spawn.InstantTeleporting"));
            return true;
        }
        this.plugin.getTeleportMap().put(player, Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (player.isOnline()) {
                Teleport.teleport(Objects.requireNonNull(player.getPlayer()), location);

                commandSender.sendMessage(SpawnCommand.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&',
                                                                                                                                      SpawnCommand.this.plugin.getMessages()
                                                                                                                                                              .getCfg()
                                                                                                                                                              .getString(
                                                                                                                                                                      "Messages.Misc.Teleportation.Success")));
                SpawnCommand.this.plugin.getTeleportMap().remove(player);
            }
        }, 20L * this.plugin.getConfigReader().getInt("teleportation.spawn.delay")));
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Home.Teleporting"));
        return true;
    }

    public static Location GetSpawnLocation(FileConfiguration cfg, Location location) {
        location.setX(cfg.getDouble("Spawn.X"));
        location.setY(cfg.getDouble("Spawn.Y"));
        location.setZ(cfg.getDouble("Spawn.Z"));
        location.setYaw((float) cfg.getDouble("Spawn.Yaw"));
        location.setPitch((float) cfg.getDouble("Spawn.Pitch"));
        location.setWorld(Bukkit.getWorld(Objects.requireNonNull(cfg.getString("Spawn.World"))));
        return location;
    }
}
