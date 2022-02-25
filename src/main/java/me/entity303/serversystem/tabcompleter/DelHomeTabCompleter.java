package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelHomeTabCompleter implements TabCompleter {
    private final ServerSystem plugin;

    public DelHomeTabCompleter(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) return Collections.singletonList("");

        if (args.length == 1) {
            File f = new File("plugins//ServerSystem//Homes", ((Player) cs).getUniqueId() + ".yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            if (!f.exists()) return Collections.singletonList("");
            List<String> homes = new ArrayList<>(cfg.getConfigurationSection("Homes").getKeys(false));

            if (homes.size() >= 1) {
                List<String> tabs = new ArrayList<>();
                for (String home : homes)
                    if (home.toLowerCase().startsWith(args[0].toLowerCase())) tabs.add(home.toUpperCase());

                return tabs.size() > 0 ? tabs : homes;
            }
        }

        return Collections.singletonList("");
    }
}
