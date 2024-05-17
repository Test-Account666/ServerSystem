package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

public abstract class AbstractEconomyManager {
    protected final ServerSystem _plugin;
    protected final String _currencySingular;
    protected final String _currencyPlural;
    protected final String _startingMoney;
    protected final String _displayFormat;
    protected final String _moneyFormat;
    protected final String _separator;
    protected final String _thousands;
    protected final HashMap<OfflinePlayer, Double> _moneyCache = new HashMap<>();
    protected final HashMap<OfflinePlayer, Boolean> _accountCache = new HashMap<>();
    protected LinkedHashMap<OfflinePlayer, Double> _topTen = new LinkedHashMap<>();

    public AbstractEconomyManager(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat,
                                  String separator, String thousands, ServerSystem plugin) {
        this._plugin = plugin;
        this._currencySingular = currencySingular;
        this._currencyPlural = currencyPlural;
        this._startingMoney = startingMoney;
        this._displayFormat = displayFormat;
        this._moneyFormat = moneyFormat;
        this._separator = separator;
        this._thousands = thousands;
    }

    public final boolean HasEnoughMoney(Player player, double amount) {
        return this.HasEnoughMoney((OfflinePlayer) player, amount);
    }

    public boolean HasEnoughMoney(OfflinePlayer offlinePlayer, double amount) {
        return this.GetMoneyAsNumber(offlinePlayer) >= amount;
    }

    public abstract Double GetMoneyAsNumber(OfflinePlayer offlinePlayer);

    public final Double GetMoneyAsNumber(Player player) {
        return this.GetMoneyAsNumber((OfflinePlayer) player);
    }

    public final String GetMoney(Player player) {
        return this.GetMoney((OfflinePlayer) player);
    }

    public final String GetMoney(OfflinePlayer offlinePlayer) {
        return this.Format(this.GetMoneyAsNumber(offlinePlayer));
    }

    public final String Format(double money) {
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

    public final String GetFormattedMoney(String moneyStr) {
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

    public final String GetThousands() {
        return this._thousands;
    }

    public final void MakeTransaction(Player sender, Player target, double amount) {
        this.MakeTransaction((OfflinePlayer) sender, (OfflinePlayer) target, amount);
    }

    public final void MakeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount) {
        if (sender == null)
            return;
        if (target == null)
            return;

        this.RemoveMoney(sender, amount);
        this.AddMoney(target, amount);
    }

    public final void RemoveMoney(OfflinePlayer offlinePlayer, double amount) {
        var newBalance = this.GetMoneyAsNumber(offlinePlayer) - amount;

        newBalance = this.Clamp(newBalance);

        this.SetMoney(offlinePlayer, newBalance);
    }

    public final void AddMoney(OfflinePlayer offlinePlayer, double amount) {
        var newBalance = this.GetMoneyAsNumber(offlinePlayer) + amount;

        newBalance = this.Clamp(newBalance);

        this.SetMoney(offlinePlayer, newBalance);
    }

    private double Clamp(double value) {
        if (value < 0.0)
            value = 0.0;

        if (value > Double.MAX_VALUE)
            value = Double.MAX_VALUE;

        return value;
    }

    public abstract void SetMoney(OfflinePlayer offlinePlayer, double amount);

    public final void SetMoney(Player player, double amount) {
        this.SetMoney((OfflinePlayer) player, amount);
    }

    public final void RemoveMoney(Player player, double amount) {
        this.RemoveMoney((OfflinePlayer) player, amount);
    }

    public final void AddMoney(Player player, double amount) {
        this.AddMoney((OfflinePlayer) player, amount);
    }

    public final void CreateAccount(Player player) {
        this.CreateAccount((OfflinePlayer) player);
    }

    public abstract void CreateAccount(OfflinePlayer offlinePlayer);

    public abstract void DeleteAccount(OfflinePlayer offlinePlayer);

    public abstract boolean HasAccount(OfflinePlayer offlinePlayer);

    public abstract void FetchTopTen();

    public abstract void Close();

    public final ServerSystem GetPlugin() {
        return this._plugin;
    }

    public final String GetMoneyFormat() {
        return this._moneyFormat;
    }

    public final String GetSeparator() {
        return this._separator;
    }

    public final String GetStartingMoney() {
        return this._startingMoney;
    }

    public final String GetDisplayFormat() {
        return this._displayFormat;
    }

    public final String GetCurrencySingular() {
        return this._currencySingular;
    }

    public final String GetCurrencyPlural() {
        return this._currencyPlural;
    }

    public LinkedHashMap<OfflinePlayer, Double> GetTopTen() {
        return this._topTen;
    }
}
