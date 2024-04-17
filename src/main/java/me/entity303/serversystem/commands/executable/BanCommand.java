package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.bansystem.TimeUnit;
import me.entity303.serversystem.commands.CommandExecutorOverload;
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

public class BanCommand extends CommandUtils implements CommandExecutorOverload {

    public BanCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.getPlugin().getBanManager() == null) {
            this.plugin.error("BanManager is null?!");
            return true;
        }
        if (!this.plugin.getPermissions().hasPermission(commandSender, "ban.use.general")) {
            var permission = this.plugin.getPermissions().getPermission("ban.use.general");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length <= 1) {
            var target = arguments.length == 1? arguments[0] : null;
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getSyntaxWithStringTarget(commandLabel, command, commandSender, target,
                                                                                                                    "Ban")
                                                                                         .replace("<YEAR>", getName(YEAR))
                                                                                         .replace("<MONTH>", getName(MONTH))
                                                                                         .replace("<WEEK>", getName(WEEK))
                                                                                         .replace("<DAY>", getName(DAY))
                                                                                         .replace("<HOUR>", getName(HOUR))
                                                                                         .replace("<MINUTE>", getName(MINUTE))
                                                                                         .replace("<SECOND>", getName(SECOND)));
            return true;
        }

        if (arguments.length == 2)
            if (!arguments[1].equalsIgnoreCase(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "PermanentName"))) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getSyntaxWithStringTarget(commandLabel, command, commandSender,
                                                                                                                        arguments[0], "Ban")
                                                                                             .replace("<YEAR>", YEAR.getName())
                                                                                             .replace("<MONTH>", MONTH.getName())
                                                                                             .replace("<WEEK>", WEEK.getName())
                                                                                             .replace("<DAY>", DAY.getName())
                                                                                             .replace("<HOUR>", HOUR.getName())
                                                                                             .replace("<MINUTE>", MINUTE.getName())
                                                                                             .replace("<SECOND>", SECOND.getName()));
                return true;
            }

        if (arguments[1].equalsIgnoreCase(this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "PermanentName"))) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "ban.use.permanent")) {
                var permission = this.plugin.getPermissions().getPermission("ban.use.permanent");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

            var target = BanCommand.getPlayer(arguments[0]);

            long time = -1;

            var reason = this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Ban.DefaultReason");

            if (arguments.length >= 3)
                reason = IntStream.range(2, arguments.length).mapToObj(i -> arguments[i] + " ").collect(Collectors.joining());

            this.CreateBan(commandSender, command, commandLabel, target, time, reason, YEAR);
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "ban.use.temporary")) {
            var permission = this.plugin.getPermissions().getPermission("ban.use.temporary");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var target = BanCommand.getPlayer(arguments[0]);

        long time;

        try {
            time = Long.parseLong(arguments[1]);
        } catch (NumberFormatException ignored) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Ban.NotANumber")
                                                                                         .replace("<TIME>", arguments[1]));
            return true;
        }

        if (time < 1)
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Ban.NotANumber")
                                                                                         .replace("<TIME>", arguments[1]));

        var timeUnit = getFromName(arguments[2]);
        if (timeUnit == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     target.getName(), "Ban.NotATimeUnit")
                                                                                         .replace("<TIMEUNIT>", arguments[2]));
            return true;
        }
        this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Ban.DefaultReason");
        var reason = this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target.getName(), "Ban.DefaultReason");

        if (arguments.length >= 4)
            reason = IntStream.range(3, arguments.length).mapToObj(i -> arguments[i] + " ").collect(Collectors.joining());

        this.CreateBan(commandSender, command, commandLabel, target, time, reason, timeUnit);
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

    private void CreateBan(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target, long time, String reason, TimeUnit timeUnit) {
        if (reason.equalsIgnoreCase("")) {
            var target1 = target.getName();
            reason = this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target1, "Ban.DefaultReason");
        }

        if (this.IsExemptFromBans(commandSender, command, commandLabel, target))
            return;

        var ban = this.getPlugin()
                      .getBanManager()
                      .createBan(target.getUniqueId(), commandSender instanceof Player?
                                                       ((Player) commandSender).getUniqueId().toString() :
                                                       this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ConsoleName"), reason, time,
                                 timeUnit);


        var target1 = target.getName();
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessageWithStringTarget(commandLabel, command, commandSender, target1,
                                                                                                                 "Ban.Success")
                                                                                     .replace("<DATE>", ban.UNBAN_DATE()));

        if (target.isOnline()) {
            var command1 = command.getName();
            CommandSender target2 = target.getPlayer();
            target.getPlayer()
                  .kickPlayer(this.plugin.getMessages()
                                         .getMessage(commandLabel, command1, commandSender, target2, "Ban.Kick")
                                         .replace("<REASON>", ChatColor.translateAlternateColorCodes('&', ban.BAN_REASON()))
                                         .replace("<DATE>", ChatColor.translateAlternateColorCodes('&', ban.UNBAN_DATE())));
        }

        var finalReason = reason;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            var asyncBanEvent = new AsyncBanEvent(commandSender, target, finalReason, ban.UNBAN_DATE());
            Bukkit.getPluginManager().callEvent(asyncBanEvent);
        });
    }

    private boolean IsExemptFromBans(CommandSender commandSender, Command command, String commandLabel, OfflinePlayer target) {
        if (target.isOnline()) {
            CommandSender cs1 = target.getPlayer();
            if (this.plugin.getPermissions().hasPermission(cs1, "ban.exempt", true)) {

                CommandSender target1 = target.getPlayer();
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target1, "Ban.Cannotban"));
                return true;
            }
        }
        return false;
    }
}
