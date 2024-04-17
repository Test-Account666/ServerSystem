package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class KitTabCompleter implements TabCompleter {
    private final ServerSystem plugin;

    public KitTabCompleter(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> kits = new ArrayList<>(this.plugin.getKitsManager().getKitNames());
            List<String> tab = new ArrayList<>();
            for (var kit : kits)
                if (this.plugin.getKitsManager().isKitAllowed(cs, kit, false, true))
                    tab.add(kit);
            return tab;
        }
        return null;
    }
}
