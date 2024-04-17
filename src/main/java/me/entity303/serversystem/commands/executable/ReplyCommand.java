package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.entity303.serversystem.commands.executable.MsgCommand.reply;

public class ReplyCommand extends CommandUtils implements CommandExecutorOverload {

    public ReplyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getMessages().getCfg().getBoolean("Permissions.reply.required"))
                if (!this.plugin.getPermissions().hasPermission(commandSender, "reply.permission")) {
                    var permission = this.plugin.getPermissions().getPermission("reply.permission");
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                    return;
                }

            if (!reply.containsKey(commandSender)) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Reply"));
                return;
            }

            var target = reply.get(commandSender);

            if (target == null) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Reply"));
                return;
            }

            if (target instanceof Player)
                if (isAwayFromKeyboard((Player) target)) {
                    
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Msg.Afk"));
                }

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
                        
                        commandSender.sendMessage(this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Msg.Sender").replace("<MESSAGE>", msg));
                        return;
                    }
                }
            }

            var msg = Arrays.stream(arguments).map(arg -> arg + " ").collect(Collectors.joining());
            var command2 = command.getName();
            target.sendMessage(this.plugin.getMessages().getMessage(commandLabel, command2, commandSender, target, "Msg.Target").replace("<MESSAGE>", msg));
            var command1 = command.getName();
            commandSender.sendMessage(this.plugin.getMessages().getMessage(commandLabel, command1, commandSender, target, "Msg.Sender").replace("<MESSAGE>", msg));

            for (var all : this.plugin.getSocialSpy()) {
                
                all.sendMessage(this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Msg.SocialSpy").replace("<MESSAGE>", msg));
            }

            reply.remove(target);
            reply.put(commandSender, target);
            reply.put(target, commandSender);
        });
        return true;
    }
}
