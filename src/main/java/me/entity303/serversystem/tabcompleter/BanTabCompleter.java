package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class BanTabCompleter extends CommandUtils implements ITabCompleterOverload {

    public BanTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.general", true))
            return Collections.singletonList("");
        if (arguments.length == 1)
            return null;
        if (arguments.length == 2)
            if (this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.permanent"))
                return Collections.singletonList(this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "PermanentName"));
            else
                return Collections.singletonList("");
        if (arguments.length == 3) {
            List<String> timeUnitList = new ArrayList<>();
            timeUnitList.add(YEAR_NAME);
            timeUnitList.add(MONTH_NAME);
            timeUnitList.add(WEEK_NAME);
            timeUnitList.add(DAY_NAME);
            timeUnitList.add(HOUR_NAME);
            timeUnitList.add(MINUTE_NAME);
            timeUnitList.add(SECOND_NAME);

            var tabList = timeUnitList.stream().filter(timeUnit -> timeUnit.toLowerCase().startsWith(arguments[2].toLowerCase())).collect(Collectors.toList());
            return tabList.isEmpty()? timeUnitList : tabList;
        }
        return null;
    }
}
