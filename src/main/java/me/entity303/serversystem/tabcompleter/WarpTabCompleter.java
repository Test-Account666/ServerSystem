package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpTabCompleter extends CommandUtils implements TabCompleter {

    public WarpTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.warp.required"))
                if (!this.plugin.getPermissions().hasPermission(cs, "warp.permission", true))
                    return Collections.singletonList("");
            var warpManager = this.plugin.getWarpManager();
            var warps = warpManager.getWarps();
            List<String> tabCompletions = new ArrayList<>();
            if (!warps.isEmpty())
                for (var warp : warps)
                    if (warp.toLowerCase().startsWith(args[0].toLowerCase()))
                        tabCompletions.add(warp);
            return tabCompletions.isEmpty()? warps : tabCompletions;
        }
        if (args.length == 2)
            if (this.plugin.getPermissions().hasPermission(cs, "warp.others", true))
                return null;
        return Collections.singletonList("");
    }
}
