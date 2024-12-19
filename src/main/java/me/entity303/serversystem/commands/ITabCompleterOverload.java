package me.entity303.serversystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@SuppressWarnings({ "NullableProblems", "ParameterNameDiffersFromOverriddenParameter" })
public interface ITabCompleterOverload extends TabCompleter {
    @Override
    List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments);
}
