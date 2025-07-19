package me.testaccount666.serversystem.userdata.money.vault;

import me.testaccount666.serversystem.ServerSystem;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;

import java.math.BigDecimal;
import java.util.List;

public class VaultEconomyProvider extends AbstractEconomy {
    @Override
    public boolean isEnabled() {
        return ServerSystem.Instance.isEnabled();
    }

    @Override
    public String getName() {
        return ServerSystem.Instance.getName();
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
        return ServerSystem.Instance.getEconomyProvider().formatMoney(BigDecimal.valueOf(amount));
    }

    @Override
    public String currencyNamePlural() {
        return ServerSystem.Instance.getEconomyProvider().getCurrencyPlural();
    }

    @Override
    public String currencyNameSingular() {
        return ServerSystem.Instance.getEconomyProvider().getCurrencySingular();
    }

    @Override
    public boolean hasAccount(String name) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(name);
        return userOptional.isPresent();
    }

    @Override
    public boolean hasAccount(String name, String world) {
        return hasAccount(name);
    }

    @Override
    public double getBalance(String name) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(name);
        if (userOptional.isEmpty()) return 0;

        var user = userOptional.get();
        var offlineUser = user.getOfflineUser();
        var bankAccount = offlineUser.getBankAccount();

        return bankAccount.getBalance().doubleValue();
    }

    @Override
    public double getBalance(String name, String world) {
        return getBalance(name);
    }

    @Override
    public boolean has(String name, double amount) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(name);
        if (userOptional.isEmpty()) return false;

        var user = userOptional.get();
        var offlineUser = user.getOfflineUser();
        var bankAccount = offlineUser.getBankAccount();

        return bankAccount.hasEnoughMoney(BigDecimal.valueOf(amount));
    }

    @Override
    public boolean has(String name, String world, double amount) {
        return has(name, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, double amount) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(name);
        if (userOptional.isEmpty()) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User not found!");

        var user = userOptional.get();
        var offlineUser = user.getOfflineUser();
        var bankAccount = offlineUser.getBankAccount();

        bankAccount.withdraw(BigDecimal.valueOf(amount));

        var newBalance = bankAccount.getBalance();

        return new EconomyResponse(amount, newBalance.doubleValue(), EconomyResponse.ResponseType.SUCCESS, "Withdraw successful!");
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, String world, double amount) {
        return withdrawPlayer(name, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String name, double amount) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(name);
        if (userOptional.isEmpty()) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User not found!");

        var user = userOptional.get();
        var offlineUser = user.getOfflineUser();
        var bankAccount = offlineUser.getBankAccount();

        bankAccount.deposit(BigDecimal.valueOf(amount));

        var newBalance = bankAccount.getBalance();

        return new EconomyResponse(amount, newBalance.doubleValue(), EconomyResponse.ResponseType.SUCCESS, "Deposit successful!");
    }

    @Override
    public EconomyResponse depositPlayer(String name, String world, double amount) {
        return depositPlayer(name, amount);
    }

    @Override
    public EconomyResponse createBank(String owner, String id) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank creation is not supported!");
    }

    @Override
    public EconomyResponse deleteBank(String id) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank deletion is not supported!");
    }

    @Override
    public EconomyResponse bankBalance(String id) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank balance retrieval is not supported!");
    }

    @Override
    public EconomyResponse bankHas(String id, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank balance retrieval is not supported!");
    }

    @Override
    public EconomyResponse bankWithdraw(String id, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank withdrawal is not supported!");
    }

    @Override
    public EconomyResponse bankDeposit(String id, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank deposit is not supported!");
    }

    @Override
    public EconomyResponse isBankOwner(String id, String owner) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank owner retrieval is not supported!");
    }

    @Override
    public EconomyResponse isBankMember(String id, String member) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank member retrieval is not supported!");
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String name, String world) {
        return createPlayerAccount(name);
    }
}
