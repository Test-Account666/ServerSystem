package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnBanTabCompleter implements ITabCompleterOverload {

    protected final ServerSystem _plugin;

    public UnBanTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "unban", true))
            return Collections.singletonList("");
        if (arguments.length == 1) {
            if (this._plugin.GetBanManager().GetBannedPlayerNames().isEmpty())
                return Collections.singletonList("");
            var playerNameList = this._plugin.GetBanManager().GetBannedPlayerNames();
            if (playerNameList == null)
                return Collections.singletonList("");
            if (playerNameList.isEmpty())
                return Collections.singletonList("");
            List<String> tabList = new ArrayList<>();
            for (var playerName : playerNameList) {
                if (playerName == null)
                    continue;
                if (playerName.toLowerCase().startsWith(arguments[0].toLowerCase()) || playerName.equalsIgnoreCase(arguments[0]))
                    tabList.add(playerName);
            }
            return tabList.isEmpty()? playerNameList : tabList;
        }
        return Collections.singletonList("");
    }

    private ServerSystem GetPlugin() {
        return this._plugin;
    }
}
