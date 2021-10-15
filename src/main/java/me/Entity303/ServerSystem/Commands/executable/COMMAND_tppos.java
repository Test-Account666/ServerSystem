package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_tppos extends MessageUtils implements CommandExecutor {

    public COMMAND_tppos(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "tppos.self", true) && !this.isAllowed(cs, "tppos.others", true)) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tppos.self")));
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

            Location location = this.getLocationFromString(cs, label, cmd.getName(), ((Player) cs), args[0], args[1], args[2]);

            if (location == ((Player) cs).getLocation()) return true;


            ((Player) cs).teleport(location);
            cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Self", label, cmd.getName(), cs, null).replace("<X>", String.valueOf(location.getX())).replace("<Y>", String.valueOf(location.getY())).replace("<Z>", String.valueOf(location.getZ())));
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

            Location location = this.getLocationFromString(cs, label, cmd.getName(), target, args[1], args[2], args[3]);

            if (location == target.getLocation()) return true;

            target.getPlayer().teleport(location);
            cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Others", label, cmd.getName(), cs, target).replace("<X>", String.valueOf(location.getX())).replace("<Y>", String.valueOf(location.getY())).replace("<Z>", String.valueOf(location.getZ())));
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

            Location location = this.getLocationFromString(cs, label, cmd.getName(), ((Player) cs), args[0], args[1], args[2], args[3], args[4]);

            if (location == ((Player) cs).getLocation()) return true;

            ((Player) cs).teleport(location);
            cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Self", label, cmd.getName(), cs, null).replace("<X>", String.valueOf(location.getX())).replace("<Y>", String.valueOf(location.getY())).replace("<Z>", String.valueOf(location.getZ())));
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

        Location location = this.getLocationFromString(cs, label, cmd.getName(), target, args[1], args[2], args[3], args[4], args[5]);

        if (location == target.getLocation()) return true;

        target.getPlayer().teleport(location);
        cs.sendMessage(this.getPrefix() + this.getMessage("TPPos.Success.Others", label, cmd.getName(), cs, null).replace("<X>", String.valueOf(location.getX())).replace("<Y>", String.valueOf(location.getY())).replace("<Z>", String.valueOf(location.getZ())));
        return true;
    }


    private Location getLocationFromString(CommandSender cs, String label, String command, Player target, String... locationData) {
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
            return location;
        }
        return null;
    }
}
