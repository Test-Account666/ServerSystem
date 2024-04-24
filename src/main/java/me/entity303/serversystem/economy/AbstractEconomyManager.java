package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;

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

    public abstract String Format(double money);

    public abstract boolean HasEnoughMoney(Player player, double amount);

    public abstract void MakeTransaction(Player sender, Player target, double amount);

    public abstract void SetMoney(Player player, double amount);

    public abstract void RemoveMoney(Player player, double amount);

    public abstract void AddMoney(Player player, double amount);

    public abstract void CreateAccount(Player player);

    public abstract boolean HasEnoughMoney(OfflinePlayer player, double amount);

    public abstract void MakeTransaction(OfflinePlayer sender, OfflinePlayer target, double amount);

    public abstract void SetMoney(OfflinePlayer player, double amount);

    public abstract void RemoveMoney(OfflinePlayer player, double amount);

    public abstract void AddMoney(OfflinePlayer player, double amount);

    public abstract void CreateAccount(OfflinePlayer player);

    public abstract void DeleteAccount(OfflinePlayer player);

    public abstract Double GetMoneyAsNumber(Player player);

    ////////////////////////////////////

    public abstract String GetMoney(Player player);

    public abstract Double GetMoneyAsNumber(OfflinePlayer player);

    public abstract String GetMoney(OfflinePlayer player);

    public abstract boolean HasAccount(OfflinePlayer player);

    public abstract void FetchTopTen();

    public abstract void Close();


    ///////////////////////////////////

    public ServerSystem GetPlugin() {
        return this._plugin;
    }

    public abstract String GetMoneyFormat();

    public abstract String GetSeparator();

    public abstract String GetStartingMoney();

    public abstract String GetDisplayFormat();

    public abstract String GetCurrencySingular();

    public abstract String GetCurrencyPlural();

    public String GetThousands() {
        return this._thousands;
    }

    public abstract LinkedHashMap<OfflinePlayer, Double> GetTopTen();
}
