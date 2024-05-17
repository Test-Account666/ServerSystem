package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpTabCompleter implements ITabCompleterOverload {

    protected final ServerSystem _plugin;

    public WarpTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 1) {
            if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.warp.required"))
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "warp.permission", true))
                    return Collections.singletonList("");
            var warpManager = this._plugin.GetWarpManager();
            var warps = warpManager.GetWarps();
            List<String> tabCompletions = new ArrayList<>();
            if (!warps.isEmpty())
                for (var warp : warps)
                    if (warp.toLowerCase().startsWith(arguments[0].toLowerCase()))
                        tabCompletions.add(warp);
            return tabCompletions.isEmpty()? warps : tabCompletions;
        }
        if (arguments.length == 2)
            if (this._plugin.GetPermissions().HasPermission(commandSender, "warp.others", true))
                return null;
        return Collections.singletonList("");
    }
}
