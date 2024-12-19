package me.entity303.serversystem.bstats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 bStats collects some data for plugin authors.
 <p>
 Check out <a href="https://bStats.org/">...</a> to learn more about bStats!
 */
public class MetricsLite {
    // The version of this bStats class
    public static final int B_STATS_VERSION = 1;
    // The url to which the data is sent
    private static final String SUBMIT_URL = "https://bStats.org/submitData/bukkit";
    // Should failed requests be logged?
    private static boolean LOG_FAILED_REQUESTS;
    // Should the sent data be logged?
    private static boolean LOG_SENT_DATA;
    // Should the response text be logged?
    private static boolean LOG_RESPONSE_DATA_TEXT;
    // The uuid of the server
    private static String SERVER_UUID;

    static {
        // You can use the property to disable the check in your test environment
        if (System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {
            // Maven's Relocate is clever and changes strings, too. So we have to use this little "trick" ... :D
            final var defaultPackage = new String(new byte[] { 'o', 'r', 'g', '.', 'b', 's', 't', 'a', 't', 's', '.', 'b', 'u', 'k', 'k', 'i', 't' });
            final var examplePackage = new String(new byte[] { 'y', 'o', 'u', 'r', '.', 'p', 'a', 'c', 'k', 'a', 'g', 'e' });
            // We want to make sure nobody just copy & pastes the example and use the wrong package names
            if (MetricsLite.class.getPackage().getName().equals(defaultPackage) || MetricsLite.class.getPackage().getName().equals(examplePackage)) {
                throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
            }
        }
    }

    // The plugin
    private final ServerSystem _plugin;
    // The plugin id
    private final int _pluginId;
    // Is bStats enabled on this server?
    private final boolean _enabled;

    /**
     Class constructor.

     @param plugin The plugin which stats should be submitted.
     @param pluginId The id of the plugin.
     It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
     */
    public MetricsLite(ServerSystem plugin, int pluginId) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null!");
        this._plugin = plugin;
        this._pluginId = pluginId;

        // Get the config file
        var bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
        var configFile = new File(bStatsFolder, "config.yml");
        var config = YamlConfiguration.loadConfiguration(configFile);

        // Check if the config file exists
        if (!config.isSet("serverUuid")) {

            // Add default values
            config.addDefault("enabled", true);
            // Every server gets it's unique random id.
            config.addDefault("serverUuid", UUID.randomUUID().toString());
            // Should failed request be logged?
            config.addDefault("logFailedRequests", false);
            // Should the sent data be logged?
            config.addDefault("logSentData", false);
            // Should the response text be logged?
            config.addDefault("logResponseStatusText", false);

            // Inform the server owners about bStats
            config.options().header("""
                                    bStats collects some data for plugin authors like how many servers are using their plugins.
                                    To honor their work, you should not disable it.
                                    This has nearly no effect on the server performance!
                                    Check out https://bStats.org/ to learn more :)""").copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException ignored) {
            }
        }

        // Load the data
        MetricsLite.SERVER_UUID = config.getString("serverUuid");
        MetricsLite.LOG_FAILED_REQUESTS = config.getBoolean("logFailedRequests", false);
        this._enabled = config.getBoolean("enabled", true);
        MetricsLite.LOG_SENT_DATA = config.getBoolean("logSentData", false);
        MetricsLite.LOG_RESPONSE_DATA_TEXT = config.getBoolean("logResponseStatusText", false);
        if (this._enabled) {
            var found = false;
            // Search for all other bStats Metrics classes to see if we are the first one
            for (var service : Bukkit.getServicesManager().getKnownServices())
                try {
                    service.getField("B_STATS_VERSION"); // Our identifier :)
                    found = true; // We aren't the first
                    break;
                } catch (NoSuchFieldException ignored) {
                }
            // Register our service
            Bukkit.getServicesManager().register(MetricsLite.class, this, plugin, ServicePriority.Normal);
            // We are the first!
            if (!found) this.StartSubmitting();
        }
    }

    /**
     Starts the Scheduler which submits our data every 30 minutes.
     */
    private void StartSubmitting() {
        final var timer = new Timer(true); // We use a timer cause the Bukkit scheduler is affected by server lags
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!MetricsLite.this._plugin.isEnabled()) { // Plugin was disabled
                    timer.cancel();
                    return;
                }
                // Nevertheless we want our code to run in the Bukkit main thread, so we have to use the Bukkit scheduler
                // Don't be afraid! The connection to the bStats server is still async, only the stats collection is sync ;)
                Bukkit.getScheduler().runTask(MetricsLite.this._plugin, MetricsLite.this::SubmitData);
            }
        }, 1000 * 60 * 5, 1000 * 60 * 30);
        // Submit the data every 30 minutes, first time after 5 minutes to give other plugins enough time to start
        // WARNING: Changing the frequency has no effect but your plugin WILL be blocked/deleted!
        // WARNING: Just don't do it!
    }

    /**
     Collects the data and sends it afterwards.
     */
    private void SubmitData() {
        final var data = this.GetServerData();

        var pluginData = new JsonArray();
        // Search for all other bStats Metrics classes to get their plugin data
        for (var service : Bukkit.getServicesManager().getKnownServices())
            try {
                service.getField("B_STATS_VERSION"); // Our identifier :)

                for (var provider : Bukkit.getServicesManager().getRegistrations(service))
                    try {
                        var plugin = provider.getService().getMethod("getPluginData").invoke(provider.getProvider());
                        // old bstats version compatibility
                        if (plugin instanceof JsonObject) {
                            pluginData.add((JsonObject) plugin);
                        } else {
                            try {
                                var jsonObjectJsonSimple = Class.forName("org.json.simple.JSONObject");
                                if (plugin.getClass().isAssignableFrom(jsonObjectJsonSimple)) {
                                    var jsonStringGetter = jsonObjectJsonSimple.getDeclaredMethod("toJSONString");
                                    jsonStringGetter.setAccessible(true);
                                    var jsonString = (String) jsonStringGetter.invoke(plugin);
                                    var object = new JsonParser().parse(jsonString).getAsJsonObject();
                                    pluginData.add(object);
                                }
                            } catch (ClassNotFoundException exception) {
                                // minecraft version 1.14+
                                if (MetricsLite.LOG_FAILED_REQUESTS) {
                                    this._plugin.Error("Encountered unexpected exception: " + exception.getMessage());
                                    exception.printStackTrace();
                                }
                            }
                        }
                    } catch (NullPointerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                    }
            } catch (NoSuchFieldException ignored) {
            }

        data.add("plugins", pluginData);

        // Create a new thread for the connection to the bStats server
        new Thread(() -> {
            try {
                // Send the data
                MetricsLite.SendData(this._plugin, data);
            } catch (Exception exception) {
                // Something went wrong! :(
                if (MetricsLite.LOG_FAILED_REQUESTS) {
                    this._plugin.Error("Could not submit plugin stats of " + this._plugin.getName());
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    /**
     Gets the server specific data.

     @return The server specific data.
     */
    private JsonObject GetServerData() {
        // Minecraft specific data
        int playerAmount;
        try {
            // Around MC 1.8 the return type was changed to a collection from an array,
            // This fixes java.lang.NoSuchMethodError: org.bukkit.Bukkit.getOnlinePlayers()Ljava/util/Collection;
            var onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            playerAmount = onlinePlayersMethod.getReturnType().equals(Collection.class)?
                           ((Collection<?>) onlinePlayersMethod.invoke(Bukkit.getServer())).size() :
                           ((Player[]) onlinePlayersMethod.invoke(Bukkit.getServer())).length;
        } catch (Exception exception) {
            playerAmount = Bukkit.getOnlinePlayers().size(); // Just use the new method if the Reflection failed
        }
        var onlineMode = Bukkit.getOnlineMode()? 1 : 0;
        var bukkitVersion = Bukkit.getVersion();
        var bukkitName = Bukkit.getName();

        // OS/Java specific data
        var javaVersion = System.getProperty("java.version");
        var osName = System.getProperty("os.name");
        var osArch = System.getProperty("os.arch");
        var osVersion = System.getProperty("os.version");
        var coreCount = Runtime.getRuntime().availableProcessors();

        var data = new JsonObject();

        data.addProperty("serverUUID", MetricsLite.SERVER_UUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("bukkitVersion", bukkitVersion);
        data.addProperty("bukkitName", bukkitName);

        data.addProperty("javaVersion", javaVersion);
        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    /**
     Sends the data to the bStats server.

     @param plugin Any plugin. It's just used to get a logger instance.
     @param data The data to send.

     @throws Exception If the request failed.
     */
    private static void SendData(ServerSystem plugin, JsonObject data) throws Exception {
        if (data == null) throw new IllegalArgumentException("Data cannot be null!");
        if (Bukkit.isPrimaryThread()) throw new IllegalAccessException("This method must not be called from the main thread!");
        if (MetricsLite.LOG_SENT_DATA) plugin.Info("Sending data to bStats: " + data);
        var connection = (HttpsURLConnection) new URL(MetricsLite.SUBMIT_URL).openConnection();

        // Compress the data to save bandwidth
        var compressedData = MetricsLite.Compress(data.toString());

        // Add headers
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
        connection.setRequestProperty("User-Agent", "MC-Server/" + MetricsLite.B_STATS_VERSION);

        // Send data
        connection.setDoOutput(true);
        try (var outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(compressedData);
        }

        var builder = new StringBuilder();
        try (var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) builder.append(line);
        } catch (IOException exception) {
            if (!exception.getMessage().toLowerCase().contains("code: 429")) {
                exception.printStackTrace();
                return;
            }
        }

        if (MetricsLite.LOG_RESPONSE_DATA_TEXT) plugin.Info("Sent data to bStats and received response: " + builder);
    }

    /**
     Gzips the given String.

     @param str The string to gzip.

     @return The gzipped String.

     @throws IOException If the compression failed.
     */
    private static byte[] Compress(final String str) throws IOException {
        if (str == null) return null;
        var outputStream = new ByteArrayOutputStream();
        try (var gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
        }
        return outputStream.toByteArray();
    }

    /**
     Checks if bStats is enabled.

     @return Whether bStats is enabled or not.
     */
    public boolean IsEnabled() {
        return this._enabled;
    }

    /**
     Gets the plugin specific data.
     This method is called using Reflection.

     @return The plugin specific data.
     */
    public JsonObject GetPluginData() {
        var data = new JsonObject();

        var pluginName = this._plugin.getDescription().getName();
        var pluginVersion = this._plugin.getDescription().getVersion();

        data.addProperty("pluginName", pluginName); // Append the name of the plugin
        data.addProperty("id", this._pluginId); // Append the id of the plugin
        data.addProperty("pluginVersion", pluginVersion); // Append the version of the plugin
        data.add("customCharts", new JsonArray());

        return data;
    }

}
