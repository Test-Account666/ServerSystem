package me.testaccount666.serversystem.updates

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import org.bukkit.Bukkit
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.logging.Level
import java.util.stream.Collectors

/**
 * Manages update checking and downloading lifecycle for the plugin.
 */
class UpdateManager(private val _plugin: ServerSystem, private val _configManager: ConfigurationManager) {
    var updateChecker: AbstractUpdateChecker? = null
        private set

    fun start() {
        val generalConfig = _configManager.generalConfig
        val updateCheckerType = resolveUpdateCheckerType(generalConfig!!)

        if (updateCheckerType.isEmpty) {
            handleInvalidUpdateCheckerType(generalConfig)
            return
        }

        initializeUpdateChecker(updateCheckerType.get(), generalConfig)
        scheduleUpdateChecks(generalConfig)
    }

    private fun resolveUpdateCheckerType(generalConfig: ConfigReader): Optional<UpdateCheckerType> {
        var typeString = generalConfig.getString("UpdateChecker.Type.Value", null)
        if (typeString.isNullOrBlank()) typeString = "DISABLED"
        return UpdateCheckerType.of(typeString)
    }

    private fun handleInvalidUpdateCheckerType(generalConfig: ConfigReader) {
        val typeString = generalConfig.getString("UpdateChecker.Type.Value", null)
        val availableTypes = formatAvailableUpdateCheckerTypes()

        ServerSystem.log.warning("Updater type '${typeString}' not found. Available options: ${availableTypes}")
        updateChecker = UpdateCheckerType.DISABLED.factory.get()
    }

    private fun formatAvailableUpdateCheckerTypes(): String {
        return Arrays.stream<UpdateCheckerType?>(UpdateCheckerType.entries.toTypedArray())
            .map<String?> { obj: UpdateCheckerType? -> obj!!.name }
            .collect(Collectors.joining(", "))
    }

    private fun initializeUpdateChecker(type: UpdateCheckerType, generalConfig: ConfigReader) {
        val autoUpdate = determineAutoUpdateSetting(generalConfig)

        updateChecker = type.factory.get()
        updateChecker!!.autoUpdate = autoUpdate
    }

    private fun determineAutoUpdateSetting(generalConfig: ConfigReader): Boolean {
        var autoUpdate = generalConfig.getBoolean("UpdateChecker.AutoUpdate", false)
        if (System.getProperty("serversystem.disable-auto-download", "false").toBoolean()) autoUpdate = false
        return autoUpdate
    }

    private fun scheduleUpdateChecks(generalConfig: ConfigReader) {
        val checkForUpdates = generalConfig.getBoolean("UpdateChecker.CheckForUpdates", false)
        if (!checkForUpdates) return
        Bukkit.getScheduler().scheduleSyncRepeatingTask(_plugin, { this.performUpdateCheck() }, 20L, 20L * 60 * 60)
    }

    private fun performUpdateCheck() {
        updateChecker!!.hasUpdate()
            .exceptionally(Function { throwable -> this.handleUpdateCheckError(throwable) })
            .thenAccept(Consumer { hasUpdate -> this.handleUpdateCheckResult(hasUpdate) })
    }

    private fun handleUpdateCheckError(throwable: Throwable): Boolean {
        ServerSystem.log.log(Level.WARNING, "Error checking for updates", throwable)
        return false
    }

    private fun handleUpdateCheckResult(hasUpdate: Boolean) {
        if (!hasUpdate) return

        updateChecker!!.downloadUpdate()
            .exceptionally(Function { throwable -> this.handleUpdateDownloadError(throwable) })
            .thenAccept(Consumer { success -> this.handleUpdateDownloadResult(success) })
    }

    private fun handleUpdateDownloadError(throwable: Throwable): Boolean {
        ServerSystem.log.log(Level.WARNING, "Error downloading update", throwable)
        return false
    }

    private fun handleUpdateDownloadResult(success: Boolean) {
        if (success) return
        ServerSystem.log.warning("Update-Download failed!")
    }
}