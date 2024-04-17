package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends CommandUtils implements CommandExecutorOverload {

    public VanishCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "vanish.self")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("vanish.self")));
                return true;
            }

            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "Vanish"));
                return true;
            }

            var vanish = !this.plugin.getVanish().isVanish(player);

            this.setVanish(player, !vanish, commandSender, command, commandLabel);

            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "vanish.others")) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("vanish.others")));
            return true;
        }

        var targetPlayer = this.getPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        var vanish = !this.plugin.getVanish().isVanish(targetPlayer);

        this.setVanish(targetPlayer, vanish, commandSender, command, commandLabel);
        return true;
    }

    private void setVanish(Player targetPlayer, boolean vanish, CommandSender commandSender, Command command, String commandLabel) {
        this.plugin.getVanish().setVanishData(targetPlayer, vanish);
        this.plugin.getVanish().setVanish(vanish, targetPlayer);

        if (!vanish)
            Bukkit.getOnlinePlayers().forEach(all -> all.showPlayer(targetPlayer));

        if (vanish)
            Bukkit.getOnlinePlayers()
                  .stream()
                  .filter(player -> !this.plugin.getPermissions().hasPermission(player, "vanish.see", true))
                  .forEachOrdered(player -> player.hidePlayer(targetPlayer));

        if (targetPlayer == commandSender) {
            if (vanish) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Vanish.Self.Activated"));
                return;
            }

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Vanish.Self.DeActivated"));

            return;
        }

        if (vanish) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                     "Vanish.Others.Activated.Sender"));
            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                        .getMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                    "Vanish.Others.Activated.Target"));
            return;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                 "Vanish.Others.DeActivated.Sender"));
        targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                    .getMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                "Vanish.Others.DeActivated.Target"));
    }
}
