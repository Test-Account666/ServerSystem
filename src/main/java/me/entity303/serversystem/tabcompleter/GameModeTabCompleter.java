package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameModeTabCompleter implements ITabCompleterOverload {

    private static final String[] gameModes = { "0", "1", "2", "3", "s", "c", "a", "sp", "survival", "creative", "adventure", "spectator" };
    protected final ServerSystem _plugin;

    public GameModeTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!command.getName().equalsIgnoreCase("gamemode")) return null;

        if (arguments.length != 1) return null;

        var tabList = new ArrayList<String>();
        for (var gameMode : gameModes) {
            if (!gameMode.toLowerCase().startsWith(arguments[0].toLowerCase())) continue;

            tabList.add(gameMode);
        }
        return tabList.isEmpty()? Arrays.stream(gameModes).toList() : tabList;
    }
}
