package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class DefaultTabCompleter extends MessageUtils implements TabCompleter {

    public DefaultTabCompleter(ss plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
