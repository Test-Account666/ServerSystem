package me.testaccount666.serversystem.listener.executables.minecraftdiscordchat;

import com.google.gson.JsonObject;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.SneakyThrows;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public class ListenerMinecraftDiscordChat implements Listener {
    private final boolean _enabled;
    private final URI _webHookUri;

    @SneakyThrows
    public ListenerMinecraftDiscordChat() {
        var generalConfig = ServerSystem.Instance.getConfigManager().getGeneralConfig();
        _enabled = generalConfig.getBoolean("MinecraftDiscordChat.Enabled");
        _webHookUri = new URI(generalConfig.getString("MinecraftDiscordChat.WebhookUrl"));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!_enabled) return;

        var message = ComponentColor.componentToString(event.message());
        message = ChatColor.stripColor(message);
        message = message.replace("@", "\\@");

        try (var httpClient = HttpClient.newHttpClient()) {
            var jsonObject = new JsonObject();
            jsonObject.addProperty("content", message);
            jsonObject.addProperty("username", event.getPlayer().getName());
            jsonObject.addProperty("avatar_url", "https://minotar.net/armor/bust/${event.getPlayer().getName()}/500.png");

            var request = HttpRequest.newBuilder()
                    .uri(_webHookUri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .exceptionally(exception -> {
                        ServerSystem.getLog().log(Level.SEVERE, "Couldn't send Minecraft Discord Chat message to Webhook '${_webHookUri}'", exception);
                        return null;
                    });
        }
    }
}
