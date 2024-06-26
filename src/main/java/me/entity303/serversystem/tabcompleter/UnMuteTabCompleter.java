package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnMuteTabCompleter implements ITabCompleterOverload {

    protected final ServerSystem _plugin;

    public UnMuteTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "unmute", true))
            return Collections.singletonList("");
        if (arguments.length == 1) {
            if (this._plugin.GetMuteManager().GetMutedPlayerNames().isEmpty())
                return Collections.singletonList("");
            var playerNameList = this._plugin.GetMuteManager().GetMutedPlayerNames();
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

    public ServerSystem GetPlugin() {
        return this._plugin;
    }
}
