package me.testaccount666.serversystem.userdata.money;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class AbstractBankAccount {
    protected final UUID owner;
    protected final BigInteger accountId;

    public AbstractBankAccount(UUID owner, BigInteger accountId) {
        this.owner = owner;
        this.accountId = accountId;
    }

    public abstract BigDecimal getBalance();

    public abstract void setBalance(BigDecimal balance);

    public abstract void delete();

    public abstract void save();

    public void deposit(BigDecimal amount) {
        setBalance(getBalance().add(amount));
    }

    public void withdraw(BigDecimal amount) {
        setBalance(getBalance().subtract(amount));
    }

    public void transfer(BigDecimal amount, AbstractBankAccount targetAccount) {
        withdraw(amount);
        targetAccount.deposit(amount);
    }

    public boolean hasEnoughMoney(BigDecimal amount) {
        return getBalance().compareTo(amount) >= 0;
    }

    public abstract Map<UUID, BigDecimal> getTopTen();
}
