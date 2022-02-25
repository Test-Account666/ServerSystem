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

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class BanTabCompleter extends MessageUtils implements TabCompleter {

    public BanTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "ban.use.general", true)) return Collections.singletonList("");
        if (args.length == 1) return null;
        if (args.length == 2)
            if (this.isAllowed(cs, "ban.use.permanent")) return Collections.singletonList(this.getBanSystem("PermanentName"));
            else
                return Collections.singletonList("");
        if (args.length == 3) {
            List<String> timeUnitList = new ArrayList<>();
            timeUnitList.add(yearName);
            timeUnitList.add(monthName);
            timeUnitList.add(weekName);
            timeUnitList.add(dayName);
            timeUnitList.add(hourName);
            timeUnitList.add(minuteName);
            timeUnitList.add(secondName);

            List<String> tabList = timeUnitList.stream().filter(timeUnit -> timeUnit.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
            return tabList.isEmpty() ? timeUnitList : tabList;
        }
        return null;
    }
}
