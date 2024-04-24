package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class EconomyManager extends AbstractEconomyManager {
    private final File _file;
    private final FileConfiguration _configuration;
    private final String _currencySingular;
    private final String _currencyPlural;
    private final String _startingMoney;
    private final String _displayFormat;
    private final String _moneyFormat;
    private final String _separator;
    private final String _thousand;

    public EconomyManager(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator,
                          String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);

        this._file = new File("plugins//ServerSystem", "economy.yml");
        this._configuration = YamlConfiguration.loadConfiguration(this._file);
        this._currencySingular = currencySingular;
        this._currencyPlural = currencyPlural;
        this._startingMoney = startingMoney;
        this._displayFormat = displayFormat;
        this._moneyFormat = moneyFormat;
        this._separator = separator;
        this._thousand = thousands;
    }

    public String GetThousand() {
        return this._thousand;
    }

    @Override
    public String Format(double money) {
        var moneyStr = String.format(Locale.US, "%1$,.2f", money);

        moneyStr = moneyStr.replace(",", "<THOUSAND>");

        var formattedMoney = this.GetFormattedMoney(moneyStr);

        var plural = false;

        var moneyWorth = money;

        if (money < 0)
            moneyWorth = money * -1;

        if (moneyWorth < 1)
            plural = true;

        if (money > 1)
            plural = true;

        return this._displayFormat.replace("<MONEY>", formattedMoney).replace("<CURRENCY>", plural? this._currencyPlural : this._currencySingular);
    }

    private String GetFormattedMoney(String moneyStr) {
        var moneyString = moneyStr.split("\\.")[0] + "." + moneyStr.split("\\.")[1];
        String formattedMoney;
        var first = "0";
        var last = "00";
        try {
            first = moneyString.split("\\.")[0];
            last = moneyString.split("\\.")[1];
        } catch (Exception ignored) {

        }

        if (last.length() == 1)
            last = last + "0";
        formattedMoney = this._moneyFormat.replace("<FIRST>", first)
                                          .replace("<LAST>", last)
                                          .replace("<SEPARATOR>", this._separator)
                                          .replace("<THOUSAND>", this.GetThousands());
        return formattedMoney;
    }

    @Override
    public boolean HasEnoughMoney(Player player, double amount) {
        return this.GetMoneyAsNumber((OfflinePlayer) player) >= amount;
    }

    @Override
    public void MakeTransaction(Player sender, Player target, double amount) {
        this.RemoveMoney((OfflinePlayer) sender, amount);
        this.AddMoney((OfflinePlayer) target, amount);
    }

    @Override
    public void SetMoney(Player player, double amount) {
        this.Save(player, String.valueOf(amount));
    }

    @Override
    public void RemoveMoney(Player player, double amount) {
        this.Save(player, String.valueOf(this.GetMoneyAsNumber((OfflinePlayer) player) - amount));
    }

    @Override
    public void AddMoney(Player player, double amount) {
        this.Save(player, String.valueOf(this.GetMoneyAsNumber((OfflinePlayer) player) + amount));
    }

    @Override
    public void CreateAccount(Player player) {
        this.Save(player, this._startingMoney);
    }

    ////////////////////////////////////
    @Override
    public boolean HasEnoughMoney(OfflinePlayer player, double amount) {
        return this.GetMoneyAsNumber(player) >= amount;
    }

    @Override
    public void MakeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {
        if (sender == null)
            return;
        if (target == null)
            return;
        this.RemoveMoney(sender, amount);
        this.AddMoney(target, amount);
    }

    @Override
    public void SetMoney(OfflinePlayer player, double amount) {
        if (player == null)
            return;
        this._configuration.set("Money." + player.getUniqueId(), String.valueOf(amount));


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

        if (player.isOnline())
            this.SetMoney(player.getPlayer(), amount);
    }

    @Override
    public void RemoveMoney(OfflinePlayer player, double amount) {
        if (player == null)
            return;
        this._configuration.set("Money." + player.getUniqueId(), String.valueOf(this.GetMoneyAsNumber(player) - amount));

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

        if (player.isOnline())
            this.RemoveMoney(player.getPlayer(), amount);
    }

    @Override
    public void AddMoney(OfflinePlayer player, double amount) {
        if (player == null)
            return;
        this._configuration.set("Money." + player.getUniqueId(), String.valueOf(this.GetMoneyAsNumber(player) + amount));


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

        if (player.isOnline())
            this.AddMoney(player.getPlayer(), amount);
    }

    @Override
    public void CreateAccount(OfflinePlayer player) {
        if (player == null)
            return;
        this._configuration.set("Money." + player.getUniqueId(), String.valueOf(this._startingMoney));


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
    public void DeleteAccount(OfflinePlayer player) {
        if (player == null)
            return;
        this._configuration.set("Money." + player.getUniqueId(), null);

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
    public Double GetMoneyAsNumber(Player player) {
        return this.GetMoneyAsNumber((OfflinePlayer) player);
    }

    @Override
    public String GetMoney(Player player) {
        return this.GetMoney((OfflinePlayer) player);
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
    public String GetMoney(OfflinePlayer player) {
        if (player == null)
            return this.Format(0.0D);
        try {
            if (!this._file.exists())
                return this.Format(0.0D);
            return this.Format(Double.parseDouble(this._configuration.getString("Money." + player.getUniqueId())));
        } catch (Exception ignored) {
            return this.Format(0.0D);
        }
    }

    @Override
    public boolean HasAccount(OfflinePlayer player) {
        if (player == null)
            return false;
        try {
            if (!this._file.exists())
                return false;
            return (this._configuration.getString("Money." + player.getUniqueId()) != null && !this._configuration.getString("Money." + player.getUniqueId()).equalsIgnoreCase("null"));
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    @Override
    public void Close() {

    }


    ///////////////////////////////////

    public void Save(Player player, String balance) {
        if (player == null)
            return;
        balance = String.format("%.2f", Double.parseDouble(balance)).replace(",", ".");
        this._configuration.set("Money." + player.getUniqueId(), balance);

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

    @Override
    public String GetMoneyFormat() {
        return this._moneyFormat;
    }

    @Override
    public String GetSeparator() {
        return this._separator;
    }

    @Override
    public String GetStartingMoney() {
        return this._startingMoney;
    }

    @Override
    public String GetDisplayFormat() {
        return this._displayFormat;
    }

    @Override
    public String GetCurrencySingular() {
        return this._currencySingular;
    }

    @Override
    public String GetCurrencyPlural() {
        return this._currencyPlural;
    }


    @Override
    public LinkedHashMap<OfflinePlayer, Double> GetTopTen() {
        return this._topTen;
    }
}
