package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public DelHomeCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.delhome.required"))
            if (!this.plugin.getPermissions().hasPerm(cs, "delhome.permission")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("delhome.permission")));
                return true;
            }

        if (args.length <= 0) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, cmd.getName(), cs, null, "DelHome"));
            return true;
        }

        File homeFile = new File("plugins//ServerSystem//Homes", ((Player) cs).getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        if (!homeFile.exists()) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "DelHome.NoHomes"));
            return true;
        }

        if (homeFile.exists()) {
            List<String> homes = new ArrayList<>(homeCfg.getConfigurationSection("Homes").getKeys(false));
            if (homes.size() <= 0) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "DelHome.NoHomes"));
                return true;
            }
        }


        homeCfg.set("Homes." + args[0].toUpperCase(), null);
        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "DelHome.Success").replace("<HOME>", args[0].toUpperCase()));

        try {
            homeCfg.save(homeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
