package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.events.AsyncBanEvent;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public class BanCommand extends CommandUtils implements ICommandExecutorOverload {

    public BanCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetBanManager() == null) {
            this._plugin.Error("BanManager is null?!");
            return true;
        }
        var pluginMessages = this._plugin.GetMessages();
        var prefix = pluginMessages.GetPrefix();
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.general")) {
            var permission = this._plugin.GetPermissions().GetPermission("ban.use.general");
            var noPermissionMessage = pluginMessages.GetNoPermission(permission);
            commandSender.sendMessage(prefix + noPermissionMessage);
            return true;
        }

        if (arguments.length <= 1) {
            var target = arguments.length == 1? arguments[0] : null;
            var syntax = pluginMessages.GetSyntaxWithStringTarget(commandLabel, command, commandSender, target, "Ban");
            commandSender.sendMessage(prefix + syntax.replace("<YEAR>", GetName(YEAR))
                                                     .replace("<MONTH>", GetName(MONTH))
                                                     .replace("<WEEK>", GetName(WEEK))
                                                     .replace("<DAY>", GetName(DAY))
                                                     .replace("<HOUR>", GetName(HOUR))
                                                     .replace("<MINUTE>", GetName(MINUTE))
                                                     .replace("<SECOND>", GetName(SECOND)));
            return true;
        }

        var config = pluginMessages.GetConfiguration();
        var permanentName = config.GetString("Messages.Misc.BanSystem." + "PermanentName");
        if (arguments.length == 2)
            if (!arguments[1].equalsIgnoreCase(permanentName)) {
                var syntax = pluginMessages.GetSyntaxWithStringTarget(commandLabel, command, commandSender, arguments[0], "Ban");
                commandSender.sendMessage(prefix + syntax.replace("<YEAR>", YEAR.GetName())
                                                         .replace("<MONTH>", MONTH.GetName())
                                                         .replace("<WEEK>", WEEK.GetName())
                                                         .replace("<DAY>", DAY.GetName())
                                                         .replace("<HOUR>", HOUR.GetName())
                                                         .replace("<MINUTE>", MINUTE.GetName())
                                                         .replace("<SECOND>", SECOND.GetName()));
                return true;
            }

        if (arguments[1].equalsIgnoreCase(permanentName)) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.permanent")) {
                var permission = this._plugin.GetPermissions().GetPermission("ban.use.permanent");
                var noPermissionMessage = pluginMessages.GetNoPermission(permission);
                commandSender.sendMessage(prefix + noPermissionMessage);
                return true;
            }

            var target = BanCommand.GetPlayer(arguments[0]);

            long time = -1;

            var reason = pluginMessages.GetMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Ban.DefaultReason");

            if (arguments.length >= 3)
                reason = IntStream.range(2, arguments.length).mapToObj(index -> arguments[index] + " ").collect(Collectors.joining());

            this.CreateBan(commandSender, command, commandLabel, target, time, reason, YEAR);
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "ban.use.temporary")) {
            var permission = this._plugin.GetPermissions().GetPermission("ban.use.temporary");
            commandSender.sendMessage(prefix + pluginMessages.GetNoPermission(permission));
            return true;
        }

        var target = BanCommand.GetPlayer(arguments[0]);

        long time;

        var targetName = target.getName();
        var notNumberMessage = pluginMessages.GetMessageWithStringTarget(commandLabel, command, commandSender, targetName, "Ban.NotANumber");
        try {
            time = Long.parseLong(arguments[1]);
        } catch (NumberFormatException ignored) {
            commandSender.sendMessage(prefix + notNumberMessage.replace("<TIME>", arguments[1]));
            return true;
        }

        if (time < 1)
            commandSender.sendMessage(prefix + notNumberMessage.replace("<TIME>", arguments[1]));

        var timeUnit = GetFromName(arguments[2]);
        if (timeUnit == null) {
            var notTimeUnitMessage = pluginMessages.GetMessageWithStringTarget(commandLabel, command, commandSender, targetName, "Ban.NotATimeUnit");
            commandSender.sendMessage(prefix + notTimeUnitMessage.replace("<TIMEUNIT>", arguments[2]));
            return true;
        }
        pluginMessages.GetMessageWithStringTarget(commandLabel, command, commandSender, targetName, "Ban.DefaultReason");
        var reason = pluginMessages.GetMessageWithStringTarget(commandLabel, command, commandSender, targetName, "Ban.DefaultReason");

        if (arguments.length >= 4)
            reason = IntStream.range(3, arguments.length).mapToObj(index -> arguments[index] + " ").collect(Collectors.joining());

        this.CreateBan(commandSender, command, commandLabel, target, time, reason, timeUnit);
        return true;
    }

    private static OfflinePlayer GetPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (player == null)
            player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private void CreateBan(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, long time, String reason,
                           TimeUnit timeUnit) {
        var pluginMessages = this._plugin.GetMessages();
        if (reason.equalsIgnoreCase("")) {
            var target1 = target.getName();
            reason = pluginMessages.GetMessageWithStringTarget(commandLabel, command, commandSender, target1, "Ban.DefaultReason");
        }

        if (this.IsExemptFromBans(commandSender, command, commandLabel, target))
            return;

        var consoleName = pluginMessages.GetConfiguration().GetString("Messages.Misc.BanSystem." + "ConsoleName");
        var ban = this._plugin.GetBanManager()
                             .CreateBan(target.getUniqueId(),
                                        commandSender instanceof Player? ((Player) commandSender).getUniqueId().toString() : consoleName, reason, time,
                                        timeUnit);


        var target1 = target.getName();
        var prefix = pluginMessages.GetPrefix();
        var banSuccessMessage = pluginMessages.GetMessageWithStringTarget(commandLabel, command, commandSender, target1, "Ban.Success");
        var unbanDate = ban.GetExpireDate();
        commandSender.sendMessage(prefix + banSuccessMessage.replace("<DATE>", unbanDate));

        if (target.isOnline()) {
            var command1 = command.getName();
            var kickMessage = pluginMessages.GetMessage(commandLabel, command1, commandSender, target.getPlayer(), "Ban.Kick");
            var coloredBanReason = ChatColor.TranslateAlternateColorCodes('&', ban.GetReason());
            var coloredUnbanDate = ChatColor.TranslateAlternateColorCodes('&', unbanDate);
            target.getPlayer().kickPlayer(kickMessage.replace("<REASON>", coloredBanReason).replace("<DATE>", coloredUnbanDate));
        }

        var finalReason = reason;
        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            var asyncBanEvent = new AsyncBanEvent(commandSender, target, finalReason, unbanDate);
            Bukkit.getPluginManager().callEvent(asyncBanEvent);
        });
    }

    private boolean IsExemptFromBans(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target) {
        if (target.isOnline())
            if (this._plugin.GetPermissions().HasPermission(target.getPlayer(), "ban.exempt", true)) {

                var pluginMessages = this._plugin.GetMessages();
                var prefix = pluginMessages.GetPrefix();
                commandSender.sendMessage(prefix + pluginMessages.GetMessage(commandLabel, command, commandSender, target.getPlayer(), "Ban.Cannotban"));
                return true;
            }
        return false;
    }

}
