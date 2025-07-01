package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.ServerSystem;
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
        var balance = _fileConfig.getString("User.BankAccounts.${accountId.toString()}.Balance");
        if (balance == null) balance = ServerSystem.Instance.getEconomyManager().getDefaultBalance();

        return new BigDecimal(balance);
    }

    @Override
    public void setBalance(BigDecimal balance) {
        _fileConfig.set("User.BankAccounts.${accountId.toString()}.Balance", balance.toString());
        save(false);
    }

    public void save(boolean setBalance) {
        if (setBalance) _fileConfig.set("User.BankAccounts.${accountId.toString()}.Balance", getBalance().toString());

        try {
            _fileConfig.save(_userFile);
        } catch (IOException exception) {
            throw new RuntimeException("Encountered error trying to save bank account '${accountId.toString()}' of user '${owner.toString()}'!", exception);
        }
    }

    @Override
    public void save() {
        save(true);
    }

    @Override
    public void delete() {
        _fileConfig.set("User.BankAccounts.${accountId.toString()}", null);
        save(false);
    }
}
