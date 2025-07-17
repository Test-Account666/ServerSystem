package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.userdata.ConsoleUser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

public class ConsoleBankAccount extends AbstractBankAccount {
    public ConsoleBankAccount() {
        super(ConsoleUser.CONSOLE_UUID, BigInteger.valueOf(-1L));
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
        return Map.of();
    }
}
