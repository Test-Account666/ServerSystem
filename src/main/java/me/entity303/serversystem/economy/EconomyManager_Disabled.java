package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

public class EconomyManager_Disabled extends AbstractEconomyManager {

    public EconomyManager_Disabled(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat,
                                   String separator, String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
    }

    @Override
    public boolean HasEnoughMoney(OfflinePlayer offlinePlayer, double amount) {
        return false;
    }

    @Override
    public Double GetMoneyAsNumber(OfflinePlayer offlinePlayer) {
        return 0.0;
    }

    @Override
    public void SetMoney(OfflinePlayer offlinePlayer, double amount) {

    }

    @Override
    public void CreateAccount(OfflinePlayer offlinePlayer) {

    }

    @Override
    public void DeleteAccount(OfflinePlayer offlinePlayer) {

    }

    @Override
    public boolean HasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public void FetchTopTen() {

    }

    @Override
    public void Close() {

    }
}
