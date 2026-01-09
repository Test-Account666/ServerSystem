package me.testaccount666.serversystem.updates

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.utils.Version
import org.bukkit.Bukkit
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

abstract class AbstractUpdateChecker(@JvmField protected val updateURI: URI?) {
    protected var isUpdateAvailable: Boolean = false
    protected var isUpdateDownloaded: Boolean = false
    internal open var autoUpdate: Boolean = false

    @JvmField
    protected var latestVersion: Version? = null
    protected var notifiedOnce: Boolean = false

    /**
     * Template method for checking updates. Subclasses should implement parseLatestVersion.
     */
    open fun hasUpdate(): CompletableFuture<Boolean> {
        ServerSystem.log.fine("Checking for updates...")
        if (isUpdateAvailable) return CompletableFuture.completedFuture(true)

        val future = CompletableFuture<Boolean>()

        Bukkit.getScheduler().runTaskAsynchronously(ServerSystem.instance, Runnable {
            try {
                val parsedLatestVersion = getLatestVersion().join()

                if (parsedLatestVersion > ServerSystem.CURRENT_VERSION) {
                    isUpdateAvailable = true
                    latestVersion = parsedLatestVersion
                    ServerSystem.log.info(
                        "A new update is available! Current version: ${ServerSystem.CURRENT_VERSION}" +
                                ", Latest version: $parsedLatestVersion"
                    )
                } else if (!notifiedOnce) {
                    notifiedOnce = true
                    ServerSystem.log.info("You're running the latest version of ServerSystem <3")
                }

                future.complete(isUpdateAvailable)
            } catch (exception: Exception) {
                future.completeExceptionally(exception)
            }
        })

        return future
    }

    fun getLatestVersion(): CompletableFuture<Version> {
        val future = CompletableFuture<Version>()
        try {
            HttpClient.newHttpClient().use { client ->
                val request = HttpRequest.newBuilder()
                    .timeout(Duration.ofMinutes(1))
                    .uri(updateURI).GET().build()
                val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                val body = response.body()

                val parsedLatestVersion = parseLatestVersion(body)
                future.complete(parsedLatestVersion)
                return future
            }
        } catch (exception: Exception) {
            ServerSystem.log.log(Level.WARNING, "Failed to check for updates!", exception)
            future.completeExceptionally(exception)

            return future
        }
    }

    /**
     * Template method for downloading updates. Subclasses should implement getDownloadUrl.
     */
    open fun downloadUpdate(): CompletableFuture<Boolean> {
        if (!autoUpdate) {
            ServerSystem.log.info("Auto update is disabled. Update can be downloaded manually.")
            return CompletableFuture.completedFuture(false)
        }
        if (isUpdateDownloaded) return CompletableFuture.completedFuture(true)

        ServerSystem.log.info("Downloading update...")

        val future = CompletableFuture<Boolean>()

        Bukkit.getScheduler().runTaskAsynchronously(ServerSystem.instance, Runnable {
            if (latestVersion == null) {
                future.completeExceptionally(IllegalStateException("No version info available for download."))
                return@Runnable
            }
            val downloadUrl = this.downloadUrl
            try {
                HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofMinutes(1)).build().use { client ->
                        val request = HttpRequest.newBuilder()
                            .timeout(Duration.ofMinutes(1))
                            .uri(URI.create(downloadUrl)).GET().build()
                        ServerSystem.log.info("Downloading update from ${downloadUrl}")

                        val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())

                        if (response.statusCode() != 200) {
                            future.completeExceptionally(RuntimeException("Failed to download update: HTTP ${response.statusCode()}"))
                            return@Runnable
                        }

                        val pluginDir = ServerSystem.instance.dataFolder.parentFile.toPath()
                        val updateFile = pluginDir.resolve("update").resolve(this.jarFileName)
                        Files.createDirectories(updateFile.parent)

                        val `in` = response.body()
                        val out = Files.newOutputStream(updateFile)

                        val buffer = ByteArray(8192)
                        var bytesRead: Int

                        while ((`in`.read(buffer).also { bytesRead = it }) != -1) out.write(buffer, 0, bytesRead)

                        out.close()

                        ServerSystem.log.info("Update downloaded to ${updateFile}")

                        isUpdateDownloaded = true
                        future.complete(true)
                    }
            } catch (exception: Exception) {
                future.completeExceptionally(exception)
            }
        })

        return future
    }

    /**
     * Parse the latest version from the response body.
     *
     * @param responseBody The HTTP response body
     * @return The latest version as a Version object
     */
    protected abstract fun parseLatestVersion(responseBody: String): Version?

    /**
     * Get the download URL for the latest version.
     *
     * @return The download URL as a string
     */
    protected abstract val downloadUrl: String


    protected val jarFileName: String
        get() {
            val jarPath = Paths.get(
                ServerSystem::class.java
                    .protectionDomain
                    .codeSource
                    .location.toURI()
            )

            var name = jarPath.fileName.toString()
            if (!name.lowercase(Locale.getDefault()).endsWith(".jar")) name = "${name}.jar"

            return name
        }
}