package me.testaccount666.serversystem.userdata.money

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class DisabledBankAccount(owner: UUID, accountId: BigInteger) : AbstractBankAccount(owner, accountId) {
    override var balance: BigDecimal
        get() = BigDecimal.ZERO
        set(_) {}

    override fun delete() {
    }

    override fun save() {
    }

    override val topTen
        get() = mapOf<UUID, BigDecimal>()
}