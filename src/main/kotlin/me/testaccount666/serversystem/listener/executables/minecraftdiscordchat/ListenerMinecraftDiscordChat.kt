package me.testaccount666.serversystem.listener.executables.minecraftdiscordchat

import com.google.gson.JsonObject
import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.paperktx.extensions.ComponentExtensions.asString
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import org.bukkit.event.*
import java.net.URI
import java.net.http.*
import java.util.function.Function
import java.util.logging.Level

class ListenerMinecraftDiscordChat : Listener {
    private val _enabled: Boolean
    private val _webHookUri: URI

    init {
        val configManager = getService<ConfigurationManager>()
        val generalConfig = configManager.generalConfig

        var uri = generalConfig.getString("MinecraftDiscordChat.WebhookUrl")
        requireNotNull(uri) { "MinecraftDiscordChat.WebhookUrl not set!" }

        _enabled = generalConfig.getBoolean("MinecraftDiscordChat.Enabled")
        _webHookUri = URI(uri)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onChat(event: AsyncChatEvent) {
        if (!_enabled) return

        val message = event.message().asString(true).replace("@", "\\@")

        HttpClient.newHttpClient().use { httpClient ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("content", message)
            jsonObject.addProperty("username", event.getPlayer().name)
            jsonObject.addProperty("avatar_url", "https://minotar.net/armor/bust/${event.getPlayer().name}/500.png")

            val request =
                HttpRequest
                    .newBuilder()
                    .uri(_webHookUri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build()
            httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .exceptionally(
                    Function { exception ->
                        log.log(Level.SEVERE, "Couldn't send Minecraft Discord Chat message to Webhook '${_webHookUri}'", exception)
                        null
                    },
                )
        }
    }
}
