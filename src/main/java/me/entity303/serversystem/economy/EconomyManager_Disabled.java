package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class EconomyManager_Disabled extends AbstractEconomyManager {

    public EconomyManager_Disabled(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat,
                                   String separator, String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
    }

    @Override
    public String Format(double money) {
        return "";
    }

    @Override
    public boolean HasEnoughMoney(Player player, double amount) {
        return false;
    }

    @Override
    public void MakeTransaction(Player sender, Player target, double amount) {

    }

    @Override
    public void SetMoney(Player player, double amount) {

    }

    @Override
    public void RemoveMoney(Player player, double amount) {

    }

    @Override
    public void AddMoney(Player player, double amount) {

    }

    @Override
    public void CreateAccount(Player player) {

    }

    @Override
    public boolean HasEnoughMoney(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public void MakeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {

    }

    @Override
    public void SetMoney(OfflinePlayer player, double amount) {

    }

    @Override
    public void RemoveMoney(OfflinePlayer player, double amount) {

    }

    @Override
    public void AddMoney(OfflinePlayer player, double amount) {

    }

    @Override
    public void CreateAccount(OfflinePlayer player) {

    }

    @Override
    public void DeleteAccount(OfflinePlayer player) {

    }

    @Override
    public Double GetMoneyAsNumber(Player player) {
        return 0.0;
    }

    @Override
    public String GetMoney(Player player) {
        return "";
    }

    @Override
    public Double GetMoneyAsNumber(OfflinePlayer player) {
        return 0.0;
    }

    @Override
    public String GetMoney(OfflinePlayer player) {
        return "";
    }

    @Override
    public boolean HasAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public void FetchTopTen() {

    }

    @Override
    public void Close() {

    }

    @Override
    public String GetMoneyFormat() {
        return "";
    }

    @Override
    public String GetSeparator() {
        return "";
    }

    @Override
    public String GetStartingMoney() {
        return "";
    }

    @Override
    public String GetDisplayFormat() {
        return "";
    }

    @Override
    public String GetCurrencySingular() {
        return "";
    }

    @Override
    public String GetCurrencyPlural() {
        return "";
    }

    @Override
    public LinkedHashMap<OfflinePlayer, Double> GetTopTen() {

        return new LinkedHashMap<>();
    }
}
