package me.Entity303.ServerSystem.Vault;

import me.Entity303.ServerSystem.Main.ss;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class AbstractServerSystemEconomy extends AbstractEconomy {
    private final ss plugin;

    public AbstractServerSystemEconomy(ss plugin) {
        this.plugin = plugin;
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
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
        return 1;
    }

    @Override
    public String format(double v) {
        return this.getPlugin().getEconomyManager().format(v);
    }

    @Override
    public String currencyNamePlural() {
        return this.getPlugin().getEconomyManager().getCurrencyPlural();
    }

    @Override
    public String currencyNameSingular() {
        return this.getPlugin().getEconomyManager().getCurrencySingular();
    }

    @Override
    public boolean hasAccount(String s) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        return this.getPlugin().getEconomyManager().hasAccount(player);
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        return this.getPlugin().getEconomyManager().hasAccount(player);
    }

    @Override
    public double getBalance(String s) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player)) return 0.0;
        return this.getPlugin().getEconomyManager().getMoneyAsNumber(player);
    }

    @Override
    public double getBalance(String s, String s1) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player)) return 0.0;
        return this.getPlugin().getEconomyManager().getMoneyAsNumber(player);
    }

    @Override
    public boolean has(String s, double v) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player)) return false;
        return this.getPlugin().getEconomyManager().hasEnoughMoney(player, v);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player)) return false;
        return this.getPlugin().getEconomyManager().hasEnoughMoney(player, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return new EconomyResponse(v, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + s);
        if (!this.getPlugin().getEconomyManager().hasEnoughMoney(player, v))
            return new EconomyResponse(v, 0.0, EconomyResponse.ResponseType.FAILURE, "Player " + s + " does not have enough money");
        this.getPlugin().getEconomyManager().removeMoney(player, v);
        return new EconomyResponse(v, this.getPlugin().getEconomyManager().getMoneyAsNumber(player), EconomyResponse.ResponseType.SUCCESS, "Success!");
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return new EconomyResponse(v, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + s);
        if (!this.getPlugin().getEconomyManager().hasEnoughMoney(player, v))
            return new EconomyResponse(v, 0.0, EconomyResponse.ResponseType.FAILURE, "Player " + s + " does not have enough money");
        this.getPlugin().getEconomyManager().removeMoney(player, v);
        return new EconomyResponse(v, this.getPlugin().getEconomyManager().getMoneyAsNumber(player), EconomyResponse.ResponseType.SUCCESS, "Success!");
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return new EconomyResponse(v, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + s);
        this.getPlugin().getEconomyManager().addMoney(player, v);
        return new EconomyResponse(v, this.getPlugin().getEconomyManager().getMoneyAsNumber(player), EconomyResponse.ResponseType.SUCCESS, "Success!");
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        if (!this.getPlugin().getEconomyManager().hasAccount(player))
            return new EconomyResponse(v, 0.0, EconomyResponse.ResponseType.FAILURE, "No account found for player " + s);
        this.getPlugin().getEconomyManager().addMoney(player, v);
        return new EconomyResponse(v, this.getPlugin().getEconomyManager().getMoneyAsNumber(player), EconomyResponse.ResponseType.SUCCESS, "Success!");
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented!");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        this.getPlugin().getEconomyManager().createAccount(player);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        OfflinePlayer player = Bukkit.getPlayer(s);
        if (player == null) player = Bukkit.getOfflinePlayer(s);
        this.getPlugin().getEconomyManager().createAccount(player);
        return true;
    }
}
