package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeTabCompleter implements ITabCompleterOverload {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player))
            return Collections.singletonList("");

        if (arguments.length == 1) {
            var homeFile = new File("plugins//ServerSystem//Homes", ((Player) commandSender).getUniqueId() + ".yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(homeFile);
            if (!homeFile.exists())
                return Collections.singletonList("");

            if (cfg.getConfigurationSection("Homes") == null)
                return Collections.singletonList("");

            List<String> homes = new ArrayList<>(cfg.getConfigurationSection("Homes").getKeys(false));

            if (!homes.isEmpty()) {
                List<String> tabs = new ArrayList<>();
                for (var home : homes)
                    if (home.toLowerCase().startsWith(arguments[0].toLowerCase()))
                        tabs.add(home.toUpperCase());

                return !tabs.isEmpty()? tabs : homes;
            }
        }

        return Collections.singletonList("");
    }
}
