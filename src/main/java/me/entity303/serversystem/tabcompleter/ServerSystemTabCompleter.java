package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerSystemTabCompleter extends CommandUtils implements TabCompleter {

    public ServerSystemTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPermission(cs, "serversystem.use", true))
            return Collections.singletonList("");
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (this.plugin.getPermissions().hasPermission(cs, "serversystem.version", true))
                options.add("version");
            if (this.plugin.getPermissions().hasPermission(cs, "serversystem.reload", true))
                options.add("reload");
            if (this.plugin.getPermissions().hasPermission(cs, "serversystem.update", true))
                options.add("update");
            List<String> tab = new ArrayList<>();
            if (!options.isEmpty())
                for (var option : options)
                    if (option.toLowerCase().startsWith(args[0].toLowerCase()))
                        tab.add(option);
            return !tab.isEmpty()? tab : options;
        }
        return Collections.singletonList("");
    }
}
