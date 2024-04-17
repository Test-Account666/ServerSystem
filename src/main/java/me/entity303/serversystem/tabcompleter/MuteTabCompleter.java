package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class MuteTabCompleter extends CommandUtils implements TabCompleter {

    public MuteTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPermission(cs, "mute.use", true))
            return Collections.singletonList("");
        if (args.length == 2)
            if (this.plugin.getPermissions().hasPermission(cs, "mute.permanent", true))
                return Collections.singletonList(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "PermanentName"));
        if (args.length == 3) {
            List<String> timeUnitList = new ArrayList<>();
            if (this.plugin.getPermissions().hasPermission(cs, "mute.temporary", true)) {
                timeUnitList.add(yearName);
                timeUnitList.add(monthName);
                timeUnitList.add(weekName);
                timeUnitList.add(dayName);
                timeUnitList.add(hourName);
                timeUnitList.add(minuteName);
                timeUnitList.add(secondName);
            }
            if (this.plugin.getPermissions().hasPermission(cs, "mute.shadow.permanent", true))
                timeUnitList.add(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ShadowBan"));
            var tabList = timeUnitList.stream().filter(timeUnit -> timeUnit.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
            return tabList.isEmpty()? timeUnitList : tabList;
        }

        if (args.length == 4)
            if (this.plugin.getPermissions().hasPermission(cs, "mute.shadow.temporary", true))
                return Collections.singletonList(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ShadowBan"));
        return null;
    }
}
