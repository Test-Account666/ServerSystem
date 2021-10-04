package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TABCOMPLETER_kit implements TabCompleter {
    private final ss plugin;

    public TABCOMPLETER_kit(ss plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> kits = new ArrayList<>(this.plugin.getKitsManager().getKitNames());
            List<String> tab = new ArrayList<>();
            for (String kit : kits) if (this.plugin.getKitsManager().isKitAllowed(cs, kit, false, true)) tab.add(kit);
            return tab;
        }
        return null;
    }
}
