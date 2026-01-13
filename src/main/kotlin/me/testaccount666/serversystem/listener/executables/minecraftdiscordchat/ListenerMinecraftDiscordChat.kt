package me.testaccount666.serversystem.listener.executables.minecraftdiscordchat

import com.google.gson.JsonObject
import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.ComponentColor.Companion.componentToString
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.function.Function
import java.util.logging.Level

class ListenerMinecraftDiscordChat : Listener {
    private val _enabled: Boolean
    private val _webHookUri: URI

    init {
        val configManager = instance.registry.getService<ConfigurationManager>()
        val generalConfig = configManager.generalConfig

        var uri = generalConfig.getString("MinecraftDiscordChat.WebhookUrl")
        requireNotNull(uri) { "MinecraftDiscordChat.WebhookUrl not set!" }

        _enabled = generalConfig.getBoolean("MinecraftDiscordChat.Enabled")
        _webHookUri = URI(uri)
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        if (!_enabled) return

        var message = componentToString(event.message())
        message = stripColor(message)
        message = message.replace("@", "\\@")

        HttpClient.newHttpClient().use { httpClient ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("content", message)
            jsonObject.addProperty("username", event.getPlayer().name)
            jsonObject.addProperty("avatar_url", "https://minotar.net/armor/bust/${event.getPlayer().name}/500.png")

            val request = HttpRequest.newBuilder()
                .uri(_webHookUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                .build()
            httpClient.sendAsync<String?>(request, HttpResponse.BodyHandlers.ofString())
                .exceptionally(Function { exception ->
                    log.log(Level.SEVERE, "Couldn't send Minecraft Discord Chat message to Webhook '${_webHookUri}'", exception)
                    null
                })
        }
    }
}
