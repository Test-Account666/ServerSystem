package me.testaccount666.serversystem.userdata.money

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.userdata.ConsoleUser
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class ConsoleBankAccount : AbstractBankAccount(ConsoleUser.Companion.CONSOLE_UUID, BigInteger.valueOf(-1L)) {
    private val _topTenFetcher: AbstractBankAccount

    init {
        val economyProvider = ServerSystem.Companion.instance.registry.getService<EconomyProvider>()
        val economyType = economyProvider.economyType

        _topTenFetcher = when (economyType) {
            EconomyProvider.Type.MYSQL -> MySqlBankAccount(owner, accountId)
            EconomyProvider.Type.SQLITE -> SqliteBankAccount(owner, accountId)
            else -> DisabledBankAccount(owner, accountId)
        }
    }

    override var balance: BigDecimal
        get() = BigDecimal.valueOf(Double.MAX_VALUE)
        set(_) {}

    override fun hasEnoughMoney(amount: BigDecimal): Boolean = true

    override fun delete() {}

    override fun save() {}

    override val topTen: MutableMap<UUID, BigDecimal>
        get() = _topTenFetcher.topTen
}