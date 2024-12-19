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
    public static final String SUCCESS = "Success!";
    private final ServerSystem _plugin;

    public AbstractServerSystemEconomy(ServerSystem plugin) {
        this._plugin = plugin;
    }

    private OfflinePlayer GetPlayer(String username) {
        OfflinePlayer player = Bukkit.getPlayer(username);
        if (player == null) player = Bukkit.getOfflinePlayer(username);

        return player;
    }

    private String GetCurrencySymbol() {
        var localeUserProperty = System.getProperty("user.language");
        var locale = Locale.forLanguageTag(localeUserProperty);

        var currency = Currency.getInstance(locale);

        return currency.getSymbol();
    }

    @Override
    public boolean isEnabled() {
        return this._plugin.GetEconomyManager() != null;
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
    public String format(double amount) {
        if (!this.isEnabled()) {
            var locale = Locale.ROOT;

            var currency = Currency.getInstance(locale);

            var currencyFormat = NumberFormat.getCurrencyInstance(locale);
            return currencyFormat.format(amount) + currency.getSymbol();
        }

        return this._plugin.GetEconomyManager().Format(amount);
    }

    private ServerSystem GetPlugin() {
        return this._plugin;
    }

    @Override
    public String currencyNamePlural() {
        if (!this.isEnabled()) return this.GetCurrencySymbol();

        return this._plugin.GetEconomyManager().GetCurrencyPlural();
    }

    @Override
    public String currencyNameSingular() {
        if (!this.isEnabled()) return this.GetCurrencySymbol();

        return this._plugin.GetEconomyManager().GetCurrencySingular();
    }


    @Override
    public boolean hasAccount(String playerName) {
        return this.HasEconomyAccount(playerName);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return this.HasEconomyAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return this.GetMoney(playerName);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.GetMoney(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return this.HasMoney(playerName, amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.HasMoney(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return this.RemoveMoney(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.RemoveMoney(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return this.AddMoney(playerName, amount);
    }

    private EconomyResponse AddMoney(String username, double amount) {
        if (!this.isEnabled()) return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, "EconomyManager hasn't loaded, yet?!");

        var player = this.GetPlayer(username);
        var economyManager = this.GetPlugin().GetEconomyManager();
        if (!economyManager.HasAccount(player)) return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + username);

        economyManager.AddMoney(player, amount);
        var moneyAsNumber = economyManager.GetMoneyAsNumber(player);
        return new EconomyResponse(amount, moneyAsNumber, EconomyResponse.ResponseType.SUCCESS, SUCCESS);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.AddMoney(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return this.CreateAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return this.CreateAccount(playerName);
    }

    private boolean CreateAccount(String username) {
        if (!this.isEnabled()) return false;

        var player = this.GetPlayer(username);
        this._plugin.GetEconomyManager().CreateAccount(player);
        return true;
    }

    private EconomyResponse RemoveMoney(String username, double amount) {
        if (!this.isEnabled()) return new EconomyResponse(amount, -1, EconomyResponse.ResponseType.FAILURE, "EconomyManager hasn't loaded, yet?!");

        var player = this.GetPlayer(username);
        var economyManager = this._plugin.GetEconomyManager();
        if (!economyManager.HasAccount(player)) return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + username);

        if (!economyManager.HasEnoughMoney(player, amount)) {
            return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Player " + username + " does not have enough money");
        }

        economyManager.RemoveMoney(player, amount);

        var moneyAsNumber = economyManager.GetMoneyAsNumber(player);
        return new EconomyResponse(amount, moneyAsNumber, EconomyResponse.ResponseType.SUCCESS, SUCCESS);
    }

    private boolean HasMoney(String username, double amount) {
        if (!this.isEnabled()) return false;

        var player = this.GetPlayer(username);
        if (!this._plugin.GetEconomyManager().HasAccount(player)) return false;

        return this._plugin.GetEconomyManager().HasEnoughMoney(player, amount);
    }

    private double GetMoney(String username) {
        if (!this.isEnabled()) return -1;

        var player = this.GetPlayer(username);
        if (!this._plugin.GetEconomyManager().HasAccount(player)) return 0.0;

        return this._plugin.GetEconomyManager().GetMoneyAsNumber(player);
    }

    private boolean HasEconomyAccount(String username) {
        if (!this.isEnabled()) return false;

        var player = this.GetPlayer(username);
        return this._plugin.GetEconomyManager().HasAccount(player);
    }


}
