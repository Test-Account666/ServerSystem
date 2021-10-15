package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.DatabaseManager.WarpManager;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TABCOMPLETER_warp extends MessageUtils implements TabCompleter {

    public TABCOMPLETER_warp(ss plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.warp.required"))
                if (!this.isAllowed(cs, "warp.permission", true)) return Collections.singletonList("");
            WarpManager warpManager = this.plugin.getWarpManager();
            List<String> warps = warpManager.getWarps();
            List<String> tabCompletions = new ArrayList<>();
            if (warps.size() >= 1) for (String warp : warps)
                if (warp.toLowerCase().startsWith(args[0].toLowerCase())) tabCompletions.add(warp);
            return tabCompletions.size() <= 0 ? warps : tabCompletions;
        }
        if (args.length == 2 && this.isAllowed(cs, "warp.others", true)) return null;
        return Collections.singletonList("");
    }
}
