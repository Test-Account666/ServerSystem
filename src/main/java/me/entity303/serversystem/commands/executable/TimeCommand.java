package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.WorldTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@ServerSystemCommand(name = "Time", tabCompleter = WorldTabCompleter.class)
public class TimeCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public TimeCommand(ServerSystem plugin) {
        this._plugin = plugin;

        plugin.GetCommandManager().RegisterCommand("day", new DayCommand(plugin, this), new WorldTabCompleter(plugin));
        plugin.GetCommandManager().RegisterCommand("night", new NightCommand(plugin, this), new WorldTabCompleter(plugin));
        plugin.GetCommandManager().RegisterCommand("noon", new NoonCommand(plugin, this), new WorldTabCompleter(plugin));
    }

    @SuppressWarnings("DuplicatedCode")
    public void ExecuteTime(String time, CommandSender commandSender, Command command, String commandLabel, String... arguments) {
        if (arguments.length == 0) {
            arguments = new String[] { time };
        } else if (arguments.length == 1) {
            arguments = new String[] { time, arguments[0] };
        } else {
            List<String> argumentList = new LinkedList<>();

            Collections.addAll(argumentList, arguments);

            argumentList.add(0, time);

            arguments = argumentList.toArray(new String[0]);
        }

        this.onCommand(commandSender, command, commandLabel, arguments);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "time")) {
            var permission = this._plugin.GetPermissions().GetPermission("time");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Time"));
            return true;
        }

        if (arguments.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Time"));
                return true;
            }
            if ("Tag".equalsIgnoreCase(arguments[0]) || "Day".equalsIgnoreCase(arguments[0])) {
                ((Player) commandSender).getWorld().setTime(0);

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, null, "Time.Success")
                                                                                               .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                               .replace("<TIME>", this.GetTime("Day")));
                return true;
            }

            if ("Nacht".equalsIgnoreCase(arguments[0]) || "Night".equalsIgnoreCase(arguments[0])) {
                ((Player) commandSender).getWorld().setTime(16000);

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, null, "Time.Success")
                                                                                               .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                               .replace("<TIME>", this.GetTime("Night")));
                return true;
            }

            if ("Mittag".equalsIgnoreCase(arguments[0]) || "noon".equalsIgnoreCase(arguments[0])) {
                ((Player) commandSender).getWorld().setTime(6000);

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessage(commandLabel, command, commandSender, null, "Time.Success")
                                                                                               .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                               .replace("<TIME>", this.GetTime("Noon")));
                return true;
            }

            try {
                ((Player) commandSender).getWorld().setTime(Long.parseLong(arguments[0]));
            } catch (Exception exception) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Time"));
            }
            return true;
        }


        if (Bukkit.getWorld(arguments[1]) == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Time.NoWorld").replace("<WORLD>", arguments[1]));
            return true;
        }

        if ("Tag".equalsIgnoreCase(arguments[0]) || "Day".equalsIgnoreCase(arguments[0])) {
            Bukkit.getWorld(arguments[1]).setTime(0);
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null, "Time.Success")
                                                                                           .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                           .replace("<TIME>", this.GetTime("Day")));
            return true;
        }

        if ("Nacht".equalsIgnoreCase(arguments[0]) || "Night".equalsIgnoreCase(arguments[0])) {
            Bukkit.getWorld(arguments[1]).setTime(16000);

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null, "Time.Success")
                                                                                           .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                           .replace("<TIME>", this.GetTime("Night")));
            return true;
        }

        if ("Mittag".equalsIgnoreCase(arguments[0]) || "noon".equalsIgnoreCase(arguments[0])) {
            Bukkit.getWorld(arguments[1]).setTime(6000);

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, null, "Time.Success")
                                                                                           .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                           .replace("<TIME>", this.GetTime("Noon")));
            return true;
        }

        try {
            Bukkit.getWorld(arguments[1]).setTime(Long.parseLong(arguments[0]));
        } catch (Exception exception) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Time"));
        }
        return true;
    }

    private String GetTime(String time) {
        return this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.Times." + time);
    }

    private void SetTimeForCurrentWorld(String commandLabel, String permission, Player player, String time) {
        if (!this._plugin.GetPermissions().HasPermissionString(player, permission)) {
            player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return;
        }

        player.getWorld().setTime(0);
        player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                .GetMessage(commandLabel, "time", player, null, "Time.Success")
                                                                                .replace("<WORLD>", player.getWorld().getName())
                                                                                .replace("<TIME>", this.GetTime(time)));
    }
}
