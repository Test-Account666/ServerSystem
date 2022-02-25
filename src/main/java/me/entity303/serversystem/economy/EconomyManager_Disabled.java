package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class EconomyManager_Disabled extends ManagerEconomy {

    public EconomyManager_Disabled(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat, String separator, String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
    }

    @Override
    public String getMoneyFormat() {
        return "";
    }

    @Override
    public String getSeparator() {
        return "";
    }

    @Override
    public String getStartingMoney() {
        return "";
    }

    @Override
    public String getDisplayFormat() {
        return "";
    }

    @Override
    public String getCurrencySingular() {
        return "";
    }

    @Override
    public String getCurrencyPlural() {
        return "";
    }

    @Override
    public String format(double money) {
        return "";
    }

    @Override
    public boolean hasEnoughMoney(Player player, double amount) {
        return false;
    }

    @Override
    public void makeTransaction(Player sender, Player target, double amount) {

    }

    @Override
    public void setMoney(Player player, double amount) {

    }

    @Override
    public void removeMoney(Player player, double amount) {

    }

    @Override
    public void addMoney(Player player, double amount) {

    }

    @Override
    public void createAccount(Player player) {

    }

    @Override
    public boolean hasEnoughMoney(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public void makeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {

    }

    @Override
    public void setMoney(OfflinePlayer player, double amount) {

    }

    @Override
    public void removeMoney(OfflinePlayer player, double amount) {

    }

    @Override
    public void addMoney(OfflinePlayer player, double amount) {

    }

    @Override
    public void createAccount(OfflinePlayer player) {

    }

    @Override
    public void deleteAccount(OfflinePlayer player) {

    }

    @Override
    public Double getMoneyAsNumber(Player player) {
        return 0.0;
    }

    @Override
    public String getMoney(Player player) {
        return "";
    }

    @Override
    public Double getMoneyAsNumber(OfflinePlayer player) {
        return 0.0;
    }

    @Override
    public String getMoney(OfflinePlayer player) {
        return "";
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> getTopTen() {

        return new LinkedHashMap<>();
    }

    @Override
    public void fetchTopTen() {

    }

    @Override
    public void close() {

    }
}
