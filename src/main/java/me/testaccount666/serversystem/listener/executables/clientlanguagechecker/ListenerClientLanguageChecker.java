package me.testaccount666.serversystem.listener.executables.clientlanguagechecker;

import com.destroystokyo.paper.ClientOption;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerClientLanguageChecker implements Listener {
    private boolean _useClientLanguage;

    public ListenerClientLanguageChecker() {
        var configManager = ServerSystem.Instance.getRegistry().getService(ConfigurationManager.class);
        _useClientLanguage = configManager.getGeneralConfig().getBoolean("Language.UseClientLanguage", false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var userOptional = ServerSystem.Instance.getRegistry().getService(UserManager.class).getUser(event.getPlayer());
        if (userOptional.isEmpty()) return;
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) return;
        var user = (User) cachedUser.getOfflineUser();

        if (!user.isUsesDefaultLanguage()) return;

        //TODO: Use default language
        if (!_useClientLanguage) return;

        var locale = event.getPlayer().getClientOption(ClientOption.LOCALE);
        var language = "English";

        if (locale.startsWith("en_")) language = "English";
        if (locale.startsWith("de_")) language = "German";
        if (locale.startsWith("sl_")) language = "Slovene";

        user.setPlayerLanguage(language.toLowerCase());
    }
}
