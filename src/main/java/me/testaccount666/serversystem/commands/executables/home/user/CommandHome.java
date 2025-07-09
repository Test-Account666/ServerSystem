package me.testaccount666.serversystem.commands.executables.home.user;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.home.admin.CommandAdminHome;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.home.Home;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "home", variants = {"sethome", "deletehome", "adminhome", "adminsethome", "admindeletehome"}, tabCompleter = TabCompleterHome.class)
public class CommandHome extends AbstractServerSystemCommand {
    private final CommandAdminHome _commandAdminHome;

    public CommandHome() {
        _commandAdminHome = new CommandAdminHome();
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().toLowerCase().startsWith("admin")) {
            _commandAdminHome.execute(commandSender, command, label, arguments);
            return;
        }

        if (command.getName().equalsIgnoreCase("sethome")) {
            handleSetHomeCommand(commandSender, label, arguments);
            return;
        }

        if (command.getName().equalsIgnoreCase("deletehome")) {
            handleDeleteHomeCommand(commandSender, label, arguments);
            return;
        }

        handleHomeCommand(commandSender, label, arguments);
    }

    private void handleHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Home.Use", label)) return;

        if (arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var homeManager = commandSender.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[0]);

        if (homeOptional.isEmpty()) {
            sendCommandMessage(commandSender, "Home.DoesNotExist", null, label,
                    message -> message.replace("<HOME>", arguments[0]));
            return;
        }

        var home = homeOptional.get();
        var homeLocation = home.getLocation();

        commandSender.getPlayer().teleport(homeLocation);

        sendCommandMessage(commandSender, "Home.Success", null, label,
                message -> message.replace("<HOME>", home.getDisplayName()));
    }

    private void handleDeleteHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "DeleteHome.Use", label)) return;

        if (arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var homeManager = commandSender.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[0]);

        if (homeOptional.isEmpty()) {
            sendCommandMessage(commandSender, "DeleteHome.DoesNotExist", null, label,
                    message -> message.replace("<HOME>", arguments[0]));
            return;
        }

        var home = homeOptional.get();
        homeManager.removeHome(home);

        sendCommandMessage(commandSender, "DeleteHome.Success", null, label,
                message -> message.replace("<HOME>", home.getDisplayName()));
    }

    private void handleSetHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "SetHome.Use", label)) return;

        if (arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        var homeManager = commandSender.getHomeManager();
        var maxHomesOptional = homeManager.getMaxHomeCount();

        if (maxHomesOptional.isEmpty()) {
            sendGeneralMessage(commandSender, "NotPlayer", null, label, null);
            return;
        }

        var currentHomeCount = homeManager.getHomes().size();

        var maxHomes = maxHomesOptional.get();

        if (maxHomes <= currentHomeCount) {
            sendCommandMessage(commandSender, "SetHome.MaxHomes", null, label,
                    message -> message.replace("<MAX_HOMES>", String.valueOf(maxHomes))
                            .replace("<CURRENT_HOMES>", String.valueOf(currentHomeCount)));
            return;
        }

        var homeName = arguments[0];

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

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        if (command.getName().toLowerCase().startsWith("admin")) return _commandAdminHome.hasCommandAccess(player, command);

        if (command.getName().equalsIgnoreCase("sethome")) return PermissionManager.hasCommandPermission(player, "SetHome.Use", false);

        if (command.getName().equalsIgnoreCase("deletehome")) return PermissionManager.hasCommandPermission(player, "DeleteHome.Use", false);

        return PermissionManager.hasCommandPermission(player, "Home.Use", false);
    }
}
