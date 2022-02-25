package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.bansystem.ManagerMute;
import me.entity303.serversystem.bansystem.Mute;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class MsgCommand extends MessageUtils implements CommandExecutor {
    public static final HashMap<CommandSender, CommandSender> reply = new HashMap<>();

    public MsgCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getMessages().getCfg().getBoolean("Permissions.msg.required"))
                if (!this.isAllowed(cs, "msg.permission")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("msg.permission")));
                    return;
                }
            if (args.length == 0)
                cs.sendMessage(this.getPrefix() + this.getSyntax("Msg", label, cmd.getName(), cs, null));
            else if (args.length != 1) {

                Player target = this.getPlayer(cs, args[0]);
                if (target == null) {
                    cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
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
                if (this.plugin.getMsgOff().contains(target)) {
                    cs.sendMessage(this.getPrefix() + this.getMessage("Msg.Deactivated", label, cmd.getName(), cs, target));
                    return;
                }
                String msg = IntStream.range(1, args.length).mapToObj(i -> args[i] + " ").collect(Collectors.joining());
                target.sendMessage(this.getMessage("Msg.Target", label, cmd.getName(), cs, target).replace("<MESSAGE>", msg));
                cs.sendMessage(this.getMessage("Msg.Sender", label, cmd.getName(), cs, target).replace("<MESSAGE>", msg));
                for (Player all : this.plugin.getSocialSpy())
                    all.sendMessage(this.getMessage("Msg.SocialSpy", label, cmd.getName(), cs, target).replace("<MESSAGE>", msg));
                MsgCommand.reply.remove(target);
                MsgCommand.reply.put(cs, target);
                MsgCommand.reply.put(target, cs);
            } else cs.sendMessage(this.getPrefix() + this.getSyntax("Msg", label, cmd.getName(), cs, null));

        });
        return true;
    }
}
