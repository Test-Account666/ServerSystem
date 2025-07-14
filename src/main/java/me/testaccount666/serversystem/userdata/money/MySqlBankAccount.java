package me.testaccount666.serversystem.userdata.money;

import java.math.BigInteger;
import java.util.UUID;

public class MySqlBankAccount extends AbstractSqlBankAccount {
    public MySqlBankAccount(UUID owner, BigInteger accountId) {
        super(owner, accountId);
    }
}
