package me.testaccount666.serversystem.userdata;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.util.UUID;

public class ConsoleUser extends User {
    // 00000000-0000-0000-0000-000000000000 is never a player, so let's just use that for the console
    private static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    protected ConsoleUser() {
        super(Path.of("plugins", "ServerSystem", "UserData", "${CONSOLE_UUID}.yml").toFile());
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public CommandSender getCommandSender() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public String getName() {
        return "Console";
    }

    @SuppressWarnings("SuspiciousGetterSetter")
    @Override
    public UUID getUuid() {
        return CONSOLE_UUID;
    }
}
