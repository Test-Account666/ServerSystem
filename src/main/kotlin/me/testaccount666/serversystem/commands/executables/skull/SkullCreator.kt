package me.testaccount666.serversystem.commands.executables.skull

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import com.google.gson.JsonParser
import me.testaccount666.serversystem.ServerSystem.Companion.log
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.IOException
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.util.*
import java.util.Map
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class SkullCreator {
    fun getSkull(uuid: UUID): ItemStack {
        val playerProfile = Bukkit.createProfile(uuid)

        return getSkullByPlayerProfile(playerProfile)
    }

    fun getSkull(name: String): ItemStack {
        val playerProfile = Bukkit.createProfile(name)

        return getSkullByPlayerProfile(playerProfile)
    }

    fun getSkull(offlinePlayer: OfflinePlayer): ItemStack {
        val playerProfile = offlinePlayer.playerProfile

        return getSkullByPlayerProfile(playerProfile)
    }

    fun getSkullByTexture(base64: String?): ItemStack? {
        try {
            return getSkullByTexture(URI.create(String(Base64.getDecoder().decode(base64))).toURL())
        } catch (exception: MalformedURLException) {
            throw RuntimeException(exception)
        }
    }

    fun getSkullByTexture(textureURL: URL): ItemStack? {
        try {
            val response = getResponse(textureURL)

            val root = JsonParser.parseString(response).getAsJsonObject()
            val texture = root
                .getAsJsonObject("skin")
                .getAsJsonObject("texture")
                .getAsJsonObject("data")

            val value = texture.get("value").asString
            val signature = texture.get("signature").asString

            val playerProfile = Bukkit.getServer().createProfile(UUID.randomUUID())
            playerProfile.setProperty(ProfileProperty("textures", value, signature))

            return getSkullByPlayerProfile(playerProfile)
        } catch (exception: Exception) {
            log.log(Level.WARNING, "An error occurred trying to fetch skin from Mineskin for URL '${textureURL}'", exception)
            return null
        }
    }

    @Synchronized
    @Throws(IOException::class)
    private fun getCachedResponse(textureURL: URL): String? {
        if (!Files.exists(_DATA_DIRECTORY)) Files.createDirectories(_DATA_DIRECTORY)

        val base64 = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(textureURL.toString().toByteArray(StandardCharsets.UTF_8))
        val entryName = "${base64}.json"

        if (!Files.exists(_CACHE_FILE)) return null

        val zipUri = URI.create("jar:${_CACHE_FILE.toUri()}")
        getFileSystem(zipUri, Map.of<String, Any>()).use { zipFileSystem ->
            val entry = zipFileSystem.getPath("/$entryName")
            if (!Files.exists(entry)) return null
            return Files.readString(entry, StandardCharsets.UTF_8)
        }
    }

    @kotlin.jvm.Throws(IOException::class)
    private fun getFileSystem(zipUri: URI, environment: MutableMap<String, Any>): FileSystem {
        return try {
            FileSystems.newFileSystem(zipUri, environment)
        } catch (_: FileSystemAlreadyExistsException) {
            FileSystems.getFileSystem(zipUri)
        }
    }

    @Throws(IOException::class, URISyntaxException::class)
    private fun getResponse(textureURL: URL): String {
        val cachedResponse = getCachedResponse(textureURL)
        if (cachedResponse != null) return cachedResponse

        val jsonPayload = """
                {
                  "variant":"classic",
                  "visibility":"public",
                  "url":"${textureURL}"
                }
                
                """.trimIndent()

        val connection: HttpURLConnection = createConnection()

        connection.getOutputStream().use { outputStream ->
            outputStream.write(jsonPayload.toByteArray(StandardCharsets.UTF_8))
        }
        val status = connection.getResponseCode()

        log.fine("'${textureURL}' got http response: ${status}")
        log.fine("'${textureURL}' got txt response: ${connection.getResponseMessage()}")

        val success = status in 200..<300

        val response: String
        Scanner(
            InputStreamReader(
                if (success) connection.getInputStream() else connection.errorStream,
                StandardCharsets.UTF_8
            )
        ).use { scanner ->
            response = scanner.useDelimiter("\\A").next()
        }
        if (!success) {
            require(status != 400) { "Got status '400', is '${textureURL}' directly pointing to an image? ${response}" }
            throw IllegalArgumentException("Got status '${status}' for '${textureURL}' with message: ${response}")
        }

        saveResponse(textureURL, response)

        return response
    }


    @Synchronized
    private fun saveResponse(textureURL: URL, response: String) {
        if (!Files.exists(_DATA_DIRECTORY)) Files.createDirectories(_DATA_DIRECTORY)

        val base64 = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(textureURL.toString().toByteArray(StandardCharsets.UTF_8))
        val entryName = "${base64}.json"

        val environment = HashMap<String, Any>()
        if (!Files.exists(_CACHE_FILE)) environment["create"] = true

        environment["compressionLevel"] = 3
        val zipUri = URI.create("jar:${_CACHE_FILE.toUri()}")
        getFileSystem(zipUri, environment).use { zipFileSystem ->
            val entry = zipFileSystem.getPath("/$entryName")
            Files.writeString(entry, response, StandardCharsets.UTF_8)
        }
    }

    private fun getSkullByPlayerProfile(playerProfile: PlayerProfile): ItemStack {
        val skullItem = ItemStack(Material.PLAYER_HEAD)

        val skullMeta = skullItem.itemMeta as? SkullMeta ?: return skullItem

        skullMeta.playerProfile = playerProfile
        skullItem.setItemMeta(skullMeta)

        return skullItem
    }

    companion object {
        private val _DATA_DIRECTORY: Path = Path.of("plugins", "ServerSystem", "data")

        // Yes, inconsistent with User Data, but a zip archive allows us to read individual entries more easily
        private val _CACHE_FILE: Path = _DATA_DIRECTORY.resolve("MineSkinCache.zip")

        private fun createConnection(): HttpURLConnection {
            val apiURL = URI("https://api.mineskin.org/v2/generate").toURL()
            val connection = apiURL.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setDoOutput(true)
            connection.setRequestProperty("Content-Type", "application/json")
            val timeOut = TimeUnit.SECONDS.toMillis(30).toInt()

            connection.connectTimeout = timeOut
            connection.readTimeout = timeOut
            return connection
        }
    }
}
