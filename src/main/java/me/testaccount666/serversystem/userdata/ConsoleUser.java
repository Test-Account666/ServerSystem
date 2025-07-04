package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.userdata.money.ConsoleBankAccount;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents the Console CommandSender as a User.
 * <p>
 * Has benefits like infinite money
 */
public class ConsoleUser extends User {
    // 00000000-0000-0000-0000-000000000000 is never a player, so let's just use that for the console
    public static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    protected ConsoleUser() {
        super(UserManager.USER_DATA_PATH.resolve("${CONSOLE_UUID}.yml.gz").toFile());
    }

    @Override
    protected void loadBasicData() {
        name = "Server";
        uuid = ConsoleUser.CONSOLE_UUID;
        bankAccount = new ConsoleBankAccount();
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of(name);
    }

    @Override
    public CommandSender getCommandSender() {
        return Bukkit.getConsoleSender();
    }
}
