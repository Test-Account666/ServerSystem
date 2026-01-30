package me.testaccount666.serversystem.userdata.money.vault

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import net.milkbowl.vault.economy.AbstractEconomy
import net.milkbowl.vault.economy.EconomyResponse

class VaultEconomyProvider : AbstractEconomy() {
    private val registry
        get() = ServerSystem.instance.registry

    override fun isEnabled() = ServerSystem.instance.isEnabled

    override fun getName() = ServerSystem.instance.name

    override fun hasBankSupport() = false

    override fun fractionalDigits() = 2

    override fun format(amount: Double) = registry.getService<EconomyProvider>().formatMoney(amount.toBigDecimal())

    override fun currencyNamePlural() = registry.getService<EconomyProvider>().currencyPlural

    override fun currencyNameSingular() = registry.getService<EconomyProvider>().currencySingular

    @Deprecated("Deprecated in Vault")
    override fun hasAccount(name: String): Boolean {
        val userManager = registry.getService<UserManager>()
        return userManager.getUserOrNull(name) != null
    }

    @Deprecated("Deprecated in Vault")
    override fun hasAccount(name: String, world: String?) = hasAccount(name)

    @Deprecated("Deprecated in Vault")
    override fun getBalance(name: String): Double {
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
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name) ?: return false

        val offlineUser = user.offlineUser
        return offlineUser.bankAccount.balance <= amount.toBigDecimal()
    }

    @Deprecated("Deprecated in Vault")
    override fun has(name: String, world: String?, amount: Double) = has(name, amount)

    @Deprecated("Deprecated in Vault")
    override fun withdrawPlayer(name: String, amount: Double): EconomyResponse {
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name) ?: run {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "User not found!")
        }

        val offlineUser = user.offlineUser
        val bankAccount = offlineUser.bankAccount

        bankAccount.balance -= amount.toBigDecimal()
        return EconomyResponse(amount, bankAccount.balance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "Withdraw successful!")
    }

    @Deprecated("Deprecated in Vault")
    override fun withdrawPlayer(name: String, world: String?, amount: Double) = withdrawPlayer(name, amount)

    @Deprecated("Deprecated in Vault")
    override fun depositPlayer(name: String, amount: Double): EconomyResponse {
        val userManager = registry.getService<UserManager>()
        val user = userManager.getUserOrNull(name) ?: run {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "User not found!")
        }

        val offlineUser = user.offlineUser
        val bankAccount = offlineUser.bankAccount

        bankAccount.balance += amount.toBigDecimal()
        return EconomyResponse(amount, bankAccount.balance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "Deposit successful!")
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

    override fun getBanks() = listOf<String>()

    @Deprecated("Deprecated in Vault")
    override fun createPlayerAccount(s: String?) = true

    @Deprecated("Deprecated in Vault")
    override fun createPlayerAccount(name: String?, world: String?) = createPlayerAccount(name)
}