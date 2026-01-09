package me.testaccount666.serversystem.userdata.money

import java.math.BigInteger
import java.util.*

/**
 * Implementation of AbstractSqlBankAccount for SQLite databases.
 */
class SqliteBankAccount(owner: UUID, accountId: BigInteger) : AbstractSqlBankAccount(owner, accountId)