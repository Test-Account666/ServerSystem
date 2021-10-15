package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.BanSystem.Mute;
import me.Entity303.ServerSystem.BanSystem.TimeUnit;
import me.Entity303.ServerSystem.Events.AsyncMuteEvent;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.Entity303.ServerSystem.BanSystem.TimeUnit.*;

public class COMMAND_mute extends MessageUtils implements CommandExecutor {

    public COMMAND_mute(ss plugin) {
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
        if (this.getPlugin().getMuteManager() == null) {
            this.plugin.error("BanManager is null?!");
            return true;
        }
        String sender = cs instanceof Player ? ((Player) cs).getUniqueId().toString() : this.getBanSystem("ConsoleName");
        if (!this.isAllowed(cs, "mute.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("mute.use")));
            return true;
        }
        if (args.length <= 1) {
            cs.sendMessage(this.getPrefix() +
                    this.getSyntaxWithStringTarget("Mute",
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

        OfflinePlayer target = COMMAND_mute.getPlayer(args[0]);

        if (args.length == 2) if (args[1].equalsIgnoreCase(this.getBanSystem("PermanentName"))) {
            if (!this.isAllowed(cs, "mute.permanent")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("mute.permanent")));
                return true;
            }

            if (target.isOnline()) if (this.isAllowed(target.getPlayer(), "mute.exempt", true)) {
                cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.CannotMute", label, cmd.getName(), cs, target.getName()));
                return true;
            }

            String reason = this.getMessageWithStringTarget("Mute.DefaultReason", label, cmd.getName(), cs, target.getName());
            long time = -1;
            Mute mute = this.getPlugin().getMuteManager().addMute(target.getUniqueId(), sender, reason, time, YEAR);

            if (cs instanceof Player) this.plugin.getMuteManager().removeMute(((Player) cs).getUniqueId());
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.Success", label, cmd.getName(), cs, target.getName()).replace("<REASON>", reason).replace("<UNMUTE_DATE>", mute.getUNMUTE_DATE()));

            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                AsyncMuteEvent asyncMuteEvent = new AsyncMuteEvent(cs, target, reason, mute.getUNMUTE_DATE());
                Bukkit.getPluginManager().callEvent(asyncMuteEvent);
            });
            return true;
        } else {
            cs.sendMessage(this.getPrefix() +
                    this.getSyntaxWithStringTarget("Mute",
                                    label,
                                    cmd.getName(),
                                    cs,
                                    args[0]).
                            replace("<YEAR>", getName(YEAR)).
                            replace("<MONTH>", getName(MONTH)).
                            replace("<WEEK>", getName(WEEK)).
                            replace("<DAY>", getName(DAY)).
                            replace("<HOUR>", getName(HOUR)).
                            replace("<MINUTE>", getName(MINUTE)).
                            replace("<SECOND>", getName(SECOND)));
            return true;
        }

        if (args[1].equalsIgnoreCase(this.getBanSystem("PermanentName"))) {
            if (!this.isAllowed(cs, "mute.permanent")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("mute.permanent")));
                return true;
            }

            if (target.isOnline()) if (this.isAllowed(target.getPlayer(), "mute.exempt", true)) {
                cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.CannotMute", label, cmd.getName(), cs, target.getName()));
                return true;
            }

            String reason = this.getMessageWithStringTarget("Mute.DefaultReason", label, cmd.getName(), cs, target.getName());

            boolean shadow = false;

            if (args.length > 3)
                if (args[2].equalsIgnoreCase(this.getBanSystem("ShadowBan")) && this.isAllowed(cs, "mute.shadow.permanent", true)) {
                    shadow = true;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        String s;
                        if (i == args.length - 1) s = args[i];
                        else s = args[i] + " ";
                        sb.append(s);
                    }
                    reason = sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        String s;
                        if (i == args.length - 1) s = args[i];
                        else s = args[i] + " ";
                        sb.append(s);
                    }
                    reason = sb.toString();
                }

            if (reason.equalsIgnoreCase(""))
                reason = this.getMessageWithStringTarget("Mute.DefaultReason", label, cmd.getName(), cs, target.getName());

            long time = -1;
            Mute mute;

            if (shadow)
                mute = this.getPlugin().getMuteManager().addMute(target.getUniqueId(), sender, reason, true, time, YEAR);
            else
                mute = this.getPlugin().getMuteManager().addMute(target.getUniqueId(), sender, reason, time, YEAR);

            if (cs instanceof Player) this.plugin.getMuteManager().removeMute(((Player) cs).getUniqueId());

            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.Success", label, cmd.getName(), cs, target.getName()).replace("<REASON>", reason).replace("<UNMUTE_DATE>", mute.getUNMUTE_DATE()));

            String finalReason = reason;
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                AsyncMuteEvent asyncMuteEvent = new AsyncMuteEvent(cs, target, finalReason, mute.getUNMUTE_DATE());
                Bukkit.getPluginManager().callEvent(asyncMuteEvent);
            });
            return true;
        }

        if (!this.isAllowed(cs, "mute.temporary")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("mute.temporary")));
            return true;
        }

        if (target.isOnline()) if (this.isAllowed(target.getPlayer(), "mute.exempt", true)) {
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.CannotMute", label, cmd.getName(), cs, target.getName()));
            return true;
        }

        long time;
        try {
            time = Long.parseLong(args[1]);
        } catch (NumberFormatException ignored) {
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.NotANumber", label, cmd.getName(), cs, target.getName()).replace("<TIME>", args[1]));
            return true;
        }

        TimeUnit timeUnit = TimeUnit.getFromName(args[2]);
        if (timeUnit == null) {
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.NotATimeUnit", label, cmd.getName(), cs, target.getName()).replace("<TIMEUNIT>", args[2]));
            return true;
        }

        String reason = this.getMessageWithStringTarget("Mute.DefaultReason", label, cmd.getName(), cs, target.getName());

        boolean shadow = false;

        if (args.length > 3)
            if (args[3].equalsIgnoreCase(this.getBanSystem("ShadowBan")) && this.isAllowed(cs, "mute.shadow.permanent", true)) {
                shadow = true;
                StringBuilder sb = new StringBuilder();
                for (int i = 4; i < args.length; i++) {
                    String s;
                    if (i == args.length - 1) s = args[i];
                    else s = args[i] + " ";
                    sb.append(s);
                }
                reason = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    String s;
                    if (i == args.length - 1) s = args[i];
                    else s = args[i] + " ";
                    sb.append(s);
                }
                reason = sb.toString();
            }

        if (reason.equalsIgnoreCase(""))
            reason = this.getMessageWithStringTarget("Mute.DefaultReason", label, cmd.getName(), cs, target.getName());

        Mute mute;

        if (shadow)
            mute = this.plugin.getMuteManager().addMute(target.getUniqueId(), sender, reason, true, time, timeUnit);
        else
            mute = this.plugin.getMuteManager().addMute(target.getUniqueId(), sender, reason, time, timeUnit);

        if (cs instanceof Player) this.plugin.getMuteManager().removeMute(((Player) cs).getUniqueId());

        cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Mute.Success", label, cmd.getName(), cs, target.getName()).replace("<REASON>", reason).replace("<UNMUTE_DATE>", mute.getUNMUTE_DATE()));

        String finalReason = reason;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            AsyncMuteEvent asyncMuteEvent = new AsyncMuteEvent(cs, target, finalReason, mute.getUNMUTE_DATE());
            Bukkit.getPluginManager().callEvent(asyncMuteEvent);
        });
        return true;
    }
}
