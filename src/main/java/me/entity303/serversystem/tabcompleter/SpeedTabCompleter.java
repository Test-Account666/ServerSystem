package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpeedTabCompleter extends MessageUtils implements TabCompleter {

    public SpeedTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "speed.general", true)) return Collections.singletonList("");
        if (args.length == 1) if (this.isAllowed(cs, "speed.general", true)
                && (this.isAllowed(cs, "speed.self", true)
                || this.isAllowed(cs, "speed.others", true)))
            return IntStream.range(1, 11).mapToObj(String::valueOf).collect(Collectors.toList());

        if (args.length == 2) if (this.isAllowed(cs, "speed.general", true)
                && (this.isAllowed(cs, "speed.self", true)
                || this.isAllowed(cs, "speed.others", true))) {
            List<String> list = new ArrayList<>();
            List<String> tabList;
            if (this.isAllowed(cs, "speed.self", true)) {
                list.add("walk");
                list.add("laufen");
                list.add("walking");
                list.add("lauf");
                list.add("run");
                list.add("running");
                list.add("gehen");

                list.add("fly");
                list.add("flying");
                list.add("flight");
                list.add("flug");
                list.add("fliegen");
            }

            if (this.isAllowed(cs, "speed.others", true)) return null;

            tabList = list.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());

            if (tabList.size() <= 0) return list;
            return tabList;
        }

        if (args.length == 3) {
            List<String> list = new ArrayList<>();
            List<String> tabList;
            if (this.isAllowed(cs, "speed.others", true)) {
                list.add("walk");
                list.add("laufen");
                list.add("walking");
                list.add("lauf");
                list.add("run");
                list.add("running");
                list.add("gehen");

                list.add("fly");
                list.add("flying");
                list.add("flight");
                list.add("flug");
                list.add("fliegen");
            }

            tabList = list.stream().filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());

            if (tabList.size() <= 0) return list;
            return tabList;
        }
        return Collections.singletonList("");
    }
}
