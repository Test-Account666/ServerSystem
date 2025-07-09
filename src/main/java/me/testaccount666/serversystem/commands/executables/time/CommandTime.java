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

@ServerSystemCommand(name = "time", variants = {"day", "night", "noon", "midnight"}, tabCompleter = TabCompleterTime.class)
public class CommandTime extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("time")) {
            handleTimeCommand(commandSender, label, arguments);
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        World world;

        if (arguments.length >= 1) {
            if (!checkBasePermission(commandSender, "Time.World", label)) return;
            world = Bukkit.getWorld(arguments[0]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            sendCommandMessage(commandSender, "Time.WorldNotFound", arguments[0], label, null);
            return;
        }

        handleTimeCommand(commandSender, label, command.getName(), world.getName());
    }

    private void handleTimeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Time.Use", label)) return;

        if (arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 1) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        World world;

        if (arguments.length >= 2) {
            if (!checkBasePermission(commandSender, "Time.World", label)) return;
            world = Bukkit.getWorld(arguments[1]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            sendCommandMessage(commandSender, "Time.WorldNotFound", arguments[1], label, null);
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
                    sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
                    return;
                }
            }
        }

        sendCommandMessage(commandSender, "Time.Success", world.getName(), label,
                message -> message.replace("<TIME>", arguments[0])
                        .replace("<WORLD>", world.getName()));
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Time.Use", false);
    }
}
