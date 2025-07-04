package me.testaccount666.serversystem.userdata.money;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.OfflineUser;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A bank account implementation that stores data in a YAML file.
 * This class supports compression of the user file when saving,
 * using the same compression settings as OfflineUser.
 */
public class YamlBankAccount extends AbstractBankAccount {
    private final FileConfiguration _fileConfig;
    private final OfflineUser _owner;

    public YamlBankAccount(OfflineUser owner, BigInteger accountId, FileConfiguration userConfig) {
        super(owner.getUuid(), accountId);
        _owner = owner;
        _fileConfig = userConfig;
    }

    @Override
    public BigDecimal getBalance() {
        var balance = _fileConfig.getString("User.BankAccounts.${accountId.toString()}.Balance");
        if (balance == null) balance = ServerSystem.Instance.getEconomyManager().getDefaultBalance();

        return new BigDecimal(balance);
    }

    @Override
    public void setBalance(BigDecimal balance) {
        balance = balance.max(BigDecimal.ZERO);

        _fileConfig.set("User.BankAccounts.${accountId.toString()}.Balance", balance.toString());
        save(false);
    }

    public void save(boolean setBalance) {
        if (setBalance) _fileConfig.set("User.BankAccounts.${accountId.toString()}.Balance", getBalance().toString());

        _owner.save();
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
