package me.testaccount666.serversystem.userdata.money

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.database.economy.EconomyDatabaseManager
import me.testaccount666.serversystem.userdata.OfflineUser
import org.bukkit.configuration.file.FileConfiguration
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Locale.getDefault
import java.util.logging.Level

class EconomyProvider {
    val currencySingular: String?
    val currencyPlural: String?
    private val _thousandSeparator: String?
    private val _decimalSeparator: String?
    private val _moneyFormat: String?
    val defaultBalance: String?
    val economyType: Type

    constructor(configReader: ConfigReader) {
        currencySingular = configReader.getString("Economy.Format.CurrencySymbol.Singular")
        currencyPlural = configReader.getString("Economy.Format.CurrencySymbol.Plural")
        _thousandSeparator = configReader.getString("Economy.Format.Separators.Thousands")
        _decimalSeparator = configReader.getString("Economy.Format.Separators.Decimals")
        _moneyFormat = configReader.getString("Economy.Format.MoneyFormat")
        defaultBalance = configReader.getString("Economy.StartingBalance")

        if (!configReader.getBoolean("Economy.Enabled")) {
            economyType = Type.DISABLED
            return
        }

        val economyTypeParsed =
            Type.parseType(configReader.getString("Economy.StorageType.Value")?.uppercase(getDefault()))
        val databaseManager = ServerSystem.instance.registry.getService<EconomyDatabaseManager>()

        if (economyTypeParsed == null) {
            economyType = Type.SQLITE
            ServerSystem.log.warning("Found invalid economy type in the 'economy.yml'! Using SQLITE as default!")
            return
        }

        economyType = economyTypeParsed
        databaseManager.initialize()
    }

    fun instantiateBankAccount(offlineUser: OfflineUser, accountId: BigInteger, userConfig: FileConfiguration): AbstractBankAccount {
        if (economyType == Type.DISABLED) return DisabledBankAccount(offlineUser.uuid, accountId)
        migrateYamlBankAccountIfNeeded(offlineUser, accountId, userConfig)

        return when (economyType) {
            Type.MYSQL -> MySqlBankAccount(offlineUser.uuid, accountId)
            Type.SQLITE -> SqliteBankAccount(offlineUser.uuid, accountId)
        }
    }

    /**
     * Checks if a user has YAML bank account data and migrates it to the new SQLite database if needed.
     *
     * @param offlineUser The offline user
     * @param accountId   The bank account ID being instantiated
     * @param userConfig  The user's configuration
     */
    private fun migrateYamlBankAccountIfNeeded(offlineUser: OfflineUser, accountId: BigInteger, userConfig: FileConfiguration) {
        if (!userConfig.isSet("User.BankAccounts")) return

        val bankAccountsSection = userConfig.getConfigurationSection("User.BankAccounts")
        if (bankAccountsSection == null) {
            ServerSystem.log.severe("Failed to get YAML bank accounts for user ${offlineUser.getNameOrNull()} (${offlineUser.uuid}): bankAccountsSection is null!")
            return
        }

        var anyMigrated = false

        for (key in bankAccountsSection.getKeys(false)) {
            val balance = bankAccountsSection.getString("${key}.Balance")
            if (balance == null) {
                ServerSystem.log.severe("Failed to get YAML bank account balance for user ${offlineUser.getNameOrNull()} (${offlineUser.uuid}, AccountID: ${key}): balance is null!")
                continue
            }

            try {
                val currentAccountId = BigInteger(key)

                val bankAccount = when (economyType) {
                    Type.MYSQL -> MySqlBankAccount(offlineUser.uuid, currentAccountId)
                    Type.SQLITE -> SqliteBankAccount(offlineUser.uuid, currentAccountId)
                    else -> throw IllegalStateException("Unexpected economy type: ${economyType} - Supported values: mysql, sqlite")
                }

                bankAccount.balance = BigDecimal(balance)

                if (currentAccountId == accountId) anyMigrated = true

                ServerSystem.log.info("Migrated YAML bank account data for user ${offlineUser.getNameOrNull()} (${offlineUser.uuid}, AccountID: ${currentAccountId}) to ${economyType} database. Balance: ${balance}")
            } catch (exception: NumberFormatException) {
                ServerSystem.log.log(
                    Level.SEVERE,
                    "Failed to migrate YAML bank account data for user ${offlineUser.getNameOrNull()} (${offlineUser.uuid}, AccountID: ${key}): ${exception.message}",
                    exception
                )
            }
        }

        if (!anyMigrated) return
        userConfig.set("User.BankAccounts", null)
        offlineUser.save()

        ServerSystem.log.info("Completed migration of all YAML bank accounts for user ${offlineUser.getNameOrNull()} (${offlineUser.uuid})")
    }

    fun formatMoney(balance: BigDecimal): String {
        var balanceString = balance.toPlainString()

        if (!balanceString.contains(".")) balanceString += ".00"

        var major = balanceString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        var decimal = balanceString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        @Suppress("ConvertToStringTemplate")
        major = major.replace("(\\d)(?=(\\d\\d\\d)+(?!\\d))".toRegex(), "$1" + _thousandSeparator)

        if (decimal.length > 2) decimal = decimal.substring(0, 2)
        if (decimal.length == 1) decimal += "0"
        if (decimal.isEmpty()) decimal = "00"

        val currencySymbol = (if (balance > BigDecimal.ZERO) currencySingular else currencyPlural)!!

        return _moneyFormat!!.replace("<MAJOR>", major).replace("<DECIMAL>", decimal)
            .replace("<DECIMAL_SEPARATOR>", _decimalSeparator!!)
            .replace("<CURRENCY>", currencySymbol)
    }

    enum class Type {
        MYSQL,
        SQLITE,
        DISABLED;

        companion object {
            fun parseType(value: String?): Type? {
                if (value == null) return null

                return try {
                    valueOf(value.uppercase(getDefault()))
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
        }
    }
}