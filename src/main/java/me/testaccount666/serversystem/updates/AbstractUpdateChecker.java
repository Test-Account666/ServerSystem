package me.testaccount666.serversystem.updates;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.utils.Version;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@RequiredArgsConstructor
public abstract class AbstractUpdateChecker {
    protected final URI updateURI;
    protected boolean isUpdateAvailable;
    protected boolean isUpdateDownloaded;
    @Setter
    protected boolean autoUpdate;
    protected Version latestVersion = null;
    protected boolean notifiedOnce;

    /**
     * Template method for checking updates. Subclasses should implement parseLatestVersion.
     */
    public CompletableFuture<Boolean> hasUpdate() {
        ServerSystem.getLog().fine("Checking for updates...");
        if (isUpdateAvailable) return CompletableFuture.completedFuture(true);

        var future = new CompletableFuture<Boolean>();

        Bukkit.getScheduler().runTaskAsynchronously(ServerSystem.Instance, () -> {
            try (var client = HttpClient.newHttpClient()) {
                var request = HttpRequest.newBuilder()
                        .timeout(Duration.ofMinutes(1))
                        .uri(updateURI).GET().build();

                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                var body = response.body();

                var parsedLatestVersion = parseLatestVersion(body);

                if (parsedLatestVersion.compareTo(ServerSystem.CURRENT_VERSION) > 0) {
                    isUpdateAvailable = true;
                    latestVersion = parsedLatestVersion;
                    ServerSystem.getLog().info("A new update is available! Current version: ${ServerSystem.CURRENT_VERSION}" +
                            ", Latest version: ${parsedLatestVersion}");
                } else if (!notifiedOnce) {
                    notifiedOnce = true;
                    ServerSystem.getLog().info("You're running the latest version of ServerSystem <3");
                }

                future.complete(isUpdateAvailable);

            } catch (Exception exception) {
                ServerSystem.getLog().log(Level.WARNING, "Failed to check for updates!", exception);
                future.completeExceptionally(exception);
            }
        });

        return future;
    }

    /**
     * Template method for downloading updates. Subclasses should implement getDownloadUrl.
     */
    public CompletableFuture<Boolean> downloadUpdate() {
        if (!autoUpdate) {
            ServerSystem.getLog().info("Auto update is disabled. Update can be downloaded manually.");
            return CompletableFuture.completedFuture(false);
        }
        if (isUpdateDownloaded) return CompletableFuture.completedFuture(true);

        ServerSystem.getLog().info("Downloading update...");

        var future = new CompletableFuture<Boolean>();

        Bukkit.getScheduler().runTaskAsynchronously(ServerSystem.Instance, () -> {
            if (latestVersion == null) {
                future.completeExceptionally(new IllegalStateException("No version info available for download."));
                return;
            }

            var downloadUrl = getDownloadUrl();

            try (var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofMinutes(1)).build()) {
                var request = HttpRequest.newBuilder()
                        .timeout(Duration.ofMinutes(1))
                        .uri(URI.create(downloadUrl)).GET().build();

                ServerSystem.getLog().info("Downloading update from ${downloadUrl}");

                var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                if (response.statusCode() != 200) {
                    future.completeExceptionally(new RuntimeException("Failed to download update: HTTP ${response.statusCode()}"));
                    return;
                }

                var pluginDir = ServerSystem.Instance.getDataFolder().getParentFile().toPath();
                var updateFile = pluginDir.resolve("update").resolve(getJarFileName());
                Files.createDirectories(updateFile.getParent());

                var in = response.body();
                var out = Files.newOutputStream(updateFile);

                var buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) out.write(buffer, 0, bytesRead);

                out.close();

                ServerSystem.getLog().info("Update downloaded to ${updateFile}");

                isUpdateDownloaded = true;
                future.complete(true);
            } catch (Exception exception) {
                future.completeExceptionally(exception);
            }
        });

        return future;
    }

    /**
     * Parse the latest version from the response body.
     *
     * @param responseBody The HTTP response body
     * @return The latest version as a Version object
     */
    protected abstract Version parseLatestVersion(String responseBody);

    /**
     * Get the download URL for the latest version.
     *
     * @return The download URL as a string
     */
    protected abstract String getDownloadUrl();


    @SneakyThrows
    protected String getJarFileName() {
        var jarPath = Paths.get(ServerSystem.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation().toURI());

        var name = jarPath.getFileName().toString();
        if (!name.toLowerCase().endsWith(".jar")) name = "${name}.jar";

        return name;
    }
}
