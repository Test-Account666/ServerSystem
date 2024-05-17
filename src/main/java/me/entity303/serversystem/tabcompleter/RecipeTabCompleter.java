package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeTabCompleter implements ITabCompleterOverload {

    public static final Material[] MATERIALS = Material.values();
    protected final ServerSystem _plugin;

    public RecipeTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length != 1)
            return null;

        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.recipe.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "recipe.permission", true))
                return Collections.singletonList("");

        var tabCompletions = Arrays.stream(MATERIALS)
                                   .filter(material -> material.name().toLowerCase().startsWith(arguments[0].toLowerCase()) && !material.name().endsWith("AIR") &&
                                                       !material.name().startsWith("LEGACY"))
                                   .map(Enum::name)
                                   .collect(Collectors.toList());

        if (!tabCompletions.isEmpty())
            return tabCompletions;

        tabCompletions.addAll(Arrays.stream(RecipeTabCompleter.MATERIALS).map(Enum::name).toList());
        tabCompletions.removeIf(material -> material.endsWith("AIR") || material.startsWith("LEGACY"));
        return tabCompletions;

    }
}
