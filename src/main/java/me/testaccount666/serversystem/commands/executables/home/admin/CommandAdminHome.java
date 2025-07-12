package me.testaccount666.serversystem.commands.executables.home.admin;

import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.home.Home;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

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
        if (!checkBasePermission(commandSender, "AdminHome.Use")) return;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (arguments.length <= 1) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();

        var homeManager = targetUser.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[1]);

        if (homeOptional.isEmpty()) {
            command("Home.DoesNotExist", targetUser).target(targetUser.getName().get())
                    .modifier(message -> message.replace("<HOME>", arguments[1])).build();
            return;
        }

        var home = homeOptional.get();
        var homeLocation = home.getLocation();

        commandSender.getPlayer().teleport(homeLocation);

        command("Home.Success", targetUser).target(targetUser.getName().get())
                .modifier(message -> message.replace("<HOME>", home.getDisplayName())).build();
    }

    private void handleDeleteHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "AdminHome.Delete")) return;

        if (arguments.length <= 1) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }


        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();

        var homeManager = targetUser.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[1]);

        if (homeOptional.isEmpty()) {

            general("Home.DoesNotExist", commandSender).target(targetUser.getName().get())
                    .modifier(message -> message.replace("<HOME>", arguments[1])).build();
            return;
        }

        var home = homeOptional.get();
        homeManager.removeHome(home);

        general("DeleteHome.Success", commandSender).target(targetUser.getName().get())
                .modifier(message -> message.replace("<HOME>", home.getDisplayName())).build();
    }

    private void handleSetHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "AdminHome.Set")) return;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (arguments.length <= 1) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();

        var homeManager = targetUser.getHomeManager();
        var maxHomesOptional = homeManager.getMaxHomeCount();

        if (maxHomesOptional.isEmpty()) {
            general("ErrorOccurred", targetUser).label(label).build();
            return;
        }

        var homeName = arguments[1];
        if (homeManager.hasHome(homeName)) {
            command("SetHome.AlreadyExists", targetUser).label(label).target(targetUser.getName().get())
                    .modifier(message -> message.replace("<HOME>", homeName)).build();
            return;
        }

        var newHomeOptional = Home.of(homeName, commandSender.getPlayer().getLocation());

        if (newHomeOptional.isEmpty()) {
            command("SetHome.InvalidName", commandSender).label(label).target(targetUser.getName().get()).build();
            return;
        }

        var newHome = newHomeOptional.get();
        homeManager.addHome(newHome);

        command("SetHome.Success", targetUser).label(label).target(targetUser.getName().get())
                .modifier(message -> message.replace("<HOME>", newHome.getDisplayName())).build();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var commandName = command.getName().substring("admin".length());

        if (commandName.equalsIgnoreCase("sethome")) return PermissionManager.hasCommandPermission(player, "AdminHome.Set", false);

        if (commandName.equalsIgnoreCase("deletehome")) return PermissionManager.hasCommandPermission(player, "AdminHome.Delete", false);

        return PermissionManager.hasCommandPermission(player, "AdminHome.Use", false);
    }
}
