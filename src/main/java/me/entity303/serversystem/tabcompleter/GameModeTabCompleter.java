package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GameModeTabCompleter implements ITabCompleterOverload {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (command.getName().equalsIgnoreCase("gamemode"))
            if (arguments.length == 1) {
                List<String> gamemodes = new ArrayList<>();
                gamemodes.add("0");
                gamemodes.add("1");
                gamemodes.add("2");
                gamemodes.add("3");
                gamemodes.add("s");
                gamemodes.add("c");
                gamemodes.add("a");
                gamemodes.add("sp");
                gamemodes.add("ü");
                gamemodes.add("k");
                gamemodes.add("z");
                gamemodes.add("survival");
                gamemodes.add("creative");
                gamemodes.add("adventure");
                gamemodes.add("spectator");
                gamemodes.add("überleben");
                gamemodes.add("kreativ");
                gamemodes.add("abenteuer");
                gamemodes.add("zuschauer");
                List<String> tabList = new ArrayList<>();
                for (var gamemode : gamemodes)
                    if (gamemode.toLowerCase().startsWith(arguments[0].toLowerCase()))
                        tabList.add(gamemode);
                return tabList.isEmpty()? gamemodes : tabList;
            }
        return null;
    }
}
