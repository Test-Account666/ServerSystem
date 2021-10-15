package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class TABCOMPLETER_deletekit extends MessageUtils implements TabCompleter {

    public TABCOMPLETER_deletekit(ss plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1)
            if (this.isAllowed(cs, "deletekit", true)) return this.plugin.getKitsManager().getKitNames();
        return Collections.singletonList("");
    }
}
