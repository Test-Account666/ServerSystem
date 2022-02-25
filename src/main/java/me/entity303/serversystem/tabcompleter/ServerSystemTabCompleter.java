package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerSystemTabCompleter extends MessageUtils implements TabCompleter {

    public ServerSystemTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "serversystem.use", true)) return Collections.singletonList("");
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (this.isAllowed(cs, "serversystem.version", true)) options.add("version");
            if (this.isAllowed(cs, "serversystem.reload", true)) options.add("reload");
            if (this.isAllowed(cs, "serversystem.update", true)) options.add("update");
            List<String> tab = new ArrayList<>();
            if (options.size() > 0) for (String option : options)
                if (option.toLowerCase().startsWith(args[0].toLowerCase())) tab.add(option);
            return tab.size() > 0 ? tab : options;
        }
        return Collections.singletonList("");
    }
}
