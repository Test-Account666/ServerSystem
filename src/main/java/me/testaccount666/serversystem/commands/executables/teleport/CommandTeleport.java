package me.testaccount666.serversystem.commands.executables.teleport;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.function.Consumer;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "teleport", variants = {"teleportposition", "teleporthere", "teleportall"})
public class CommandTeleport extends AbstractServerSystemCommand {
    private static final Vector _X_AXIS = new Vector(1, 0, 0);
    private static final Vector _Y_AXIS = new Vector(0, 1, 0);
    private static final Vector _Z_AXIS = new Vector(0, 0, 1);


    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        var commandName = command.getName().toLowerCase();
        switch (commandName) {
            case "teleportposition" -> executeTeleportPosition(commandSender, command, label, arguments);
            case "teleporthere" -> executeTeleportHere(commandSender, command, label, arguments);
            case "teleportall" -> executeTeleportAll(commandSender);
            default -> {
                if (arguments.length == 2) executeTeleportOther(commandSender, command, label, arguments);
                else if (arguments.length > 2) executeTeleportPosition(commandSender, command, label, arguments);
                else executeTeleport(commandSender, arguments);
            }
        }
    }

    private void executeTeleportAll(User commandSender) {
        if (!validateSenderAndPermission(commandSender, "TeleportAll.Use")) return;

        var senderLocation = commandSender.getPlayer().getLocation();
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(senderLocation));
        command("TeleportAll.Success", commandSender).target("*").build();
    }

    private void executeTeleportHere(User commandSender, Command command, String label, String[] arguments) {
        if (!validateSenderAndPermission(commandSender, "TeleportHere.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        getTargetUserAndTeleport(commandSender, arguments,
                targetPlayer -> targetPlayer.teleport(commandSender.getPlayer().getLocation()),
                "TeleportHere.Success");
    }

    private void executeTeleport(User commandSender, String[] arguments) {
        if (!validateSenderAndPermission(commandSender, "Teleport.Use")) return;

        getTargetUserAndTeleport(commandSender, arguments,
                targetPlayer -> commandSender.getPlayer().teleport(targetPlayer.getLocation()),
                "Teleport.Success");
    }

    private void executeTeleportOther(User commandSender, Command command, String label, String[] arguments) {
        if (!validatePermissions(commandSender, "Teleport.Use", "Teleport.Other")) return;
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 1, arguments)) return;

        var sourceUserOpt = getTargetUser(commandSender, arguments);
        var targetUserOpt = getTargetUser(commandSender, 1, false, arguments);

        if (sourceUserOpt.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        if (targetUserOpt.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[1]).build();
            return;
        }

        var sourcePlayer = sourceUserOpt.get().getPlayer();
        var targetPlayer = targetUserOpt.get().getPlayer();

        sourcePlayer.teleport(targetPlayer.getLocation());
        command("Teleport.SuccessOther", commandSender)
                .target(sourcePlayer.getName())
                .postModifier(message -> message.replace("<TARGET2>", targetPlayer.getName()))
                .build();
    }

    private void executeTeleportPosition(User commandSender, Command command, String label, String[] arguments) {
        if (!validatePermissions(commandSender, "TeleportPosition.Use")) return;
        if (arguments.length < 3) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var isSelf = true;
        var startIndex = 0;

        if (arguments.length > 3) {
            var potentialTargetUser = getTargetUser(commandSender, arguments);
            if (potentialTargetUser.isPresent()) {
                isSelf = false;
                startIndex = 1;
            }
        }

        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, 3, arguments)) return;

        var targetUserOpt = isSelf? Optional.of(commandSender) : getTargetUser(commandSender, arguments);
        if (targetUserOpt.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOpt.get();
        var targetPlayer = targetUser.getPlayer();

        if (!isSelf && !checkOtherPermission(commandSender, "TeleportPosition.Other", targetPlayer.getName())) return;

        var executionLocation = commandSender instanceof ConsoleUser? targetPlayer.getLocation() : commandSender.getPlayer().getLocation();

        var locationOpt = extractLocationWithRotation(
                executionLocation,
                targetPlayer,
                arguments,
                startIndex
        );

        if (locationOpt.isEmpty()) {
            command("TeleportPosition.InvalidLocation", commandSender).target(targetPlayer.getName()).build();
            return;
        }

        var location = locationOpt.get();

        if (!isValidTeleportLocation(location, commandSender, targetPlayer)) return;

        targetPlayer.teleport(location);
        sendTeleportPositionSuccess(commandSender, targetPlayer, location, isSelf);
    }

    private boolean isValidTeleportLocation(Location location, User commandSender, Player targetPlayer) {
        if (!location.getWorld().getWorldBorder().isInside(location)) {
            command("TeleportPosition.OutsideBorder", commandSender).target(targetPlayer.getName()).build();
            return false;
        }

        return location.getWorld() == targetPlayer.getWorld() ||
                checkBasePermission(commandSender, "TeleportPosition.World");
    }

    private void sendTeleportPositionSuccess(User commandSender, Player targetPlayer, Location location, boolean isSelf) {
        var messagePath = isSelf? "TeleportPosition.Success" : "TeleportPosition.SuccessOther";
        command(messagePath, commandSender)
                .target(targetPlayer.getName())
                .postModifier(message -> formatLocationMessage(message, location))
                .build();
    }

    private String formatLocationMessage(String message, Location location) {
        return message.replace("<X>", roundDecimal(location.getX()))
                .replace("<Y>", roundDecimal(location.getY()))
                .replace("<Z>", roundDecimal(location.getZ()))
                .replace("<WORLD>", location.getWorld().getName());
    }

    private boolean validateSenderAndPermission(User commandSender, String permission) {
        if (!checkBasePermission(commandSender, permission)) return false;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return false;
        }
        return true;
    }

    private boolean validatePermissions(User commandSender, String... permissions) {
        for (var permission : permissions) if (!checkBasePermission(commandSender, permission)) return false;
        return true;
    }

    private void getTargetUserAndTeleport(User commandSender, String[] arguments, Consumer<Player> teleportAction, String successMessage) {
        var targetUserOptional = getTargetUser(commandSender, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetPlayer = targetUserOptional.get().getPlayer();
        teleportAction.accept(targetPlayer);
        command(successMessage, commandSender).target(targetPlayer.getName()).build();
    }


    private String roundDecimal(double location) {
        var format = new DecimalFormat("0.##");

        return format.format(location).replace(",", ".");
    }

    public Optional<Location> extractLocationWithRotation(Location executeLocation, Entity target, String[] arguments, int startIndex) {
        var coordinateEndIndex = startIndex + 3;

        if (arguments.length < coordinateEndIndex) return Optional.empty();

        var x = calculateRelativePosition(_X_AXIS, arguments[startIndex], executeLocation, target.getLocation());
        var y = calculateRelativePosition(_Y_AXIS, arguments[startIndex + 1], executeLocation, target.getLocation());
        var z = calculateRelativePosition(_Z_AXIS, arguments[startIndex + 2], executeLocation, target.getLocation());

        if (x.isEmpty() || y.isEmpty() || z.isEmpty()) return Optional.empty();

        var yaw = target.getLocation().getYaw();
        var pitch = target.getLocation().getPitch();

        var currentIndex = coordinateEndIndex;

        if (arguments.length > currentIndex) {
            var yawOpt = calculateRelativeRotation(arguments[currentIndex], executeLocation, target.getLocation(), true);
            if (yawOpt.isPresent()) {
                yaw = yawOpt.get();
                currentIndex++;
            }
            // If parsing fails, it might be a world name, so we skip yaw parsing
        }

        if (arguments.length > currentIndex) {
            var pitchOpt = calculateRelativeRotation(arguments[currentIndex], executeLocation, target.getLocation(), false);
            if (pitchOpt.isPresent()) {
                pitch = pitchOpt.get();
                currentIndex++;
            }
            // If parsing fails, it might be a world name, so we skip pitch parsing
        }

        var world = target.getWorld();
        if (arguments.length > currentIndex) {
            var worldName = arguments[currentIndex];
            var targetWorld = target.getServer().getWorld(worldName);
            if (targetWorld != null) world = targetWorld;
        }

        return Optional.of(new Location(world, x.get(), y.get(), z.get(), yaw, pitch));
    }

    private Optional<Double> calculateRelativePosition(Vector axis, String input, Location senderLocation, Location targetLocation) {
        try {
            if (input.equals("~")) return Optional.of(getCoordinate(targetLocation, axis));
            if (input.equals("@")) return Optional.of(getCoordinate(senderLocation, axis));

            if (input.startsWith("~")) return Optional.of(parseOffset(input, targetLocation, axis, 1));
            if (input.startsWith("@")) return Optional.of(parseOffset(input, senderLocation, axis, 1));

            return Optional.of(Double.parseDouble(input));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    private Optional<Float> calculateRelativeRotation(String input, Location senderLocation, Location targetLocation, boolean isYaw) {
        try {
            if (input.equals("~")) return Optional.of(isYaw? targetLocation.getYaw() : targetLocation.getPitch());
            if (input.equals("@")) return Optional.of(isYaw? senderLocation.getYaw() : senderLocation.getPitch());

            if (input.startsWith("~")) return Optional.of(parseRotationOffset(input, targetLocation, isYaw, 1));
            if (input.startsWith("@")) return Optional.of(parseRotationOffset(input, senderLocation, isYaw, 1));

            return Optional.of(Float.parseFloat(input));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    private float parseRotationOffset(String input, Location location, boolean isYaw, int offsetStartIndex) {
        var offset = Float.parseFloat(input.substring(offsetStartIndex));
        var currentValue = isYaw? location.getYaw() : location.getPitch();
        return currentValue + offset;
    }

    private double parseOffset(String input, Location location, Vector axis, int offsetStartIndex) {
        var offset = Double.parseDouble(input.substring(offsetStartIndex));
        return getCoordinate(location, axis) + offset;
    }

    private double getCoordinate(Location location, Vector axis) {
        return axis.getX() * location.getX()
                + axis.getY() * location.getY()
                + axis.getZ() * location.getZ();
    }

    @Override
    public String getSyntaxPath(Command command) {
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "teleportposition" -> "TeleportPosition";
            case "teleporthere" -> "TeleportHere";
            case "teleportall" -> "TeleportAll";
            default -> "Teleport";
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var commandName = command.getName().toLowerCase();
        var permission = switch (commandName) {
            case "teleportposition" -> "TeleportPosition.Use";
            case "teleporthere" -> "TeleportHere.Use";
            case "teleportall" -> "TeleportAll.Use";
            default -> "Teleport.Use";
        };
        return PermissionManager.hasCommandPermission(player, permission, false);
    }

}