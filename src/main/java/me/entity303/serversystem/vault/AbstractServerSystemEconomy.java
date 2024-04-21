package me.entity303.serversystem.vault;

import me.entity303.serversystem.main.ServerSystem;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class AbstractServerSystemEconomy extends AbstractEconomy {
    private final ServerSystem plugin;

    public AbstractServerSystemEconomy(ServerSystem plugin) {
        this.plugin = plugin;
    }

    private OfflinePlayer getPlayer(String username) {
        OfflinePlayer player = Bukkit.getPlayer(username);
        if (player == null)
            player = Bukkit.getOfflinePlayer(username);

        return player;
    }

    private String getCurrencySymbol() {
        var locale = Locale.forLanguageTag(System.getProperty("user.language"));

        var currency = Currency.getInstance(locale);

        return currency.getSymbol();
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.getEconomyManager() != null;
    }

    @Override
    public String getName() {
        return "ServerSystem";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double balance) {
        if (!this.isEnabled()) {
            var locale = Locale.ROOT;

            var currency = Currency.getInstance(locale);

            var currencyFormat = NumberFormat.getCurrencyInstance(locale);
            return currencyFormat.format(balance) + currency.getSymbol();
        }

        return this.getPlugin().getEconomyManager().format(balance);
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }

    @Override
    public String currencyNamePlural() {
        if (!this.isEnabled())
            return this.getCurrencySymbol();

        return this.getPlugin().getEconomyManager().getCurrencyPlural();
    }

    @Override
    public String currencyNameSingular() {
        if (!this.isEnabled())
            return this.getCurrencySymbol();

        return this.getPlugin().getEconomyManager().getCurrencySingular();
    }


    @Override
    public boolean hasAccount(String username) {
        return this.hasEconomyAccount(username);
    }

    @Override
    public boolean hasAccount(String username, String worldName) {
        return this.hasEconomyAccount(username);
    }

    @Override
    public double getBalance(String username) {
        return this.getMoney(username);
    }

    @Override
    public double getBalance(String username, String worldName) {
        return this.getMoney(username);
    }

    @Override
    public boolean has(String username, double amount) {
        return this.hasMoney(username, amount);
    }

    @Override
    public boolean has(String username, String worldName, double amount) {
        return this.hasMoney(username, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String username, double amount) {
        return this.removeMoney(username, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String username, String worldName, double amount) {
        return this.removeMoney(username, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String username, double amount) {
        return this.addMoney(username, amount);
    }

    private EconomyResponse addMoney(String username, double amount) {
        if (!this.isEnabled())
            return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, "EconomyManager hasn't loaded, yet?!");

        var player = this.getPlayer(username);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + username);

        this.getPlugin().getEconomyManager().addMoney(player, amount);
        return new EconomyResponse(amount, this.getPlugin().getEconomyManager().getMoneyAsNumber(player), EconomyResponse.ResponseType.SUCCESS, "Success!");
    }

    @Override
    public EconomyResponse depositPlayer(String username, String worldName, double amount) {
        return this.addMoney(username, amount);
    }

    @Override
    public EconomyResponse createBank(String username, String worldName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse deleteBank(String username) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankBalance(String username) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankHas(String username, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankWithdraw(String username, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankDeposit(String username, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse isBankOwner(String username, String worldName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse isBankMember(String username, String worldName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String username) {
        return this.createAccount(username);
    }

    @Override
    public boolean createPlayerAccount(String username, String worldName) {
        return this.createAccount(username);
    }

    private boolean createAccount(String username) {
        if (!this.isEnabled())
            return false;

        var player = this.getPlayer(username);
        this.getPlugin().getEconomyManager().createAccount(player);
        return true;
    }

    private EconomyResponse removeMoney(String username, double amount) {
        if (!this.isEnabled())
            return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, "EconomyManager hasn't loaded, yet?!");

        var player = this.getPlayer(username);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + username);

        if (!this.getPlugin().getEconomyManager().hasEnoughMoney(player, amount))
            return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Player " + username + " does not have enough money");

        this.getPlugin().getEconomyManager().removeMoney(player, amount);

        return new EconomyResponse(amount, this.getPlugin().getEconomyManager().getMoneyAsNumber(player), EconomyResponse.ResponseType.SUCCESS, "Success!");
    }

    private boolean hasMoney(String username, double amount) {
        if (!this.isEnabled())
            return false;

        var player = this.getPlayer(username);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return false;

        return this.getPlugin().getEconomyManager().hasEnoughMoney(player, amount);
    }

    private double getMoney(String username) {
        if (!this.isEnabled())
            return -1;

        var player = this.getPlayer(username);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return 0.0;

        return this.getPlugin().getEconomyManager().getMoneyAsNumber(player);
    }

    private boolean hasEconomyAccount(String username) {
        if (!this.isEnabled())
            return false;

        var player = this.getPlayer(username);
        return this.getPlugin().getEconomyManager().hasAccount(player);
    }


}
