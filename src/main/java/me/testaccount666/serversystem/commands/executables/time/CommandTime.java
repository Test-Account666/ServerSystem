package me.testaccount666.serversystem.commands.executables.time;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "time", variants = {"day", "night", "noon", "midnight"}, tabCompleter = TabCompleterTime.class)
public class CommandTime extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("time")) {
            handleTimeCommand(commandSender, command, label, arguments);
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 0) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
            return;
        }

        World world;

        if (arguments.length >= 1) {
            if (!checkBasePermission(commandSender, "Time.World")) return;
            world = Bukkit.getWorld(arguments[0]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            command("Time.WorldNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        handleTimeCommand(commandSender, command, label, command.getName(), world.getName());
    }

    private void handleTimeCommand(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Time.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 1) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
            return;
        }

        World world;

        if (arguments.length >= 2) {
            if (!checkBasePermission(commandSender, "Time.World")) return;
            world = Bukkit.getWorld(arguments[1]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            command("Time.WorldNotFound", commandSender).target(arguments[1]).build();
            return;
        }

        switch (arguments[0].toLowerCase()) {
            case "day":
                world.setTime(0);
                break;
            case "night":
                world.setTime(13000);
                break;
            case "noon":
                world.setTime(6000);
                break;
            case "midnight":
                world.setTime(18000);
                break;
            default: {
                try {
                    var time = Long.parseLong(arguments[0]);
                    time *= 20;

                    world.setTime(time);
                    break;
                } catch (NumberFormatException ignored) {
                    general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
                    return;
                }
            }
        }

        command("Time.Success", commandSender).target(world.getName())
                .postModifier(message -> message.replace("<TIME>", arguments[0])
                        .replace("<WORLD>", world.getName())).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "time" -> "Time";
            case "day" -> "Day";
            case "night" -> "Night";
            case "noon" -> "Noon";
            case "midnight" -> "Midnight";
            default -> throw new IllegalStateException("(CommandTime) Unexpected value: ${commandName}");
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Time.Use", false);
    }
}
