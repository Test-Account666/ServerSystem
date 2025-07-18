package me.testaccount666.serversystem.listener.executables.clientlanguagechecker;

import com.destroystokyo.paper.ClientOption;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerClientLanguageChecker implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(event.getPlayer());
        if (userOptional.isEmpty()) return;
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) return;
        var user = (User) cachedUser.getOfflineUser();

        if (!user.isUsesDefaultLanguage()) return;

        var locale = event.getPlayer().getClientOption(ClientOption.LOCALE);
        var language = "English";

        if (locale.startsWith("en_")) language = "English";
        if (locale.startsWith("de_")) language = "German";
        if (locale.startsWith("sl_")) language = "Slovene";

        user.setPlayerLanguage(language.toLowerCase());
    }
}
