package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.BanSystem.Ban;
import me.Entity303.ServerSystem.BanSystem.TimeUnit;
import me.Entity303.ServerSystem.Events.AsyncBanEvent;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.Entity303.ServerSystem.BanSystem.TimeUnit.*;

public class COMMAND_ban extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_ban(ss plugin) {
        super(plugin);
    }

    private static OfflinePlayer getPlayer(String name) {
        OfflinePlayer player;
        player = Bukkit.getPlayer(name);
        if (player == null) player = Bukkit.getOfflinePlayer(name);
        return player;
    }

    private ss getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.getPlugin().getBanManager() == null) {
            this.plugin.error("BanManager is null?!");
            return true;
        }
        if (!this.isAllowed(cs, "ban.use.general")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("ban.use.general")));
            return true;
        }

        if (args.length <= 1) {
            cs.sendMessage(this.getPrefix() +
                    this.getSyntaxWithStringTarget("Ban",
                                    label,
                                    cmd.getName(),
                                    cs,
                                    args.length >= 1 ? args[0] : null).
                            replace("<YEAR>", getName(YEAR)).
                            replace("<MONTH>", getName(MONTH)).
                            replace("<WEEK>", getName(WEEK)).
                            replace("<DAY>", getName(DAY)).
                            replace("<HOUR>", getName(HOUR)).
                            replace("<MINUTE>", getName(MINUTE)).
                            replace("<SECOND>", getName(SECOND)));
            return true;
        }

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase(this.getBanSystem("PermanentName"))) {
                if (!this.isAllowed(cs, "ban.use.permanent")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("ban.use.permanent")));
                    return true;
                }

                OfflinePlayer target = COMMAND_ban.getPlayer(args[0]);

                long time = -1;

                String reason = this.getMessageWithStringTarget("Ban.DefaultReason", label, cmd.getName(), cs, target.getName());
                if (target.isOnline()) if (this.isAllowed(target.getPlayer(), "ban.exempt", true)) {
                    cs.sendMessage(this.getPrefix() + this.getMessage("Ban.Cannotban", label, cmd.getName(), cs, target.getPlayer()));
                    return true;
                }

                Ban ban = this.getPlugin().getBanManager().createBan(target.getUniqueId(), cs instanceof Player ? ((Player) cs).getUniqueId().toString() : this.getBanSystem("ConsoleName"), reason, time, YEAR);

                cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Ban.Success", label, cmd.getName(), cs, target.getName()).replace("<DATE>", ban.getUNBAN_DATE()));

                if (target.isOnline())
                    target.getPlayer().kickPlayer(this.getMessageWithStringTarget("Ban.Kick", label, cmd.getName(), cs, target.getName()).replace("<REASON>", ChatColor.translateAlternateColorCodes('&', ban.getBAN_REASON())).replace("<DATE>", ChatColor.translateAlternateColorCodes('&', ban.getUNBAN_DATE())));

                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    AsyncBanEvent asyncBanEvent = new AsyncBanEvent(cs, target, reason, ban.getUNBAN_DATE());
                    Bukkit.getPluginManager().callEvent(asyncBanEvent);
                });
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getSyntaxWithStringTarget("Ban", label, cmd.getName(), cs, args[0]).replace("<YEAR>", YEAR.getName()).replace("<MONTH>", MONTH.getName()).replace("<WEEK>", WEEK.getName()).replace("<DAY>", DAY.getName()).replace("<HOUR>", HOUR.getName()).replace("<MINUTE>", MINUTE.getName()).replace("<SECOND>", SECOND.getName()));
            return true;
        }

        if (args[1].equalsIgnoreCase(this.getBanSystem("PermanentName"))) {
            if (!this.isAllowed(cs, "ban.use.permanent")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("ban.use.permanent")));
                return true;
            }

            OfflinePlayer target = COMMAND_ban.getPlayer(args[0]);

            long time = -1;

            String reason = "";

            reason = IntStream.range(2, args.length).mapToObj(i -> args[i] + " ").collect(Collectors.joining());

            if (reason.equalsIgnoreCase(""))
                reason = this.getMessageWithStringTarget("Ban.DefaultReason", label, cmd.getName(), cs, target.getName());

            if (target.isOnline()) if (this.isAllowed(target.getPlayer(), "ban.exempt", true)) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Ban.Cannotban", label, cmd.getName(), cs, target.getPlayer()));
                return true;
            }

            Ban ban;

            ban = this.getPlugin().getBanManager().createBan(target.getUniqueId(), cs instanceof Player ? ((Player) cs).getUniqueId().toString() : this.getBanSystem("ConsoleName"), reason, time, YEAR);

            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Ban.Success", label, cmd.getName(), cs, target.getName()).replace("<DATE>", ban.getUNBAN_DATE()));

            if (target.isOnline())
                target.getPlayer().kickPlayer(this.getMessage("Ban.Kick", label, cmd.getName(), cs, target.getPlayer()).replace("<REASON>", ChatColor.translateAlternateColorCodes('&', ban.getBAN_REASON())).replace("<DATE>", ChatColor.translateAlternateColorCodes('&', ban.getUNBAN_DATE())));

            String finalReason = reason;
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                AsyncBanEvent asyncBanEvent = new AsyncBanEvent(cs, target, finalReason, ban.getUNBAN_DATE());
                Bukkit.getPluginManager().callEvent(asyncBanEvent);
            });
            return true;
        }

        if (!this.isAllowed(cs, "ban.use.temporary")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("ban.use.temporary")));
            return true;
        }

        OfflinePlayer target = COMMAND_ban.getPlayer(args[0]);

        long time;

        try {
            time = Long.parseLong(args[1]);
        } catch (NumberFormatException ignored) {
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Ban.NotANumber", label, cmd.getName(), cs, target.getName()).replace("<TIME>", args[1]));
            return true;
        }

        if (time < 1)
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Ban.NotANumber", label, cmd.getName(), cs, target.getName()).replace("<TIME>", args[1]));

        TimeUnit timeUnit = getFromName(args[2]);
        if (timeUnit == null) {
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Ban.NotATimeUnit", label, cmd.getName(), cs, target.getName()).replace("<TIMEUNIT>", args[2]));
            return true;
        }


        String reason = this.getMessageWithStringTarget("Ban.DefaultReason", label, cmd.getName(), cs, target.getName());
        reason = IntStream.range(3, args.length).mapToObj(i -> args[i] + " ").collect(Collectors.joining());

        if (reason.equalsIgnoreCase(""))
            reason = this.getMessageWithStringTarget("Ban.DefaultReason", label, cmd.getName(), cs, target.getName());

        if (target.isOnline()) if (this.isAllowed(target.getPlayer(), "ban.exempt", true)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Ban.Cannotban", label, cmd.getName(), cs, target.getPlayer()));
            return true;
        }

        Ban ban;

        ban = this.getPlugin().getBanManager().createBan(target.getUniqueId(), cs instanceof Player ? ((Player) cs).getUniqueId().toString() : this.getBanSystem("ConsoleName"), reason, time, timeUnit);


        cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Ban.Success", label, cmd.getName(), cs, target.getName()).replace("<DATE>", ban.getUNBAN_DATE()));

        if (target.isOnline())
            target.getPlayer().kickPlayer(this.getMessage("Ban.Kick", label, cmd.getName(), cs, target.getPlayer()).replace("<REASON>", ChatColor.translateAlternateColorCodes('&', ban.getBAN_REASON())).replace("<DATE>", ChatColor.translateAlternateColorCodes('&', ban.getUNBAN_DATE())));

        String finalReason = reason;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            AsyncBanEvent asyncBanEvent = new AsyncBanEvent(cs, target, finalReason, ban.getUNBAN_DATE());
            Bukkit.getPluginManager().callEvent(asyncBanEvent);
        });
        return true;
    }
}
