package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.WorldTabCompleter;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class TimeCommand extends CommandUtils implements CommandExecutorOverload {

    public TimeCommand(ServerSystem plugin) {
        super(plugin);

        this.plugin.getCommandManager().registerCommand("day", new DayCommand(this.plugin, this), new WorldTabCompleter());
        this.plugin.getCommandManager().registerCommand("night", new NightCommand(this.plugin, this), new WorldTabCompleter());
        this.plugin.getCommandManager().registerCommand("noon", new NoonCommand(this.plugin, this), new WorldTabCompleter());
    }

    @SuppressWarnings("DuplicatedCode")
    public void ExecuteTime(String time, CommandSender commandSender, Command command, String commandLabel, String... arguments) {
        if (arguments.length == 0)
            arguments = new String[] { time };
        else if (arguments.length == 1)
            arguments = new String[] { time, arguments[0] };
        else {
            List<String> argumentList = new LinkedList<>();

            Collections.addAll(argumentList, arguments);

            argumentList.add(0, time);

            arguments = argumentList.toArray(new String[0]);
        }

        this.onCommand(commandSender, command, commandLabel, arguments);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "time")) {
            var permission = this.plugin.getPermissions().getPermission("time");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0)
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Time"));
        else if (arguments.length == 1) {
            if (!(commandSender instanceof Player)) {

                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Time"));
                return true;
            }
            if ("Tag".equalsIgnoreCase(arguments[0]) || "Day".equalsIgnoreCase(arguments[0])) {
                ((Player) commandSender).getWorld().setTime(0);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Time.Success")
                                                                                             .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                             .replace("<TIME>", this.getTime("Day")));
            } else if ("Nacht".equalsIgnoreCase(arguments[0]) || "Night".equalsIgnoreCase(arguments[0])) {
                ((Player) commandSender).getWorld().setTime(16000);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Time.Success")
                                                                                             .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                             .replace("<TIME>", this.getTime("Night")));
            } else if ("Mittag".equalsIgnoreCase(arguments[0]) || "noon".equalsIgnoreCase(arguments[0])) {
                ((Player) commandSender).getWorld().setTime(6000);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Time.Success")
                                                                                             .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                             .replace("<TIME>", this.getTime("Noon")));
            } else
                try {
                    ((Player) commandSender).getWorld().setTime(Long.parseLong(arguments[0]));
                } catch (Exception e) {

                    commandSender.sendMessage(
                            this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Time"));
                }
        } else {
            if (Bukkit.getWorld(arguments[1]) == null) {

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Time.NoWorld")
                                                                                             .replace("<WORLD>", arguments[1]));
                return true;
            }
            if ("Tag".equalsIgnoreCase(arguments[0]) || "Day".equalsIgnoreCase(arguments[0])) {
                Bukkit.getWorld(arguments[1]).setTime(0);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Time.Success")
                                                                                             .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                             .replace("<TIME>", this.getTime("Day")));
            } else if ("Nacht".equalsIgnoreCase(arguments[0]) || "Night".equalsIgnoreCase(arguments[0])) {
                Bukkit.getWorld(arguments[1]).setTime(16000);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Time.Success")
                                                                                             .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                             .replace("<TIME>", this.getTime("Night")));
            } else if ("Mittag".equalsIgnoreCase(arguments[0]) || "noon".equalsIgnoreCase(arguments[0])) {
                Bukkit.getWorld(arguments[1]).setTime(6000);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Time.Success")
                                                                                             .replace("<WORLD>", ((Player) commandSender).getWorld().getName())
                                                                                             .replace("<TIME>", this.getTime("Noon")));
            } else
                try {
                    Bukkit.getWorld(arguments[1]).setTime(Long.parseLong(arguments[0]));
                } catch (Exception e) {

                    commandSender.sendMessage(
                            this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Time"));
                }
        }
        return true;
    }

    private String getTime(String time) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.Times." + time);
    }

    private void SetTimeForCurrentWorld(String commandLabel, String permission, Player player, String time) {
        if (!this.plugin.getPermissions().hasPermissionString(player, permission)) {
            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return;
        }

        player.getWorld().setTime(0);
        player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                              .getMessage(commandLabel, "time", player, null, "Time.Success")
                                                                              .replace("<WORLD>", player.getWorld().getName())
                                                                              .replace("<TIME>", this.getTime(time)));
    }
}
