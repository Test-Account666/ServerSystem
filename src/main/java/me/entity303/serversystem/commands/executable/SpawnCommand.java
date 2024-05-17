package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

public class SpawnCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public SpawnCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.spawn.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "spawn.permission")) {
                var permission = this._plugin.GetPermissions().GetPermission("spawn.permission");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
        var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        if (!spawnFile.exists()) {

            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Spawn.NoSpawn"));
            return true;
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
        var location = GetSpawnLocation(cfg, player.getLocation());
        if (!this._plugin.GetConfigReader().GetBoolean("teleportation.spawn.enableDelay") ||
            this._plugin.GetPermissions().HasPermission(commandSender, "spawn.bypassdelay", true)) {

            ((Player) commandSender).teleport(location);


            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Spawn.InstantTeleporting"));
            return true;
        }
        this._plugin.GetTeleportMap().put(player, Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
            if (player.isOnline()) {
                var player1 = Objects.requireNonNull(player.getPlayer());
                player1.teleport(location);

                commandSender.sendMessage(SpawnCommand.this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&',
                                                                                                                                      SpawnCommand.this._plugin.GetMessages()
                                                                                                                                                              .GetConfiguration()
                                                                                                                                                              .GetString(
                                                                                                                                                                      "Messages.Misc.Teleportation.Success")));
                SpawnCommand.this._plugin.GetTeleportMap().remove(player);
            }
        }, 20L * this._plugin.GetConfigReader().GetInt("teleportation.spawn.delay")));
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Home.Teleporting"));
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
