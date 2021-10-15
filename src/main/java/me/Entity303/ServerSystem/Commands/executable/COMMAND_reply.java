package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.BanSystem.ManagerMute;
import me.Entity303.ServerSystem.BanSystem.Mute;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.Entity303.ServerSystem.Commands.executable.COMMAND_msg.reply;

public class COMMAND_reply extends MessageUtils implements CommandExecutor {

    public COMMAND_reply(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getMessages().getCfg().getBoolean("Permissions.reply.required"))
                if (!this.isAllowed(cs, "reply.permission")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("reply.permission")));
                    return;
                }

            if (!reply.containsKey(cs)) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Reply", label, cmd.getName(), cs, null));
                return;
            }

            CommandSender target = reply.get(cs);

            if (target == null) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Reply", label, cmd.getName(), cs, null));
                return;
            }

            if (cs instanceof Player) {
                ManagerMute muteManager = this.plugin.getMuteManager();
                Player player = (Player) cs;
                if (muteManager.isMuted(player)) {
                    Mute mute = muteManager.getMute(player);
                    boolean unmuted = false;
                    if (mute.getUNMUTE_TIME() > 0) if (mute.getUNMUTE_TIME() <= System.currentTimeMillis()) {
                        muteManager.removeMute(player.getUniqueId());
                        unmuted = true;
                    }
                    if (!unmuted) {
                        if (!mute.isSHADOW()) {
                            String senderName = null;
                            try {
                                senderName = Bukkit.getOfflinePlayer(UUID.fromString(mute.getSENDER_UUID())).getName();
                            } catch (Exception ignored) {
                            }
                            if (senderName == null) senderName = mute.getSENDER_UUID();
                            player.sendMessage(this.getPrefix() + this.getMessage("Mute.Muted", "mute", "mute", senderName, player).replace("<UNMUTE_DATE>", mute.getUNMUTE_DATE()));
                            return;
                        }
                        String msg = IntStream.range(1, args.length).mapToObj(i -> args[i] + " ").collect(Collectors.joining());
                        cs.sendMessage(this.getMessage("Msg.Sender", label, cmd.getName(), cs, target).replace("<MESSAGE>", msg));
                        return;
                    }
                }
            }

            String msg = Arrays.stream(args).map(arg -> arg + " ").collect(Collectors.joining());
            target.sendMessage(this.getMessage("Msg.Target", label, cmd.getName(), cs, target).replace("<MESSAGE>", msg));
            cs.sendMessage(this.getMessage("Msg.Sender", label, cmd.getName(), cs, target).replace("<MESSAGE>", msg));

            reply.remove(target);
            reply.put(cs, target);
            reply.put(target, cs);
        });
        return true;
    }
}
