package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@ServerSystemCommand(name = "CheckGameMode")
public class CheckGameModeCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public CheckGameModeCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "checkgamemode")) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("checkgamemode")));
            return true;
        }
        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "CheckGameMode"));
            return true;
        }

        var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (target != null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                       "CheckGamemode")
                                                                                           .replace("<MODE>", this.GetMode(target.getGameMode())));
        } else if (arguments[0].equalsIgnoreCase("Konsole") || arguments[0].equalsIgnoreCase("Console")) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + (arguments[0].equalsIgnoreCase("Konsole")?
                                                                                "Die Konsole ist allmächtig und wird " + "uns alle TÖTEN!" :
                                                                                "The console is almighty and will " + "KILL us all!"));
        } else {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
        }
        return true;
    }

    private String GetMode(GameMode gamemode) {
        return this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.GameModes." + gamemode);
    }
}
