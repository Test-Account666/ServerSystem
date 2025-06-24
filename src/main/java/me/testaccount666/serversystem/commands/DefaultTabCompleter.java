package me.testaccount666.serversystem.commands;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultTabCompleter implements ServerSystemTabCompleter {

    @Override
    public @Nullable List<String> tabComplete(User commandSender, Command command, String label, String... arguments) {
        return null;
    }
}
