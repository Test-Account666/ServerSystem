package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SetSpawnCommand extends CommandUtils implements CommandExecutorOverload {

    public SetSpawnCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (!this.plugin.getPermissions().hasPermission(commandSender, "setspawn")) {
            var permission = this.plugin.getPermissions().getPermission("setspawn");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
        var spawnLocation = ((Player) commandSender).getLocation();
        cfg.set("Spawn.X", spawnLocation.getX());
        cfg.set("Spawn.Y", spawnLocation.getY());
        cfg.set("Spawn.Z", spawnLocation.getZ());
        cfg.set("Spawn.Yaw", spawnLocation.getYaw());
        cfg.set("Spawn.Pitch", spawnLocation.getPitch());
        cfg.set("Spawn.World", spawnLocation.getWorld().getName());
        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "SetSpawn"));
        try {
            cfg.save(spawnFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
