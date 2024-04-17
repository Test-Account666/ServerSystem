package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarpsCommand extends CommandUtils implements CommandExecutorOverload {

    public WarpsCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.warps.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "warps.permission")) {
                var permission = this.plugin.getPermissions().getPermission("warps.permission");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

        var warpBuilder = new StringBuilder();
        var separator = this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Warps.Format.Separator");
        var warpFormat = this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Warps.Format.Format");

        if (this.plugin.getWarpManager().getWarps().isEmpty()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
            return true;
        }

        var warps = this.plugin.getWarpManager().getWarps();

        for (var warp : warps)
            warpBuilder.append(warpFormat.replace("<SEPERATOR>", separator).replace("<Warp>", warp));

        if (warpBuilder.toString().toLowerCase().startsWith(separator))
            warpBuilder.delete(0, separator.length());

        var warpMessage = this.plugin.getMessages()
                                     .getMessage(commandLabel, command.getName(), commandSender, null, "Warps.Format.Message")
                                     .replace("<AMOUNT>", String.valueOf(warps.size()))
                                     .replace("<WARPS>", warpBuilder.toString());

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + warpMessage);
        return true;
    }
}
