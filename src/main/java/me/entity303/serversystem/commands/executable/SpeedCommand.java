package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends CommandUtils implements ICommandExecutorOverload {

    public SpeedCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "speed.general")) {
            var permission = this._plugin.GetPermissions().GetPermission("speed.general");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (arguments.length == 0) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Speed"));
            return true;
        }
        var speed = -1F;
        var isFly = false;
        Player player = null;

        if (arguments.length == 1) {
            if (!(commandSender instanceof Player)) {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Speed"));
                return true;
            }
            player = (Player) commandSender;
            isFly = ((Player) commandSender).isFlying();
            try {
                speed = this.GetRealMoveSpeed(this.GetMoveSpeed(Float.parseFloat(arguments[0])), isFly);
            } catch (NumberFormatException ignored) {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Speed.NotANumber"));
                return true;
            }
        }

        if (arguments.length == 2) {
            var target = this.GetPlayer(commandSender, arguments[1]);
            if (target == null)
                if ("walk".equalsIgnoreCase(arguments[1]) || "laufen".equalsIgnoreCase(arguments[1]) || "walking".equalsIgnoreCase(arguments[1]) ||
                    "lauf".equalsIgnoreCase(arguments[1]) || "run".equalsIgnoreCase(arguments[1]) || "running".equalsIgnoreCase(arguments[1]) ||
                    "gehen".equalsIgnoreCase(arguments[1])) {
                    if (!(commandSender instanceof Player)) {
                        
                        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command,
                                                                                                                              commandSender, null, "Speed"));
                        return true;
                    }
                    isFly = false;
                    player = (Player) commandSender;
                } else if ("fly".equalsIgnoreCase(arguments[1]) || "flying".equalsIgnoreCase(arguments[1]) || "flight".equalsIgnoreCase(arguments[1]) ||
                           "flug".equalsIgnoreCase(arguments[1]) || "fliegen".equalsIgnoreCase(arguments[1])) {
                    if (!(commandSender instanceof Player)) {
                        
                        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command,
                                                                                                                              commandSender, null, "Speed"));
                        return true;
                    }
                    isFly = true;
                    player = (Player) commandSender;
                } else {
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[1]));
                    return true;
                }
            else
                player = target;
            try {
                speed = this.GetRealMoveSpeed(this.GetMoveSpeed(Float.parseFloat(arguments[0])), isFly);
            } catch (NumberFormatException ignored) {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Speed.NotANumber"));
                return true;
            }
        }

        if (arguments.length >= 3) {
            var target = this.GetPlayer(commandSender, arguments[1]);
            if (target == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[1]));
                return true;
            } else
                player = target;
            if ("walk".equalsIgnoreCase(arguments[2]) || "laufen".equalsIgnoreCase(arguments[2]) || "walking".equalsIgnoreCase(arguments[2]) ||
                "lauf".equalsIgnoreCase(arguments[2]) || "run".equalsIgnoreCase(arguments[2]) || "running".equalsIgnoreCase(arguments[2]) || "gehen".equalsIgnoreCase(
                    arguments[2]))
                isFly = false;
            else if ("fly".equalsIgnoreCase(arguments[2]) || "flying".equalsIgnoreCase(arguments[2]) || "flight".equalsIgnoreCase(arguments[2]) ||
                     "flug".equalsIgnoreCase(arguments[2]) || "fliegen".equalsIgnoreCase(arguments[2]))
                isFly = true;
            else {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Speed"));
                return true;
            }
            try {
                speed = this.GetRealMoveSpeed(this.GetMoveSpeed(Float.parseFloat(arguments[0])), isFly);
            } catch (NumberFormatException ignored) {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Speed.NotANumber"));
                return true;
            }
        }

        if (speed == -1F) {
            commandSender.sendMessage(ChatColor.DARK_RED + "Error!");
            return true;
        }

        if (player != commandSender) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "speed.others")) {
                var permission = this._plugin.GetPermissions().GetPermission("speed.others");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
        } else if (!this._plugin.GetPermissions().HasPermission(commandSender, "speed.self")) {
            var permission = this._plugin.GetPermissions().GetPermission("speed.self");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        this.SetMovementSpeed(player, speed, isFly, commandSender, arguments[0], command.getName(), commandLabel);
        return true;
    }

    private float GetRealMoveSpeed(float userSpeed, boolean isFly) {
        final var defaultSpeed = isFly? 0.1f : 0.2f;
        var maxSpeed = 1f;

        if (userSpeed < 1f)
            return defaultSpeed * userSpeed;
        else {
            var ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }

    private float GetMoveSpeed(float userSpeed) {
        if (userSpeed > 10f)
            userSpeed = 10f;
        else if (userSpeed < 0.0001f)
            userSpeed = 0.0001f;
        return userSpeed;
    }

    private void SetMovementSpeed(Player player, Float speed, boolean isFly, CommandSender commandSender, String sped, String command, String commandLabel) {
        if (isFly)
            player.setFlySpeed(speed);
        else
            player.setWalkSpeed(speed);
        if (commandSender == player)
            if (isFly)
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "Speed.Fly.Self").replace("<SPEED>", sped));
            else
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "Speed.Walk.Self").replace("<SPEED>", sped));
        else if (isFly) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                           this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "Speed.Fly.Others.Sender").replace("<SPEED>", sped));
            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "Speed.Fly.Others.Target").replace("<SPEED>", sped));
        } else {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                           this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "Speed.Walk.Others.Sender").replace("<SPEED>", sped));
            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, player, "Speed.Walk.Others.Target").replace("<SPEED>", sped));
        }
    }
}
