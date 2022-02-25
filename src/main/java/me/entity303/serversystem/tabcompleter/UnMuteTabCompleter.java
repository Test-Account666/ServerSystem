package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnMuteTabCompleter extends MessageUtils implements TabCompleter {

    public UnMuteTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    public ServerSystem getPlugin() {
        return this.plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "unmute", true)) return Collections.singletonList("");
        if (args.length == 1) {
            if (this.getPlugin().getMuteManager().getMutedPlayerNames().size() <= 0) return Collections.singletonList("");
            List<String> playerNameList = this.getPlugin().getMuteManager().getMutedPlayerNames();
            if (playerNameList == null) return Collections.singletonList("");
            if (playerNameList.size() <= 0) return Collections.singletonList("");
            List<String> tabList = new ArrayList<>();
            for (String playerName : playerNameList) {
                if (playerName == null) continue;
                if (playerName.toLowerCase().startsWith(args[0].toLowerCase()) || playerName.equalsIgnoreCase(args[0]))
                    tabList.add(playerName);
            }
            return tabList.isEmpty() ? playerNameList : tabList;
        }
        return Collections.singletonList("");
    }
}
