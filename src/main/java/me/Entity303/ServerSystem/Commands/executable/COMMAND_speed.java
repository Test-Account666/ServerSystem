package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_speed extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_speed(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "speed.general")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("speed.general")));
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Speed", label, cmd.getName(), cs, null));
            return true;
        }
        float speed = -1F;
        boolean isFly = false;
        Player player = null;

        if (args.length == 1) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Speed", label, cmd.getName(), cs, null));
                return true;
            }
            player = (Player) cs;
            isFly = ((Player) cs).isFlying();
            try {
                speed = this.getRealMoveSpeed(this.getMoveSpeed(Float.parseFloat(args[0])), isFly);
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Speed.NotANumber", label, cmd.getName(), cs, null));
                return true;
            }
        }

        if (args.length == 2) {
            Player target = this.getPlayer(cs, args[1]);
            if (target == null)
                if ("walk".equalsIgnoreCase(args[1]) || "laufen".equalsIgnoreCase(args[1]) || "walking".equalsIgnoreCase(args[1]) || "lauf".equalsIgnoreCase(args[1]) || "run".equalsIgnoreCase(args[1]) || "running".equalsIgnoreCase(args[1]) || "gehen".equalsIgnoreCase(args[1])) {
                    if (!(cs instanceof Player)) {
                        cs.sendMessage(this.getPrefix() + this.getSyntax("Speed", label, cmd.getName(), cs, null));
                        return true;
                    }
                    isFly = false;
                    player = (Player) cs;
                } else if ("fly".equalsIgnoreCase(args[1]) || "flying".equalsIgnoreCase(args[1]) || "flight".equalsIgnoreCase(args[1]) || "flug".equalsIgnoreCase(args[1]) || "fliegen".equalsIgnoreCase(args[1])) {
                    if (!(cs instanceof Player)) {
                        cs.sendMessage(this.getPrefix() + this.getSyntax("Speed", label, cmd.getName(), cs, null));
                        return true;
                    }
                    isFly = true;
                    player = (Player) cs;
                } else {
                    cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
                    return true;
                }
            else player = target;
            try {
                speed = this.getRealMoveSpeed(this.getMoveSpeed(Float.parseFloat(args[0])), isFly);
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Speed.NotANumber", label, cmd.getName(), cs, null));
                return true;
            }
        }

        if (args.length >= 3) {
            Player target = this.getPlayer(cs, args[1]);
            if (target == null) {
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
                return true;
            } else player = target;
            if ("walk".equalsIgnoreCase(args[2]) || "laufen".equalsIgnoreCase(args[2]) || "walking".equalsIgnoreCase(args[2]) || "lauf".equalsIgnoreCase(args[2]) || "run".equalsIgnoreCase(args[2]) || "running".equalsIgnoreCase(args[2]) || "gehen".equalsIgnoreCase(args[2]))
                isFly = false;
            else if ("fly".equalsIgnoreCase(args[2]) || "flying".equalsIgnoreCase(args[2]) || "flight".equalsIgnoreCase(args[2]) || "flug".equalsIgnoreCase(args[2]) || "fliegen".equalsIgnoreCase(args[2]))
                isFly = true;
            else {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Speed", label, cmd.getName(), cs, null));
                return true;
            }
            try {
                speed = this.getRealMoveSpeed(this.getMoveSpeed(Float.parseFloat(args[0])), isFly);
            } catch (NumberFormatException ignored) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Speed.NotANumber", label, cmd.getName(), cs, null));
                return true;
            }
        }

        if (speed == -1F) {
            cs.sendMessage(ChatColor.DARK_RED + "Error!");
            return true;
        }

        if (player != cs) {
            if (!this.isAllowed(cs, "speed.others")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("speed.others")));
                return true;
            }
        } else if (!this.isAllowed(cs, "speed.self")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("speed.self")));
            return true;
        }

        this.setMovementSpeed(player, speed, isFly, cs, args[0], cmd.getName(), label);
        return true;
    }

    private void setMovementSpeed(Player player,
                                  Float speed,
                                  boolean isFly,
                                  CommandSender cs,
                                  String sped,
                                  String cmd,
                                  String label) {
        if (isFly) player.setFlySpeed(speed);
        else player.setWalkSpeed(speed);
        if (cs == player) if (isFly)
            cs.sendMessage(this.getPrefix() + this.getMessage("Speed.Fly.Self", label, cmd, cs, player).replace("<SPEED>", sped));
        else
            cs.sendMessage(this.getPrefix() + this.getMessage("Speed.Walk.Self", label, cmd, cs, player).replace("<SPEED>", sped));
        else if (isFly) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Speed.Fly.Others.Sender", label, cmd, cs, player).replace("<SPEED>", sped));
            player.sendMessage(this.getPrefix() + this.getMessage("Speed.Fly.Others.Target", label, cmd, cs, player).replace("<SPEED>", sped));
        } else {
            cs.sendMessage(this.getPrefix() + this.getMessage("Speed.Walk.Others.Sender", label, cmd, cs, player).replace("<SPEED>", sped));
            player.sendMessage(this.getPrefix() + this.getMessage("Speed.Walk.Others.Target", label, cmd, cs, player).replace("<SPEED>", sped));
        }
    }

    private float getMoveSpeed(float userSpeed) {
        if (userSpeed > 10f) userSpeed = 10f;
        else if (userSpeed < 0.0001f) userSpeed = 0.0001f;
        return userSpeed;
    }

    private float getRealMoveSpeed(float userSpeed, boolean isFly) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;

        if (userSpeed < 1f) return defaultSpeed * userSpeed;
        else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
