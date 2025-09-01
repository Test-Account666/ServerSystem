package me.testaccount666.serversystem.userdata.money;

import lombok.Getter;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.database.economy.AbstractEconomyDatabaseManager;
import me.testaccount666.serversystem.userdata.OfflineUser;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.logging.Level;

public class EconomyProvider {
    @Getter
    private final String _currencySingular;
    @Getter
    private final String _currencyPlural;
    private final String _thousandSeparator;
    private final String _decimalSeparator;
    private final String _moneyFormat;
    @Getter
    private final String _defaultBalance;
    @Getter
    private final Type _economyType;

    public EconomyProvider(ConfigReader configReader) {
        _currencySingular = configReader.getString("Economy.Format.CurrencySymbol.Singular");
        _currencyPlural = configReader.getString("Economy.Format.CurrencySymbol.Plural");
        _thousandSeparator = configReader.getString("Economy.Format.Separators.Thousands");
        _decimalSeparator = configReader.getString("Economy.Format.Separators.Decimals");
        _moneyFormat = configReader.getString("Economy.Format.MoneyFormat");
        _defaultBalance = configReader.getString("Economy.StartingBalance");

        if (!configReader.getBoolean("Economy.Enabled", true)) {
            _economyType = Type.DISABLED;
            return;
        }

        var economyTypeOptional = Type.parseType(configReader.getString("Economy.StorageType.Value").toUpperCase());
        var databaseManager = ServerSystem.Instance.getRegistry().getService(AbstractEconomyDatabaseManager.class);

        if (economyTypeOptional.isEmpty()) {
            _economyType = Type.SQLITE;
            ServerSystem.getLog().warning("Found invalid economy type in the 'economy.yml'! Using SQLITE as default!");
            return;
        }

        _economyType = economyTypeOptional.get();
        databaseManager.initialize();
    }

    public AbstractBankAccount instantiateBankAccount(OfflineUser offlineUser, BigInteger accountId, FileConfiguration userConfig) {
        if (_economyType == Type.DISABLED) return new DisabledBankAccount(offlineUser.getUuid(), accountId);
        migrateYamlBankAccountIfNeeded(offlineUser, accountId, userConfig);

        return switch (_economyType) {
            case MYSQL -> new MySqlBankAccount(offlineUser.getUuid(), accountId);
            case SQLITE -> new SqliteBankAccount(offlineUser.getUuid(), accountId);
            default -> {
                ServerSystem.getLog().warning("Found invalid economy type '${_economyType}' in the 'economy.yml'! Using Sqlite as default!");
                yield new SqliteBankAccount(offlineUser.getUuid(), accountId);
            }
        };
    }

    /**
     * Checks if a user has YAML bank account data and migrates it to the new SQLite database if needed.
     *
     * @param offlineUser The offline user
     * @param accountId   The bank account ID being instantiated
     * @param userConfig  The user's configuration
     */
    private void migrateYamlBankAccountIfNeeded(OfflineUser offlineUser, BigInteger accountId, FileConfiguration userConfig) {
        if (!userConfig.isSet("User.BankAccounts")) return;

        var bankAccountsSection = userConfig.getConfigurationSection("User.BankAccounts");
        if (bankAccountsSection == null) {
            ServerSystem.getLog().severe("Failed to get YAML bank accounts for user ${offlineUser.getName()} (${offlineUser.getUuid()}): bankAccountsSection is null!");
            return;
        }

        var anyMigrated = false;

        for (var key : bankAccountsSection.getKeys(false)) {
            var balance = bankAccountsSection.getString("${key}.Balance");
            if (balance == null) {
                ServerSystem.getLog().severe("Failed to get YAML bank account balance for user ${offlineUser.getName()} (${offlineUser.getUuid()}, AccountID: ${key}): balance is null!");
                continue;
            }

            try {
                var currentAccountId = new BigInteger(key);

                var bankAccount = switch (_economyType) {
                    case MYSQL -> new MySqlBankAccount(offlineUser.getUuid(), currentAccountId);
                    case SQLITE -> new SqliteBankAccount(offlineUser.getUuid(), currentAccountId);
                    default -> throw new IllegalStateException("Unexpected economy type: ${_economyType} - Supported values: mysql, sqlite");
                };

                bankAccount.setBalance(new BigDecimal(balance));

                if (currentAccountId.equals(accountId)) anyMigrated = true;

                ServerSystem.getLog().info("Migrated YAML bank account data for user ${offlineUser.getName()} (${offlineUser.getUuid()}, AccountID: ${currentAccountId}) to ${_economyType} database. Balance: ${balance}");
            } catch (NumberFormatException exception) {
                ServerSystem.getLog().log(Level.SEVERE, "Failed to migrate YAML bank account data for user ${offlineUser.getName()} (${offlineUser.getUuid()}, AccountID: ${key}): ${exception.getMessage()}", exception);
            }
        }

        if (!anyMigrated) return;
        userConfig.set("User.BankAccounts", null);
        offlineUser.save();

        ServerSystem.getLog().info("Completed migration of all YAML bank accounts for user ${offlineUser.getName()} (${offlineUser.getUuid()})");
    }

    public String formatMoney(BigDecimal balance) {
        var balanceString = balance.toPlainString();

        if (!balanceString.contains(".")) balanceString += ".00";

        var major = balanceString.split("\\.")[0];
        var decimal = balanceString.split("\\.")[1];

        major = major.replaceAll("(\\d)(?=(\\d\\d\\d)+(?!\\d))", "$1" + _thousandSeparator);

        if (decimal.length() > 2) decimal = decimal.substring(0, 2);
        if (decimal.length() == 1) decimal += "0";
        if (decimal.isEmpty()) decimal = "00";

        var currencySymbol = balance.compareTo(BigDecimal.ZERO) > 0? _currencySingular : _currencyPlural;

        return _moneyFormat.replace("<MAJOR>", major).replace("<DECIMAL>", decimal)
                .replace("<DECIMAL_SEPARATOR>", _decimalSeparator)
                .replace("<CURRENCY>", currencySymbol);
    }

    public enum Type {
        MYSQL,
        SQLITE,
        DISABLED;

        public static Optional<Type> parseType(String value) {
            if (value == null) return Optional.empty();

            try {
                return Optional.of(Type.valueOf(value.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }
    }
}
