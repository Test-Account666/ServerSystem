package me.testaccount666.serversystem.userdata.money;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public abstract class AbstractBankAccount {
    protected final UUID owner;
    protected final BigInteger accountId;

    public AbstractBankAccount(UUID owner, BigInteger accountId) {
        this.owner = owner;
        this.accountId = accountId;
    }

    public UUID getOwner() {
        return owner;
    }

    public BigInteger getAccountId() {
        return accountId;
    }

    public abstract BigDecimal getBalance();

    public abstract void setBalance(BigDecimal balance);

    public abstract void delete();

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
}
