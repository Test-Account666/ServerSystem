package me.testaccount666.serversystem.commands.executables.skull;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonParser;
import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SkullCreator {

    private static HttpURLConnection createConnection() throws URISyntaxException, IOException {
        var apiURL = new URI("https://api.mineskin.org/v2/generate").toURL();
        var connection = (HttpURLConnection) apiURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        var timeOut = (int) TimeUnit.SECONDS.toMillis(30);

        connection.setConnectTimeout(timeOut);
        connection.setReadTimeout(timeOut);
        return connection;
    }

    public ItemStack getSkull(UUID uuid) {
        var playerProfile = Bukkit.createProfile(uuid);

        return getSkullByPlayerProfile(playerProfile);
    }

    public ItemStack getSkull(String name) {
        var playerProfile = Bukkit.createProfile(name);

        return getSkullByPlayerProfile(playerProfile);
    }

    public ItemStack getSkull(OfflinePlayer offlinePlayer) {
        var playerProfile = offlinePlayer.getPlayerProfile();

        return getSkullByPlayerProfile(playerProfile);
    }

    public ItemStack getSkullByTexture(String base64) {
        try {
            return getSkullByTexture(URI.create(new String(Base64.getDecoder().decode(base64))).toURL());
        } catch (MalformedURLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public ItemStack getSkullByTexture(URL textureURL) {
        try {
            var response = getResponse(textureURL);

            var root = JsonParser.parseString(response).getAsJsonObject();
            var texture = root
                    .getAsJsonObject("skin")
                    .getAsJsonObject("texture")
                    .getAsJsonObject("data");

            var value = texture.get("value").getAsString();
            var signature = texture.get("signature").getAsString();

            var playerProfile = Bukkit.getServer().createProfile(UUID.randomUUID());
            playerProfile.setProperty(new ProfileProperty("textures", value, signature));

            return getSkullByPlayerProfile(playerProfile);
        } catch (Exception exception) {
            ServerSystem.getLog().log(Level.WARNING, "An error occurred trying to fetch skin from Mineskin for URL '${textureURL}'", exception);
            return null;
        }
    }

    private synchronized Optional<String> getCachedResponse(URL textureURL) throws IOException {
        var pluginDir = Path.of("plugins", "ServerSystem", "data");
        if (!Files.exists(pluginDir)) Files.createDirectories(pluginDir);

        // Yes, inconsistent with User Data, but a zip archive allows us to read individual entries more easily
        var cacheZip = pluginDir.resolve("MineSkinCache.zip");

        var base64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(textureURL.toString().getBytes(StandardCharsets.UTF_8));
        var entryName = "${base64}.json";

        if (!Files.exists(cacheZip)) return Optional.empty();

        var zipUri = URI.create("jar:${cacheZip.toUri()}");
        try (var zipFileSystem = getFileSystem(zipUri, Map.of())) {
            var entry = zipFileSystem.getPath("/" + entryName);
            if (!Files.exists(entry)) return Optional.empty();

            return Optional.of(Files.readString(entry, StandardCharsets.UTF_8));
        }
    }

    private FileSystem getFileSystem(URI zipUri, Map<String, Object> environment) throws IOException {
        try {
            return FileSystems.newFileSystem(zipUri, environment);
        } catch (FileSystemAlreadyExistsException exception) {
            return FileSystems.getFileSystem(zipUri);
        }
    }

    private String getResponse(URL textureURL) throws IOException, URISyntaxException {
        var cachedResponse = getCachedResponse(textureURL);
        if (cachedResponse.isPresent()) return cachedResponse.get();

        var jsonPayload = """
                {
                  "variant":"classic",
                  "visibility":"public",
                  "url":"${textureURL}"
                }
                """;

        var connection = createConnection();

        //TODO: Should we add a config option for this?
        //connection.setRequestProperty("Authorization", "Bearer <token>");

        try (var outputStream = connection.getOutputStream()) {
            outputStream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        var status = connection.getResponseCode();

        ServerSystem.getLog().fine("'${textureURL}' got http response: ${status}");
        ServerSystem.getLog().fine("'${textureURL}' got txt response: ${connection.getResponseMessage()}");

        var success = status >= 200 && status < 300;

        String response;
        try (var scanner = new Scanner(new InputStreamReader(
                success? connection.getInputStream() : connection.getErrorStream(),
                StandardCharsets.UTF_8))) {
            response = scanner.useDelimiter("\\A").next();
        }

        if (!success) {
            if (status == 400) throw new IllegalArgumentException("Got status '400', is '${textureURL}' directly pointing to an image? ${response}");
            throw new IllegalArgumentException("Got status '${status}' for '${textureURL}' with message: ${response}");
        }

        saveResponse(textureURL, response);

        return response;
    }


    private synchronized void saveResponse(URL textureURL, String response) throws IOException {
        var pluginDir = Path.of("plugins", "ServerSystem", "data");
        if (!Files.exists(pluginDir)) Files.createDirectories(pluginDir);

        // Yes, inconsistent with User Data, but a zip archive allows us to read individual entries more easily
        var cacheZip = pluginDir.resolve("MineSkinCache.zip");

        var base64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(textureURL.toString().getBytes(StandardCharsets.UTF_8));
        var entryName = "${base64}.json";

        var environment = new HashMap<String, Object>();
        if (!Files.exists(cacheZip)) environment.put("create", true);

        environment.put("compressionLevel", 3);
        var zipUri = URI.create("jar:${cacheZip.toUri()}");
        try (var zipFileSystem = getFileSystem(zipUri, environment)) {
            var entry = zipFileSystem.getPath("/" + entryName);
            Files.writeString(entry, response, StandardCharsets.UTF_8);
        }
    }

    private ItemStack getSkullByPlayerProfile(PlayerProfile playerProfile) {
        var skullItem = new ItemStack(Material.PLAYER_HEAD);

        var skullMeta = (SkullMeta) skullItem.getItemMeta();

        assert skullMeta != null;
        skullMeta.setPlayerProfile(playerProfile);

        skullItem.setItemMeta(skullMeta);

        return skullItem;
    }
}
