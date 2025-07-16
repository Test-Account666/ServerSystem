package me.testaccount666.serversystem.commands.executables.home.user;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.home.admin.CommandAdminHome;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.home.Home;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

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
        if (!checkBasePermission(commandSender, "Home.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var homeManager = commandSender.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[0]);

        if (homeOptional.isEmpty()) {
            command("Home.DoesNotExist", commandSender)
                    .postModifier(message -> message.replace("<HOME>", arguments[0])).build();
            return;
        }

        var home = homeOptional.get();
        var homeLocation = home.getLocation();

        commandSender.getPlayer().teleport(homeLocation);

        command("Home.Success", commandSender)
                .postModifier(message -> message.replace("<HOME>", home.getDisplayName())).build();
    }

    private void handleDeleteHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Home.Delete")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var homeManager = commandSender.getHomeManager();
        var homeOptional = homeManager.getHomeByName(arguments[0]);

        if (homeOptional.isEmpty()) {
            command("Home.DoesNotExist", commandSender)
                    .postModifier(message -> message.replace("<HOME>", arguments[0])).build();
            return;
        }

        var home = homeOptional.get();
        homeManager.removeHome(home);

        command("DeleteHome.Success", commandSender)
                .postModifier(message -> message.replace("<HOME>", home.getDisplayName())).build();
    }

    private void handleSetHomeCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Home.Set")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var homeManager = commandSender.getHomeManager();
        var maxHomesOptional = homeManager.getMaxHomeCount();

        if (maxHomesOptional.isEmpty()) {
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }

        var currentHomeCount = homeManager.getHomes().size();

        var maxHomes = maxHomesOptional.get();

        if (maxHomes <= currentHomeCount) {

            command("SetHome.MaxHomes", commandSender)
                    .postModifier(message -> message.replace("<MAX_HOMES>", String.valueOf(maxHomes))
                            .replace("<CURRENT_HOMES>", String.valueOf(currentHomeCount))).build();
            return;
        }

        var homeName = arguments[0];

        if (homeManager.hasHome(homeName)) {
            command("SetHome.AlreadyExists", commandSender)
                    .postModifier(message -> message.replace("<HOME>", homeName)).build();
            return;
        }

        var newHomeOptional = Home.of(homeName, commandSender.getPlayer().getLocation());

        if (newHomeOptional.isEmpty()) {
            command("SetHome.InvalidName", commandSender).build();
            return;
        }

        var newHome = newHomeOptional.get();
        homeManager.addHome(newHome);

        command("SetHome.Success", commandSender)
                .postModifier(message -> message.replace("<HOME>", newHome.getDisplayName())).build();
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        if (command.getName().toLowerCase().startsWith("admin")) return _commandAdminHome.hasCommandAccess(player, command);

        if (command.getName().equalsIgnoreCase("sethome")) return PermissionManager.hasCommandPermission(player, "Home.Set", false);

        if (command.getName().equalsIgnoreCase("deletehome")) return PermissionManager.hasCommandPermission(player, "Home.Delete", false);

        return PermissionManager.hasCommandPermission(player, "Home.Use", false);
    }
}
