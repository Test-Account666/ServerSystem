package me.testaccount666.serversystem.updates;

import lombok.Getter;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Manages update checking and downloading lifecycle for the plugin.
 */
public final class UpdateManager {
    private final ServerSystem _plugin;
    private final ConfigurationManager _configManager;

    @Getter
    private AbstractUpdateChecker _updateChecker;

    public UpdateManager(ServerSystem plugin, ConfigurationManager configManager) {
        this._plugin = plugin;
        this._configManager = configManager;
    }

    public void start() {
        var generalConfig = _configManager.getGeneralConfig();
        var updateCheckerType = resolveUpdateCheckerType(generalConfig);

        if (updateCheckerType.isEmpty()) {
            handleInvalidUpdateCheckerType(generalConfig);
            return;
        }

        initializeUpdateChecker(updateCheckerType.get(), generalConfig);
        scheduleUpdateChecks(generalConfig);
    }

    private Optional<UpdateCheckerType> resolveUpdateCheckerType(ConfigReader generalConfig) {
        var typeString = generalConfig.getString("UpdateChecker.Type.Value");
        if (typeString == null || typeString.isBlank()) typeString = "DISABLED";
        return UpdateCheckerType.of(typeString);
    }

    private void handleInvalidUpdateCheckerType(ConfigReader generalConfig) {
        var typeString = generalConfig.getString("UpdateChecker.Type.Value");
        var availableTypes = formatAvailableUpdateCheckerTypes();

        ServerSystem.getLog().warning("Updater type '${typeString}' not found. Available options: ${availableTypes}");
        _updateChecker = UpdateCheckerType.DISABLED.getFactory().get();
    }

    private String formatAvailableUpdateCheckerTypes() {
        return Arrays.stream(UpdateCheckerType.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    private void initializeUpdateChecker(UpdateCheckerType type, ConfigReader generalConfig) {
        var autoUpdate = determineAutoUpdateSetting(generalConfig);

        _updateChecker = type.getFactory().get();
        _updateChecker.setAutoUpdate(autoUpdate);
    }

    private boolean determineAutoUpdateSetting(ConfigReader generalConfig) {
        var autoUpdate = generalConfig.getBoolean("UpdateChecker.AutoUpdate");
        if (Boolean.parseBoolean(System.getProperty("serversystem.disable-auto-download", "false"))) autoUpdate = false;
        return autoUpdate;
    }

    private void scheduleUpdateChecks(ConfigReader generalConfig) {
        var checkForUpdates = generalConfig.getBoolean("UpdateChecker.CheckForUpdates");
        if (!checkForUpdates) return;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(_plugin, this::performUpdateCheck, 20L, 20L * 60 * 60);
    }

    private void performUpdateCheck() {
        _updateChecker.hasUpdate()
                .exceptionally(this::handleUpdateCheckError)
                .thenAccept(this::handleUpdateCheckResult);
    }

    private Boolean handleUpdateCheckError(Throwable throwable) {
        ServerSystem.getLog().log(Level.WARNING, "Error checking for updates", throwable);
        return false;
    }

    private void handleUpdateCheckResult(boolean hasUpdate) {
        if (!hasUpdate) return;

        _updateChecker.downloadUpdate()
                .exceptionally(this::handleUpdateDownloadError)
                .thenAccept(this::handleUpdateDownloadResult);
    }

    private Boolean handleUpdateDownloadError(Throwable throwable) {
        ServerSystem.getLog().log(Level.WARNING, "Error downloading update", throwable);
        return false;
    }

    private void handleUpdateDownloadResult(boolean success) {
        if (success) return;
        ServerSystem.getLog().warning("Update-Download failed!");
    }
}
