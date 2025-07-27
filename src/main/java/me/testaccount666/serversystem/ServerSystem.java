package me.testaccount666.serversystem;

import lombok.Getter;
import lombok.SneakyThrows;
import me.testaccount666.migration.LegacyDataMigrator;
import me.testaccount666.serversystem.clickablesigns.SignManager;
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager;
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager;
import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.listener.management.ListenerManager;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.managers.database.economy.AbstractEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.economy.MySqlEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.economy.SqliteEconomyDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.MySqlModerationDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.SqliteModerationDatabaseManager;
import me.testaccount666.serversystem.placeholderapi.PlaceholderApiSupport;
import me.testaccount666.serversystem.placeholderapi.PlaceholderManager;
import me.testaccount666.serversystem.updates.AbstractUpdateChecker;
import me.testaccount666.serversystem.updates.UpdateCheckerType;
import me.testaccount666.serversystem.userdata.CachedUser;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.UserManager;
import me.testaccount666.serversystem.userdata.money.EconomyProvider;
import me.testaccount666.serversystem.userdata.money.vault.EconomyVaultAPI;
import me.testaccount666.serversystem.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//TODO: This class seems to be getting quite big again, I don't like it
public final class ServerSystem extends JavaPlugin {
    public static final Version CURRENT_VERSION = new Version("3.1.0");
    public static ServerSystem Instance;
    @Getter
    private static Logger _Log;
    @Getter
    private UserManager _userManager;
    @Getter
    private CommandManager _commandManager;
    @Getter
    private ListenerManager _listenerManager;
    @Getter
    private PlaceholderManager _placeholderManager;
    @Getter
    private EconomyProvider _economyProvider;
    @Getter
    private ConfigurationManager _configManager;
    @Getter
    private AbstractEconomyDatabaseManager _economyDatabaseManager;
    @Getter
    private WarpManager _warpManager;
    @Getter
    private AbstractModerationDatabaseManager _moderationDatabaseManager;
    @Getter
    @Nullable
    private KitManager _kitManager;
    @Getter
    private SignManager _signManager;
    @Getter
    private AbstractUpdateChecker _updateChecker;

    public static Version getServerVersion() {
        var version = Bukkit.getVersion();

        version = version.substring(version.indexOf("MC: ") + 4, version.indexOf(")"));

        getLog().log(Level.FINE, "Server version: ${version}");

        return new Version(version);
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        Instance = this;
        _Log = getLogger();

        var migrator = new LegacyDataMigrator();
        if (migrator.isLegacyDataPresent()) {
            getLog().log(Level.INFO, "Legacy data detected. Attempting to migrate...");
            migrator.prepareMigration();
        }

        var previousVersionFile = new File(getDataFolder(), "previousVersion.yml");
        var previousVersionConfig = YamlConfiguration.loadConfiguration(previousVersionFile);
        previousVersionConfig.set("previousVersion", CURRENT_VERSION.toString());
        try {
            previousVersionConfig.save(previousVersionFile);
        } catch (IOException exception) {
            throw new RuntimeException("Error updating 'previousVersion'", exception);
        }

        try {
            initialize();
        } catch (Exception exception) {
            getLog().log(Level.SEVERE, "Failed to initialize the plugin", exception);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        startUpdateChecker();

        Bukkit.getScheduler().runTaskLater(this, migrator::migrateLegacyData, 1L);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            _commandManager.registerCommands();
            _listenerManager.registerListeners();
            _placeholderManager.registerPlaceholders();
        }, 2);
    }

    private void initialize() throws Exception {
        _configManager = new ConfigurationManager(this);
        _configManager.loadAllConfigs();

        var moderationType = _configManager.getModerationConfig().getString("Moderation.StorageType.Value");
        _moderationDatabaseManager = switch (moderationType.toLowerCase()) {
            case "sqlite" -> new SqliteModerationDatabaseManager(getDataFolder());
            case "mysql" -> new MySqlModerationDatabaseManager(_configManager.getModerationConfig());
            default -> throw new IllegalStateException("Unsupported moderation storage: ${moderationType} - Supported values: sqlite, mysql");
        };

        if (EconomyVaultAPI.isVaultInstalled()) EconomyVaultAPI.initialize();
        PlaceholderApiSupport.registerPlaceholders();

        _moderationDatabaseManager.initialize();

        var economyStorageType = _configManager.getEconomyConfig().getString("Economy.StorageType.Value");
        _economyDatabaseManager = switch (economyStorageType.toLowerCase()) {
            case "sqlite" -> new SqliteEconomyDatabaseManager(getDataFolder());
            case "mysql" -> new MySqlEconomyDatabaseManager(_configManager.getEconomyConfig());
            default -> throw new IllegalStateException("Unsupported economy storage: ${economyStorageType} - Supported values: sqlite, mysql");
        };

        _commandManager = new CommandManager(_configManager.getCommandsConfig());
        _listenerManager = new ListenerManager(_commandManager);
        _placeholderManager = new PlaceholderManager();
        _economyProvider = new EconomyProvider(_configManager.getEconomyConfig());
        _userManager = new UserManager();

        _kitManager = new KitManager();

        Bukkit.getScheduler().runTask(this, () -> {
            var warpFile = Path.of(getDataFolder().getPath(), "data", "warps.yml").toFile();
            var warpConfig = YamlConfiguration.loadConfiguration(warpFile);
            _warpManager = new WarpManager(warpConfig, warpFile);

            _signManager = new SignManager();
            _signManager.loadSignTypes();
        });
    }

    @Override
    public void onDisable() {
        if (_userManager != null) saveAllUsers();

        if (_commandManager != null) _commandManager.unregisterCommands();

        if (_listenerManager != null) _listenerManager.unregisterListeners();

        PlaceholderApiSupport.unregisterPlaceholders();

        if (_economyDatabaseManager != null) _economyDatabaseManager.shutdown();

        if (_moderationDatabaseManager != null) _moderationDatabaseManager.shutdown();
    }

    private void saveAllUsers() {
        _userManager.getCachedUsers().stream().map(CachedUser::getOfflineUser).forEach(OfflineUser::save);
    }

    private void startUpdateChecker() {
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

        getLog().warning("Updater type '${typeString}' not found. Available options: ${availableTypes}");
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::performUpdateCheck, 20L, 20L * 60 * 60);
    }

    private void performUpdateCheck() {
        _updateChecker.hasUpdate()
                .exceptionally(this::handleUpdateCheckError)
                .thenAccept(this::handleUpdateCheckResult);
    }

    private Boolean handleUpdateCheckError(Throwable throwable) {
        getLog().log(Level.WARNING, "Error checking for updates", throwable);
        return false;
    }

    private void handleUpdateCheckResult(Boolean hasUpdate) {
        if (!hasUpdate) return;

        _updateChecker.downloadUpdate()
                .exceptionally(this::handleUpdateDownloadError)
                .thenAccept(this::handleUpdateDownloadResult);
    }

    private Boolean handleUpdateDownloadError(Throwable throwable) {
        getLog().log(Level.WARNING, "Error downloading update", throwable);
        return false;
    }

    private void handleUpdateDownloadResult(Boolean success) {
        if (success) return;
        getLog().warning("Update-Download failed!");
    }
}
