package me.Entity303.ServerSystem.TabCompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class GameModeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("gamemode")) if (args.length == 1) {
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
            for (String gamemode : gamemodes)
                if (gamemode.toLowerCase().startsWith(args[0].toLowerCase())) tabList.add(gamemode);
            return tabList.isEmpty() ? gamemodes : tabList;
        }
        return null;
    }
}
