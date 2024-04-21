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
import me.entity303.serversystem.config.ConfigReader;
import me.entity303.serversystem.config.DefaultConfigReader;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

@SuppressWarnings("UnstableApiUsage") public final class ServerSystem extends JavaPlugin {
    public static boolean debug = false;
    private static EconomyAPI economyAPI;
    private static MuteAPI muteAPI;
    private static BanAPI banAPI;
    private static VanishAPI vanishAPI;
    public final String CONFIG_VERSION = "6.4";
    public final String JAR_NAME = this.getFile().getName();
    private final File RULES_FILE = new File("plugins//ServerSystem", "rules.yml");
    private final List<Player> cmdSpy = new ArrayList<>();
    private final List<Player> godList = new ArrayList<>();
    private final List<Player> msgOff = new ArrayList<>();
    private final List<Player> socialSpy = new ArrayList<>();
    private final Map<Player, Location> backloc = new HashMap<>();
    private final Map<Player, String> backreason = new HashMap<>();
    private final Map<Player, BukkitTask> teleportMap = new HashMap<>();
    private final Map<Player, TpaData> tpaDataMap = new HashMap<>();
    private final Map<Player, Player> enderchest = new HashMap<>();
    private final VersionStuff versionStuff = new VersionStuff(this);
    private EventManager eventManager;
    private VersionManager versionManager;
    private CommandManager commandManager;
    private boolean onceTold = false;
    private String serverName;
    private String newVersion = this.getDescription().getVersion();
    private Message messages;
    private PermissionsChecker PermissionsChecker;
    private boolean starting = true;
    private boolean registered = false;
    private boolean maintenance = false;
    private boolean stopFlightOnHit = false;
    private boolean disableFlightOnHit = false;
    private boolean specialSudo = true;
    private boolean advancedInvsee = true;
    private boolean clientsideOp = true;
    private Vanish vanish;
    private MetaValue metaValue;
    private WantsTeleport wantsTeleport;
    private KitsManager kitsManager;
    private ServerSystemTimer timer;
    private ManagerBan banManager;
    private ManagerMute muteManager;
    private WarpManager warpManager;
    private HomeManager homeManager;
    private MySQL mySQL;
    private ManagerEconomy economyManager;
    private Vault vault;
    private VaultHookManager vaultHookManager;
    private EssentialsCommandListener essentialsCommandListener = null;
    private Furnace furnace;
    private MetricsLite metrics;
    private FileConfiguration rulesConfig;
    private Method syncCommandsMethod = null;
    private ConfigReader configReader;

    public static EconomyAPI getEconomyAPI() {
        return ServerSystem.economyAPI;
    }

    public static MuteAPI getMuteAPI() {
        return ServerSystem.muteAPI;
    }

    public static BanAPI getBanAPI() {
        return ServerSystem.banAPI;
    }

    public static VanishAPI getVanishAPI() {
        return ServerSystem.vanishAPI;
    }

    public WantsTeleport getWantsTeleport() {
        return this.wantsTeleport;
    }

    public boolean isSpecialSudo() {
        return this.specialSudo;
    }

    public VersionManager getVersionManager() {
        return this.versionManager;
    }

    public VersionStuff getVersionStuff() {
        return this.versionStuff;
    }

    public EssentialsCommandListener getEssentialsCommandListener() {
        return this.essentialsCommandListener;
    }

    public HomeManager getHomeManager() {
        return this.homeManager;
    }

    public boolean isStopFlightOnHit() {
        return this.stopFlightOnHit;
    }

    public void setStopFlightOnHit(boolean stopFlightOnHit) {
        this.stopFlightOnHit = stopFlightOnHit;
    }

    public List<Player> getSocialSpy() {
        return this.socialSpy;
    }

    public boolean isDisableFlightOnHit() {
        return this.disableFlightOnHit;
    }

    public void setDisableFlightOnHit(boolean disableFlightOnHit) {
        this.disableFlightOnHit = disableFlightOnHit;
    }

    public File getRULES_FILE() {
        return this.RULES_FILE;
    }

    public FileConfiguration getRulesConfig() {
        return this.rulesConfig;
    }

    public boolean isMaintenance() {
        return this.maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public boolean isAdvancedInvsee() {
        return this.advancedInvsee;
    }

    public void setAdvancedInvsee(boolean advancedInvsee) {
        this.advancedInvsee = advancedInvsee;
    }

    public boolean isClientsideOp() {
        return this.clientsideOp;
    }

    public void setClientsideOp(boolean clientsideOp) {
        this.clientsideOp = clientsideOp;
    }    /*
    Fix for something, don't know anymore
     */

    public boolean isRegistered() {
        return this.registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public KitsManager getKitsManager() {
        return this.kitsManager;
    }

    public List<Player> getGodList() {
        return this.godList;
    }

    public List<Player> getCmdSpy() {
        return this.cmdSpy;
    }

    public Map<Player, Player> getEnderchest() {
        return this.enderchest;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

    public ManagerMute getMuteManager() {
        return this.muteManager;
    }

    public ManagerBan getBanManager() {
        return this.banManager;
    }

    public Map<Player, TpaData> getTpaDataMap() {
        return this.tpaDataMap;
    }

    public MetricsLite getMetrics() {
        return this.metrics;
    }

    public List<Player> getMsgOff() {
        return this.msgOff;
    }

    public Map<Player, Location> getBackloc() {
        return this.backloc;
    }

    public Map<Player, String> getBackreason() {
        return this.backreason;
    }

    public MetaValue getMetaValue() {
        return this.metaValue;
    }

    public Map<Player, BukkitTask> getTeleportMap() {
        return this.teleportMap;
    }

    public boolean isStarting() {
        return this.starting;
    }

    public PermissionsChecker getPermissions() {
        return this.PermissionsChecker;
    }

    public String getNewVersion() {
        return this.newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public WarpManager getWarpManager() {
        return this.warpManager;
    }

    public ServerSystemTimer getTimer() {
        return this.timer;
    }

    public String getConfigVersion() {
        return this.CONFIG_VERSION;
    }

    public Vault getVault() {
        return this.vault;
    }

    public VaultHookManager getVaultHookManager() {
        return this.vaultHookManager;
    }

    public Furnace getFurnace() {
        return this.furnace;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    private boolean checkMainServerForUpdates(String currentVersion, boolean autoUpdate) {
        var foundVersion = this.getDescription().getVersion();

        Document doc;
        try {
            doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem")
                       .referrer("ServerSystem")
                       .timeout(30000)
                       .get();
        } catch (IOException e) {
            this.error("An error occurred while trying to connect to the updater!");
            //e.printStackTrace();
            this.log("Please ignore this error. The update server is currently down. Please be patient");
            return false;
        }

        for (var remoteFile : doc.getElementsContainingOwnText(".jar")) {
            var remoteFileName = remoteFile.attr("href");
            remoteFileName = remoteFileName.substring(0, remoteFileName.lastIndexOf('.'));
            foundVersion = remoteFileName;
        }

        var isFoundVersionMoreRecent = this.isFoundVersionMoreRecent(foundVersion, currentVersion);

        if (!isFoundVersionMoreRecent || currentVersion.equalsIgnoreCase(foundVersion)) {
            if (this.onceTold)
                return true;

            this.log("You are using the latest version of ServerSystem <3");
            this.onceTold = true;
            return true;
        }

        this.warn("There is a new version available! (" + foundVersion + ")");
        if (!autoUpdate) {
            if (!this.getConfigReader().getBoolean("updates.notifyOnJoin"))
                return true;

            if (!foundVersion.equalsIgnoreCase(this.newVersion))
                this.newVersion = foundVersion;

            if (this.registered)
                return true;

            this.registered = true;
            this.getEventManager().registerEvent(new JoinUpdateListener(this));
            return true;
        }

        this.log("Auto-updating!");
        this.log("(You need to restart the server so the update can take effect)");
        try {
            var resultImageResponse = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + foundVersion + ".jar")
                                           .referrer("ServerSystem")
                                           .timeout(30000)
                                           .ignoreContentType(true)
                                           .execute();

            var in = new BufferedInputStream(
                    new URL("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + foundVersion + ".jar").openStream());
            var fileOutputStream = new FileOutputStream(new File("plugins/update", this.JAR_NAME));
            var dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                fileOutputStream.write(dataBuffer, 0, bytesRead);

            in.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            this.error("Error while trying downloading the update!");
            e.printStackTrace();
        }
        return false;
    }

    public void error(String text) {
        Bukkit.getConsoleSender()
              .sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&4Error&8] [&4ServerSystem&8] &7>> &4" + text));
    }

    public void log(String text) {
        Bukkit.getConsoleSender()
              .sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aInfo&8] [&aServerSystem&8] &7>> &a" + text));
    }

    private boolean isFoundVersionMoreRecent(String foundVersion, String currentVersion) {
        var foundVersionSplit = foundVersion.split("\\.");

        if (foundVersionSplit.length < 3)
            return false;

        var foundVersionMajor = Long.parseLong(foundVersionSplit[0]);
        var foundVersionMinor = Long.parseLong(foundVersionSplit[1]);
        var foundVersionPatch = Long.parseLong(foundVersionSplit[2]);

        var currentVersionSplit = currentVersion.split("\\.");

        if (currentVersionSplit.length < 3)
            return true;

        var currentVersionMajor = Long.parseLong(currentVersionSplit[0]);
        var currentVersionMinor = Long.parseLong(currentVersionSplit[1]);
        var currentVersionPatch = Long.parseLong(currentVersionSplit[2]);


        if (currentVersionMajor < foundVersionMajor)
            return true;

        if (currentVersionMinor < foundVersionMinor)
            return true;

        return currentVersionPatch < foundVersionPatch;
    }

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        this.configReader = new NonValidatingConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
    }

    public void warn(String text) {
        Bukkit.getConsoleSender()
              .sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&cWarning&8] [&cServerSystem&8] &7>> &c" + text));
    }

    public ConfigReader getConfigReader() {
        return this.configReader;
    }

    @Override
    public void reloadConfig() {
        if (this.configReader != null)
            this.configReader.reload();
        else
            this.configReader = new NonValidatingConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public void reloadConfigValidating() {
        if (this.configReader == null || this.configReader instanceof NonValidatingConfigReader) {
            this.configReader = new DefaultConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
            return;
        }

        this.configReader.reload();
    }


    @Override
    public void saveConfig() {
        if (this.configReader != null)
            this.configReader.save();
    }


    private void checkServerSoftware() {
        try {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                try {
                    Bukkit.class.getDeclaredMethod("spigot");
                } catch (Exception ignored) {
                    this.error("Unsupported Serversoftware!");
                    this.warn("ServerSystem may not work (correctly)!");
                }
            }, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLoad() {
        this.loadConfigs();

        this.reloadConfigValidating();


        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            this.vault = new Vault();
            this.vaultHookManager = new VaultHookManager(this);
            this.vaultHookManager.hook();
        }
    }


    @Override
    public void onEnable() {
        if (!new File("plugins//update").exists())
            new File("plugins//update").mkdirs();

        this.starting = true;
        this.metaValue = new MetaValue(this);
        this.vanish = new Vanish(this);

        ServerSystem.debug = this.getConfigReader().getBoolean("debug");

        this.messages = new Message(this);
        this.PermissionsChecker = new PermissionsChecker(this);

        this.versionManager = new VersionManager(this);

        this.versionManager.registerVersionStuff();

        var file = new File("plugins//ServerSystem", "vanish.yml");
        if (file.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.getConfigurationSection("Vanish") != null)
                if (!cfg.getConfigurationSection("Vanish").getKeys(false).isEmpty())
                    for (var uuidString : cfg.getConfigurationSection("Vanish").getKeys(false))
                        this.getVanish().getVanishList().add(UUID.fromString(uuidString));
            file.delete();
        }

        this.kitsManager = new KitsManager(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new ServerSystemExpansion(this).register();

        this.commandManager = new CommandManager(this);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.commandManager.registerCommands();

            if (!this.SyncCommands())
                return;

            if (!Bukkit.getOnlinePlayers().isEmpty())
                for (var player : Bukkit.getOnlinePlayers())
                    player.updateCommands();
        }, 5L);

        this.eventManager = new EventManager(this);

        this.eventManager.registerEvents();

        Bukkit.getScheduler().runTaskLater(this, () -> this.starting = false, 100L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<Player> players = new ArrayList<>();
            for (var entry : this.tpaDataMap.entrySet()) {
                var player = entry.getKey();

                if (entry.getValue().getEnd() <= System.currentTimeMillis())
                    players.add(player);
            }

            if (!players.isEmpty())
                for (var player : players)
                    this.tpaDataMap.remove(player);
        }, 20L, 20L);

        this.wantsTeleport = new WantsTeleport(this);
        this.timer = new ServerSystemTimer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this.timer, 1000L, 50L);

        this.setupEconomyBanSystem();

        ServerSystem.economyAPI = new EconomyAPI(this);
        ServerSystem.muteAPI = new MuteAPI(this);
        ServerSystem.banAPI = new BanAPI(this);
        ServerSystem.vanishAPI = new VanishAPI(this);

        this.warpManager = new WarpManager(this);

        this.homeManager = new HomeManager();

        yearName = this.getBanSystem("YearName");
        monthName = this.getBanSystem("MonthName");
        weekName = this.getBanSystem("WeekName");
        dayName = this.getBanSystem("DayName");
        hourName = this.getBanSystem("HourName");
        minuteName = this.getBanSystem("MinuteName");
        secondName = this.getBanSystem("SecondName");

        this.disableFlightOnHit = this.getConfigReader().getBoolean("fly.disableWhenHit");
        this.stopFlightOnHit = this.getConfigReader().getBoolean("fly.stopWhenHit");

        if (this.disableFlightOnHit)
            this.stopFlightOnHit = true;

        if (this.stopFlightOnHit)
            this.getEventManager().registerEvent(new FlightHitListener(this));

        if (this.getConfigReader().getBoolean("metrics"))
            this.metrics = new MetricsLite(this, 9043);

        Bukkit.getScheduler().runTaskLater(this, () -> this.furnace = new Furnace(this), 10L);

        this.startDeactivatingCommands();

        this.specialSudo = this.getConfigReader().getBoolean("specialSudo", true);

        this.advancedInvsee = this.getConfigReader().getBoolean("advancedInvSee", true);

        this.clientsideOp = this.getConfigReader().getBoolean("clientsideOpSpoof", true);

        var commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFiles);

        if (commandsConfig.getBoolean("baltop"))
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> this.getEconomyManager().fetchTopTen(), 20L, 72000L);

        this.startSwappingCommands();

        this.startUpdateCheck();

        this.checkServerSoftware();

        Bukkit.getScheduler().runTask(this, () -> {
            var markerFile = new File("plugins" + File.separator + "ServerSystem", "marker.ignore");
            if (markerFile.exists())
                return;

            this.warn("!!!!! BREAKING CHANGES !!!!!");
            this.warn("As of 2.0.0, there are some breaking changes!");
            this.warn("If you updated from 1.8.3, you should be fine.");
            this.warn("If you're using a minecraft version below 1.17.1, this version of ServerSystem will NOT work!");
            this.warn("Support for every version below 1.17.1 was completely dropped!");
            this.warn("!!!!! BREAKING CHANGES !!!!!");

            try {
                markerFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean SyncCommands() {
        if (this.syncCommandsMethod == null)
            try {
                this.syncCommandsMethod = Class.forName("org.bukkit.craftbukkit." + this.versionManager.getNMSVersion() + ".CraftServer")
                                               .getDeclaredMethod("syncCommands");
                this.syncCommandsMethod.setAccessible(true);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }

        try {
            this.syncCommandsMethod.invoke(Bukkit.getServer());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private void startDeactivatingCommands() {
        if (this.getConfigReader().getBoolean("deactivatedCommands.enabled"))
            Bukkit.getScheduler()
                  .runTaskLater(this,
                                () -> this.getConfigReader().getConfigurationSection("deactivatedCommands").getKeys(false).forEach(cmd -> {
                                    if (!cmd.equalsIgnoreCase("enabled")) {
                                        this.log("Deactivating command " + cmd + " from plugin " +
                                                 this.getConfigReader().getString("deactivatedCommands." + cmd) + "!");
                                        this.commandManager.deactivateBukkitCommand(cmd.toLowerCase(), this.getConfigReader()
                                                                                                           .getString(
                                                                                                                   "deactivatedCommands." +
                                                                                                                   cmd)
                                                                                                           .toLowerCase());
                                    }
                                }), 40L);
    }


    private void startSwappingCommands() {
        if (this.getConfigReader().getBoolean("swapCommands.enabled"))
            Bukkit.getScheduler()
                  .runTaskLater(this,
                                () -> this.getConfigReader().getConfigurationSection("swapCommands").getKeys(false).forEach(cmdFrom -> {
                                    if (!cmdFrom.equalsIgnoreCase("enabled")) {
                                        var pluginFrom = this.getConfigReader().getString("swapCommands." + cmdFrom + ".fromPlugin");
                                        var cmdTo = this.getConfigReader().getString("swapCommands." + cmdFrom + ".toCommand");
                                        var pluginTo = this.getConfigReader().getString("swapCommands." + cmdFrom + ".toPlugin");

                                        this.log("Swapping command " + cmdFrom + " from plugin " + pluginFrom + " to command " + cmdTo +
                                                 " from plugin " + pluginTo + "!");

                                        var cmdFromToLower = pluginFrom.toLowerCase() + ":" + cmdFrom.toLowerCase();

                                        var commandFrom = Bukkit.getPluginCommand(cmdFromToLower);
                                        var commandToPluginCommand =
                                                Bukkit.getPluginCommand(pluginTo.toLowerCase() + ":" + cmdTo.toLowerCase());

                                        if (commandFrom == null) {
                                            this.warn("Command " + cmdFrom + " does not exist in plugin " + pluginFrom + "!");
                                            return;
                                        }

                                        if (commandToPluginCommand == null) {
                                            this.warn("Command " + cmdTo + " does not exist in plugin " + pluginTo + "!");
                                            return;
                                        }

                                        commandFrom.setExecutor(commandToPluginCommand.getExecutor());
                                        commandFrom.setTabCompleter(commandToPluginCommand.getTabCompleter());
                                        commandFrom.setPermission(commandToPluginCommand.getPermission());
                                        commandFrom.setPermissionMessage(commandToPluginCommand.getPermissionMessage());
                                        commandFrom.setDescription(commandToPluginCommand.getDescription());

                                        if (pluginTo.equalsIgnoreCase("Essentials")) {
                                            if (this.essentialsCommandListener == null) {
                                                var essentialsPlugin = JavaPlugin.getProvidingPlugin(
                                                        this.getServer().getPluginManager().getPlugin("Essentials").getClass());
                                                var essentials = (Essentials) essentialsPlugin;
                                                this.essentialsCommandListener = new EssentialsCommandListener(essentials, this);
                                                this.getEventManager().registerEvent(this.essentialsCommandListener);
                                            }
                                            if (this.getServer().getPluginCommand(cmdFromToLower) ==
                                                this.getServer().getPluginCommand(cmdFrom.toLowerCase()))
                                                this.essentialsCommandListener.addCommand(cmdFrom, cmdTo);
                                            else
                                                this.essentialsCommandListener.addCommand(pluginFrom + ":" + cmdFrom, cmdTo);
                                        }
                                    }
                                }), 60L);
    }


    private void startUpdateCheck() {
        if (this.getConfigReader().getBoolean("updates.check"))
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
                var autoUpdate = this.getConfigReader().getBoolean("updates.autoUpdate");

                var currentVersion = this.getDescription().getVersion();

                if (this.checkMainServerForUpdates(currentVersion, autoUpdate))
                    return;

                this.checkBackupServerForUpdates(currentVersion, autoUpdate);
            }, 80L, 7200L * 20L);
    }

    private void checkBackupServerForUpdates(String currentVersion, Boolean autoUpdate) {
        this.log("Switching to backup updater!");
        new UpdateChecker(this, "78974").getVersion(foundVersion -> {
            var isFoundVersionMoreRecent = this.isFoundVersionMoreRecent(foundVersion, currentVersion);

            if (!isFoundVersionMoreRecent || currentVersion.equalsIgnoreCase(foundVersion)) {
                if (this.onceTold)
                    return;

                this.log("You are using the latest version of ServerSystem <3");
                this.onceTold = true;
            }

            this.warn("There is a new update available (" + foundVersion + ")!");
            if (!autoUpdate) {
                if (!this.getConfigReader().getBoolean("updates.notifyOnJoin"))
                    return;

                if (!foundVersion.equalsIgnoreCase(this.newVersion))
                    this.newVersion = foundVersion;

                if (this.registered)
                    return;

                this.registered = true;
                this.getEventManager().registerEvent(new JoinUpdateListener(this));
                return;
            }

            this.log("Auto-updating!");
            this.log("(You need to restart the server so the update can take effect)");

            try (var inputStream = new BufferedInputStream(new URL("https://api.spiget.org/v2/resources/78974/download").openStream());
                 var fileOutputStream = new FileOutputStream(new File("plugins/update", this.JAR_NAME))) {
                var dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1)
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
            } catch (IOException e) {
                e.printStackTrace();
                this.error("Error while trying downloading the update!");
                this.error("Please download it by yourself (https://www.spigotmc.org/resources/serversystem.78974/)!");
                if (!this.registered) {
                    this.registered = true;
                    this.getEventManager().registerEvent(new JoinUpdateListener(this));
                }

                if (!foundVersion.equalsIgnoreCase(this.newVersion))
                    this.newVersion = foundVersion;
            }
        });
    }


    public void setupEconomyBanSystem() {
        String dateFormat;
        dateFormat = this.getConfigReader().getString("banSystem.dateFormat");


        var currencySingular = this.getConfigReader().getString("economy.currency.singular");
        var currencyPlural = this.getConfigReader().getString("economy.currency.plural");
        var startingMoney = this.getConfigReader().getString("economy.startingMoney");
        var displayFormat = this.getConfigReader().getString("economy.displayFormat");
        var moneyFormat = this.getConfigReader().getString("economy.moneyFormat");
        var separator = this.getConfigReader().getString("economy.separator");
        var thousands = this.getConfigReader().getString("economy.thousand");

        if (this.getConfigReader().getBoolean("mysql.use")) {
            var hostname = this.getConfigReader().getString("mysql.hostname");
            var port = this.getConfigReader().getString("mysql.port");
            var username = this.getConfigReader().getString("mysql.username");
            var password = this.getConfigReader().getString("mysql.password");
            var database = this.getConfigReader().getString("mysql.database");

            var mariadb = this.getConfigReader().getBoolean("mysql.mariadb");

            this.mySQL = new MySQL(hostname, port, username, password, database, mariadb, this);
            this.mySQL.connect();
            this.mySQL.createTable();
        }

        if (!this.getConfigReader().getBoolean("economy.enabled")) {
            this.log("Economy disabled! Not using it...");
            this.economyManager = new EconomyManager_Disabled("", "", "", "", "", "", "", this);
        }

        if (!this.getConfigReader().getBoolean("banSystem.enabled")) {
            this.log("BanSystem disabled! Not using it...");
            this.banManager = new BanManager_Disabled(new File(""), "", this);
            this.muteManager = new MuteManager_Disabled(new File(""), "", this);
        }

        if (this.getConfigReader().getBoolean("economy.enabled") || this.getConfigReader().getBoolean("banSystem.enabled")) {
            if (this.getConfigReader().getBoolean("mysql.use")) {
                this.log("MySQL enabled, using it...");
                if (this.getConfigReader().getBoolean("mysql.economy.enabled") && this.getConfigReader().getBoolean("economy.enabled")) {
                    this.log("Using Economy with MySQL...");
                    if (this.economyManager != null)
                        this.error("You cannot have two databases at the same time for economy activated!");
                    else {
                        this.setServerName(this.getConfigReader().getString("mysql.economy.serverName"));
                        this.economyManager =
                                new EconomyManager_MySQL(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat,
                                                         separator, thousands, this);
                    }
                }
                if (this.getConfigReader().getBoolean("mysql.banSystem") && this.getConfigReader().getBoolean("banSystem.enabled")) {
                    this.log("Using BanSystem with MySQL...");
                    if (this.banManager != null || this.muteManager != null)
                        this.error("You cannot have two databases at the same time for BanSystem activated!");
                    else {
                        this.banManager = new BanManager_MySQL(dateFormat, this);
                        this.muteManager = new MuteManager_MySQL(this, dateFormat);
                    }
                }
            }

            if (this.getConfigReader().getBoolean("h2.use")) {
                this.log("H2 enabled, using it...");
                if (this.getConfigReader().getBoolean("h2.economy") && this.getConfigReader().getBoolean("economy.enabled")) {
                    this.log("Using Economy with H2...");
                    if (this.economyManager != null)
                        this.error("You cannot have two databases at the same time for economy activated!");
                    else
                        this.economyManager =
                                new EconomyManager_H2(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat,
                                                      separator, thousands, this);
                }

                if (this.getConfigReader().getBoolean("h2.banSystem") && this.getConfigReader().getBoolean("banSystem.enabled")) {
                    this.log("Using BanSystem with H2...");
                    if (this.banManager != null || this.muteManager != null)
                        this.error("You cannot have two databases at the same time for BanSystem activated!");
                    else {
                        this.banManager = new BanManager_H2(dateFormat, this);
                        this.muteManager = new MuteManager_H2(this, dateFormat);
                    }
                }
            }

            if (this.getConfigReader().getBoolean("sqlite.use")) {
                this.log("SQLite enabled, using it...");
                if (this.getConfigReader().getBoolean("sqlite.economy") && this.getConfigReader().getBoolean("economy.enabled")) {
                    this.log("Using Economy with SQLite...");
                    if (this.economyManager != null)
                        this.error("You cannot have two databases at the same time for economy activated!");
                    else
                        this.economyManager =
                                new EconomyManager_SQLite(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat,
                                                          separator, thousands, this);
                }

                if (this.getConfigReader().getBoolean("sqlite.banSystem") && this.getConfigReader().getBoolean("banSystem.enabled")) {
                    this.log("Using BanSystem with SQLite...");
                    if (this.banManager != null || this.muteManager != null)
                        this.error("You cannot have two databases at the same time for BanSystem activated!");
                    else {
                        this.banManager = new BanManager_SQLite(dateFormat, this);
                        this.muteManager = new MuteManager_SQLite(this, dateFormat);
                    }
                }
            }

            if (this.economyManager == null) {
                this.warn("Not using any database for Economy...");
                this.economyManager =
                        new EconomyManager(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator,
                                           thousands, this);
            }
            if (this.banManager == null) {
                this.warn("Not using any database for BanSystem...");
                this.banManager = new BanManager(new File("plugins//ServerSystem", "bans.yml"), dateFormat, this);
            }
            if (this.muteManager == null)
                this.muteManager = new MuteManager(new File("plugins//ServerSystem", "muted.yml"), dateFormat, this);

        }
    }


    private void loadConfigs() {
        this.saveDefaultConfig();
        this.reloadConfig();

        var permFile = new File("plugins//ServerSystem", "permissions.yml");
        var commandsFile = new File("plugins//ServerSystem", "commands.yml");
        var aliasesFile = new File("plugins//ServerSystem", "aliases.yml");
        var kitsFile = new File("plugins//ServerSystem", "kits.yml");

        if (!permFile.exists())
            this.saveResource("permissions.yml", false);

        this.CreateMessagesFiles();

        if (!commandsFile.exists())
            this.saveResource("commands.yml", false);

        if (!aliasesFile.exists())
            this.saveResource("aliases.yml", false);

        if (!kitsFile.exists())
            this.saveResource("kits.yml", false);

        this.rulesConfig = YamlConfiguration.loadConfiguration(this.RULES_FILE);
    }

    @Override
    public void onDisable() {
        this.log("Shutting down...");

        this.log("Cancelling leftover tasks...");
        Bukkit.getScheduler().cancelTasks(this);

        if (this.vaultHookManager != null) {
            this.log("Unhooking from vault...");
            this.vaultHookManager.unhook();
        }

        this.log("Saving vanished players...");
        var file = new File("plugins//ServerSystem", "vanish.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (var uuid : this.getVanish().getVanishList())
            cfg.set("Vanish." + uuid.toString(), true);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.log("Closing banManager database...");
        if (this.banManager != null)
            this.banManager.close();
        this.log("Closing muteManager database...");
        if (this.muteManager != null)
            this.muteManager.close();
        this.log("Closing economyManager database...");
        if (this.economyManager != null)
            this.economyManager.close();
        this.log("Closing MySQL...");
        if (this.mySQL != null)
            this.mySQL.close();

        this.banManager = null;
        this.muteManager = null;
        this.economyManager = null;

        this.log("Unregistering commands...");
        this.commandManager.unregisterCommands();

        this.log("Syncing commands...");

        if (!this.SyncCommands())
            return;

        this.log("Unregistering Handlers...");

        HandlerList.unregisterAll(this);

        this.log("Shutdown done!");
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

        if (!msgDEFile.exists())
            this.saveResource("messages_de.yml", false);

        if (!msgENFile.exists())
            this.saveResource("messages_en.yml", false);

        if (!msgCZFile.exists())
            this.saveResource("messages_cz.yml", false);

        if (!msgTRFile.exists())
            this.saveResource("messages_tr.yml", false);

        if (!msgZHCNFile.exists())
            this.saveResource("messages_zhcn.yml", false);

        if (msgITFile.exists())
            this.saveResource("messages_it.yml", false);

        if (msgRUFile.exists())
            this.saveResource("messages_ru.yml", false);

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
            if (locale.equalsIgnoreCase("de"))
                try {
                    Files.copy(msgDEFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if (locale.equalsIgnoreCase("cz"))
                try {
                    Files.copy(msgCZFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if (locale.equalsIgnoreCase("tr"))
                try {
                    Files.copy(msgTRFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if (locale.toLowerCase(Locale.ROOT).contains("zh"))
                try {
                    Files.copy(msgZHCNFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if (locale.toLowerCase(Locale.ROOT).contains("it"))
                try {
                    Files.copy(msgITFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if (locale.toLowerCase(Locale.ROOT).contains("ru"))
                try {
                    Files.copy(msgRUFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                try {
                    Files.copy(msgENFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    private String getBanSystem(String action) {
        return this.getMessages().getCfg().getString("Messages.Misc.BanSystem." + action);
    }


    public ManagerEconomy getEconomyManager() {
        return this.economyManager;
    }


    public Message getMessages() {
        return this.messages;
    }


    public Vanish getVanish() {
        return this.vanish;
    }


}
