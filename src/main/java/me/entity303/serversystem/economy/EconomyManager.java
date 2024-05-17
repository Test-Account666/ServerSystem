package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class EconomyManager extends AbstractEconomyManager {
    private final File _file;
    private final FileConfiguration _configuration;
    private final String _startingMoney;

    public EconomyManager(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator,
                          String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);

        this._file = new File("plugins//ServerSystem", "economy.yml");
        this._configuration = YamlConfiguration.loadConfiguration(this._file);
        this._startingMoney = startingMoney;
    }

    @Override
    public void SetMoney(OfflinePlayer offlinePlayer, double amount) {
        if (offlinePlayer == null)
            return;
        this._configuration.set("Money." + offlinePlayer.getUniqueId(), String.valueOf(amount));


        try {
            this._configuration.save(this._file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        try {
            this._configuration.load(this._file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void CreateAccount(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null)
            return;

        this._configuration.set("Money." + offlinePlayer.getUniqueId(), String.valueOf(this._startingMoney));


        try {
            this._configuration.save(this._file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        try {
            this._configuration.load(this._file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void DeleteAccount(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null)
            return;

        this._configuration.set("Money." + offlinePlayer.getUniqueId(), null);

        try {
            this._configuration.save(this._file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            this._configuration.load(this._file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Double GetMoneyAsNumber(OfflinePlayer player) {
        if (player == null)
            return 0.0D;

        try {
            if (!this._file.exists())
                return 0.0;
            return Double.valueOf(this._configuration.getString("Money." + player.getUniqueId()));
        } catch (NullPointerException ignored) {
            return 0.0;
        }
    }

    @Override
    public boolean HasAccount(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null)
            return false;

        try {
            if (!this._file.exists())
                return false;
            return (this._configuration.getString("Money." + offlinePlayer.getUniqueId()) != null &&
                    !this._configuration.getString("Money." + offlinePlayer.getUniqueId()).equalsIgnoreCase("null"));
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    @Override
    public void Close() {

    }


    ///////////////////////////////////

    @Override
    public void FetchTopTen() {
        if (!this._topTen.isEmpty())
            this._topTen.clear();

        var topTenMoneyHash = new HashMap<OfflinePlayer, Double>();

        if (this._configuration.getConfigurationSection("Money") == null)
            return;

        this._configuration.getConfigurationSection("Money").getKeys(false).forEach(uuid -> {
            Double money = this._configuration.getDouble("Money." + uuid);
            topTenMoneyHash.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), money);
        });

        if (topTenMoneyHash.isEmpty())
            return;

        LinkedHashMap<OfflinePlayer, Double> topTenMoneyHashSorted;

        topTenMoneyHashSorted = topTenMoneyHash.entrySet()
                                               .stream()
                                               .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                                               .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (entry1, entry2) -> entry2, LinkedHashMap::new));

        var topTenMoney = new LinkedHashMap<OfflinePlayer, Double>();

        var iterator = topTenMoneyHashSorted.entrySet().iterator();

        var index = 0;

        while (index < 10) {
            index = index + 1;
            var entry = iterator.next();
            topTenMoney.put(entry.getKey(), entry.getValue());
        }

        this._topTen = topTenMoney;
    }
}
