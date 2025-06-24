package me.testaccount666.serversystem.commands.interfaces;

import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ServerSystemTabCompleter {
    @Nullable List<String> tabComplete(User commandSender, Command command, String label, String... arguments);
}
