package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public DelHomeCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.delhome.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "delhome.permission")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("delhome.permission")));
                return true;
            }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "DelHome"));
            return true;
        }

        var homeFile = new File("plugins//ServerSystem//Homes", ((Player) commandSender).getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        if (!homeFile.exists()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "DelHome.NoHomes"));
            return true;
        }

        if (homeFile.exists()) {
            List<String> homes = new ArrayList<>(homeCfg.getConfigurationSection("Homes").getKeys(false));
            if (homes.isEmpty()) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "DelHome.NoHomes"));
                return true;
            }
        }


        homeCfg.set("Homes." + arguments[0].toUpperCase(), null);
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                 "DelHome.Success")
                                                                                     .replace("<HOME>", arguments[0].toUpperCase()));

        try {
            homeCfg.save(homeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
