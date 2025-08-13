package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

public class ConsoleBankAccount extends AbstractBankAccount {
    private final AbstractBankAccount _topTenFetcher;

    public ConsoleBankAccount() {
        super(ConsoleUser.CONSOLE_UUID, BigInteger.valueOf(-1L));

        var economyProvider = ServerSystem.Instance.getRegistry().getService(EconomyProvider.class);
        var economyType = economyProvider.getEconomyType();

        _topTenFetcher = switch (economyType) {
            case MYSQL -> new MySqlBankAccount(getOwner(), getAccountId());
            case SQLITE -> new SqliteBankAccount(getOwner(), getAccountId());
            default -> new DisabledBankAccount(getOwner(), getAccountId());
        };
    }

    @Override
    public BigDecimal getBalance() {
        return BigDecimal.valueOf(Double.MAX_VALUE);
    }

    @Override
    public void setBalance(BigDecimal balance) {
    }

    @Override
    public boolean hasEnoughMoney(BigDecimal amount) {
        return true;
    }

    @Override
    public void delete() {
    }

    @Override
    public void save() {
    }

    @Override
    public Map<UUID, BigDecimal> getTopTen() {
        return _topTenFetcher.getTopTen();
    }
}
