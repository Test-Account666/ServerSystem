package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
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

public class DelHomeCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public DelHomeCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.delhome.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "delhome.permission")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("delhome.permission")));
                return true;
            }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "DelHome"));
            return true;
        }

        var homeFile = new File("plugins//ServerSystem//Homes", ((Player) commandSender).getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        if (!homeFile.exists()) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "DelHome.NoHomes"));
            return true;
        }

        if (homeFile.exists()) {
            List<String> homes = new ArrayList<>(homeCfg.getConfigurationSection("Homes").getKeys(false));
            if (homes.isEmpty()) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "DelHome.NoHomes"));
                return true;
            }
        }


        homeCfg.set("Homes." + arguments[0].toUpperCase(), null);
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                 "DelHome.Success")
                                                                                     .replace("<HOME>", arguments[0].toUpperCase()));

        try {
            homeCfg.save(homeFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }
}
