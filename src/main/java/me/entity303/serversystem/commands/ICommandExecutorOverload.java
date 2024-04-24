package me.entity303.serversystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings({ "NullableProblems", "ParameterNameDiffersFromOverriddenParameter" })
public interface ICommandExecutorOverload extends CommandExecutor {

    @Override
    boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments);
}
