package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.bansystem.moderation.Moderation;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.events.AsyncMuteEvent;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class MuteCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public MuteCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetMuteManager() == null) {
            this._plugin.Error("MuteManager is null?!");
            return true;
        }

        var commandSenderName = commandSender instanceof Player player?
                                player.getUniqueId().toString() :
                                this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ConsoleName");

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "mute.use")) {
            var permission = this._plugin.GetPermissions().GetPermission("mute.use");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length <= 1) {
            var targetName = arguments.length == 1? arguments[0] : null;

            var message = this._plugin.GetMessages().GetPrefix() +
                          this._plugin.GetMessages().GetSyntaxWithStringTarget(commandLabel, command, commandSender, targetName, "Mute");

            message = this.ReplaceTimePlaceholders(message);

            commandSender.sendMessage(message);
            return true;
        }

        var target = MuteCommand.GetPlayer(arguments[0]);

        if (target.isOnline() && this._plugin.GetPermissions().HasPermission(target.getPlayer(), "mute.exempt", true)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                       commandSender, target.getName(),
                                                                                                                       "Mute.CannotMute"));
            return true;
        }

        var permanentMute = false;

        var permanentName = this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "PermanentName");

        if (arguments[1].equalsIgnoreCase(permanentName)) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "mute.permanent")) {
                var permission = this._plugin.GetPermissions().GetPermission("mute.permanent");

                var message = this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission);

                commandSender.sendMessage(message);
                return true;
            }

            permanentMute = true;
        }

        if (!permanentMute && arguments.length == 2) {
            var message = this._plugin.GetMessages().GetPrefix() +
                          this._plugin.GetMessages().GetSyntaxWithStringTarget(commandLabel, command, commandSender, target.getName(), "Mute");

            message = this.ReplaceTimePlaceholders(message);

            commandSender.sendMessage(message);
            return true;
        }

        if (!permanentMute && !this._plugin.GetPermissions().HasPermission(commandSender, "mute.temporary")) {
            var permission = this._plugin.GetPermissions().GetPermission("mute.temporary");

            var message = this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission);

            commandSender.sendMessage(message);
            return true;
        }

        var shadowName = this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ShadowBan");

        var shadowMute = false;

        if (arguments.length == 3)
            shadowMute = arguments[2].equalsIgnoreCase(shadowName);
        else if (arguments.length >= 4)
            shadowMute = arguments[3].equalsIgnoreCase(shadowName);


        var shadowMutePermission = permanentMute? "mute.shadow.permanent" : "mute.shadow.temporary";

        if (shadowMute && !this._plugin.GetPermissions().HasPermission(commandSender, shadowMutePermission)) {
            var permission = this._plugin.GetPermissions().GetPermission(shadowMutePermission);

            var message = this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission);

            commandSender.sendMessage(message);
            return true;
        }

        var muteTime = -1L;
        var muteTimeUnit = YEAR;


        var reason = this._plugin.GetMessages()
                                 .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "Mute.DefaultReason");


        if (permanentMute)
            if (shadowMute)
                reason = arguments.length >= 4? this.ExtractReason(3, arguments) : reason;
            else
                reason = arguments.length >= 3? this.ExtractReason(2, arguments) : reason;
        else {
            try {
                muteTime = Long.parseLong(arguments[1]);
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                           commandSender, target.getName(),
                                                                                                                           "Mute.NotANumber")
                                                                                               .replace("<TIME>", arguments[1]));
                return true;
            }

            muteTimeUnit = TimeUnit.GetFromName(arguments[2]);
            if (muteTimeUnit == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                           commandSender, target.getName(),
                                                                                                                           "Mute.NotATimeUnit")
                                                                                               .replace("<TIMEUNIT>", arguments[2]));
                return true;
            }

            if (shadowMute)
                reason = arguments.length >= 5? this.ExtractReason(4, arguments) : reason;
            else
                reason = arguments.length >= 4? this.ExtractReason(3, arguments) : reason;
        }


        var mute = this._plugin.GetMuteManager().CreateMute(target.getUniqueId(), commandSenderName, reason, shadowMute, muteTime, muteTimeUnit);

        this.SendSuccessMessageAndInvokeEvent(commandSender, command, commandLabel, target, reason, mute);
        return true;
    }

    private String ReplaceTimePlaceholders(String message) {
        return message.replace("<YEAR>", GetName(YEAR))
                      .replace("<MONTH>", GetName(MONTH))
                      .replace("<WEEK>", GetName(WEEK))
                      .replace("<DAY>", GetName(DAY))
                      .replace("<HOUR>", GetName(HOUR))
                      .replace("<MINUTE>", GetName(MINUTE))
                      .replace("<SECOND>", GetName(SECOND));
    }

    private static OfflinePlayer GetPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (player == null)
            player = Bukkit.getOfflinePlayer(name);
        return player;
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
                                                  Moderation moderation) {
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                   target.getName(), "Mute.Success")
                                                                                       .replace("<REASON>", reason)
                                                                                       .replace("<UNMUTE_DATE>", moderation.GetExpireDate()));

        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            var asyncMuteEvent = new AsyncMuteEvent(commandSender, target, reason, moderation.GetExpireDate());
            Bukkit.getPluginManager().callEvent(asyncMuteEvent);
        });
    }
}
