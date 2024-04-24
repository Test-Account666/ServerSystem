package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.bansystem.moderation.MuteModeration;
import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.events.AsyncMuteEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class MuteCommand extends CommandUtils implements ICommandExecutorOverload {

    public MuteCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetMuteManager() == null) {
            this._plugin.Error("BanManager is null?!");
            return true;
        }

        var commandSenderName = commandSender instanceof Player?
                                ((Player) commandSender).getUniqueId().toString() :
                                this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ConsoleName");

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "mute.use")) {
            var permission = this._plugin.GetPermissions().GetPermission("mute.use");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length <= 1) {
            var targetName = arguments.length == 1? arguments[0] : null;
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetSyntaxWithStringTarget(commandLabel, command, commandSender,
                                                                                                                    targetName, "Mute")
                                                                                         .replace("<YEAR>", GetName(YEAR))
                                                                                         .replace("<MONTH>", GetName(MONTH))
                                                                                         .replace("<WEEK>", GetName(WEEK))
                                                                                         .replace("<DAY>", GetName(DAY))
                                                                                         .replace("<HOUR>", GetName(HOUR))
                                                                                         .replace("<MINUTE>", GetName(MINUTE))
                                                                                         .replace("<SECOND>", GetName(SECOND)));
            return true;
        }

        var target = MuteCommand.GetPlayer(arguments[0]);

        if (arguments.length == 2)
            if (!arguments[1].equalsIgnoreCase(this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "PermanentName"))) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetSyntaxWithStringTarget(commandLabel, command, commandSender,
                                                                                                                        arguments[0], "Mute")
                                                                                             .replace("<YEAR>", GetName(YEAR))
                                                                                             .replace("<MONTH>", GetName(MONTH))
                                                                                             .replace("<WEEK>", GetName(WEEK))
                                                                                             .replace("<DAY>", GetName(DAY))
                                                                                             .replace("<HOUR>", GetName(HOUR))
                                                                                             .replace("<MINUTE>", GetName(MINUTE))
                                                                                             .replace("<SECOND>", GetName(SECOND)));
                return true;
            }

        if (arguments[1].equalsIgnoreCase(this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "PermanentName"))) {
            if (this.IsPlayerExempt(commandSender, command, commandLabel, target))
                return true;

            var reason =
                    this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "Mute.DefaultReason");

            var shadow = false;

            if (arguments.length > 3)
                if (!arguments[2].equalsIgnoreCase(this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ShadowBan")) ||
                    !this._plugin.GetPermissions().HasPermission(commandSender, "mute.shadow.permanent", true))
                    reason = this.ExtractReason(2, arguments);
                else {
                    shadow = true;
                    reason = this.ExtractReason(3, arguments);
                }

            if (reason.equalsIgnoreCase(""))
                reason = this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Mute.DefaultReason");

            long time = -1;
            var mute = this._plugin.GetMuteManager().CreateMute(target.getUniqueId(), commandSenderName, reason, shadow, time, YEAR);

            this.SendSuccessMessageAndInvokeEvent(commandSender, command, commandLabel, target, reason, mute);
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "mute.temporary")) {
            var permission = this._plugin.GetPermissions().GetPermission("mute.temporary");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (target.isOnline())
            if (this._plugin.GetPermissions().HasPermission(target.getPlayer(), "mute.exempt", true)) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(), "Mute.CannotMute"));
                return true;
            }

        long time;
        try {
            time = Long.parseLong(arguments[1]);
        } catch (NumberFormatException ignored) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Mute.NotANumber")
                                                                                         .replace("<TIME>", arguments[1]));
            return true;
        }

        var timeUnit = TimeUnit.GetFromName(arguments[2]);
        if (timeUnit == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Mute.NotATimeUnit")
                                                                                         .replace("<TIMEUNIT>", arguments[2]));
            return true;
        }

        var reason = this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "Mute.DefaultReason");

        var shadow = false;

        if (arguments.length > 3) {
            if (!arguments[3].equalsIgnoreCase(this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ShadowBan")) ||
                !this._plugin.GetPermissions().HasPermission(commandSender, "mute.shadow.permanent", true))
                return true;

            shadow = true;

            reason = this.ExtractReason(4, arguments);
        }

        if (reason.equalsIgnoreCase(""))
            reason = this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Mute.DefaultReason");

        var mute = this._plugin.GetMuteManager().CreateMute(target.getUniqueId(), commandSenderName, reason, shadow, time, timeUnit);

        this.SendSuccessMessageAndInvokeEvent(commandSender, command, commandLabel, target, reason, mute);
        return true;
    }

    private ServerSystem GetPlugin() {
        return this._plugin;
    }

    private static OfflinePlayer GetPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (player == null)
            player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private boolean IsPlayerExempt(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "mute.permanent")) {
            var permission = this._plugin.GetPermissions().GetPermission("mute.permanent");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (target.isOnline())
            if (this._plugin.GetPermissions().HasPermission(target.getPlayer(), "mute.exempt", true)) {

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                             .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target.getName(), "Mute.CannotMute"));
                return true;
            }
        return false;
    }

    private String ExtractReason(int start, String... arguments) {
        var reasonBuilder = new StringBuilder();
        for (var index = start; index < arguments.length; index++)
            if (index == arguments.length - 1)
                reasonBuilder.append(arguments[index]);
            else
                reasonBuilder.append(arguments[index]).append(" ");
        return reasonBuilder.toString();
    }

    private void SendSuccessMessageAndInvokeEvent(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, String reason,
                                                  MuteModeration mute) {
        if (commandSender instanceof Player)
            this._plugin.GetMuteManager().RemoveMute(((Player) commandSender).getUniqueId());

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                 target.getName(), "Mute.Success")
                                                                                     .replace("<REASON>", reason)
                                                                                     .replace("<UNMUTE_DATE>", mute.GetExpireDate()));

        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            var asyncMuteEvent = new AsyncMuteEvent(commandSender, target, reason, mute.GetExpireDate());
            Bukkit.getPluginManager().callEvent(asyncMuteEvent);
        });
    }
}
