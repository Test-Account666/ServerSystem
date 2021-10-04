package me.Entity303.ServerSystem.Economy;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class ManagerEconomy {
    protected final ss plugin;
    protected final String thousands;
    protected final HashMap<OfflinePlayer, Double> moneyCache = new HashMap<>();
    protected final HashMap<OfflinePlayer, Boolean> accountCache = new HashMap<>();
    protected LinkedHashMap<OfflinePlayer, Double> topTen = new LinkedHashMap<>();

    public ManagerEconomy(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator, String thousands, ss plugin) {
        this.plugin = plugin;
        this.thousands = thousands;
    }

    public ss getPlugin() {
        return this.plugin;
    }

    public abstract String getMoneyFormat();

    public abstract String getSeparator();

    public abstract String getStartingMoney();

    public abstract String getDisplayFormat();

    public abstract String getCurrencySingular();

    public abstract String getCurrencyPlural();

    public String getThousands() {
        return this.thousands;
    }

    public abstract String format(double money);

    public abstract boolean hasEnoughMoney(Player player, double amount);

    public abstract void makeTransaction(Player sender, Player target, double amount);

    public abstract void setMoney(Player player, double amount);

    public abstract void removeMoney(Player player, double amount);

    public abstract void addMoney(Player player, double amount);

    public abstract void createAccount(Player player);

    ////////////////////////////////////


    public abstract boolean hasEnoughMoney(OfflinePlayer player, double amount);

    public abstract void makeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount);

    public abstract void setMoney(OfflinePlayer player, double amount);

    public abstract void removeMoney(OfflinePlayer player, double amount);

    public abstract void addMoney(OfflinePlayer player, double amount);

    public abstract void createAccount(OfflinePlayer player);


    ///////////////////////////////////

    public abstract void deleteAccount(OfflinePlayer player);

    public abstract Double getMoneyAsNumber(Player player);

    public abstract String getMoney(Player player);

    public abstract Double getMoneyAsNumber(OfflinePlayer player);

    public abstract String getMoney(OfflinePlayer player);

    public abstract boolean hasAccount(OfflinePlayer player);

    public abstract LinkedHashMap<OfflinePlayer, Double> getTopTen();

    public abstract void fetchTopTen();

    public abstract void close();
}
