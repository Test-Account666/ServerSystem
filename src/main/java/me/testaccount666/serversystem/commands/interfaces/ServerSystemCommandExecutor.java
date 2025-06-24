package me.testaccount666.serversystem.commands.interfaces;

import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

public interface ServerSystemCommandExecutor {

    void execute(User commandSender, Command command, String label, String... arguments);
}
