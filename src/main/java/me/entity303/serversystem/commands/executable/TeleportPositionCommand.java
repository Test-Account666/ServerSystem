package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.regex.Pattern;

@ServerSystemCommand(name = "TeleportPosition")
public class TeleportPositionCommand implements ICommandExecutorOverload {

    private static final Pattern WORLD_PATTERN = Pattern.compile("(world|w):");
    protected final ServerSystem _plugin;

    public TeleportPositionCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "tppos.self", true)) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tppos.others", true)) {
                var permission = this._plugin.GetPermissions().GetPermission("tppos.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
        }

        if (arguments.length == 0) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "TPPos"));
            return true;
        }

        var potentialWorld = arguments[arguments.length - 1];

        World world = null;

        if (potentialWorld.toLowerCase(Locale.ROOT).startsWith("w:") || potentialWorld.toLowerCase(Locale.ROOT).startsWith("world:")) {
            var argsCopy = arguments;

            arguments = new String[argsCopy.length - 1];

            System.arraycopy(argsCopy, 0, arguments, 0, arguments.length);

            potentialWorld = WORLD_PATTERN.matcher(potentialWorld).replaceAll("");

            var finalPotentialWorld = potentialWorld;
            world = Bukkit.getWorlds().stream().filter(world1 -> world1.getName().equalsIgnoreCase(finalPotentialWorld)).findFirst().orElse(null);

            //TODO: Send error
            if (world == null) return true;
        }

        if (arguments.length <= 2) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "TPPos"));
            return true;
        }

        if (arguments.length == 3) {
            if (!(commandSender instanceof Player)) {

                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "TPPos"));
                return true;
            }

            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tppos.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("tppos.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            var location =
                    this.GetLocationFromString(commandSender, commandLabel, command.getName(), ((Player) commandSender), world, arguments[0], arguments[1], arguments[2]);

            //TODO: Send error
            if (!location.getWorld().getWorldBorder().isInside(location)) return true;

            var playerLocation = ((Player) commandSender).getLocation();

            if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName())) {
                if (location.distance(playerLocation) <= 0) {
                    if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0) {
                        if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0) return true;
                    }
                }
            }

            ((Player) commandSender).teleport(location);


            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null, "TPPos.Success.Self")
                                                                                           .replace("<X>", String.format("%.2f", location.getX()))
                                                                                           .replace("<Y>", String.format("%.2f", location.getY()))
                                                                                           .replace("<Z>", String.format("%.2f", location.getZ())));
            return true;
        }

        if (arguments.length == 4) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tppos.others")) {
                var permission = this._plugin.GetPermissions().GetPermission("tppos.others");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);

            if (target == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
                return true;
            }

            var location = this.GetLocationFromString(commandSender, commandLabel, command.getName(), target, world, arguments[1], arguments[2], arguments[3]);

            //TODO: Send error
            if (!location.getWorld().getWorldBorder().isInside(location)) return true;

            var playerLocation = target.getLocation();

            if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName())) {
                if (location.distance(playerLocation) <= 0) {
                    if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0) {
                        if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0) return true;
                    }
                }
            }

            var player = target.getPlayer();
            player.teleport(location);


            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, target,
                                                                                                       "TPPos.Success.Others")
                                                                                           .replace("<X>", String.format("%.2f", location.getX()))
                                                                                           .replace("<Y>", String.format("%.2f", location.getY()))
                                                                                           .replace("<Z>", String.format("%.2f", location.getZ())));
            return true;
        }

        if (arguments.length == 5) {
            if (!(commandSender instanceof Player)) {

                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "TPPos"));
                return true;
            }

            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tppos.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("tppos.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            var location =
                    this.GetLocationFromString(commandSender, commandLabel, command.getName(), ((Player) commandSender), world, arguments[0], arguments[1], arguments[2],
                                               arguments[3], arguments[4]);


            //TODO: Send error
            if (!location.getWorld().getWorldBorder().isInside(location)) return true;

            var playerLocation = ((Player) commandSender).getLocation();

            if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName())) {
                if (location.distance(playerLocation) <= 0) {
                    if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0) {
                        if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0) return true;
                    }
                }
            }

            ((Player) commandSender).teleport(location);


            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null, "TPPos.Success.Self")
                                                                                           .replace("<X>", String.format("%.2f", location.getX()))
                                                                                           .replace("<Y>", String.format("%.2f", location.getY()))
                                                                                           .replace("<Z>", String.format("%.2f", location.getZ())));
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "tppos.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("tppos.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);

        if (target == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        var location = this.GetLocationFromString(commandSender, commandLabel, command.getName(), target, world, arguments[1], arguments[2], arguments[3], arguments[4],
                                                  arguments[5]);

        //TODO: Send error
        if (!location.getWorld().getWorldBorder().isInside(location)) return true;

        var playerLocation = target.getLocation();

        if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName())) {
            if (location.distance(playerLocation) <= 0) {
                if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0) {
                    if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0) return true;
                }
            }
        }

        var player = target.getPlayer();
        player.teleport(location);


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessage(commandLabel, command, commandSender, null, "TPPos.Success.Others")
                                                                                       .replace("<X>", String.format("%.2f", location.getX()))
                                                                                       .replace("<Y>", String.format("%.2f", location.getY()))
                                                                                       .replace("<Z>", String.format("%.2f", location.getZ())));
        return true;
    }


    private Location GetLocationFromString(CommandSender commandSender, String commandLabel, String command, Player target, World world, String... locationData) {
        for (var index = 0; index < locationData.length; index++) {
            if (!locationData[index].startsWith("~")) continue;

            double add = 0;
            if (locationData[index].length() > 1) {
                var addition = locationData[index].split("~")[1];
                try {
                    add = Double.parseDouble(addition);
                } catch (NumberFormatException exception) {
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                   .GetMessage(commandLabel, command, commandSender, target,
                                                                                                               "TPPos.NotANumber")
                                                                                                   .replace("<NUMBER>", addition));
                    return target.getLocation();
                }
            }

            switch (index) {
                case 0:
                    locationData[index] = String.valueOf((target.getLocation().getX() + add));
                    break;
                case 1:
                    locationData[index] = String.valueOf((target.getLocation().getY() + add));
                    break;
                case 2:
                    locationData[index] = String.valueOf((target.getLocation().getZ() + add));
                    break;
                case 3:
                    locationData[index] = String.valueOf((target.getLocation().getYaw() + add));
                    break;
                case 4:
                    locationData[index] = String.valueOf((target.getLocation().getPitch() + add));
                    break;
            }
        }

        if (locationData.length == 3) {
            var location = target.getLocation();

            try {
                location.setX(Double.parseDouble(locationData[0]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[0]));
                return target.getLocation();
            }

            try {
                location.setY(Double.parseDouble(locationData[1]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[1]));
                return target.getLocation();
            }

            try {
                location.setZ(Double.parseDouble(locationData[2]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[2]));
                return target.getLocation();
            }

            if (world != null) location.setWorld(world);
            return location;
        }

        if (locationData.length == 5) {
            var location = target.getLocation();

            try {
                location.setX(Double.parseDouble(locationData[0]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[0]));
                return target.getLocation();
            }

            try {
                location.setY(Double.parseDouble(locationData[1]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[1]));
                return target.getLocation();
            }

            try {
                location.setZ(Double.parseDouble(locationData[2]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[2]));
                return target.getLocation();
            }


            try {
                location.setYaw(Float.parseFloat(locationData[3]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[5]));
                return target.getLocation();
            }

            try {
                location.setPitch(Float.parseFloat(locationData[4]));
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, target,
                                                                                                           "TPPos.NotANumber")
                                                                                               .replace("<NUMBER>", locationData[4]));
                return target.getLocation();
            }

            if (world != null) location.setWorld(world);
            return location;
        }
        return null;
    }
}
