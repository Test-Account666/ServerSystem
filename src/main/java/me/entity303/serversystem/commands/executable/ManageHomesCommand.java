package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.ManageHomesCompleter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerSystemCommand(name = "ManageHomes", tabCompleter = ManageHomesCompleter.class)
public class ManageHomesCommand implements ICommandExecutorOverload {
    protected final ServerSystem _plugin;

    public ManageHomesCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var messages = this._plugin.GetMessages();
        var permissions = this._plugin.GetPermissions();

        if (!permissions.HasPermission(commandSender, "managehomes")) return true;

        if (arguments.length < 2 || arguments[0].length() <= 2) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetSyntax(commandLabel, command.getName(), commandSender, null, "ManageHomes"));
            return true;
        }

        var offlinePlayer = Bukkit.getOfflinePlayer(arguments[1]);

        if (!offlinePlayer.hasPlayedBefore()) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoTarget(offlinePlayer.getName()));
            return true;
        }

        var homesFile = new File("plugins//ServerSystem//Homes", offlinePlayer.getUniqueId() + ".yml");
        var cfg = YamlConfiguration.loadConfiguration(homesFile);

        var homesConfiguration = cfg.getConfigurationSection("Homes");

        var homes = homesConfiguration != null? homesConfiguration.getKeys(false) : new HashSet<String>();

        if (arguments.length == 2 || "list".startsWith(arguments[0].toLowerCase())) {
            this.ExecuteListHomes(commandSender, command, commandLabel, offlinePlayer, homes);
            return true;
        }

        if ("create".startsWith(arguments[0].toLowerCase())) {
            this.ExecuteCreate(commandSender, command, commandLabel, offlinePlayer, cfg, homesFile, arguments[2]);
            return true;
        }

        var noHomesMessage = messages.GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, offlinePlayer.getName(), "ManageHomes.NoHomesFound");

        if (homes.isEmpty()) {
            commandSender.sendMessage(messages.GetPrefix() + noHomesMessage);
            return true;
        }

        var home = homes.stream().filter(potentialHome -> potentialHome.equalsIgnoreCase(arguments[2])).findFirst().orElse(null);

        if (home == null) {
            var homeNotFoundMessage =
                    messages.GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, offlinePlayer.getName(), "ManageHomes.HomeNotFound")
                            .replace("<HOME>", arguments[2]);

            commandSender.sendMessage(messages.GetPrefix() + homeNotFoundMessage);
            return true;
        }

        if ("delete".startsWith(arguments[0].toLowerCase())) {
            this.ExecuteDelete(commandSender, command, commandLabel, offlinePlayer, cfg, homesFile, home);
            return true;
        }

        if ("teleport".startsWith(arguments[0].toLowerCase())) {
            this.ExecuteTeleport(commandSender, command, commandLabel, offlinePlayer, cfg, homesFile, home);
            return true;
        }

        return true;
    }

    public void ExecuteListHomes(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, Set<String> homes) {
        var messages = this._plugin.GetMessages();

        if (homes.isEmpty()) {
            var noHomesMessage = messages.GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "ManageHomes.NoHomesFound");

            commandSender.sendMessage(messages.GetPrefix() + noHomesMessage);
            return;
        }

        var homeBuilder = new StringBuilder();
        var separator = messages.GetMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Separator");
        var homeFormat = messages.GetMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Format");

        for (var home : homes) homeBuilder.append(homeFormat.replace("<SEPERATOR>", separator).replace("<HOME>", home));

        if (homeBuilder.toString().toLowerCase().startsWith(separator)) homeBuilder.delete(0, separator.length());

        var homeMessage = messages.GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "Home.HomeFormat.Message")
                                  .replace("<AMOUNT>", String.valueOf(homes.size()))
                                  .replace("<HOMES>", homeBuilder.toString());

        commandSender.sendMessage(messages.GetPrefix() + homeMessage);
    }

    public void ExecuteCreate(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, FileConfiguration cfg, File file, String home) {
        var messages = this._plugin.GetMessages();

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return;
        }

        cfg.set("Homes." + home.toUpperCase(), player.getLocation());
        try {
            cfg.save(file);
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            throw new RuntimeException(exception);
        }

        var createdMessage =
                messages.GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "ManageHomes.Created").replace("<HOME>", home);

        commandSender.sendMessage(messages.GetPrefix() + createdMessage);
    }

    public void ExecuteDelete(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, FileConfiguration cfg, File file, String home) {
        var messages = this._plugin.GetMessages();

        cfg.set("Homes." + home, null);
        try {
            cfg.save(file);
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            throw new RuntimeException(exception);
        }

        var deletedMessage =
                messages.GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "ManageHomes.Deleted").replace("<HOME>", home);

        commandSender.sendMessage(messages.GetPrefix() + deletedMessage);
    }

    public void ExecuteTeleport(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, FileConfiguration cfg, File file, String home) {
        var messages = this._plugin.GetMessages();

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return;
        }

        var location = (Location) cfg.get("Homes." + home.toUpperCase());

        assert location != null;
        player.teleport(location);

        var teleportedMessage =
                messages.GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "ManageHomes.Teleported").replace("<HOME>", home);

        commandSender.sendMessage(messages.GetPrefix() + teleportedMessage);
    }
}
