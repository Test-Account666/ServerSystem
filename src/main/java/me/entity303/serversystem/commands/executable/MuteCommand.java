package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.bansystem.Mute;
import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.events.AsyncMuteEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class MuteCommand extends CommandUtils implements CommandExecutorOverload {

    public MuteCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.getPlugin().getMuteManager() == null) {
            this.plugin.error("BanManager is null?!");
            return true;
        }

        var commandSenderName = commandSender instanceof Player?
                                ((Player) commandSender).getUniqueId().toString() :
                                this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ConsoleName");

        if (!this.plugin.getPermissions().hasPermission(commandSender, "mute.use")) {
            var permission = this.plugin.getPermissions().getPermission("mute.use");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length <= 1) {
            var targetName = arguments.length == 1? arguments[0] : null;
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getSyntaxWithStringTarget(commandLabel, command, commandSender,
                                                                                                                    targetName, "Mute")
                                                                                         .replace("<YEAR>", getName(YEAR))
                                                                                         .replace("<MONTH>", getName(MONTH))
                                                                                         .replace("<WEEK>", getName(WEEK))
                                                                                         .replace("<DAY>", getName(DAY))
                                                                                         .replace("<HOUR>", getName(HOUR))
                                                                                         .replace("<MINUTE>", getName(MINUTE))
                                                                                         .replace("<SECOND>", getName(SECOND)));
            return true;
        }

        var target = MuteCommand.getPlayer(arguments[0]);

        if (arguments.length == 2)
            if (!arguments[1].equalsIgnoreCase(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "PermanentName"))) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getSyntaxWithStringTarget(commandLabel, command, commandSender,
                                                                                                                        arguments[0], "Mute")
                                                                                             .replace("<YEAR>", getName(YEAR))
                                                                                             .replace("<MONTH>", getName(MONTH))
                                                                                             .replace("<WEEK>", getName(WEEK))
                                                                                             .replace("<DAY>", getName(DAY))
                                                                                             .replace("<HOUR>", getName(HOUR))
                                                                                             .replace("<MINUTE>", getName(MINUTE))
                                                                                             .replace("<SECOND>", getName(SECOND)));
                return true;
            }

        if (arguments[1].equalsIgnoreCase(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "PermanentName"))) {
            if (this.isPlayerExempt(commandSender, command, commandLabel, target))
                return true;

            var reason =
                    this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "Mute.DefaultReason");

            var shadow = false;

            if (arguments.length > 3)
                if (!arguments[2].equalsIgnoreCase(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ShadowBan")) ||
                    !this.plugin.getPermissions().hasPermission(commandSender, "mute.shadow.permanent", true))
                    reason = this.extractReason(2, arguments);
                else {
                    shadow = true;
                    reason = this.extractReason(3, arguments);
                }

            if (reason.equalsIgnoreCase(""))
                reason = this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Mute.DefaultReason");

            long time = -1;
            var mute = this.getPlugin().getMuteManager().addMute(target.getUniqueId(), commandSenderName, reason, shadow, time, YEAR);

            this.SendSuccessMessageAndInvokeEvent(commandSender, command, commandLabel, target, reason, mute);
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "mute.temporary")) {
            var permission = this.plugin.getPermissions().getPermission("mute.temporary");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (target.isOnline())
            if (this.plugin.getPermissions().hasPermission(target.getPlayer(), "mute.exempt", true)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(), "Mute.CannotMute"));
                return true;
            }

        long time;
        try {
            time = Long.parseLong(arguments[1]);
        } catch (NumberFormatException ignored) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Mute.NotANumber")
                                                                                         .replace("<TIME>", arguments[1]));
            return true;
        }

        var timeUnit = TimeUnit.getFromName(arguments[2]);
        if (timeUnit == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Mute.NotATimeUnit")
                                                                                         .replace("<TIMEUNIT>", arguments[2]));
            return true;
        }

        var reason = this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "Mute.DefaultReason");

        var shadow = false;

        if (arguments.length > 3) {
            if (!arguments[3].equalsIgnoreCase(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ShadowBan")) ||
                !this.plugin.getPermissions().hasPermission(commandSender, "mute.shadow.permanent", true))
                return true;

            shadow = true;

            reason = this.extractReason(4, arguments);
        }

        if (reason.equalsIgnoreCase(""))
            reason = this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Mute.DefaultReason");

        var mute = this.plugin.getMuteManager().addMute(target.getUniqueId(), commandSenderName, reason, shadow, time, timeUnit);

        this.SendSuccessMessageAndInvokeEvent(commandSender, command, commandLabel, target, reason, mute);
        return true;
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }

    private static OfflinePlayer getPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (player == null)
            player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private boolean isPlayerExempt(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "mute.permanent")) {
            var permission = this.plugin.getPermissions().getPermission("mute.permanent");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (target.isOnline())
            if (this.plugin.getPermissions().hasPermission(target.getPlayer(), "mute.exempt", true)) {

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(), "Mute.CannotMute"));
                return true;
            }
        return false;
    }

    private String extractReason(int start, String... arguments) {
        var reasonBuilder = new StringBuilder();
        for (var index = start; index < arguments.length; index++)
            if (index == arguments.length - 1)
                reasonBuilder.append(arguments[index]);
            else
                reasonBuilder.append(arguments[index]).append(" ");
        return reasonBuilder.toString();
    }

    private void SendSuccessMessageAndInvokeEvent(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, String reason,
                                                  Mute mute) {
        if (commandSender instanceof Player)
            this.plugin.getMuteManager().removeMute(((Player) commandSender).getUniqueId());

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                 target.getName(), "Mute.Success")
                                                                                     .replace("<REASON>", reason)
                                                                                     .replace("<UNMUTE_DATE>", mute.getUNMUTE_DATE()));

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            var asyncMuteEvent = new AsyncMuteEvent(commandSender, target, reason, mute.getUNMUTE_DATE());
            Bukkit.getPluginManager().callEvent(asyncMuteEvent);
        });
    }
}
