package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends CommandUtils implements ICommandExecutorOverload {

    public VanishCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "vanish.self")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("vanish.self")));
                return true;
            }

            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "Vanish"));
                return true;
            }

            var vanish = !this._plugin.GetVanish().IsVanish(player);

            this.SetVanish(player, vanish, commandSender, command, commandLabel);

            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "vanish.others")) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("vanish.others")));
            return true;
        }

        var targetPlayer = this.GetPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        var vanish = !this._plugin.GetVanish().IsVanish(targetPlayer);

        this.SetVanish(targetPlayer, vanish, commandSender, command, commandLabel);
        return true;
    }

    private void SetVanish(Player targetPlayer, boolean vanish, CommandSender commandSender, Command command, String commandLabel) {
        this._plugin.GetVanish().SetVanishData(targetPlayer, vanish);
        this._plugin.GetVanish().SetVanish(vanish, targetPlayer);

        if (!vanish)
            Bukkit.getOnlinePlayers().forEach(all -> all.showPlayer(targetPlayer));

        if (vanish)
            Bukkit.getOnlinePlayers()
                  .stream()
                  .filter(player -> !this._plugin.GetPermissions().HasPermission(player, "vanish.see", true))
                  .forEachOrdered(player -> player.hidePlayer(targetPlayer));

        if (targetPlayer == commandSender) {
            if (vanish) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Vanish.Self.Activated"));
                return;
            }

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Vanish.Self.DeActivated"));

            return;
        }

        if (vanish) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                     "Vanish.Others.Activated.Sender"));
            targetPlayer.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                        .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                    "Vanish.Others.Activated.Target"));
            return;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                 "Vanish.Others.DeActivated.Sender"));
        targetPlayer.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                    .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                "Vanish.Others.DeActivated.Target"));
    }
}
