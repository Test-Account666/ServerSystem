package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class TeleportPositionCommand extends MessageUtils implements CommandExecutor {

    public TeleportPositionCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "tppos.self", true) && !this.isAllowed(cs, "tppos.others", true)) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tppos.self")));
            return true;
        }

        String potentialWorld = args[args.length - 1];

        World world = null;

        if (potentialWorld.toLowerCase(Locale.ROOT).startsWith("w:") || potentialWorld.toLowerCase(Locale.ROOT).startsWith("world:")) {
            String[] argsCopy = args;

            args = new String[argsCopy.length - 1];

            System.arraycopy(argsCopy, 0, args, 0, args.length);

            potentialWorld = potentialWorld.replaceAll("(world|w):", "");

            String finalPotentialWorld = potentialWorld;
            world = Bukkit.getWorlds().stream().filter(world1 -> world1.getName().equalsIgnoreCase(finalPotentialWorld)).findFirst().orElse(null);

            //TODO: Send error
            if (world == null)
                return true;
        }

        if (args.length <= 2) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("TPPos", label, cmd.getName(), cs, null));
            return true;
        }

        if (args.length == 3) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("TPPos", label, cmd.getName(), cs, null));
                return true;
            }

            if (!this.isAllowed(cs, "tppos.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tppos.self")));
                return true;
            }

            Location location = this.getLocationFromString(cs, label, cmd.getName(), ((Player) cs), world, args[0], args[1], args[2]);

            //TODO: Send error
            if (!location.getWorld().getWorldBorder().isInside(location))
                return true;

            Location playerLocation = ((Player) cs).getLocation();

            if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName()))
                if (location.distance(playerLocation) <= 0)
                    if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0)
                        if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0)
                            return true;

            Teleport.teleport((Player) cs, location);

            cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Self", label, cmd.getName(), cs, null).replace("<X>", String.format("%.2f", location.getX())).replace("<Y>", String.format("%.2f", location.getY())).replace("<Z>", String.format("%.2f", location.getZ())));
            return true;
        }

        if (args.length == 4) {
            if (!this.isAllowed(cs, "tppos.others")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tppos.others")));
                return true;
            }

            Player target = this.getPlayer(cs, args[0]);

            if (target == null) {
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
                return true;
            }

            Location location = this.getLocationFromString(cs, label, cmd.getName(), target, world, args[1], args[2], args[3]);

            //TODO: Send error
            if (!location.getWorld().getWorldBorder().isInside(location))
                return true;

            Location playerLocation = target.getLocation();

            if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName()))
                if (location.distance(playerLocation) <= 0)
                    if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0)
                        if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0)
                            return true;

            Teleport.teleport(target.getPlayer(), location);

            cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Others", label, cmd.getName(), cs, target).replace("<X>", String.format("%.2f", location.getX())).replace("<Y>", String.format("%.2f", location.getY())).replace("<Z>", String.format("%.2f", location.getZ())));
            return true;
        }

        if (args.length == 5) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("TPPos", label, cmd.getName(), cs, null));
                return true;
            }

            if (!this.isAllowed(cs, "tppos.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tppos.self")));
                return true;
            }

            Location location = this.getLocationFromString(cs, label, cmd.getName(), ((Player) cs), world, args[0], args[1], args[2], args[3], args[4]);


            //TODO: Send error
            if (!location.getWorld().getWorldBorder().isInside(location))
                return true;

            Location playerLocation = ((Player) cs).getLocation();

            if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName()))
                if (location.distance(playerLocation) <= 0)
                    if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0)
                        if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0)
                            return true;

            Teleport.teleport((Player) cs, location);

            cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Self", label, cmd.getName(), cs, null).replace("<X>", String.format("%.2f", location.getX())).replace("<Y>", String.format("%.2f", location.getY())).replace("<Z>", String.format("%.2f", location.getZ())));
            return true;
        }

        if (!this.isAllowed(cs, "tppos.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tppos.others")));
            return true;
        }

        Player target = this.getPlayer(cs, args[0]);

        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        Location location = this.getLocationFromString(cs, label, cmd.getName(), target, world, args[1], args[2], args[3], args[4], args[5]);

        //TODO: Send error
        if (!location.getWorld().getWorldBorder().isInside(location))
            return true;

        Location playerLocation = target.getLocation();

        if (location.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName()))
            if (location.distance(playerLocation) <= 0)
                if (Math.max(playerLocation.getYaw(), location.getYaw()) - Math.min(playerLocation.getYaw(), location.getYaw()) <= 0)
                    if (Math.max(playerLocation.getPitch(), location.getPitch()) - Math.min(playerLocation.getPitch(), location.getPitch()) <= 0)
                        return true;

        Teleport.teleport(target.getPlayer(), location);

        cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Others", label, cmd.getName(), cs, null).replace("<X>", String.format("%.2f", location.getX())).replace("<Y>", String.format("%.2f", location.getY())).replace("<Z>", String.format("%.2f", location.getZ())));
        return true;
    }


    private Location getLocationFromString(CommandSender cs, String label, String command, Player target, World world, String... locationData) {
        for (int i = 0; i < locationData.length; i++) {
            if (!locationData[i].startsWith("~"))
                continue;

            double add = 0;
            if (locationData[i].length() > 1) {
                String addition = locationData[i].split("~")[1];
                try {
                    add = Double.parseDouble(addition);
                } catch (NumberFormatException e) {
                    cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", addition));
                    return target.getLocation();
                }
            }

            switch (i) {
                case 0:
                    locationData[i] = String.valueOf((target.getLocation().getX() + add));
                    break;
                case 1:
                    locationData[i] = String.valueOf((target.getLocation().getY() + add));
                    break;
                case 2:
                    locationData[i] = String.valueOf((target.getLocation().getZ() + add));
                    break;
                case 3:
                    locationData[i] = String.valueOf((target.getLocation().getYaw() + add));
                    break;
                case 4:
                    locationData[i] = String.valueOf((target.getLocation().getPitch() + add));
                    break;
            }
        }

        if (locationData.length == 3) {
            Location location = target.getLocation();

            try {
                location.setX(Double.parseDouble(locationData[0]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[0]));
                return target.getLocation();
            }

            try {
                location.setY(Double.parseDouble(locationData[1]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[1]));
                return target.getLocation();
            }

            try {
                location.setZ(Double.parseDouble(locationData[2]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[2]));
                return target.getLocation();
            }

            if (world != null)
                location.setWorld(world);
            return location;
        }

        if (locationData.length == 5) {
            Location location = target.getLocation();

            try {
                location.setX(Double.parseDouble(locationData[0]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[0]));
                return target.getLocation();
            }

            try {
                location.setY(Double.parseDouble(locationData[1]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[1]));
                return target.getLocation();
            }

            try {
                location.setZ(Double.parseDouble(locationData[2]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[2]));
                return target.getLocation();
            }


            try {
                location.setYaw(Float.parseFloat(locationData[3]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[5]));
                return target.getLocation();
            }

            try {
                location.setPitch(Float.parseFloat(locationData[4]));
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.NotANumber", label, command, cs, target).replace("<NUMBER>", locationData[4]));
                return target.getLocation();
            }

            if (world != null)
                location.setWorld(world);
            return location;
        }
        return null;
    }
}
