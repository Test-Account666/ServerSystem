package me.entity303.serversystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandExecutorOverload extends org.bukkit.command.CommandExecutor {

    @Override
    boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments);
}
