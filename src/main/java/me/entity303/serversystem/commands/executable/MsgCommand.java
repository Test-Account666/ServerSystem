package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class MsgCommand extends CommandUtils implements CommandExecutorOverload {
    public static final HashMap<CommandSender, CommandSender> reply = new HashMap<>();

    public MsgCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getMessages().getCfg().getBoolean("Permissions.msg.required"))
                if (!this.plugin.getPermissions().hasPermission(commandSender, "msg.permission")) {
                    var permission = this.plugin.getPermissions().getPermission("msg.permission");
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                    return;
                }

            if (arguments.length <= 1) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Msg"));
                return;
            }

            var target = this.getPlayer(commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
                return;
            }

            if (isAwayFromKeyboard(target))
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Msg.Afk"));

            if (commandSender instanceof Player player) {
                var muteManager = this.plugin.getMuteManager();
                if (muteManager.isMuted(player)) {
                    var mute = muteManager.getMute(player);
                    var unmuted = false;
                    if (mute.getUNMUTE_TIME() > 0)
                        if (mute.getUNMUTE_TIME() <= System.currentTimeMillis()) {
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
                            if (senderName == null)
                                senderName = mute.getSENDER_UUID();
                            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                                  .getMessage("mute", "mute", senderName, player, "Mute.Muted")
                                                                                                  .replace("<UNMUTE_DATE>", mute.getUNMUTE_DATE()));
                            return;
                        }

                        var msg = IntStream.range(1, arguments.length).mapToObj(i -> arguments[i] + " ").collect(Collectors.joining());

                        commandSender.sendMessage(
                                this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Msg.Sender").replace("<MESSAGE>", msg));
                        return;
                    }
                }
            }
            if (this.plugin.getMsgOff().contains(target)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Msg.Deactivated"));
                return;
            }

            var msg = IntStream.range(1, arguments.length).mapToObj(i -> arguments[i] + " ").collect(Collectors.joining());

            target.sendMessage(
                    this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, target, "Msg.Target").replace("<MESSAGE>", msg));

            commandSender.sendMessage(
                    this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, target, "Msg.Sender").replace("<MESSAGE>", msg));

            for (var all : this.plugin.getSocialSpy())
                all.sendMessage(this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Msg.SocialSpy").replace("<MESSAGE>", msg));

            MsgCommand.reply.remove(target);
            MsgCommand.reply.put(commandSender, target);
            MsgCommand.reply.put(target, commandSender);

        });
        return true;
    }
}
