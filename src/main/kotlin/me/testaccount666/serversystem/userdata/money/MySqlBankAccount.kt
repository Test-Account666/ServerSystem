package me.testaccount666.serversystem.userdata.money

import java.math.BigInteger
import java.util.*

class MySqlBankAccount(owner: UUID, accountId: BigInteger) : AbstractSqlBankAccount(owner, accountId)