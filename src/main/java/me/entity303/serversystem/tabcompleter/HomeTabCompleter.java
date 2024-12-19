package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeTabCompleter implements ITabCompleterOverload {

    protected final ServerSystem _plugin;

    public HomeTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) return Collections.singletonList("");

        if (arguments.length != 1) return Collections.singletonList("");

        var homeFile = new File("plugins//ServerSystem//Homes", player.getUniqueId() + ".yml");
        if (!homeFile.exists()) return Collections.singletonList("");

        var cfg = YamlConfiguration.loadConfiguration(homeFile);
        if (cfg.getConfigurationSection("Homes") == null) return Collections.singletonList("");

        var homes = new ArrayList<>(cfg.getConfigurationSection("Homes").getKeys(false));

        if (homes.isEmpty()) return Collections.singletonList("");

        var tabs = new ArrayList<String>();
        for (var home : homes) {
            if (!home.toLowerCase().startsWith(arguments[0].toLowerCase())) continue;

            tabs.add(home.toUpperCase());
        }

        return !tabs.isEmpty()? tabs : homes;

    }
}
