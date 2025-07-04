package me.testaccount666.serversystem.commands.executables.home.admin;

import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.home.Home;
import org.bukkit.command.Command;

public class CommandAdminHome extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        var commandName = command.getName().substring("admin".length());

        if (commandName.equalsIgnoreCase("sethome")) {
            handleSetHomeCommand(commandSender, label, arguments);
            return;
        }

        if (commandName.equalsIgnoreCase("deletehome")) {
            handleDeleteHomeCommand(commandSender, label, arguments);
            return;
        }

        handleHomeCommand(commandSender, label, arguments);
    }

    private void handleHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "AdminHome.Use", label)) return;

        if (commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        if (arguments.length <= 1) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();

        var homeManager = targetUser.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[1]);

        if (homeOptional.isEmpty()) {
            sendCommandMessage(commandSender, "Home.DoesNotExist", null, label,
                    message -> message.replace("<HOME>", arguments[1]));
            return;
        }

        var home = homeOptional.get();
        var homeLocation = home.getLocation();

        commandSender.getPlayer().teleport(homeLocation);

        sendCommandMessage(commandSender, "Home.Success", null, label,
                message -> message.replace("<HOME>", home.getDisplayName()));
    }

    private void handleDeleteHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "AdminDeleteHome.Use", label)) return;

        if (arguments.length <= 1) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }


        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();

        var homeManager = targetUser.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[1]);

        if (homeOptional.isEmpty()) {
            sendCommandMessage(commandSender, "DeleteHome.DoesNotExist", null, label,
                    message -> message.replace("<HOME>", arguments[1]));
            return;
        }

        var home = homeOptional.get();
        homeManager.removeHome(home);

        sendCommandMessage(commandSender, "DeleteHome.Success", null, label,
                message -> message.replace("<HOME>", home.getDisplayName()));
    }

    private void handleSetHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "AdminSetHome.Use", label)) return;

        if (commandSender instanceof ConsoleUser) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        if (arguments.length <= 1) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(commandSender, label, arguments[0]);
            return;
        }

        var targetUser = targetUserOptional.get();

        var homeManager = targetUser.getHomeManager();
        var maxHomesOptional = homeManager.getMaxHomeCount();

        if (maxHomesOptional.isEmpty()) {
            sendGeneralMessage(commandSender, "ErrorOccurred", null, label, null);
            return;
        }

        var homeName = arguments[1];

        if (homeManager.hasHome(homeName)) {
            sendCommandMessage(commandSender, "SetHome.AlreadyExists", null, label,
                    message -> message.replace("<HOME>", homeName));
            return;
        }

        var newHomeOptional = Home.of(homeName, commandSender.getPlayer().getLocation());

        if (newHomeOptional.isEmpty()) {
            sendCommandMessage(commandSender, "SetHome.InvalidName", null, label, null);
            return;
        }

        var newHome = newHomeOptional.get();
        homeManager.addHome(newHome);

        sendCommandMessage(commandSender, "SetHome.Success", null, label,
                message -> message.replace("<HOME>", newHome.getDisplayName()));
    }
}
