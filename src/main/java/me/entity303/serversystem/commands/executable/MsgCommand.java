package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
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


@ServerSystemCommand(name = "Msg")
public class MsgCommand implements ICommandExecutorOverload {
    public static final HashMap<CommandSender, CommandSender> REPLY_MAP = new HashMap<>();
    protected final ServerSystem _plugin;

    public MsgCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            if (this._plugin.GetMessages().GetConfiguration().GetBoolean("Permissions.msg.required")) {
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "msg.permission")) {
                    var permission = this._plugin.GetPermissions().GetPermission("msg.permission");
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                    return;
                }
            }

            if (arguments.length <= 1) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Msg"));
                return;
            }

            var target = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
            if (target == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
                return;
            }

            if (CommandUtils.IsAwayFromKeyboard(target)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Msg.Afk"));
            }

            if (commandSender instanceof Player player) {
                var muteManager = this._plugin.GetMuteManager();
                if (muteManager.IsMuted(player)) {
                    var mute = muteManager.GetMute(player);
                    var unmuted = false;
                    if (mute.GetExpireTime() > 0) {
                        if (mute.GetExpireTime() <= System.currentTimeMillis()) {
                            muteManager.RemoveMute(player.getUniqueId());
                            unmuted = true;
                        }
                    }
                    if (!unmuted) {
                        if (!mute.IsShadow()) {
                            String senderName = null;
                            try {
                                senderName = Bukkit.getOfflinePlayer(UUID.fromString(mute.GetSenderUuid())).getName();
                            } catch (Exception ignored) {
                            }
                            if (senderName == null) senderName = mute.GetSenderUuid();
                            player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                    .GetMessage("mute", "mute", senderName, player, "Mute.Muted")
                                                                                                    .replace("<UNMUTE_DATE>", mute.GetExpireDate()));
                            return;
                        }

                        var msg = IntStream.range(1, arguments.length).mapToObj(index -> arguments[index] + " ").collect(Collectors.joining());

                        commandSender.sendMessage(
                                this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Msg.Sender").replace("<MESSAGE>", msg));
                        return;
                    }
                }
            }
            if (this._plugin.GetMsgOff().contains(target)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Msg.Deactivated"));
                return;
            }

            var msg = IntStream.range(1, arguments.length).mapToObj(index -> arguments[index] + " ").collect(Collectors.joining());

            target.sendMessage(this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, target, "Msg.Target").replace("<MESSAGE>", msg));

            commandSender.sendMessage(
                    this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, target, "Msg.Sender").replace("<MESSAGE>", msg));

            for (var all : this._plugin.GetSocialSpy())
                all.sendMessage(this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Msg.SocialSpy").replace("<MESSAGE>", msg));

            MsgCommand.REPLY_MAP.remove(target);
            MsgCommand.REPLY_MAP.put(commandSender, target);
            MsgCommand.REPLY_MAP.put(target, commandSender);

        });
        return true;
    }
}
