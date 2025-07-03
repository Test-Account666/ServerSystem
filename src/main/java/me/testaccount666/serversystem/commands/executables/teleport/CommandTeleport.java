package me.testaccount666.serversystem.commands.executables.teleport;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractPlayerTargetingCommand;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Optional;

@ServerSystemCommand(name = "teleport", variants = "teleportposition")
public class CommandTeleport extends AbstractPlayerTargetingCommand {
    private static final Vector _X_AXIS = new Vector(1, 0, 0);
    private static final Vector _Y_AXIS = new Vector(0, 1, 0);
    private static final Vector _Z_AXIS = new Vector(0, 0, 1);


    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("teleportposition") || arguments.length > 2) {
            executeTeleportPosition(commandSender, label, arguments);
            return;
        }

        executeTeleport(commandSender, label, arguments);
    }

    private void executeTeleport(User commandSender, String label, String[] arguments) {
        if (!checkBasePermission(commandSender, "Teleport.Use", label)) return;
        if (handleConsoleWithNoTarget(commandSender, label, 1, arguments)) return;

        var targetPlayerOptional = getTargetPlayer(commandSender, false, arguments);

        if (targetPlayerOptional.isEmpty() && arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        if (targetPlayerOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetPlayer = targetPlayerOptional.get();

        @SuppressWarnings("LocalVariableNamingConvention")
        var targetPlayer2Optional = getTargetPlayer(commandSender, 1, arguments);
        if (targetPlayer2Optional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[1]);
            return;
        }

        var targetPlayer2 = targetPlayer2Optional.get();

        var targetUser2Optional = validateAndGetUser(commandSender, targetPlayer2, label, "Teleport");
        if (targetUser2Optional.isEmpty()) return;

        var targetUser2 = targetUser2Optional.get();

        var isSelf = commandSender == targetUser2;

        targetPlayer.teleport(targetPlayer2.getLocation());

        var messagePath = isSelf? "Teleport.Success" : "Teleport.SuccessOther";
        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label,
                message -> message.replace("<TARGET2>", targetPlayer2.getName()));
    }

    private void executeTeleportPosition(User commandSender, String label, String[] arguments) {
        if (!checkBasePermission(commandSender, "TeleportPosition.Use", label)) return;

        if (arguments.length < 3) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }
        if (handleConsoleWithNoTarget(commandSender, label, 3, arguments)) return;

        var targetPlayerOptional = arguments.length == 3? Optional.of(commandSender.getPlayer()) : getTargetPlayer(commandSender, arguments);

        if (targetPlayerOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetPlayer = targetPlayerOptional.get();

        var targetUserOptional = validateAndGetUser(commandSender, targetPlayer, label, "TeleportPosition");
        if (targetUserOptional.isEmpty()) return;

        var targetUser = targetUserOptional.get();

        var isSelf = commandSender == targetUser;

        if (!isSelf && !checkOtherPermission(commandSender, "TeleportPosition.Other", targetPlayer.getName(), label)) return;

        var executionLocation = commandSender instanceof ConsoleUser? targetPlayer.getLocation() : commandSender.getPlayer().getLocation();

        var locationOptional = extractLocation(executionLocation, targetPlayer, arguments, isSelf && arguments.length == 3? 0 : 1);

        if (locationOptional.isEmpty()) {
            sendCommandMessage(commandSender, "TeleportPosition.InvalidLocation", targetPlayer.getName(), label, null);
            return;
        }

        var location = locationOptional.get();
        location.setYaw(targetPlayer.getLocation().getYaw());
        location.setPitch(targetPlayer.getLocation().getPitch());

        var worldBorder = location.getWorld().getWorldBorder();

        if (!worldBorder.isInside(location)) {
            sendCommandMessage(commandSender, "TeleportPosition.OutsideBorder", targetPlayer.getName(), label, null);
            return;
        }

        if (location.getWorld() != targetPlayer.getWorld() && !checkBasePermission(commandSender, "TeleportPosition.World", label)) return;

        targetPlayer.teleport(location);

        var messagePath = isSelf? "TeleportPosition.Success" : "TeleportPosition.SuccessOther";

        sendCommandMessage(commandSender, messagePath, targetPlayer.getName(), label, message ->
                message.replace("<X>", roundDecimal(location.getX()))
                        .replace("<Y>", roundDecimal(location.getY()))
                        .replace("<Z>", roundDecimal(location.getZ()))
                        .replace("<WORLD>", location.getWorld().getName()));
    }

    private String roundDecimal(double location) {
        var format = new DecimalFormat("0.##");

        return format.format(location).replace(",", ".");
    }

    public Optional<Location> extractLocation(Location executeLocation, Entity target, String[] arguments, int startIndex) {
        var endIndex = startIndex + 3;

        if (arguments.length < endIndex) return Optional.empty();

        var world = target.getWorld();

        if (arguments.length > endIndex) world = target.getServer().getWorld(arguments[endIndex]);
        if (world == null) return Optional.empty();

        var x = calculateRelativePosition(_X_AXIS, arguments[startIndex], executeLocation, target.getLocation());
        var y = calculateRelativePosition(_Y_AXIS, arguments[startIndex + 1], executeLocation, target.getLocation());
        var z = calculateRelativePosition(_Z_AXIS, arguments[startIndex + 2], executeLocation, target.getLocation());

        if (x.isEmpty() || y.isEmpty() || z.isEmpty()) return Optional.empty();

        return Optional.of(new Location(world, x.get(), y.get(), z.get()));
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

    private double parseOffset(String input, Location location, Vector axis, int offsetStartIndex) {
        var offset = Double.parseDouble(input.substring(offsetStartIndex));
        return getCoordinate(location, axis) + offset;
    }

    private double getCoordinate(Location location, Vector axis) {
        return axis.getX() * location.getX()
                + axis.getY() * location.getY()
                + axis.getZ() * location.getZ();
    }

}