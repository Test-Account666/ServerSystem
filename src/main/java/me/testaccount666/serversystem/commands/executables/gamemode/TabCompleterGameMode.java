package me.testaccount666.serversystem.commands.executables.gamemode;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.managers.globaldata.MappingsData;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabCompleterGameMode implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), "Commands.GameMode.Use", false)) return Optional.empty();

        if (command.getName().equalsIgnoreCase("gamemode")) return handleGameModeCommand(arguments);

        return arguments.length == 1? Optional.empty() : Optional.of(List.of());
    }

    private Optional<List<String>> handleGameModeCommand(String[] arguments) {
        if (arguments.length == 1) {
            var possibleCompletions = new ArrayList<>(List.of("0", "1", "2", "3"));
            possibleCompletions.addAll(MappingsData.GameMode().getGameModeNames());

            var completions = new ArrayList<String>();

            for (var possibleCompletion : possibleCompletions) {
                if (!possibleCompletion.toLowerCase().startsWith(arguments[0].toLowerCase())) continue;

                completions.add(possibleCompletion);
            }

            return Optional.of(completions.isEmpty()? possibleCompletions : completions);
        }

        if (arguments.length == 2) return Optional.empty();

        return Optional.of(List.of());
    }
}
