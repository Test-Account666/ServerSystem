package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.database.EconomyDatabaseManager;
import me.testaccount666.serversystem.userdata.OfflineUser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Optional;

public class EconomyProvider {
    private final String _currencySingular;
    private final String _currencyPlural;
    private final String _thousandSeparator;
    private final String _decimalSeparator;
    private final String _moneyFormat;
    private final String _defaultBalance;
    private final Type _economyType;
    private final EconomyDatabaseManager _databaseManager;

    public EconomyProvider(ConfigReader configReader, EconomyDatabaseManager databaseManager) {
        _currencySingular = configReader.getString("Economy.Format.CurrencySymbol.Singular");
        _currencyPlural = configReader.getString("Economy.Format.CurrencySymbol.Plural");
        _thousandSeparator = configReader.getString("Economy.Format.Separators.Thousands");
        _decimalSeparator = configReader.getString("Economy.Format.Separators.Decimals");
        _moneyFormat = configReader.getString("Economy.Format.MoneyFormat");
        _defaultBalance = configReader.getString("Economy.StartingBalance");
        _databaseManager = databaseManager;
        var economyTypeOptional = Type.parseType(configReader.getString("Economy.StorageType.Value").toUpperCase());

        if (economyTypeOptional.isEmpty()) {
            _economyType = Type.YAML;
            Bukkit.getLogger().warning("Found invalid economy type in the 'economy.yml'! Using YAML as default!");
            return;
        }

        _economyType = economyTypeOptional.get();

        if (_economyType == Type.MYSQL) _databaseManager.initialize();
    }

    public AbstractBankAccount instantiateBankAccount(OfflineUser offlineUser, BigInteger accountId, FileConfiguration userConfig) {
        return switch (_economyType) {
            case YAML -> new YamlBankAccount(offlineUser, accountId, userConfig);
            case MYSQL -> {
                try {
                    yield new MySqlBankAccount(offlineUser.getUuid(), accountId, _databaseManager.getConnection());
                } catch (SQLException exception) {
                    Bukkit.getLogger().severe("Failed to get database connection for bank account: ${offlineUser.getName()} (${offlineUser.getUuid()}, AccountID: ${accountId})");
                    exception.printStackTrace();
                    yield new DisabledBankAccount(offlineUser.getUuid(), accountId);
                }
            }
            default -> new DisabledBankAccount(offlineUser.getUuid(), accountId);
        };
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

    public String getDefaultBalance() {
        return _defaultBalance;
    }

    public Type getEconomyType() {
        return _economyType;
    }

    public String getCurrencySingular() {
        return _currencySingular;
    }

    public String getCurrencyPlural() {
        return _currencyPlural;
    }

    public enum Type {
        YAML,
        MYSQL,
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
