package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.bansystem.moderation.Moderation;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.events.AsyncBanEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class BanCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public BanCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetBanManager() == null) {
            this._plugin.Error("BanManager is null?!");
            return true;
        }

        var commandSenderName = commandSender instanceof Player player?
                                player.getUniqueId().toString() :
                                this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ConsoleName");

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.general")) {
            var permission = this._plugin.GetPermissions().GetPermission("ban.use.general");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length <= 1) {
            var targetName = arguments.length == 1? arguments[0] : null;

            var message = this._plugin.GetMessages().GetPrefix() +
                          this._plugin.GetMessages().GetSyntaxWithStringTarget(commandLabel, command, commandSender, targetName, "Ban");

            message = this.ReplaceTimePlaceholders(message);

            commandSender.sendMessage(message);
            return true;
        }

        var target = GetPlayer(arguments[0]);

        if (target.isOnline() && this._plugin.GetPermissions().HasPermission(target.getPlayer(), "ban.exempt", true)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                       commandSender, target.getName(),
                                                                                                                       "Ban.Cannotban"));
            return true;
        }

        var permanentBan = false;

        var permanentName = this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "PermanentName");

        if (arguments[1].equalsIgnoreCase(permanentName)) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.permanent")) {
                var permission = this._plugin.GetPermissions().GetPermission("ban.use.permanent");

                var message = this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission);

                commandSender.sendMessage(message);
                return true;
            }

            permanentBan = true;
        }

        if (!permanentBan && arguments.length == 2) {
            var message = this._plugin.GetMessages().GetPrefix() +
                          this._plugin.GetMessages().GetSyntaxWithStringTarget(commandLabel, command, commandSender, target.getName(), "Ban");

            message = this.ReplaceTimePlaceholders(message);

            commandSender.sendMessage(message);
            return true;
        }

        if (!permanentBan && !this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.temporary")) {
            var permission = this._plugin.GetPermissions().GetPermission("ban.use.temporary");

            var message = this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission);

            commandSender.sendMessage(message);
            return true;
        }

        var banTime = -1L;
        var banTimeUnit = YEAR;


        var reason = this._plugin.GetMessages()
                                 .GetMessageWithStringTarget(commandLabel, command.getName(), commandSender, target.getName(), "Mute.DefaultReason");


        if (permanentBan) reason = arguments.length >= 3? this.ExtractReason(2, arguments) : reason;
        else {
            try {
                banTime = Long.parseLong(arguments[1]);
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                           commandSender, target.getName(),
                                                                                                                           "Ban.NotANumber")
                                                                                               .replace("<TIME>", arguments[1]));
                return true;
            }

            banTimeUnit = TimeUnit.GetFromName(arguments[2]);
            if (banTimeUnit == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command,
                                                                                                                           commandSender, target.getName(),
                                                                                                                           "Ban.NotATimeUnit")
                                                                                               .replace("<TIMEUNIT>", arguments[2]));
                return true;
            }

            reason = arguments.length >= 4? this.ExtractReason(3, arguments) : reason;
        }


        var ban = this._plugin.GetBanManager().CreateBan(target.getUniqueId(), commandSenderName, reason, banTime, banTimeUnit);

        this.SendSuccessMessageAndInvokeEvent(commandSender, command, commandLabel, target, reason, ban);
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
        if (player == null) player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private String ExtractReason(int start, String... arguments) {
        var reasonBuilder = new StringBuilder();
        for (var index = start; index < arguments.length; index++)
            if (index == arguments.length - 1) reasonBuilder.append(arguments[index]);
            else reasonBuilder.append(arguments[index]).append(" ");
        return reasonBuilder.toString();
    }

    private void SendSuccessMessageAndInvokeEvent(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, String reason,
                                                  Moderation moderation) {
        if (target.isOnline() && target instanceof Player player) {
            var kickMessage = this._plugin.GetMessages()
                                          .GetMessage(commandLabel, command, commandSender, player, "Ban.Kick")
                                          .replace("<REASON>", ChatColor.TranslateAlternateColorCodes('&', reason))
                                          .replace("<DATE>", moderation.GetExpireDate().replace("&", "§"));

            player.kickPlayer(kickMessage);
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                   target.getName(), "Ban.Success")
                                                                                       .replace("<REASON>", reason)
                                                                                       .replace("<DATE>", moderation.GetExpireDate()));

        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            var asyncBanEvent = new AsyncBanEvent(commandSender, target, reason, moderation.GetExpireDate());
            Bukkit.getPluginManager().callEvent(asyncBanEvent);
        });
    }
}
