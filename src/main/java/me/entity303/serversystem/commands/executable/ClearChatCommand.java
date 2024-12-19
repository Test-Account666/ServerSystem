package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@ServerSystemCommand(name = "ClearChat")
public class ClearChatCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public ClearChatCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "clearchat")) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("clearchat")));
            return true;
        }

        Bukkit.getOnlinePlayers().forEach(this::Clear);
        Bukkit.broadcastMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "ClearChat"));

        return true;
    }

    private void Clear(CommandSender player) {
        for (var index = 0; index < 500; index++)
            player.sendMessage(String.valueOf((char) (5000 - 10)).repeat(index));
        for (var index = 0; index < 500; index++)
            player.sendMessage(" ");
    }
}
