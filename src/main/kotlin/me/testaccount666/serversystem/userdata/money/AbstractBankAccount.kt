package me.testaccount666.serversystem.userdata.money

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

abstract class AbstractBankAccount(val owner: UUID, val accountId: BigInteger) {
    abstract var balance: BigDecimal

    abstract fun delete()

    abstract fun save()

    fun deposit(amount: BigDecimal) {
        balance += amount
    }

    fun withdraw(amount: BigDecimal) {
        balance -= amount
    }

    fun transfer(amount: BigDecimal, targetAccount: AbstractBankAccount) {
        withdraw(amount)
        targetAccount.deposit(amount)
    }

    open fun hasEnoughMoney(amount: BigDecimal): Boolean = balance >= amount

    abstract val topTen: MutableMap<UUID, BigDecimal>
}