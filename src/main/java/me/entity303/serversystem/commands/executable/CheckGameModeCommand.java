package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CheckGameModeCommand extends CommandUtils implements CommandExecutorOverload {

    public CheckGameModeCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "checkgamemode")) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("checkgamemode")));
            return true;
        }
        if (arguments.length == 0) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "CheckGameMode"));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target != null)
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, target,
                                                                                                     "CheckGamemode")
                                                                                         .replace("<MODE>", this.getMode(target.getGameMode())));
        else if (arguments[0].equalsIgnoreCase("Konsole") || arguments[0].equalsIgnoreCase("Console"))
            commandSender.sendMessage(arguments[0].equalsIgnoreCase("Konsole")?
                                      this.plugin.getMessages().getPrefix() + "Die Konsole ist allmächtig und wird uns alle TÖTEN!" :
                                      this.plugin.getMessages().getPrefix() + "The console is almighty and will KILL us all!");
        else
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
        return true;
    }

    private String getMode(GameMode gamemode) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.GameModes." + gamemode);
    }
}
