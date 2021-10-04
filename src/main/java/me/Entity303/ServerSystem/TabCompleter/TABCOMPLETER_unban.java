package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TABCOMPLETER_unban extends ServerSystemCommand implements TabCompleter {

    public TABCOMPLETER_unban(ss plugin) {
        super(plugin);
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "unban", true)) return Collections.singletonList("");
        if (args.length == 1) {
            if (this.getPlugin().getBanManager().getBannedPlayerNames().size() <= 0) return Collections.singletonList("");
            List<String> playerNameList = this.getPlugin().getBanManager().getBannedPlayerNames();
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
