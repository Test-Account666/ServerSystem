package me.testaccount666.serversystem.commands.executables.commandspy;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.MessageManager;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ListenerCommandSpy implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        sendCommandSpy(event.getPlayer().getName(), event.getMessage());
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        sendCommandSpy(UserManager.getConsoleUser().getName().get(), event.getCommand());
    }

    private void sendCommandSpy(String sender, String command) {
        if (!command.startsWith("/")) command = "/${command}";

        for (var cachedUser : ServerSystem.Instance.getUserManager().getCachedUsers()) {
            if (!cachedUser.isOnlineUser()) continue;

            var user = cachedUser.getOfflineUser();

            if (!user.isCommandSpyEnabled()) continue;

            var player = user.getPlayer().getPlayer();

            if (player == null) continue;

            var formatOptional = MessageManager.getCommandMessage(player, "CommandSpy.Format", sender, null, false);
            if (formatOptional.isEmpty()) {
                Bukkit.getLogger().warning("An error occurred trying to fetch CommandSpy Format!");
                continue;
            }

            var format = formatOptional.get();
            format = format.replace("<COMMAND>", command);

            player.sendMessage(format);
        }
    }
}
