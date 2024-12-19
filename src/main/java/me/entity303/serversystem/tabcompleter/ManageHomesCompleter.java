package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageHomesCompleter implements ITabCompleterOverload {

    private final ServerSystem _plugin;

    public ManageHomesCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 1) {
            var possibleArguments = new ArrayList<String>();
            possibleArguments.add("list");
            possibleArguments.add("teleport");
            possibleArguments.add("delete");
            possibleArguments.add("create");

            var tabCompleted = new ArrayList<String>();

            for (var possibleArgument : possibleArguments) {
                if (!possibleArgument.startsWith(arguments[0].toLowerCase())) continue;

                tabCompleted.add(possibleArgument);
            }

            return !tabCompleted.isEmpty()? tabCompleted : possibleArguments;
        }

        if (arguments.length == 2) return GetOfflinePlayers(arguments);

        return this.TabCompleteHomes(arguments);
    }

    public static List<String> GetOfflinePlayers(String... arguments) {
        var players = new ArrayList<String>();
        for (var offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.isOnline()) continue;

            var name = offlinePlayer.getName();
            players.add(name);
        }

        for (var onlinePlayer : Bukkit.getOnlinePlayers()) players.add(onlinePlayer.getName());

        var possiblePlayers = new ArrayList<String>();

        for (var player : players) {
            if (!player.toLowerCase(Locale.ROOT).startsWith(arguments[1].toLowerCase(Locale.ROOT))) continue;

            possiblePlayers.add(player);
        }

        return !possiblePlayers.isEmpty()? possiblePlayers : players;
    }

    public List<String> TabCompleteHomes(String[] arguments) {
        var offlinePlayer = Bukkit.getOfflinePlayer(arguments[1]);

        if (!offlinePlayer.hasPlayedBefore()) return List.of();

        var homesFile = new File("plugins//ServerSystem//Homes", offlinePlayer.getUniqueId() + ".yml");

        if (!homesFile.exists()) return List.of();
        var cfg = YamlConfiguration.loadConfiguration(homesFile);

        var homesConfiguration = cfg.getConfigurationSection("Homes");

        if (homesConfiguration == null) return List.of();

        var homes = homesConfiguration.getKeys(false).stream().toList();

        var tabCompleted = new ArrayList<String>();

        for (var possibleArgument : homes) {
            if (!possibleArgument.toLowerCase().startsWith(arguments[0].toLowerCase())) continue;

            tabCompleted.add(possibleArgument);
        }

        return !tabCompleted.isEmpty()? tabCompleted : homes;
    }
}
