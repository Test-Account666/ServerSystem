package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SetSpawnCommand extends MessageUtils implements CommandExecutor {

    public SetSpawnCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (!this.isAllowed(cs, "setspawn")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("setspawn")));
            return true;
        }
        File spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
        Location spawnLocation = ((Player) cs).getLocation();
        cfg.set("Spawn.X", spawnLocation.getX());
        cfg.set("Spawn.Y", spawnLocation.getY());
        cfg.set("Spawn.Z", spawnLocation.getZ());
        cfg.set("Spawn.Yaw", spawnLocation.getYaw());
        cfg.set("Spawn.Pitch", spawnLocation.getPitch());
        cfg.set("Spawn.World", spawnLocation.getWorld().getName());
        cs.sendMessage(this.getPrefix() + this.getMessage("SetSpawn", label, cmd.getName(), cs, null));
        try {
            cfg.save(spawnFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
