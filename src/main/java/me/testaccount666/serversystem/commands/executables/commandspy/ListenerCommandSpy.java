package me.testaccount666.serversystem.commands.executables.commandspy;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;

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

            var user = (User) cachedUser.getOfflineUser();

            if (!user.isCommandSpyEnabled()) continue;

            var finalCommand = command;
            command("CommandSpy.Format", user).prefix(false).target(sender)
                    .modifier(message -> message.replace("<COMMAND>", finalCommand)).build();
        }
    }
}
