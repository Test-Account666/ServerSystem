package me.testaccount666.serversystem.userdata.money;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public class YamlBankAccount extends AbstractBankAccount {
    private final File _userFile;
    private final FileConfiguration _fileConfig;

    public YamlBankAccount(UUID owner, BigInteger accountId, File userFile) {
        super(owner, accountId);
        _userFile = userFile;
        _fileConfig = YamlConfiguration.loadConfiguration(userFile);
    }

    @Override
    public BigDecimal getBalance() {
        //TODO: Default balance
        var balance = _fileConfig.getString("User.BankAccounts.${accountId.toString()}.Balance", "");

        return new BigDecimal(balance);
    }

    @Override
    public void setBalance(BigDecimal balance) {
        _fileConfig.set("User.BankAccounts.${accountId.toString()}.Balance", balance.toString());
        save();
    }

    private void save() {
        try {
            _fileConfig.save(_userFile);
            _fileConfig.load(_userFile);
        } catch (IOException | InvalidConfigurationException exception) {
            throw new RuntimeException("Encountered error trying to save bank account '${accountId.toString()}' of user '${owner.toString()}'!", exception);
        }
    }

    @Override
    public void delete() {
        _fileConfig.set("User.BankAccounts.${accountId.toString()}", null);
        save();
    }
}
