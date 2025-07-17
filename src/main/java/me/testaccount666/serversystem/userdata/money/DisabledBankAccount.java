package me.testaccount666.serversystem.userdata.money;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

public class DisabledBankAccount extends AbstractBankAccount {

    public DisabledBankAccount(UUID owner, BigInteger accountId) {
        super(owner, accountId);
    }

    @Override
    public BigDecimal getBalance() {
        return new BigDecimal(0);
    }

    @Override
    public void setBalance(BigDecimal balance) {

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
