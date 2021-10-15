package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TABCOMPLETER_recipe extends MessageUtils implements TabCompleter {

    public TABCOMPLETER_recipe(ss plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.recipe.required"))
                if (!this.isAllowed(cs, "recipe.permission", true)) return Collections.singletonList("");

            List<String> tabCompletions = Arrays.stream(Material.values()).filter(material -> material.name().toLowerCase().startsWith(args[0].toLowerCase()) && !material.name().endsWith("AIR") && !material.name().startsWith("LEGACY")).map(Enum::name).collect(Collectors.toList());

            if (tabCompletions.isEmpty()) {
                tabCompletions.addAll(Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()));
                tabCompletions.removeIf(s -> s.endsWith("AIR") || s.startsWith("LEGACY"));
                return tabCompletions;
            }

            return tabCompletions;
        }
        return null;
    }
}
