package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnMuteTabCompleter extends CommandUtils implements TabCompleter {

    public UnMuteTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPermission(cs, "unmute", true))
            return Collections.singletonList("");
        if (args.length == 1) {
            if (this.getPlugin().getMuteManager().getMutedPlayerNames().isEmpty())
                return Collections.singletonList("");
            var playerNameList = this.getPlugin().getMuteManager().getMutedPlayerNames();
            if (playerNameList == null)
                return Collections.singletonList("");
            if (playerNameList.isEmpty())
                return Collections.singletonList("");
            List<String> tabList = new ArrayList<>();
            for (var playerName : playerNameList) {
                if (playerName == null)
                    continue;
                if (playerName.toLowerCase().startsWith(args[0].toLowerCase()) || playerName.equalsIgnoreCase(args[0]))
                    tabList.add(playerName);
            }
            return tabList.isEmpty()? playerNameList : tabList;
        }
        return Collections.singletonList("");
    }

    public ServerSystem getPlugin() {
        return this.plugin;
    }
}
