package me.testaccount666.serversystem.commands.executables.spawn;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@ServerSystemCommand(name = "spawn", variants = "setspawn")
public class CommandSpawn extends AbstractServerSystemCommand {
    private final File _spawnFile = Path.of("plugins", "ServerSystem", "spawn.yml").toFile();
    protected FileConfiguration spawnConfiguration;
    protected boolean teleportOnJoin;
    protected boolean teleportOnFirstJoin;
    protected Optional<Location> spawnLocation = Optional.empty();

    public CommandSpawn() {
        spawnConfiguration = YamlConfiguration.loadConfiguration(_spawnFile);

        saveDefaultConfig();

        teleportOnJoin = spawnConfiguration.getBoolean("Config.TeleportOnJoin");
        teleportOnFirstJoin = spawnConfiguration.getBoolean("Config.TeleportOnFirstJoin");

        if (!spawnConfiguration.isSet("Spawn")) return;

        var worldName = spawnConfiguration.getString("Spawn.World");
        if (worldName == null) return;

        var world = Bukkit.getWorld(worldName);
        if (world == null) return;

        var x = spawnConfiguration.getDouble("Spawn.X");
        var y = spawnConfiguration.getDouble("Spawn.Y");
        var z = spawnConfiguration.getDouble("Spawn.Z");
        var yaw = (float) spawnConfiguration.getDouble("Spawn.Yaw");
        var pitch = (float) spawnConfiguration.getDouble("Spawn.Pitch");

        spawnLocation = Optional.of(new Location(world, x, y, z, yaw, pitch));
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("spawn")) {
            handleSpawnCommand(commandSender, label, arguments);
            return;
        }

        handleSetSpawnCommand(commandSender, label, arguments);
    }

    protected void handleSpawnCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Spawn.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, arguments)) return;

        if (spawnLocation.isEmpty()) {
            sendCommandMessage(commandSender, "Spawn.NoSpawnSet", null, label, null);
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        if (!isSelf && !checkOtherPermission(commandSender, "Spawn.Other", targetPlayer.getName(), label)) return;

        targetPlayer.teleport(spawnLocation.get());

        var messagePath = isSelf? "Spawn.Success" : "Spawn.SuccessOther";

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, null);

        if (isSelf) return;
        sendCommandMessage(targetUser, "Spawn.Success", commandSender.getName().get(), label, null);
    }

    private void handleSetSpawnCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "SetSpawn.Use", label)) return;

        if (commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        var currentLocation = commandSender.getPlayer().getLocation();

        spawnConfiguration.set("Spawn.World", currentLocation.getWorld().getName());
        spawnConfiguration.set("Spawn.X", currentLocation.getX());
        spawnConfiguration.set("Spawn.Y", currentLocation.getY());
        spawnConfiguration.set("Spawn.Z", currentLocation.getZ());
        spawnConfiguration.set("Spawn.Yaw", currentLocation.getYaw());
        spawnConfiguration.set("Spawn.Pitch", currentLocation.getPitch());

        try {
            spawnConfiguration.save(_spawnFile);
        } catch (IOException exception) {
            exception.printStackTrace();
            sendGeneralMessage(commandSender, "ErrorOccurred", null, label, null);
            return;
        }

        spawnLocation = Optional.of(currentLocation);

        sendCommandMessage(commandSender, "SetSpawn.Success", null, label, null);
    }

    private void saveDefaultConfig() {
        if (_spawnFile.exists()) return;

        try {
            _spawnFile.createNewFile();
        } catch (IOException exception) {
            throw new RuntimeException("Error while trying to create 'spawn.yml' file", exception);
        }

        spawnConfiguration.set("Config.TeleportOnJoin", true);
        spawnConfiguration.set("Config.TeleportOnFirstJoin", true);

        try {
            spawnConfiguration.save(_spawnFile);
        } catch (IOException exception) {
            throw new RuntimeException("Error while trying to spawn 'spawn.yml'", exception);
        }
    }
}
