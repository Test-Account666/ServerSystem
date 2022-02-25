package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class DeleteKitTabCompleter extends MessageUtils implements TabCompleter {

    public DeleteKitTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1)
            if (this.isAllowed(cs, "deletekit", true)) return this.plugin.getKitsManager().getKitNames();
        return Collections.singletonList("");
    }
}
