package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class DefaultTabCompleter extends CommandUtils implements TabCompleter {

    public DefaultTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
