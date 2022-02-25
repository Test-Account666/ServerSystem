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

public class EconomyManager extends ManagerEconomy {
    private final File file;
    private final FileConfiguration cfg;
    private final String currencySingular;
    private final String currencyPlural;
    private final String startingMoney;
    private final String displayFormat;
    private final String moneyFormat;
    private final String separator;
    private final String thousand;

    public EconomyManager(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator, String thousand, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousand, plugin);

        this.file = new File("plugins//ServerSystem", "economy.yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
        this.currencySingular = currencySingular;
        this.currencyPlural = currencyPlural;
        this.startingMoney = startingMoney;
        this.displayFormat = displayFormat;
        this.moneyFormat = moneyFormat;
        this.separator = separator;
        this.thousand = thousand;
    }

    @Override
    public String getMoneyFormat() {
        return this.moneyFormat;
    }

    @Override
    public String getSeparator() {
        return this.separator;
    }

    @Override
    public String getStartingMoney() {
        return this.startingMoney;
    }

    @Override
    public String getDisplayFormat() {
        return this.displayFormat;
    }

    @Override
    public String getCurrencySingular() {
        return this.currencySingular;
    }

    @Override
    public String getCurrencyPlural() {
        return this.currencyPlural;
    }

    public String getThousand() {
        return this.thousand;
    }

    @Override
    public String format(double money) {
        String moneyStr = String.format(Locale.US, "%1$,.2f", money);

        moneyStr = moneyStr.replace(",", "<THOUSAND>");

        String moneyString = moneyStr.split("\\.")[0] + "." + moneyStr.split("\\.")[1];
        String formattedMoney;
        String first = "0";
        String last = "00";
        try {
            first = moneyString.split("\\.")[0];
            last = moneyString.split("\\.")[1];
        } catch (Exception ignored) {

        }

        if (last.length() == 1) last = last + "0";
        formattedMoney = this.moneyFormat.
                replace("<FIRST>", first).
                replace("<LAST>", last).
                replace("<SEPARATOR>", this.separator).
                replace("<THOUSAND>", this.getThousands());
        return this.displayFormat.replace("<MONEY>", formattedMoney).replace("<CURRENCY>", money >= 2 ? this.currencyPlural : this.currencySingular);
    }

    @Override
    public boolean hasEnoughMoney(Player player, double amount) {
        return this.getMoneyAsNumber((OfflinePlayer) player) >= amount;
    }

    @Override
    public void makeTransaction(Player sender, Player target, double amount) {
        this.removeMoney((OfflinePlayer) sender, amount);
        this.addMoney((OfflinePlayer) target, amount);
    }

    @Override
    public void setMoney(Player player, double amount) {
        this.save(player, String.valueOf(amount));
    }

    @Override
    public void removeMoney(Player player, double amount) {
        this.save(player, String.valueOf(this.getMoneyAsNumber((OfflinePlayer) player) - amount));
    }

    @Override
    public void addMoney(Player player, double amount) {
        this.save(player, String.valueOf(this.getMoneyAsNumber((OfflinePlayer) player) + amount));
    }

    @Override
    public void createAccount(Player player) {
        this.save(player, this.startingMoney);
    }

    ////////////////////////////////////
    @Override
    public boolean hasEnoughMoney(OfflinePlayer player, double amount) {
        return this.getMoneyAsNumber(player) >= amount;
    }

    @Override
    public void makeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {
        if (sender == null) return;
        if (target == null) return;
        this.removeMoney(sender, amount);
        this.addMoney(target, amount);
    }

    @Override
    public void setMoney(OfflinePlayer player, double amount) {
        if (player == null) return;
        this.cfg.set("Money." + player.getUniqueId(), String.valueOf(amount));


        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (player.isOnline()) this.setMoney(player.getPlayer(), amount);
    }

    @Override
    public void removeMoney(OfflinePlayer player, double amount) {
        if (player == null) return;
        this.cfg.set("Money." + player.getUniqueId(), String.valueOf(this.getMoneyAsNumber(player) - amount));

        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (player.isOnline()) this.removeMoney(player.getPlayer(), amount);
    }

    @Override
    public void addMoney(OfflinePlayer player, double amount) {
        if (player == null) return;
        this.cfg.set("Money." + player.getUniqueId(), String.valueOf(this.getMoneyAsNumber(player) + amount));


        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (player.isOnline()) this.addMoney(player.getPlayer(), amount);
    }

    @Override
    public void createAccount(OfflinePlayer player) {
        if (player == null) return;
        this.cfg.set("Money." + player.getUniqueId(), String.valueOf(this.startingMoney));


        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    ///////////////////////////////////

    @Override
    public void deleteAccount(OfflinePlayer player) {
        if (player == null) return;
        this.cfg.set("Money." + player.getUniqueId(), null);

        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Double getMoneyAsNumber(Player player) {
        return this.getMoneyAsNumber((OfflinePlayer) player);
    }

    @Override
    public String getMoney(Player player) {
        return this.getMoney((OfflinePlayer) player);
    }

    @Override
    public Double getMoneyAsNumber(OfflinePlayer player) {
        if (player == null) return 0.0D;
        try {
            if (!this.file.exists()) return 0.0;
            return Double.valueOf(this.cfg.getString("Money." + player.getUniqueId()));
        } catch (NullPointerException ignored) {
            return 0.0;
        }
    }

    @Override
    public String getMoney(OfflinePlayer player) {
        if (player == null) return this.format(0.0D);
        try {
            if (!this.file.exists()) return this.format(0.0D);
            return this.format(Double.parseDouble(this.cfg.getString("Money." + player.getUniqueId())));
        } catch (Exception ignored) {
            return this.format(0.0D);
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        if (player == null) return false;
        try {
            if (!this.file.exists()) return false;
            return (this.cfg.getString("Money." + player.getUniqueId()) != null && !this.cfg.getString("Money." + player.getUniqueId()).equalsIgnoreCase("null"));
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    @Override
    public void close() {

    }

    public void save(Player player, String balance) {
        if (player == null) return;
        balance = String.format("%.2f", Double.parseDouble(balance)).replace(",", ".");
        this.cfg.set("Money." + player.getUniqueId(), balance);

        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> getTopTen() {
        return this.topTen;
    }

    @Override
    public void fetchTopTen() {
        if (!this.topTen.isEmpty()) this.topTen.clear();
        HashMap<OfflinePlayer, Double> topTenMoneyHash = new HashMap<>();
        this.cfg.getConfigurationSection("Money").getKeys(false).forEach(uuid -> {
            Double money = this.cfg.getDouble("Money." + uuid);
            topTenMoneyHash.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), money);
        });

        LinkedHashMap<OfflinePlayer, Double> topTenMoneyHashSorted = new LinkedHashMap<>();

        topTenMoneyHashSorted = topTenMoneyHash.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        LinkedHashMap<OfflinePlayer, Double> topTenMoney = new LinkedHashMap<>();

        Iterator<Map.Entry<OfflinePlayer, Double>> iterator = topTenMoneyHashSorted.entrySet().iterator();

        int i = 0;

        while (i < 10) {
            i = i + 1;
            Map.Entry<OfflinePlayer, Double> entry = iterator.next();
            topTenMoney.put(entry.getKey(), entry.getValue());
        }

        this.topTen = topTenMoney;
    }
}
