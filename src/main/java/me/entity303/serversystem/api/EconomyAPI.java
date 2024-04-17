package me.entity303.serversystem.api;

import me.entity303.serversystem.economy.EconomyManager_Disabled;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;

public class EconomyAPI {
    private final ServerSystem plugin;

    public EconomyAPI(ServerSystem plugin) {
        this.plugin = plugin;
    }

    /**
     @param player = The player which's balance should be checked

     @return = How much balance the player has
     */
    public CompletableFuture<Double> getMoney(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> this.plugin.getEconomyManager().getMoneyAsNumber(player));
    }

    /**
     @param player = The player which's balance should be checked

     @return = How much balance the player has (formatted)
     */
    public CompletableFuture<String> getFormattedMoney(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> this.plugin.getEconomyManager().getMoney(player));
    }

    /**
     @param money = The money which should be formatted in a more understandable format

     @return = The formatted money
     */
    public String formatMoney(Double money) {
        return this.plugin.getEconomyManager().format(money);
    }

    /**
     @param playerPaying = The player which is paying
     @param playerReceiving = The player which is receiving the money
     @param amountToPay = The amount which is to paid by playerPaying
     */
    public void makeTransaction(OfflinePlayer playerPaying, OfflinePlayer playerReceiving, Double amountToPay) {
        this.plugin.getEconomyManager().makeTransaction(playerPaying, playerReceiving, amountToPay);
    }

    /**
     @param playerPaying = The player which should pay some money
     @param amountToPay = The amount playerPaying needs to pay

     @return = Returns if playerPaying has enough money
     */
    public CompletableFuture<Boolean> hasEnoughMoney(OfflinePlayer playerPaying, Double amountToPay) {
        return CompletableFuture.supplyAsync(() -> this.plugin.getEconomyManager().hasEnoughMoney(playerPaying, amountToPay));
    }

    /**
     @param player = The player which's balance you want to change
     @param money = The balance you want to change to
     */
    public void setMoney(OfflinePlayer player, Double money) {
        this.plugin.getEconomyManager().setMoney(player, money);
    }

    /**
     @param player = The player which's balance you want to change
     @param amount = The amount of money which should be removed from player's balance
     */
    public void removeMoney(OfflinePlayer player, Double amount) {
        this.plugin.getEconomyManager().removeMoney(player, amount);
    }

    /**
     @param player = The player which's balance you want to change
     @param amount = The amount of money which should be added from player's balance
     */
    public void addMoney(OfflinePlayer player, Double amount) {
        this.plugin.getEconomyManager().addMoney(player, amount);
    }

    /**
     @param player = The player who's account you want to check

     @return = Returns if player has an account
     */
    public CompletableFuture<Boolean> hasAccount(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> this.plugin.getEconomyManager().hasAccount(player));
    }

    /**
     @param player = The owner of the new account
     */
    public void createAccount(OfflinePlayer player) {
        this.plugin.getEconomyManager().createAccount(player);
    }

    /**
     @param player = The player which's account you want to delete
     */
    public void deleteAccount(OfflinePlayer player) {
        this.plugin.getEconomyManager().deleteAccount(player);
    }

    /**
     @return = Returns you the starting money
     */
    public Double getStartingMoney() {
        return Double.parseDouble(this.plugin.getEconomyManager().getStartingMoney());
    }

    /**
     @return = Returns you the top 10 of the richest players
     */
    public LinkedHashMap<OfflinePlayer, Double> getTopTen() {
        return new LinkedHashMap<>(this.plugin.getEconomyManager().getTopTen());
    }

    public boolean isEconomyEnabled() {
        return !(this.plugin.getEconomyManager() instanceof EconomyManager_Disabled);
    }
}
