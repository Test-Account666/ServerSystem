package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.userdata.OfflineUser;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;

import java.io.File;
import java.math.BigInteger;
import java.util.Optional;

public class EconomyManager {
    private final String _currencySingular;
    private final String _currencyPlural;
    private final String _thousandSeparator;
    private final String _decimalSeparator;
    private final String _moneyFormat;
    private final String _defaultBalance;
    private final Type _economyType;

    public EconomyManager(ConfigReader configReader) {
        _currencySingular = configReader.getString("Economy.Format.CurrencySymbol.Singular");
        _currencyPlural = configReader.getString("Economy.Format.CurrencySymbol.Plural");
        _thousandSeparator = configReader.getString("Economy.Format.Separators.ThousandSeparator");
        _decimalSeparator = configReader.getString("Economy.Format.Separators.DecimalSeparator");
        _moneyFormat = configReader.getString("Economy.Format.MoneyFormat");
        _defaultBalance = configReader.getString("Economy.StartingBalance");
        var economyTypeOptional = Type.parseType(configReader.getString("Economy.StorageType.Value").toUpperCase());

        if (economyTypeOptional.isEmpty()) {
            _economyType = Type.YAML;
            Bukkit.getLogger().warning("Found invalid economy type in the 'economy.yml'! Using YAML as default!");
            return;
        }

        _economyType = economyTypeOptional.get();
    }

    public AbstractBankAccount instantiateBankAccount(OfflineUser offlineUser, BigInteger accountId, File userFile) {
        return switch (_economyType) {
            case YAML -> new YamlBankAccount(offlineUser.getUuid(), accountId, userFile);
            //TODO: Implement MySQL
            case MYSQL -> throw new NotImplementedException("MySQL is not yet implemented for bank accounts! Please use YAML for now.");
            default -> new DisabledBankAccount(offlineUser.getUuid(), accountId);
        };
    }

    public String getCurrencySingular() {
        return _currencySingular;
    }

    public String getCurrencyPlural() {
        return _currencyPlural;
    }

    public String getThousandSeparator() {
        return _thousandSeparator;
    }

    public String getDecimalSeparator() {
        return _decimalSeparator;
    }

    public String getMoneyFormat() {
        return _moneyFormat;
    }

    public String getDefaultBalance() {
        return _defaultBalance;
    }

    public Type getEconomyType() {
        return _economyType;
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
