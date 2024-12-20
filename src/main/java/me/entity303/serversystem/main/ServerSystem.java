package me.entity303.serversystem.main;

import com.earth2me.essentials.Essentials;
import com.google.common.io.Files;
import me.entity303.serversystem.api.BanAPI;
import me.entity303.serversystem.api.EconomyAPI;
import me.entity303.serversystem.api.MuteAPI;
import me.entity303.serversystem.api.VanishAPI;
import me.entity303.serversystem.bansystem.*;
import me.entity303.serversystem.bstats.MetricsLite;
import me.entity303.serversystem.commands.util.CommandManager;
import me.entity303.serversystem.config.DefaultConfigReader;
import me.entity303.serversystem.config.IConfigReader;
import me.entity303.serversystem.config.NonValidatingConfigReader;
import me.entity303.serversystem.databasemanager.HomeManager;
import me.entity303.serversystem.databasemanager.MySQL;
import me.entity303.serversystem.databasemanager.WantsTeleport;
import me.entity303.serversystem.databasemanager.WarpManager;
import me.entity303.serversystem.economy.*;
import me.entity303.serversystem.events.EventManager;
import me.entity303.serversystem.listener.FlightHitListener;
import me.entity303.serversystem.listener.command.EssentialsCommandListener;
import me.entity303.serversystem.listener.join.JoinUpdateListener;
import me.entity303.serversystem.placeholderapi.ServerSystemExpansion;
import me.entity303.serversystem.utils.*;
import me.entity303.serversystem.utils.versions.VersionManager;
import me.entity303.serversystem.utils.versions.VersionStuff;
import me.entity303.serversystem.vanish.Vanish;
import me.entity303.serversystem.vault.Vault;
import me.entity303.serversystem.vault.VaultHookManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Pattern;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

@SuppressWarnings("UnstableApiUsage")
public final class ServerSystem extends JavaPlugin {
    public static boolean DEBUG = false;
    private static EconomyAPI ECONOMY_API;
    private static MuteAPI MUTE_API;
    private static BanAPI BAN_API;
    private static VanishAPI VANISH_API;
    public final String _configVersion = "6.4";
    public final String _jarName = this.getFile().getName();
    private final File _rulesFile = new File("plugins//ServerSystem", "rules.yml");
    private final List<Player> _commandSpyList = new ArrayList<>();
    private final List<Player> _godList = new ArrayList<>();
    private final List<Player> _msgOffList = new ArrayList<>();
    private final List<Player> _socialSpyList = new ArrayList<>();
    private final Map<Player, Location> _backLocationMap = new HashMap<>();
    private final Map<Player, String> _backReasonMap = new HashMap<>();
    private final Map<Player, BukkitTask> _teleportMap = new HashMap<>();
    private final Map<Player, TpaData> _tpaDataMap = new HashMap<>();
    private final Map<Player, Player> _enderChest = new HashMap<>();
    private final VersionStuff _versionStuff = new VersionStuff(this);
    private EventManager _eventManager;
    private VersionManager _versionManager;
    private CommandManager _commandManager;
    private boolean _onceTold = false;
    private String _serverName;
    private String _newVersion = this.getDescription().getVersion();
    private Message _messages;
    private PermissionsChecker _permissionsChecker;
    private boolean _starting = true;
    private boolean _registered = false;
    private boolean _maintenance = false;
    private boolean _stopFlightOnHit = false;
    private boolean _disableFlightOnHit = false;
    private boolean _specialSudo = true;
    private boolean _advancedInvsee = true;
    private boolean _clientsideOp = true;
    private Vanish _vanish;
    private MetaValue _metaValue;
    private WantsTeleport _wantsTeleport;
    private KitsManager _kitsManager;
    private ServerSystemTimer _timer;
    private AbstractBanManager _banManager;
    private AbstractMuteManager _muteManager;
    private WarpManager _warpManager;
    private HomeManager _homeManager;
    private MySQL _mySQL;
    private AbstractEconomyManager _economyManager;
    private Vault _vault;
    private VaultHookManager _vaultHookManager;
    private EssentialsCommandListener _essentialsCommandListener = null;
    private Furnace _furnace;
    private MetricsLite _metrics;
    private FileConfiguration _rulesConfig;
    private Method _syncCommandsMethod = null;
    private IConfigReader _configReader;
    private boolean _runningPaper = false;

    @SuppressWarnings("NewMethodNamingConvention")
    public static EconomyAPI getEconomyApi() {
        return ServerSystem.ECONOMY_API;
    }

    @SuppressWarnings({ "NewMethodNamingConvention", "MethodNamesDifferingOnlyByCase" })
    public static MuteAPI getMuteApi() {
        return ServerSystem.MUTE_API;
    }

    @SuppressWarnings({ "NewMethodNamingConvention", "MethodNamesDifferingOnlyByCase" })
    public static BanAPI getBanApi() {
        return ServerSystem.BAN_API;
    }

    @SuppressWarnings("NewMethodNamingConvention")
    public static VanishAPI getVanishApi() {
        return ServerSystem.VANISH_API;
    }

    public boolean isRunningPaper() {
        return this._runningPaper;
    }

    public WantsTeleport GetWantsTeleport() {
        return this._wantsTeleport;
    }

    public boolean IsSpecialSudo() {
        return this._specialSudo;
    }

    public VersionManager GetVersionManager() {
        return this._versionManager;
    }

    public VersionStuff GetVersionStuff() {
        return this._versionStuff;
    }

    public EssentialsCommandListener GetEssentialsCommandListener() {
        return this._essentialsCommandListener;
    }

    public HomeManager GetHomeManager() {
        return this._homeManager;
    }

    public boolean IsStopFlightOnHit() {
        return this._stopFlightOnHit;
    }

    public void SetStopFlightOnHit(boolean stopFlightOnHit) {
        this._stopFlightOnHit = stopFlightOnHit;
    }

    public List<Player> GetSocialSpy() {
        return this._socialSpyList;
    }

    public boolean IsDisableFlightOnHit() {
        return this._disableFlightOnHit;
    }

    public void SetDisableFlightOnHit(boolean disableFlightOnHit) {
        this._disableFlightOnHit = disableFlightOnHit;
    }

    public File GetRulesFile() {
        return this._rulesFile;
    }

    public FileConfiguration GetRulesConfig() {
        return this._rulesConfig;
    }

    public boolean IsMaintenance() {
        return this._maintenance;
    }

    public void SetMaintenance(boolean maintenance) {
        this._maintenance = maintenance;
    }

    public boolean IsAdvancedInvsee() {
        return this._advancedInvsee;
    }

    public void SetAdvancedInvsee(boolean advancedInvsee) {
        this._advancedInvsee = advancedInvsee;
    }

    public boolean IsClientsideOp() {
        return this._clientsideOp;
    }

    public void SetClientsideOp(boolean clientsideOp) {
        this._clientsideOp = clientsideOp;
    }    /*
    Fix for something, don't know anymore
     */

    public boolean IsRegistered() {
        return this._registered;
    }

    public void SetRegistered(boolean registered) {
        this._registered = registered;
    }

    public KitsManager GetKitsManager() {
        return this._kitsManager;
    }

    public List<Player> GetGodList() {
        return this._godList;
    }

    public List<Player> GetCommanddSpy() {
        return this._commandSpyList;
    }

    public Map<Player, Player> GetEnderchest() {
        return this._enderChest;
    }

    public String GetServerName() {
        return this._serverName;
    }

    public void GetServerName(String serverName) {
        this._serverName = serverName;
    }

    public MySQL GetMySQL() {
        return this._mySQL;
    }

    public AbstractMuteManager GetMuteManager() {
        return this._muteManager;
    }

    public AbstractBanManager GetBanManager() {
        return this._banManager;
    }

    public Map<Player, TpaData> GetTpaDataMap() {
        return this._tpaDataMap;
    }

    public MetricsLite GetMetrics() {
        return this._metrics;
    }

    public List<Player> GetMsgOff() {
        return this._msgOffList;
    }

    public Map<Player, Location> GetBackloc() {
        return this._backLocationMap;
    }

    public Map<Player, String> GetBackReason() {
        return this._backReasonMap;
    }

    public MetaValue GetMetaValue() {
        return this._metaValue;
    }

    public Map<Player, BukkitTask> GetTeleportMap() {
        return this._teleportMap;
    }

    public boolean IsStarting() {
        return this._starting;
    }

    public PermissionsChecker GetPermissions() {
        return this._permissionsChecker;
    }

    public String GetNewVersion() {
        return this._newVersion;
    }

    public void SetNewVersion(String newVersion) {
        this._newVersion = newVersion;
    }

    public WarpManager GetWarpManager() {
        return this._warpManager;
    }

    public ServerSystemTimer GetTimer() {
        return this._timer;
    }

    public String GetConfigVersion() {
        return this._configVersion;
    }

    public Vault GetVault() {
        return this._vault;
    }

    public VaultHookManager GetVaultHookManager() {
        return this._vaultHookManager;
    }

    public Furnace GetFurnace() {
        return this._furnace;
    }

    public CommandManager GetCommandManager() {
        return this._commandManager;
    }

    public IConfigReader GetConfigReader() {
        return this._configReader;
    }

    public EventManager GetEventManager() {
        return this._eventManager;
    }

    public AbstractEconomyManager GetEconomyManager() {
        return this._economyManager;
    }

    public Message GetMessages() {
        return this._messages;
    }

    public Vanish GetVanish() {
        return this._vanish;
    }

    private boolean CheckMainServerForUpdates(String currentVersion, boolean autoUpdate) {
        var url = "http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem";

        var foundVersion = this.getDescription().getVersion();

        var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        var request = HttpRequest.newBuilder().uri(URI.create(url)).header("Referer", "ServerSystem").timeout(java.time.Duration.ofSeconds(30)).build();

        String responseBody;
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = response.body();
        } catch (IOException | InterruptedException exception) {
            responseBody = "";
        }

        if (!responseBody.contains(".jar")) {
            this.Error("An error occurred while trying to connect to the updater!");
            this.Info("Please ignore this error. The update server is currently down. Please be patient");
            return false;
        }

        foundVersion = FindVersion(foundVersion, responseBody);

        var isFoundVersionMoreRecent = this.IsFoundVersionMoreRecent(foundVersion, currentVersion);

        if (!isFoundVersionMoreRecent || currentVersion.equalsIgnoreCase(foundVersion)) {
            if (this._onceTold) return true;

            this.Info("You are using the latest version of ServerSystem <3");
            this._onceTold = true;
            return true;
        }

        this.Warn("There is a new version available! (" + foundVersion + ")");
        if (!autoUpdate) {
            if (!this._configReader.GetBoolean("updates.notifyOnJoin")) return true;

            if (!foundVersion.equalsIgnoreCase(this._newVersion)) this._newVersion = foundVersion;

            if (this._registered) return true;

            this._registered = true;
            this._eventManager.RegisterEvent(new JoinUpdateListener(this));
            return true;
        }

        this.Info("Auto-updating!");
        this.Info("(You need to restart the server so the update can take effect)");
        try {
            var inputStream = new BufferedInputStream(new URL("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + foundVersion + ".jar").openStream());
            var fileOutputStream = new FileOutputStream(new File("plugins/update", this._jarName));
            var dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) fileOutputStream.write(dataBuffer, 0, bytesRead);

            inputStream.close();
            fileOutputStream.close();
            return true;
        } catch (Exception exception) {
            this.Error("Error while trying downloading the update!");
            exception.printStackTrace();
        }
        return false;
    }

    public static String FindVersion(String foundVersion, String responseBody) {
        var pattern = Pattern.compile("href=\"([^\"]*\\.jar)\"");
        var matcher = pattern.matcher(responseBody);

        while (matcher.find()) {
            var remoteFileName = matcher.group(1);
            remoteFileName = remoteFileName.substring(0, remoteFileName.lastIndexOf('.'));
            foundVersion = remoteFileName;
        }
        return foundVersion;
    }


    public void Error(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.TranslateAlternateColorCodes('&', "&8[&4Error&8] [&4ServerSystem&8] &7>> &4" + text));
    }


    public void Info(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.TranslateAlternateColorCodes('&', "&8[&aInfo&8] [&aServerSystem&8] &7>> &a" + text));
    }


    private boolean IsFoundVersionMoreRecent(String foundVersion, String currentVersion) {
        var foundVersionSplit = foundVersion.split("\\.");

        if (foundVersionSplit.length < 3) return false;

        var foundVersionMajor = Long.parseLong(foundVersionSplit[0]);
        var foundVersionMinor = Long.parseLong(foundVersionSplit[1]);
        var foundVersionPatch = Long.parseLong(foundVersionSplit[2]);

        var currentVersionSplit = currentVersion.split("\\.");

        if (currentVersionSplit.length < 3) return true;

        var currentVersionMajor = Long.parseLong(currentVersionSplit[0]);
        var currentVersionMinor = Long.parseLong(currentVersionSplit[1]);
        var currentVersionPatch = Long.parseLong(currentVersionSplit[2]);


        if (currentVersionMajor < foundVersionMajor) return true;

        if (currentVersionMinor < foundVersionMinor) return true;

        return currentVersionPatch < foundVersionPatch;
    }


    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        this._configReader = new NonValidatingConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
    }

    public void Warn(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.TranslateAlternateColorCodes('&', "&8[&cWarning&8] [&cServerSystem&8] &7>> &c" + text));
    }


    @Override
    public void reloadConfig() {
        if (this._configReader != null) {
            this._configReader.Reload();
        } else {
            this._configReader = new NonValidatingConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
        }
    }


    public void ReloadConfigValidating() {
        if (this._configReader == null || this._configReader instanceof NonValidatingConfigReader) {
            this._configReader = new DefaultConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
            return;
        }

        this._configReader.Reload();
    }


    @Override
    public void saveConfig() {
        if (this._configReader != null) this._configReader.Save();
    }


    private void CheckServerSoftware() {
        try {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                try {
                    Bukkit.class.getDeclaredMethod("spigot");
                } catch (Exception ignored) {
                    this.Error("Unsupported Serversoftware!");
                    this.Warn("ServerSystem may not work (correctly)!");
                }
            }, 1L);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    @Override
    public void onLoad() {
        try {
            Class.forName("io.papermc.paper.PaperBootstrap");
            this._runningPaper = true;
        } catch (Throwable ignored) {
        }

        this.LoadConfigs();

        this.ReloadConfigValidating();


        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            this._vault = new Vault();
            this._vaultHookManager = new VaultHookManager(this);
            this._vaultHookManager.Hook();
        }
    }


    @Override
    public void onEnable() {
        if (!new File("plugins//update").exists()) new File("plugins//update").mkdirs();

        this._starting = true;
        this._metaValue = new MetaValue();
        this._vanish = new Vanish(this);

        ServerSystem.DEBUG = this._configReader.GetBoolean("debug");

        this._messages = new Message(this);
        this._permissionsChecker = new PermissionsChecker(this);

        this._versionManager = new VersionManager(this);

        this._versionManager.RegisterVersionStuff();

        var file = new File("plugins//ServerSystem", "vanish.yml");
        if (file.exists()) {
            var cfg = YamlConfiguration.loadConfiguration(file);
            var vanishConfiguration = cfg.getConfigurationSection("Vanish");

            if (vanishConfiguration != null && !vanishConfiguration.getKeys(false).isEmpty()) {
                for (var uuidString : vanishConfiguration.getKeys(false)) this._vanish.GetVanishList().add(UUID.fromString(uuidString));
            }
            file.delete();
        }

        this._kitsManager = new KitsManager(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new ServerSystemExpansion(this).register();

        this._commandManager = new CommandManager(this);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this._commandManager.RegisterCommands();

            if (!this.SyncCommands()) return;

            if (Bukkit.getOnlinePlayers().isEmpty()) return;
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }, 5L);

        this._eventManager = new EventManager(this);

        this._eventManager.RegisterEvents();

        Bukkit.getScheduler().runTaskLater(this, () -> this._starting = false, 100L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Collection<Player> players = new ArrayList<>();
            for (var entry : this._tpaDataMap.entrySet()) {
                var player = entry.getKey();

                if (entry.getValue().GetEnd() > System.currentTimeMillis()) continue;

                players.add(player);
            }

            if (players.isEmpty()) return;

            players.forEach(this._tpaDataMap::remove);
        }, 20L, 20L);

        this._wantsTeleport = new WantsTeleport(this);
        this._timer = new ServerSystemTimer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this._timer, 1000L, 50L);

        this.SetupEconomyBanSystem();

        ServerSystem.ECONOMY_API = new EconomyAPI(this);
        ServerSystem.MUTE_API = new MuteAPI(this);
        ServerSystem.BAN_API = new BanAPI(this);
        ServerSystem.VANISH_API = new VanishAPI(this);

        this._warpManager = new WarpManager(this);

        this._homeManager = new HomeManager();

        YEAR_NAME = this.GetBanSystem("YearName");
        MONTH_NAME = this.GetBanSystem("MonthName");
        WEEK_NAME = this.GetBanSystem("WeekName");
        DAY_NAME = this.GetBanSystem("DayName");
        HOUR_NAME = this.GetBanSystem("HourName");
        MINUTE_NAME = this.GetBanSystem("MinuteName");
        SECOND_NAME = this.GetBanSystem("SecondName");

        this._disableFlightOnHit = this._configReader.GetBoolean("fly.disableWhenHit");
        this._stopFlightOnHit = this._configReader.GetBoolean("fly.stopWhenHit");

        if (this._disableFlightOnHit) this._stopFlightOnHit = true;

        if (this._stopFlightOnHit) this._eventManager.RegisterEvent(new FlightHitListener(this));

        if (this._configReader.GetBoolean("metrics")) this._metrics = new MetricsLite(this, 9043);

        Bukkit.getScheduler().runTaskLater(this, () -> this._furnace = new Furnace(this), 10L);

        this.StartDeactivatingCommands();

        this._specialSudo = this._configReader.GetBoolean("specialSudo", true);

        this._advancedInvsee = this._configReader.GetBoolean("advancedInvSee", true);

        this._clientsideOp = this._configReader.GetBoolean("clientsideOpSpoof", true);

        var commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        var commandsConfig = YamlConfiguration.loadConfiguration(commandsFiles);

        if (commandsConfig.getBoolean("Commands.baltop.enabled")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> this._economyManager.FetchTopTen(), 20L, 72000L);
        }

        this.StartSwappingCommands();

        this.StartUpdateCheck();

        this.CheckServerSoftware();

        Bukkit.getScheduler().runTask(this, () -> {
            var markerFile = new File("plugins" + File.separator + "ServerSystem", "marker.ignore");
            if (markerFile.exists()) return;

            this.Warn("!!!!! BREAKING CHANGES !!!!!");
            this.Warn("As of 2.0.0, there are some breaking changes!");
            this.Warn("If you updated from 1.8.3, you should be fine.");
            this.Warn("If you're using a minecraft version below 1.17.1, this version of ServerSystem will NOT work!");
            this.Warn("Support for every version below 1.17.1 was completely dropped!");
            this.Warn("!!!!! BREAKING CHANGES !!!!!");

            try {
                markerFile.createNewFile();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private boolean SyncCommands() {
        if (this._syncCommandsMethod == null) {
            try {
                this._syncCommandsMethod = Bukkit.getServer().getClass().getDeclaredMethod("syncCommands");
                this._syncCommandsMethod.setAccessible(true);
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
                return false;
            }
        }

        try {
            this._syncCommandsMethod.invoke(Bukkit.getServer());
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }


    private void StartDeactivatingCommands() {
        if (!this._configReader.GetBoolean("deactivatedCommands.enabled")) return;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (var command : this._configReader.GetConfigurationSection("deactivatedCommands").getKeys(false)) {
                if (command.equalsIgnoreCase("enabled")) continue;

                var plugin = this._configReader.GetString("deactivatedCommands." + command);

                this.Info("Deactivating command " + command + " from plugin " + plugin + "!");
                this._commandManager.DeactivateBukkitCommand(command.toLowerCase(), plugin.toLowerCase());
            }
        }, 40L);
    }


    private void StartSwappingCommands() {
        if (!this._configReader.GetBoolean("swapCommands.enabled")) return;

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (var cmdFrom : this._configReader.GetConfigurationSection("swapCommands").getKeys(false)) {
                if (cmdFrom.equalsIgnoreCase("enabled")) continue;

                var pluginFrom = this._configReader.GetString("swapCommands." + cmdFrom + ".fromPlugin");
                var cmdTo = this._configReader.GetString("swapCommands." + cmdFrom + ".toCommand");
                var pluginTo = this._configReader.GetString("swapCommands." + cmdFrom + ".toPlugin");

                this.Info("Swapping command " + cmdFrom + " from plugin " + pluginFrom + " to command " + cmdTo + " from plugin " + pluginTo + "!");

                var cmdFromToLower = pluginFrom.toLowerCase() + ":" + cmdFrom.toLowerCase();

                var commandFrom = Bukkit.getPluginCommand(cmdFromToLower);
                var commandToPluginCommand = Bukkit.getPluginCommand(pluginTo.toLowerCase() + ":" + cmdTo.toLowerCase());

                if (commandFrom == null) {
                    this.Warn("Command " + cmdFrom + " does not exist in plugin " + pluginFrom + "!");
                    continue;
                }

                if (commandToPluginCommand == null) {
                    this.Warn("Command " + cmdTo + " does not exist in plugin " + pluginTo + "!");
                    continue;
                }

                commandFrom.setExecutor(commandToPluginCommand.getExecutor());
                commandFrom.setTabCompleter(commandToPluginCommand.getTabCompleter());
                commandFrom.setPermission(commandToPluginCommand.getPermission());
                commandFrom.setPermissionMessage(commandToPluginCommand.getPermissionMessage());
                commandFrom.setDescription(commandToPluginCommand.getDescription());

                if (!pluginTo.equalsIgnoreCase("Essentials")) continue;

                if (this._essentialsCommandListener == null) {
                    var essentialsPlugin = JavaPlugin.getProvidingPlugin(this.getServer().getPluginManager().getPlugin("Essentials").getClass());
                    var essentials = (Essentials) essentialsPlugin;
                    this._essentialsCommandListener = new EssentialsCommandListener(essentials, this);
                    this._eventManager.RegisterEvent(this._essentialsCommandListener);
                }

                if (this.getServer().getPluginCommand(cmdFromToLower) == this.getServer().getPluginCommand(cmdFrom.toLowerCase())) {
                    this._essentialsCommandListener.AddCommand(cmdFrom, cmdTo);
                } else {
                    this._essentialsCommandListener.AddCommand(pluginFrom + ":" + cmdFrom, cmdTo);
                }
            }
        }, 60L);
    }


    private void StartUpdateCheck() {
        if (!this._configReader.GetBoolean("updates.check")) return;

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            var autoUpdate = this._configReader.GetBoolean("updates.autoUpdate");

            var currentVersion = this.getDescription().getVersion();

            if (this.CheckMainServerForUpdates(currentVersion, autoUpdate)) return;

            this.CheckBackupServerForUpdates(currentVersion, autoUpdate);
        }, 80L, 7200L * 20L);
    }

    private void CheckBackupServerForUpdates(String currentVersion, Boolean autoUpdate) {
        this.Info("Switching to backup updater!");
        new UpdateChecker(this, "78974").GetVersion(foundVersion -> {
            var isFoundVersionMoreRecent = this.IsFoundVersionMoreRecent(foundVersion, currentVersion);

            if (!isFoundVersionMoreRecent || currentVersion.equalsIgnoreCase(foundVersion)) {
                if (this._onceTold) return;

                this.Info("You are using the latest version of ServerSystem <3");
                this._onceTold = true;
            }

            this.Warn("There is a new update available (" + foundVersion + ")!");

            if (!autoUpdate) {
                if (!this._configReader.GetBoolean("updates.notifyOnJoin")) return;

                if (!foundVersion.equalsIgnoreCase(this._newVersion)) this._newVersion = foundVersion;

                if (this._registered) return;

                this._registered = true;
                this._eventManager.RegisterEvent(new JoinUpdateListener(this));
                return;
            }

            this.Info("Auto-updating!");
            this.Info("(You need to restart the server so the update can take effect)");

            try (var inputStream = new BufferedInputStream(new URL("https://api.spiget.org/v2/resources/78974/download").openStream());
                 var fileOutputStream = new FileOutputStream(new File("plugins/update", this._jarName))) {
                var dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) fileOutputStream.write(dataBuffer, 0, bytesRead);
            } catch (IOException exception) {
                exception.printStackTrace();
                this.Error("Error while trying downloading the update!");
                this.Error("Please download it by yourself (https://www.spigotmc.org/resources/serversystem.78974/)!");
                if (!this._registered) {
                    this._registered = true;
                    this._eventManager.RegisterEvent(new JoinUpdateListener(this));
                }

                if (!foundVersion.equalsIgnoreCase(this._newVersion)) this._newVersion = foundVersion;
            }
        });
    }


    public void SetupEconomyBanSystem() {
        String dateFormat;
        dateFormat = this._configReader.GetString("banSystem.dateFormat");


        var currencySingular = this._configReader.GetString("economy.currency.singular");
        var currencyPlural = this._configReader.GetString("economy.currency.plural");
        var startingMoney = this._configReader.GetString("economy.startingMoney");
        var displayFormat = this._configReader.GetString("economy.displayFormat");
        var moneyFormat = this._configReader.GetString("economy.moneyFormat");
        var separator = this._configReader.GetString("economy.separator");
        var thousands = this._configReader.GetString("economy.thousand");

        if (this._configReader.GetBoolean("mysql.use")) {
            var hostname = this._configReader.GetString("mysql.hostname");
            var port = this._configReader.GetString("mysql.port");
            var username = this._configReader.GetString("mysql.username");
            var password = this._configReader.GetString("mysql.password");
            var database = this._configReader.GetString("mysql.database");

            var mariadb = this._configReader.GetBoolean("mysql.mariadb");

            this._mySQL = new MySQL(hostname, port, username, password, database, mariadb, this);
            this._mySQL.Connect();
            this._mySQL.CreateTable();
        }

        if (!this._configReader.GetBoolean("economy.enabled")) {
            this.Info("Economy disabled! Not using it...");
            this._economyManager = new EconomyManager_Disabled("", "", "", "", "", "", "", this);
        }

        if (!this._configReader.GetBoolean("banSystem.enabled")) {
            this.Info("BanSystem disabled! Not using it...");
            this._banManager = new BanManager_Disabled(new File(""), "", this);
            this._muteManager = new MuteManager_Disabled("", this);
        }

        if (this._configReader.GetBoolean("economy.enabled") || this._configReader.GetBoolean("banSystem.enabled")) {
            if (this._configReader.GetBoolean("mysql.use")) {
                this.Info("MySQL enabled, using it...");
                if (this._configReader.GetBoolean("mysql.economy.enabled") && this._configReader.GetBoolean("economy.enabled")) {
                    this.Info("Using Economy with MySQL...");
                    if (this._economyManager != null) {
                        this.Error("You cannot have two databases at the same time for economy activated!");
                    } else {
                        this._serverName = this._configReader.GetString("mysql.economy.serverName");
                        this._economyManager =
                                new EconomyManager_MySQL(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
                    }
                }
                if (this._configReader.GetBoolean("mysql.banSystem") && this._configReader.GetBoolean("banSystem.enabled")) {
                    this.Info("Using BanSystem with MySQL...");
                    if (this._banManager != null || this._muteManager != null) {
                        this.Error("You cannot have two databases at the same time for BanSystem activated!");
                    } else {
                        this._banManager = new BanManager_MySQL(dateFormat, this);
                        this._muteManager = new MuteManager_MySQL(this, dateFormat);
                    }
                }
            }

            if (this._configReader.GetBoolean("h2.use")) {
                this.Info("H2 enabled, using it...");
                if (this._configReader.GetBoolean("h2.economy") && this._configReader.GetBoolean("economy.enabled")) {
                    this.Info("Using Economy with H2...");
                    if (this._economyManager != null) {
                        this.Error("You cannot have two databases at the same time for economy activated!");
                    } else {
                        this._economyManager =
                                new EconomyManager_H2(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
                    }
                }

                if (this._configReader.GetBoolean("h2.banSystem") && this._configReader.GetBoolean("banSystem.enabled")) {
                    this.Info("Using BanSystem with H2...");
                    if (this._banManager != null || this._muteManager != null) {
                        this.Error("You cannot have two databases at the same time for BanSystem activated!");
                    } else {
                        this._banManager = new BanManager_H2(dateFormat, this);
                        this._muteManager = new MuteManager_H2(this, dateFormat);
                    }
                }
            }

            if (this._configReader.GetBoolean("sqlite.use")) {
                this.Info("SQLite enabled, using it...");
                if (this._configReader.GetBoolean("sqlite.economy") && this._configReader.GetBoolean("economy.enabled")) {
                    this.Info("Using Economy with SQLite...");
                    if (this._economyManager != null) {
                        this.Error("You cannot have two databases at the same time for economy activated!");
                    } else {
                        this._economyManager =
                                new EconomyManager_SQLite(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
                    }
                }

                if (this._configReader.GetBoolean("sqlite.banSystem") && this._configReader.GetBoolean("banSystem.enabled")) {
                    this.Info("Using BanSystem with SQLite...");
                    if (this._banManager != null || this._muteManager != null) {
                        this.Error("You cannot have two databases at the same time for BanSystem activated!");
                    } else {
                        this._banManager = new BanManager_SQLite(dateFormat, this);
                        this._muteManager = new MuteManager_SQLite(this, dateFormat);
                    }
                }
            }

            if (this._economyManager == null) {
                this.Warn("Not using any database for Economy...");
                this._economyManager = new EconomyManager(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
            }
            if (this._banManager == null) {
                this.Warn("Not using any database for BanSystem...");
                this._banManager = new BanManager_Yaml(new File("plugins//ServerSystem", "bans.yml"), dateFormat, this);
            }
            if (this._muteManager == null) this._muteManager = new MuteManager_Yaml(new File("plugins//ServerSystem", "muted.yml"), dateFormat, this);

        }
    }


    public void LoadConfigs() {
        this.saveDefaultConfig();
        this.reloadConfig();

        var permFile = new File("plugins//ServerSystem", "permissions.yml");
        var commandsFile = new File("plugins//ServerSystem", "commands.yml");
        var kitsFile = new File("plugins//ServerSystem", "kits.yml");

        if (!permFile.exists()) this.saveResource("permissions.yml", false);

        this.CreateMessagesFiles();

        if (!commandsFile.exists()) this.saveResource("commands.yml", false);

        if (!kitsFile.exists()) this.saveResource("kits.yml", false);

        this._rulesConfig = YamlConfiguration.loadConfiguration(this._rulesFile);
    }

    @Override
    public void onDisable() {
        this.Info("Shutting down...");

        this.Info("Cancelling leftover tasks...");
        Bukkit.getScheduler().cancelTasks(this);

        if (this._vaultHookManager != null) {
            this.Info("Unhooking from vault...");
            this._vaultHookManager.Unhook();
        }

        this.Info("Saving vanished players...");
        var file = new File("plugins//ServerSystem", "vanish.yml");
        var cfg = YamlConfiguration.loadConfiguration(file);
        for (var uuid : this._vanish.GetVanishList())
            cfg.set("Vanish." + uuid.toString(), true);
        try {
            cfg.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        this.Info("Closing banManager database...");
        if (this._banManager != null) this._banManager.Close();
        this.Info("Closing muteManager database...");
        if (this._muteManager != null) this._muteManager.Close();
        this.Info("Closing economyManager database...");
        if (this._economyManager != null) this._economyManager.Close();
        this.Info("Closing MySQL...");
        if (this._mySQL != null) this._mySQL.Close();

        this._banManager = null;
        this._muteManager = null;
        this._economyManager = null;

        this.Info("Unregistering commands...");
        this._commandManager.UnregisterCommands();

        this.Info("Syncing commands...");

        if (!this.SyncCommands()) return;

        this.Info("Unregistering Handlers...");

        HandlerList.unregisterAll(this);

        this.Info("Shutdown done!");
    }

    private void CreateMessagesFiles() {
        var msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
        var msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
        var msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
        var msgTRFile = new File("plugins//ServerSystem", "messages_tr.yml");
        var msgZHCNFile = new File("plugins//ServerSystem", "messages_zhcn.yml");
        var msgITFile = new File("plugins//ServerSystem", "messages_it.yml");
        var msgRUFile = new File("plugins//ServerSystem", "messages_ru.yml");
        var msgFile = new File("plugins//ServerSystem", "messages.yml");
        var rulesFile = new File("plugins//ServerSystem", "rules.yml");

        if (!msgDEFile.exists()) this.saveResource("messages_de.yml", false);

        if (!msgENFile.exists()) this.saveResource("messages_en.yml", false);

        if (!msgCZFile.exists()) this.saveResource("messages_cz.yml", false);

        if (!msgTRFile.exists()) this.saveResource("messages_tr.yml", false);

        if (!msgZHCNFile.exists()) this.saveResource("messages_zhcn.yml", false);

        if (msgITFile.exists()) this.saveResource("messages_it.yml", false);

        if (msgRUFile.exists()) this.saveResource("messages_ru.yml", false);

        if (!rulesFile.exists()) {
            var locale = System.getProperty("user.language");
            if (locale.equalsIgnoreCase("de")) {
                this.saveResource("rules_de.yml", true);
                new File("plugins//ServerSystem", "rules_de.yml").renameTo(new File("plugins//ServerSystem", "rules.yml"));
            } else if (locale.equalsIgnoreCase("cz")) {
                this.saveResource("rules_cz.yml", true);
                new File("plugins//ServerSystem", "rules_cz.yml").renameTo(new File("plugins//ServerSystem", "rules.yml"));
            } else if (locale.equalsIgnoreCase("it")) {
                this.saveResource("rules_it.yml", true);
                new File("plugins//ServerSystem", "rules_it.yml").renameTo(new File("plugins//ServerSystem", "rules.yml"));
            } else {
                this.saveResource("rules_en.yml", true);
                new File("plugins//ServerSystem", "rules_en.yml").renameTo(new File("plugins//ServerSystem", "rules.yml"));
            }
        }

        if (!msgFile.exists()) {
            var locale = System.getProperty("user.language");
            if (locale.equalsIgnoreCase("de")) {
                try {
                    Files.copy(msgDEFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (locale.equalsIgnoreCase("cz")) {
                try {
                    Files.copy(msgCZFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (locale.equalsIgnoreCase("tr")) {
                try {
                    Files.copy(msgTRFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (locale.toLowerCase(Locale.ROOT).contains("zh")) {
                try {
                    Files.copy(msgZHCNFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (locale.toLowerCase(Locale.ROOT).contains("it")) {
                try {
                    Files.copy(msgITFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (locale.toLowerCase(Locale.ROOT).contains("ru")) {
                try {
                    Files.copy(msgRUFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                try {
                    Files.copy(msgENFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }


    private String GetBanSystem(String action) {
        return this._messages.GetConfiguration().GetString("Messages.Misc.BanSystem." + action);
    }
}
