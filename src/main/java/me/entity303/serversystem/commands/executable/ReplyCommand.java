package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.entity303.serversystem.commands.executable.MsgCommand.REPLY_MAP;

@ServerSystemCommand(name = "Reply")
public class ReplyCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public ReplyCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            if (this._plugin.GetMessages().GetConfiguration().GetBoolean("Permissions.reply.required")) {
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "reply.permission")) {
                    var permission = this._plugin.GetPermissions().GetPermission("reply.permission");
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                    return;
                }
            }

            if (!REPLY_MAP.containsKey(commandSender)) {

                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Reply"));
                return;
            }

            var target = REPLY_MAP.get(commandSender);

            if (target == null) {

                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Reply"));
                return;
            }

            if (target instanceof Player) {
                if (CommandUtils.IsAwayFromKeyboard((Player) target)) {
                    commandSender.sendMessage(
                            this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Msg.Afk"));
                }
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

            var msg = Arrays.stream(arguments).map(arg -> arg + " ").collect(Collectors.joining());
            var command2 = command.getName();
            target.sendMessage(this._plugin.GetMessages().GetMessage(commandLabel, command2, commandSender, target, "Msg.Target").replace("<MESSAGE>", msg));
            var command1 = command.getName();
            commandSender.sendMessage(this._plugin.GetMessages().GetMessage(commandLabel, command1, commandSender, target, "Msg.Sender").replace("<MESSAGE>", msg));

            for (var all : this._plugin.GetSocialSpy())
                all.sendMessage(this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, target, "Msg.SocialSpy").replace("<MESSAGE>", msg));

            REPLY_MAP.remove(target);
            REPLY_MAP.put(commandSender, target);
            REPLY_MAP.put(target, commandSender);
        });
        return true;
    }
}
