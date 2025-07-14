package me.testaccount666.serversystem.userdata.money;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Implementation of AbstractSqlBankAccount for SQLite databases.
 */
public class SqliteBankAccount extends AbstractSqlBankAccount {
    public SqliteBankAccount(UUID owner, BigInteger accountId) {
        super(owner, accountId);
    }
}