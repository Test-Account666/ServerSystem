package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SetSpawnCommand extends CommandUtils implements ICommandExecutorOverload {

    public SetSpawnCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var messages = this._plugin.GetMessages();
        var permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return true;
        }

        if (!permissions.HasPermission(player, "setspawn")) {
            var permission = permissions.GetPermission("setspawn");
            player.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            return true;
        }


        var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
        var spawnLocation = (player).getLocation();
        cfg.set("Spawn.X", spawnLocation.getX());
        cfg.set("Spawn.Y", spawnLocation.getY());
        cfg.set("Spawn.Z", spawnLocation.getZ());
        cfg.set("Spawn.Yaw", spawnLocation.getYaw());
        cfg.set("Spawn.Pitch", spawnLocation.getPitch());
        cfg.set("Spawn.World", spawnLocation.getWorld().getName());

        player.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "SetSpawn"));
        try {
            cfg.save(spawnFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }
}
