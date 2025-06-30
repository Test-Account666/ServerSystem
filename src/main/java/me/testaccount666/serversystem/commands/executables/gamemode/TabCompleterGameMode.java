package me.testaccount666.serversystem.commands.executables.gamemode;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.globaldata.MappingsData;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleterGameMode implements ServerSystemTabCompleter {

    @Override
    public @Nullable List<String> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("gamemode")) return handleGameModeCommand(arguments);

        return arguments.length == 1? null : List.of();
    }

    private @Nullable List<String> handleGameModeCommand(String[] arguments) {
        if (arguments.length == 1) {
            var possibleCompletions = new ArrayList<>(List.of("0", "1", "2", "3"));
            possibleCompletions.addAll(MappingsData.GameMode().getGameModeNames());

            var completions = new ArrayList<String>();

            for (var possibleCompletion : possibleCompletions) {
                if (!possibleCompletion.toLowerCase().startsWith(arguments[0].toLowerCase())) continue;

                completions.add(possibleCompletion);
            }

            return completions.isEmpty()? possibleCompletions : completions;
        }

        if (arguments.length == 2) return null;

        return List.of();
    }
}
