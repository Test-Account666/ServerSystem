package me.entity303.serversystem.databasemanager;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HomeManager {

    public Map<String, Location> GetHomes(OfflinePlayer player) {
        var homeFile = new File("plugins//ServerSystem//Homes", player.getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        if (!homeFile.exists())
            return new HashMap<>();

        if (homeFile.exists()) {
            Collection<String> homes = new ArrayList<>(homeCfg.getConfigurationSection("Homes").getKeys(false));
            if (homes.isEmpty())
                return new HashMap<>();

            return homes.stream()
                        .collect(Collectors.toMap(home -> home, home -> (Location) homeCfg.get("Homes." + home.toUpperCase()),
                                                  (location, location1) -> location1));
        }
        return new HashMap<>();
    }

    public List<String> GetHomeNames(OfflinePlayer player) {
        var homeFile = new File("plugins//ServerSystem//Homes", player.getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        if (!homeFile.exists())
            return new ArrayList<>();

        if (homeFile.exists()) {
            List<String> homes = new ArrayList<>(homeCfg.getConfigurationSection("Homes").getKeys(false));
            if (homes.isEmpty())
                return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public HomeManager DeleteHome(OfflinePlayer player, String home) {
        var homeFile = new File("plugins//ServerSystem//Homes", player.getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        if (!homeFile.exists())
            return this;

        if (homeFile.exists()) {
            List<String> homes = new ArrayList<>(homeCfg.getConfigurationSection("Homes").getKeys(false));
            if (homes.isEmpty())
                return this;
        }

        homeCfg.set("Homes." + home.toUpperCase(), null);

        try {
            homeCfg.save(homeFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return this;
    }
}
