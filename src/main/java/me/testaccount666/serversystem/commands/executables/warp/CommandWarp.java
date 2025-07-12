package me.testaccount666.serversystem.commands.executables.warp;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "warp", variants = {"setwarp", "deletewarp"})
public class CommandWarp extends AbstractServerSystemCommand {

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = switch (command.getName().toLowerCase()) {
            case "setwarp" -> "SetWarp.Use";
            case "warp" -> "Warp.Use";
            case "deletewarp" -> "DeleteWarp.Use";
            default -> null;
        };

        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        switch (command.getName().toLowerCase()) {
            case "warp" -> handleWarpCommand(commandSender, arguments);
            case "setwarp" -> handleSetWarpCommand(commandSender, arguments);
            case "deletewarp" -> handleDeleteWarpCommand(commandSender, arguments);
        }
    }

    private void handleWarpCommand(User commandSender, String... arguments) {
        if (!checkBasePermission(commandSender, "Warp.Use")) return;
        var warpManager = ServerSystem.Instance.getWarpManager();

        var warpOptional = warpManager.getWarpByName(arguments[0]);
        if (warpOptional.isEmpty()) {
            command("Warp.WarpNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        var warp = warpOptional.get();
        var player = commandSender.getPlayer();

        playAnimation(player.getLocation());
        player.teleport(warp.getLocation());
        playAnimation(player.getLocation());

        command("Warp.Success", commandSender)
                .modifier(message -> message.replace("<WARP>", warp.getDisplayName())).build();
    }

    private void handleSetWarpCommand(User commandSender, String... arguments) {
        if (!checkBasePermission(commandSender, "SetWarp.Use")) return;

        var warpManager = ServerSystem.Instance.getWarpManager();
        var warpName = arguments[0];
        var warpLocation = commandSender.getPlayer().getLocation();

        var warpOptional = warpManager.getWarpByName(warpName);

        if (warpOptional.isPresent()) {
            command("SetWarp.WarpAlreadyExists", commandSender)
                    .modifier(message -> message.replace("<WARP>", arguments[0])).build();
            return;
        }

        var warp = warpManager.addWarp(warpName, warpLocation);
        command("SetWarp.Success", commandSender)
                .modifier(message -> message.replace("<WARP>", warp.getDisplayName())).build();
    }

    private void handleDeleteWarpCommand(User commandSender, String... arguments) {
        if (!checkBasePermission(commandSender, "DeleteWarp.Use")) return;

        var warpManager = ServerSystem.Instance.getWarpManager();
        var warpName = arguments[0];
        var warpOptional = warpManager.getWarpByName(warpName);

        if (warpOptional.isEmpty()) {
            command("DeleteWarp.WarpNotFound", commandSender)
                    .modifier(message -> message.replace("<WARP>", warpName)).build();
            return;
        }
        var warp = warpOptional.get();

        warpManager.removeWarp(warp);
        command("DeleteWarp.Success", commandSender)
                .modifier(message -> message.replace("<WARP>", warp.getDisplayName())).build();
    }

    /**
     * Plays a teleportation animation effect at the given location
     *
     * @param location The location to play the animation at
     */
    private void playAnimation(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        location.getWorld().spawnParticle(Particle.PORTAL, location, 100, 0.5, 0.5, 0.5, 0.05);
    }
}
