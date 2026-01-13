@file:Suppress("FoldInitializerAndIfToElvis")

package me.testaccount666.serversystem.userdata.money.vault

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse
import java.math.BigDecimal

class VaultEconomyProvider : AbstractEconomy() {
    override fun isEnabled() = ServerSystem.instance.isEnabled

    override fun getName() = ServerSystem.instance.name

    override fun hasBankSupport() = false

    override fun fractionalDigits() = 2

    override fun format(amount: Double): String {
        val registry = ServerSystem.instance.registry
        val economyProvider = registry.getService<EconomyProvider>()
        return economyProvider.formatMoney(BigDecimal.valueOf(amount))
    }

    override fun currencyNamePlural(): String? {
        val registry = ServerSystem.instance.registry
        val economyProvider = registry.getService<EconomyProvider>()
        return economyProvider.currencyPlural
    }

    override fun currencyNameSingular(): String? {
        val registry = ServerSystem.instance.registry
        val economyProvider = registry.getService<EconomyProvider>()
        return economyProvider.currencySingular
    }

    @Deprecated("Deprecated in Vault")
    override fun hasAccount(name: String): Boolean {
        val registry = ServerSystem.instance.registry
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name)
        return user != null
    }

    @Deprecated("Deprecated in Vault")
    override fun hasAccount(name: String, world: String?) = hasAccount(name)

    @Deprecated("Deprecated in Vault")
    override fun getBalance(name: String): Double {
        val registry = ServerSystem.instance.registry
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name) ?: return 0.0

        val offlineUser = user.offlineUser
        val bankAccount = offlineUser.bankAccount

        return bankAccount.balance.toDouble()
    }

    @Deprecated("Deprecated in Vault")
    override fun getBalance(name: String, world: String?) = getBalance(name)

    @Deprecated("Deprecated in Vault")
    override fun has(name: String, amount: Double): Boolean {
        val registry = ServerSystem.instance.registry
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name) ?: return false

        val offlineUser = user.offlineUser
        val bankAccount = offlineUser.bankAccount

        return bankAccount.hasEnoughMoney(BigDecimal.valueOf(amount))
    }

    @Deprecated("Deprecated in Vault")
    override fun has(name: String, world: String?, amount: Double) = has(name, amount)

    @Deprecated("Deprecated in Vault")
    override fun withdrawPlayer(name: String, amount: Double): EconomyResponse {
        val registry = ServerSystem.instance.registry
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name)
        if (user == null) return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "User not found!")

        val offlineUser = user.offlineUser
        val bankAccount = offlineUser.bankAccount

        bankAccount.withdraw(BigDecimal.valueOf(amount))

        val newBalance = bankAccount.balance

        return EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "Withdraw successful!")
    }

    @Deprecated("Deprecated in Vault")
    override fun withdrawPlayer(name: String, world: String?, amount: Double) = withdrawPlayer(name, amount)

    @Deprecated("Deprecated in Vault")
    override fun depositPlayer(name: String, amount: Double): EconomyResponse {
        val registry = ServerSystem.instance.registry
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name)
        if (user == null) return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "User not found!")

        val offlineUser = user.offlineUser
        val bankAccount = offlineUser.bankAccount

        bankAccount.deposit(BigDecimal.valueOf(amount))

        val newBalance = bankAccount.balance

        return EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "Deposit successful!")
    }

    @Deprecated("Deprecated in Vault")
    override fun depositPlayer(name: String, world: String?, amount: Double) = depositPlayer(name, amount)

    @Deprecated("Deprecated in Vault")
    override fun createBank(owner: String?, id: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank creation is not supported!")
    }

    override fun deleteBank(id: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank deletion is not supported!")
    }

    override fun bankBalance(id: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank balance retrieval is not supported!")
    }

    override fun bankHas(id: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank balance retrieval is not supported!")
    }

    override fun bankWithdraw(id: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank withdrawal is not supported!")
    }

    override fun bankDeposit(id: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank deposit is not supported!")
    }

    @Deprecated("Deprecated in Vault")
    override fun isBankOwner(id: String?, owner: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank owner retrieval is not supported!")
    }

    @Deprecated("Deprecated in Vault")
    override fun isBankMember(id: String?, member: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Bank member retrieval is not supported!")
    }

    override fun getBanks(): MutableList<String> = mutableListOf()

    @Deprecated("Deprecated in Vault")
    override fun createPlayerAccount(s: String?) = true

    @Deprecated("Deprecated in Vault")
    override fun createPlayerAccount(name: String?, world: String?) = createPlayerAccount(name)
}