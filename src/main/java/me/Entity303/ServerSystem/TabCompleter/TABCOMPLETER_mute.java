package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.Entity303.ServerSystem.BanSystem.TimeUnit.*;

public class TABCOMPLETER_mute extends ServerSystemCommand implements TabCompleter {

    public TABCOMPLETER_mute(ss plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "mute.use", true)) return Collections.singletonList("");
        if (args.length == 2)
            if (this.isAllowed(cs, "mute.permanent", true)) return Collections.singletonList(this.getBanSystem("PermanentName"));
        if (args.length == 3) {
            List<String> timeUnitList = new ArrayList<>();
            if (this.isAllowed(cs, "mute.temporary", true)) {
                timeUnitList.add(yearName);
                timeUnitList.add(monthName);
                timeUnitList.add(weekName);
                timeUnitList.add(dayName);
                timeUnitList.add(hourName);
                timeUnitList.add(minuteName);
                timeUnitList.add(secondName);
            }
            if (this.isAllowed(cs, "mute.shadow.permanent", true)) timeUnitList.add(this.getBanSystem("ShadowBan"));
            List<String> tabList = timeUnitList.stream().filter(timeUnit -> timeUnit.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
            return tabList.isEmpty() ? timeUnitList : tabList;
        }

        if (args.length == 4) if (this.isAllowed(cs, "mute.shadow.temporary", true))
            return Collections.singletonList(this.getBanSystem("ShadowBan"));
        return null;
    }
}
