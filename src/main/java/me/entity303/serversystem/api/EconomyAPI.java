package me.entity303.serversystem.api;

import me.entity303.serversystem.economy.EconomyManager_Disabled;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("NewMethodNamingConvention")
public class EconomyAPI {
    private final ServerSystem _plugin;

    public EconomyAPI(ServerSystem plugin) {
        this._plugin = plugin;
    }

    /**
     @param player = The player which's balance should be checked

     @return = How much balance the player has
     */
    public CompletableFuture<Double> getMoney(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> this._plugin.GetEconomyManager().GetMoneyAsNumber(player));
    }

    /**
     @param player = The player which's balance should be checked

     @return = How much balance the player has (formatted)
     */
    public CompletableFuture<String> getFormattedMoney(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> this._plugin.GetEconomyManager().GetMoney(player));
    }

    /**
     @param money = The money which should be formatted in a more understandable format

     @return = The formatted money
     */
    public String formatMoney(Double money) {
        return this._plugin.GetEconomyManager().Format(money);
    }

    /**
     @param playerPaying = The player which is paying
     @param playerReceiving = The player which is receiving the money
     @param amountToPay = The amount which is to paid by playerPaying
     */
    public void makeTransaction(OfflinePlayer playerPaying, OfflinePlayer playerReceiving, Double amountToPay) {
        this._plugin.GetEconomyManager().MakeTransaction(playerPaying, playerReceiving, amountToPay);
    }

    /**
     @param playerPaying = The player which should pay some money
     @param amountToPay = The amount playerPaying needs to pay

     @return = Returns if playerPaying has enough money
     */
    public CompletableFuture<Boolean> HasEnoughMoney(OfflinePlayer playerPaying, Double amountToPay) {
        return CompletableFuture.supplyAsync(() -> this._plugin.GetEconomyManager().HasEnoughMoney(playerPaying, amountToPay));
    }

    /**
     @param player = The player which's balance you want to change
     @param money = The balance you want to change to
     */
    public void setMoney(OfflinePlayer player, Double money) {
        this._plugin.GetEconomyManager().SetMoney(player, money);
    }

    /**
     @param player = The player which's balance you want to change
     @param amount = The amount of money which should be removed from player's balance
     */
    public void removeMoney(OfflinePlayer player, Double amount) {
        this._plugin.GetEconomyManager().RemoveMoney(player, amount);
    }

    /**
     @param player = The player which's balance you want to change
     @param amount = The amount of money which should be added from player's balance
     */
    public void addMoney(OfflinePlayer player, Double amount) {
        this._plugin.GetEconomyManager().AddMoney(player, amount);
    }

    /**
     @param player = The player who's account you want to check

     @return = Returns if player has an account
     */
    public CompletableFuture<Boolean> HasAccount(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> this._plugin.GetEconomyManager().HasAccount(player));
    }

    /**
     @param player = The owner of the new account
     */
    public void createAccount(OfflinePlayer player) {
        this._plugin.GetEconomyManager().CreateAccount(player);
    }

    /**
     @param player = The player which's account you want to delete
     */
    public void deleteAccount(OfflinePlayer player) {
        this._plugin.GetEconomyManager().DeleteAccount(player);
    }

    /**
     @return = Returns you the starting money
     */
    public Double getStartingMoney() {
        return Double.parseDouble(this._plugin.GetEconomyManager().GetStartingMoney());
    }

    /**
     @return = Returns you the top 10 of the richest players
     */
    public LinkedHashMap<OfflinePlayer, Double> getTopTen() {
        return new LinkedHashMap<>(this._plugin.GetEconomyManager().GetTopTen());
    }

    public boolean isEconomyEnabled() {
        return !(this._plugin.GetEconomyManager() instanceof EconomyManager_Disabled);
    }
}
