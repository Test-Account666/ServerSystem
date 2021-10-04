package me.Entity303.ServerSystem.Main;

import com.earth2me.essentials.Essentials;
import com.google.common.io.Files;
import me.Entity303.ServerSystem.API.BanAPI;
import me.Entity303.ServerSystem.API.EconomyAPI;
import me.Entity303.ServerSystem.API.MuteAPI;
import me.Entity303.ServerSystem.API.VanishAPI;
import me.Entity303.ServerSystem.BanSystem.*;
import me.Entity303.ServerSystem.Commands.util.CommandManager;
import me.Entity303.ServerSystem.DatabaseManager.HomeManager;
import me.Entity303.ServerSystem.DatabaseManager.MySQL;
import me.Entity303.ServerSystem.DatabaseManager.WarpManager;
import me.Entity303.ServerSystem.Economy.*;
import me.Entity303.ServerSystem.Events.EventManager;
import me.Entity303.ServerSystem.Listener.Command.EssentialsCommandListener;
import me.Entity303.ServerSystem.Listener.FlightHitListener;
import me.Entity303.ServerSystem.Listener.Join.JoinUpdateListener;
import me.Entity303.ServerSystem.PlaceholderAPI.ServerSystemExpansion;
import me.Entity303.ServerSystem.Utils.*;
import me.Entity303.ServerSystem.Utils.versions.VersionManager;
import me.Entity303.ServerSystem.Utils.versions.VersionStuff;
import me.Entity303.ServerSystem.Vanish.MetaValue;
import me.Entity303.ServerSystem.Vanish.Vanish;
import me.Entity303.ServerSystem.Vault.Vault;
import me.Entity303.ServerSystem.Vault.VaultHookManager;
import me.Entity303.ServerSystem.bStats.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.crypto.Cipher;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static me.Entity303.ServerSystem.BanSystem.TimeUnit.*;

public final class ss extends JavaPlugin {
    public static boolean debug = false;
    private static EconomyAPI economyAPI;
    private static MuteAPI muteAPI;
    private static BanAPI banAPI;
    private static VanishAPI vanishAPI;
    public final String CONFIG_VERSION = "6.3";
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
    private permissions permissions;
    private boolean starting = true;
    private boolean registered = false;
    private boolean maintenance = false;
    private boolean stopFlightOnHit = false;
    private boolean disableFlightOnHit = false;
    private Vanish vanish;
    private MetaValue metaValue;
    private WantsTP wantsTP;
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

    public static EconomyAPI getEconomyAPI() {
        return ss.economyAPI;
    }

    public static MuteAPI getMuteAPI() {
        return ss.muteAPI;
    }

    public static BanAPI getBanAPI() {
        return ss.banAPI;
    }

    public static VanishAPI getVanishAPI() {
        return ss.vanishAPI;
    }

    public EventManager getEventManager() {
        return this.eventManager;
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

    public ManagerEconomy getEconomyManager() {
        return this.economyManager;
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

    public boolean wantsTP(Player player) {
        ResultSet resultSet = this.wantsTP.query("SELECT wants FROM wantsTP WHERE UUID='" + player.getUniqueId() + "'");
        if (resultSet != null) while (true) {
            try {
                if (!resultSet.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                return Boolean.parseBoolean(resultSet.getString("wants"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else this.error("ResultSet null");
        return true;
    }

    public List<Player> getMsgOff() {
        return this.msgOff;
    }

    public void setWantsTP(Player player, Boolean wants) {
        this.wantsTP.update("DELETE FROM wantsTP WHERE UUID='" + player.getUniqueId() + "'");
        this.wantsTP.update("INSERT INTO wantsTP (UUID, wants) VALUES ('" + player.getUniqueId() + "','" + wants.toString() + "')");
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

    public Message getMessages() {
        return this.messages;
    }

    public permissions getPermissions() {
        return this.permissions;
    }

    private void check() {
        try {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                try {
                    Bukkit.class.getDeclaredMethod("spigot");
                } catch (Exception ignored) {
                    this.error("Unsupported Serversoftware!");
                    this.warn("ServerSystem may not work (correctly)!");
                }
                try {
                    Class.forName("com.tuinity.tuinity.config.TuinityConfig");
                } catch (Exception ignored) {
                    this.log("I recommend you using Tuinity");
                    this.log("This is just a RECOMMENDATION!!!!!!!");
                }
            }, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        this.check();

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            this.vault = new Vault();
            this.vaultHookManager = new VaultHookManager(this);
            this.vaultHookManager.hook();
        }
    }

    @Override
    public void onEnable() {
        this.fixKeyLength();

        if (!new File("plugins//update").exists()) new File("plugins//update").mkdirs();

        this.starting = true;
        this.metaValue = new MetaValue(this);
        this.vanish = new Vanish(this);

        this.loadConfigs();

        ConfigUpdater updater = new ConfigUpdater(this);

        if (updater.configUpdateNeeded(this.getConfig().getString("version"))) {
            updater.updateConfig(this.getConfig().getString("version"));
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) this.vault = new Vault();

        ss.debug = this.getConfig().getBoolean("debug");

        this.messages = new Message(this);
        this.permissions = new permissions(this);

        this.versionManager = new VersionManager(this);

        this.versionManager.registerVersionStuff();

        File file = new File("plugins//ServerSystem", "vanish.yml");
        if (file.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.getConfigurationSection("Vanish") != null)
                if (cfg.getConfigurationSection("Vanish").getKeys(false).size() > 0)
                    for (String uuidString : cfg.getConfigurationSection("Vanish").getKeys(false))
                        this.getVanish().getVanishList().add(UUID.fromString(uuidString));
            file.delete();
        }

        this.kitsManager = new KitsManager(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new ServerSystemExpansion(this).register();

        this.commandManager = new CommandManager(this);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.commandManager.registerCommands();

            if (this.getVersionManager().isV113()) if (Bukkit.getOnlinePlayers().size() >= 1)
                for (Player player : Bukkit.getOnlinePlayers()) player.updateCommands();
        }, 5L);

        this.eventManager = new EventManager(this);

        this.eventManager.registerEvents();

        Bukkit.getScheduler().runTaskLater(this, () -> this.starting = false, 100L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<Player> players = new ArrayList<>();
            for (Player player : this.tpaDataMap.keySet())
                if (this.tpaDataMap.get(player).getEnd() <= System.currentTimeMillis()) players.add(player);

            if (players.size() >= 1)
                for (Player player : players) this.tpaDataMap.remove(player);
        }, 20L, 20L);

        this.wantsTP = new WantsTP(this);
        this.timer = new ServerSystemTimer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this.timer, 1000L, 50L);

        this.setupEconomyBanSystem();

        ss.economyAPI = new EconomyAPI(this);
        ss.muteAPI = new MuteAPI(this);
        ss.banAPI = new BanAPI(this);
        ss.vanishAPI = new VanishAPI(this);

        this.warpManager = new WarpManager(this);

        this.homeManager = new HomeManager();

        yearName = this.getBanSystem("YearName");
        monthName = this.getBanSystem("MonthName");
        weekName = this.getBanSystem("WeekName");
        dayName = this.getBanSystem("DayName");
        hourName = this.getBanSystem("HourName");
        minuteName = this.getBanSystem("MinuteName");
        secondName = this.getBanSystem("SecondName");

        this.disableFlightOnHit = this.getConfig().getBoolean("fly.disablewhenhit");
        this.stopFlightOnHit = this.getConfig().getBoolean("fly.stopwhenhit");

        if (this.disableFlightOnHit)
            this.stopFlightOnHit = true;

        if (this.stopFlightOnHit) this.getEventManager().re(new FlightHitListener(this));

        if (this.getConfig().getBoolean("metrics"))
            this.metrics = new MetricsLite(this, 9043);

        Bukkit.getScheduler().runTaskLater(this, () -> this.furnace = new Furnace(this), 10L);

        this.startDeactivatingCommands();

        File commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFiles);

        if (commandsConfig.getBoolean("baltop"))
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> this.getEconomyManager().fetchTopTen(), 20L, 72000L);

        this.startSwappingCommands();

        this.startUpdateCheck();
    }

    private void startDeactivatingCommands() {
        if (this.getConfig().getBoolean("deactivatedcommands.enabled"))
            Bukkit.getScheduler().runTaskLater(this, () -> this.getConfig().getConfigurationSection("deactivatedcommands").getKeys(false).forEach(cmd -> {
                if (!cmd.equalsIgnoreCase("enabled")) {
                    this.log("Deactivating command " + cmd + " from plugin " + this.getConfig().getString("deactivatedcommands." + cmd) + "!");
                    this.commandManager.deactivateBukkitCommand(cmd.toLowerCase(), this.getConfig().getString("deactivatedcommands." + cmd).toLowerCase());
                }
            }), 40L);
    }

    private void startSwappingCommands() {
        if (this.getConfig().getBoolean("swapCommands.enabled"))
            Bukkit.getScheduler().runTaskLater(this, () -> this.getConfig().getConfigurationSection("swapCommands").getKeys(false).forEach(cmdFrom -> {
                if (!cmdFrom.equalsIgnoreCase("enabled")) {
                    String pluginFrom = this.getConfig().getString("swapCommands." + cmdFrom + ".fromplugin");
                    String cmdTo = this.getConfig().getString("swapCommands." + cmdFrom + ".tocommand");
                    String pluginTo = this.getConfig().getString("swapCommands." + cmdFrom + ".toplugin");

                    this.log("Swapping command " + cmdFrom + " from plugin " + pluginFrom + " to command " + cmdTo + " from plugin " + pluginTo + "!");

                    String cmdFromToLower = pluginFrom.toLowerCase() + ":" + cmdFrom.toLowerCase();

                    PluginCommand commandFrom = Bukkit.getPluginCommand(cmdFromToLower);
                    PluginCommand commandToPluginCommand = Bukkit.getPluginCommand(pluginTo.toLowerCase() + ":" + cmdTo.toLowerCase());

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
                            JavaPlugin essentialsPlugin = JavaPlugin.getProvidingPlugin(this.getServer().getPluginManager().getPlugin("Essentials").getClass());
                            Essentials essentials = (Essentials) essentialsPlugin;
                            this.essentialsCommandListener = new EssentialsCommandListener(essentials, this);
                            this.getEventManager().re(this.essentialsCommandListener);
                        }
                        if (this.getServer().getPluginCommand(cmdFromToLower) == this.getServer().getPluginCommand(cmdFrom.toLowerCase()))
                            this.essentialsCommandListener.addCommand(cmdFrom, cmdTo);
                        else
                            this.essentialsCommandListener.addCommand(pluginFrom + ":" + cmdFrom, cmdTo);
                    }
                }
            }), 60L);
    }

    private void startUpdateCheck() {
        if (this.getConfig().getBoolean("updates.check")) Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            boolean autoUpdate = this.getConfig().getBoolean("updates.autoUpdate");
            String version = this.getDescription().getVersion();

            Document doc = null;
            try {
                doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem").referrer("ServerSystem").timeout(30000).get();
            } catch (IOException e) {
                this.error("An error occurred while trying to connect to the updater!");
                e.printStackTrace();
                this.log("Please ignore this error. The update server is currently down. Please be patient");
            }

            if (doc != null) {
                for (Element f : doc.getElementsContainingOwnText(".jar")) {
                    String s = f.attr("href");
                    s = s.substring(0, s.lastIndexOf("."));
                    version = s;
                }

                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    this.warn("There is a new version available! (" + version + ")");
                    if (autoUpdate) {
                        this.log("Auto-updating!");
                        this.log("(You need to restart the server so the update can take effect)");
                        try {
                            //Open a URL Stream
                            Connection.Response resultImageResponse = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar").referrer("ServerSystem").timeout(30000).ignoreContentType(true).execute();

                            // output here
                            FileOutputStream out = (new FileOutputStream(new File("plugins/update", this.JAR_NAME)));
                            out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
                            out.close();
                        } catch (IOException e) {
                            this.error("Error while trying downloading the update!");
                            e.printStackTrace();
                        }
                    } else if (this.getConfig().getBoolean("updates.notifyOnJoin")) {
                        if (!this.registered) {
                            this.registered = true;
                            this.getEventManager().re(new JoinUpdateListener(this));
                        }
                        if (!version.equalsIgnoreCase(this.newVersion)) this.newVersion = version;
                    }
                } else if (!this.onceTold) {
                    this.log("You are using the latest version of ServerSystem <3");
                    this.onceTold = true;
                }
            } else {
                this.log("Switching to backup updater!");
                String finalVersion = version;
                new UpdateChecker(this, "78974").getVersion(checkedVersion -> {
                    if (checkedVersion.equalsIgnoreCase(finalVersion) || checkedVersion.equalsIgnoreCase("1.6.7"))
                        if (!this.onceTold) {
                            this.log("You are using the latest version of ServerSystem <3");
                            this.onceTold = true;
                        } else {
                            this.warn("There is a new update available (" + checkedVersion + ")!");
                            if (autoUpdate) {
                                this.log("Auto-updating!");
                                this.log("(You need to restart the server so the update can take effect)");

                                try (BufferedInputStream in = new BufferedInputStream(new URL("https://api.spiget.org/v2/resources/78974/download").openStream());
                                     FileOutputStream fileOutputStream = new FileOutputStream(new File("plugins/update", this.JAR_NAME))) {
                                    byte[] dataBuffer = new byte[1024];
                                    int bytesRead;
                                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    this.error("Error while trying downloading the update!");
                                    this.error("Please download it by yourself (https://www.spigotmc.org/resources/serversystem.78974/)!");
                                    if (!this.registered) {
                                        this.registered = true;
                                        this.getEventManager().re(new JoinUpdateListener(this));
                                    }
                                    if (!finalVersion.equalsIgnoreCase(this.newVersion))
                                        this.newVersion = finalVersion;
                                }
                            } else if (this.getConfig().getBoolean("updates.notifyOnJoin")) {
                                if (!this.registered) {
                                    this.registered = true;
                                    this.getEventManager().re(new JoinUpdateListener(this));
                                }
                                if (!finalVersion.equalsIgnoreCase(this.newVersion)) this.newVersion = finalVersion;
                            }
                        }
                });
            }
        }, 80L, 7200L * 20L);
    }

    public void setupEconomyBanSystem() {
        String dateFormat;
        dateFormat = this.getConfig().getString("bansystem.dateformat");


        String currencySingular = this.getConfig().getString("economy.currency.singular");
        String currencyPlural = this.getConfig().getString("economy.currency.plural");
        String startingMoney = this.getConfig().getString("economy.startingmoney");
        String displayFormat = this.getConfig().getString("economy.displayformat");
        String moneyFormat = this.getConfig().getString("economy.moneyformat");
        String separator = this.getConfig().getString("economy.separator");
        String thousands = this.getConfig().getString("economy.thousand");

        if (this.getConfig().getBoolean("mysql.use")) {
            String hostname = this.getConfig().getString("mysql.hostname");
            String port = this.getConfig().getString("mysql.port");
            String username = this.getConfig().getString("mysql.username");
            String password = this.getConfig().getString("mysql.password");
            String database = this.getConfig().getString("mysql.database");

            this.mySQL = new MySQL(hostname, port, username, password, database, this);
            this.mySQL.connect();
            this.mySQL.createTable();
        }

        if (!this.getConfig().getBoolean("economy.enabled")) {
            this.log("Economy disabled! Not using it...");
            this.economyManager = new EconomyManager_Disabled("", "", "", "", "", "", "", this);
        }

        if (!this.getConfig().getBoolean("bansystem.enabled")) {
            this.log("BanSystem disabled! Not using it...");
            this.banManager = new BanManager_Disabled(new File(""), "", this);
            this.muteManager = new MuteManager_Disabled(new File(""), "", this);
        }

        if (this.getConfig().getBoolean("economy.enabled") || this.getConfig().getBoolean("bansystem.enabled")) {
            if (this.getConfig().getBoolean("mysql.use")) {
                this.log("MySQL enabled, using it...");
                if (this.getConfig().getBoolean("mysql.economy.enabled") && this.getConfig().getBoolean("economy.enabled")) {
                    this.log("Using economy with MySQL...");
                    if (this.economyManager != null)
                        this.error("You cannot have two databases at the same time for economy activated!");
                    else {
                        this.setServerName(this.getConfig().getString("mysql.economy.servername"));
                        this.economyManager = new EconomyManager_MySQL(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
                    }
                }
                if (this.getConfig().getBoolean("mysql.bansystem") && this.getConfig().getBoolean("bansystem.enabled")) {
                    this.log("Using bansystem with MySQL...");
                    if (this.banManager != null || this.muteManager != null)
                        this.error("You cannot have two databases at the same time for bansystem activated!");
                    else {
                        this.banManager = new BanManager_MySQL(dateFormat, this);
                        this.muteManager = new MuteManager_MySQL(this, dateFormat);
                    }
                }
            }

            if (this.getConfig().getBoolean("h2.use")) {
                this.log("H2 enabled, using it...");
                if (this.getConfig().getBoolean("h2.economy") && this.getConfig().getBoolean("economy.enabled")) {
                    this.log("Using economy with H2...");
                    if (this.economyManager != null)
                        this.error("You cannot have two databases at the same time for economy activated!");
                    else
                        this.economyManager = new EconomyManager_H2(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
                }

                if (this.getConfig().getBoolean("h2.bansystem") && this.getConfig().getBoolean("bansystem.enabled")) {
                    this.log("Using bansystem with H2...");
                    if (this.banManager != null || this.muteManager != null)
                        this.error("You cannot have two databases at the same time for bansystem activated!");
                    else {
                        this.banManager = new BanManager_H2(dateFormat, this);
                        this.muteManager = new MuteManager_H2(this, dateFormat);
                    }
                }
            }

            if (this.getConfig().getBoolean("sqlite.use")) {
                this.log("SQLite enabled, using it...");
                if (this.getConfig().getBoolean("sqlite.economy") && this.getConfig().getBoolean("economy.enabled")) {
                    this.log("Using economy with SQLite...");
                    if (this.economyManager != null)
                        this.error("You cannot have two databases at the same time for economy activated!");
                    else
                        this.economyManager = new EconomyManager_SQLite(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
                }

                if (this.getConfig().getBoolean("sqlite.bansystem") && this.getConfig().getBoolean("bansystem.enabled")) {
                    this.log("Using bansystem with SQLite...");
                    if (this.banManager != null || this.muteManager != null)
                        this.error("You cannot have two databases at the same time for bansystem activated!");
                    else {
                        this.banManager = new BanManager_SQLite(dateFormat, this);
                        this.muteManager = new MuteManager_SQLite(this, dateFormat);
                    }
                }
            }

            if (this.economyManager == null) {
                this.warn("Not using any database for economy...");
                this.economyManager = new EconomyManager(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
            }
            if (this.banManager == null) {
                this.warn("Not using any database for bansystem...");
                this.banManager = new BanManager(new File("plugins//ServerSystem", "bans.yml"), dateFormat, this);
            }
            if (this.muteManager == null)
                this.muteManager = new MuteManager(new File("plugins//ServerSystem", "muted.yml"), dateFormat, this);

        }
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

    public Vanish getVanish() {
        return this.vanish;
    }

    private void loadConfigs() {
        this.saveDefaultConfig();
        this.reloadConfig();

        File permFile = new File("plugins//ServerSystem", "permissions.yml");
        File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
        File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
        File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
        File msgTRFile = new File("plugins//ServerSystem", "messages_tr.yml");
        File msgZHCNFile = new File("plugins//ServerSystem", "messages_zhcn.yml");
        File msgFile = new File("plugins//ServerSystem", "messages.yml");
        File commandsFile = new File("plugins//ServerSystem", "commands.yml");
        File aliasesFile = new File("plugins//ServerSystem", "aliases.yml");
        File kitsFile = new File("plugins//ServerSystem", "kits.yml");
        File rulesFile = new File("plugins//ServerSystem", "rules.yml");

        if (!permFile.exists()) this.saveResource("permissions.yml", false);

        if (!msgDEFile.exists()) this.saveResource("messages_de.yml", false);

        if (!msgENFile.exists()) this.saveResource("messages_en.yml", false);

        if (!msgCZFile.exists()) this.saveResource("messages_cz.yml", false);

        if (!msgTRFile.exists()) this.saveResource("messages_tr.yml", false);

        if (!msgZHCNFile.exists()) this.saveResource("messages_zhcn.yml", false);

        if (!rulesFile.exists()) {
            String locale = System.getProperty("user.language");
            if (locale.equalsIgnoreCase("de")) {
                this.saveResource("rules_de.yml", true);
                new File("plugins//ServerSystem", "rules_de.yml").renameTo(new File("plugins//ServerSystem", "rules.yml"));
            } else if (locale.equalsIgnoreCase("cz")) {
                this.saveResource("rules_cz.yml", true);
                new File("plugins//ServerSystem", "rules_cz.yml").renameTo(new File("plugins//ServerSystem", "rules.yml"));
            } else {
                this.saveResource("rules_en.yml", true);
                new File("plugins//ServerSystem", "rules_en.yml").renameTo(new File("plugins//ServerSystem", "rules.yml"));
            }
        }

        if (!msgFile.exists()) {
            String locale = System.getProperty("user.language");
            if (locale.equalsIgnoreCase("de")) try {
                Files.copy(msgDEFile, new File("plugins//ServerSystem", "messages.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            else if (locale.equalsIgnoreCase("cz")) try {
                Files.copy(msgCZFile, new File("plugins//ServerSystem", "messages.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            else if (locale.equalsIgnoreCase("tr")) try {
                Files.copy(msgTRFile, new File("plugins//ServerSystem", "messages.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            else if (locale.toLowerCase(Locale.ROOT).contains("zh")) try {
                Files.copy(msgZHCNFile, new File("plugins//ServerSystem", "messages.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            else try {
                    Files.copy(msgENFile, new File("plugins//ServerSystem", "messages.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        if (!commandsFile.exists()) this.saveResource("commands.yml", false);

        if (!aliasesFile.exists()) this.saveResource("aliases.yml", false);

        if (!kitsFile.exists()) this.saveResource("kits.yml", false);

        this.rulesConfig = YamlConfiguration.loadConfiguration(this.RULES_FILE);
    }

    public Vault getVault() {
        return this.vault;
    }

    public VaultHookManager getVaultHookManager() {
        return this.vaultHookManager;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if (this.vaultHookManager != null) this.vaultHookManager.unhook();
        File file = new File("plugins//ServerSystem", "vanish.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (UUID uuid : this.getVanish().getVanishList()) cfg.set("Vanish." + uuid.toString(), true);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.wantsTP != null)
            this.wantsTP.close();
        if (this.banManager != null)
            this.banManager.close();
        if (this.muteManager != null)
            this.muteManager.close();
        if (this.economyManager != null)
            this.economyManager.close();
        if (this.mySQL != null)
            this.mySQL.close();
        if (this.warpManager != null)
            this.warpManager.close();

        this.banManager = null;
        this.muteManager = null;
        this.economyManager = null;

        this.commandManager.unregisterCommands();
        HandlerList.unregisterAll(this);
    }

    public void log(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aInfo&8] [&aServerSystem&8] &7>> &a" + text));
    }

    public void warn(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&cWarning&8] [&cServerSystem&8] &7>> &c" + text));
    }

    public void error(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&4Error&8] [&4ServerSystem&8] &7>> &4" + text));
    }

    private String getBanSystem(String action) {
        return this.getMessages().getCfg().getString("Messages.Misc.BanSystem." + action);
    }

    public Furnace getFurnace() {
        return this.furnace;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    /*
    Fix for something, don't know anymore
     */
    public void fixKeyLength() {
        String errorString = "Failed manually overriding key-length permissions.";
        int newMaxKeyLength;
        try {
            if ((newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES")) < 256) {
                Class c = Class.forName("javax.crypto.CryptoAllPermissionCollection");
                Constructor con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissionCollection = con.newInstance();
                Field f = c.getDeclaredField("all_allowed");
                f.setAccessible(true);
                f.setBoolean(allPermissionCollection, true);

                c = Class.forName("javax.crypto.CryptoPermissions");
                con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissions = con.newInstance();
                f = c.getDeclaredField("perms");
                f.setAccessible(true);
                ((Map) f.get(allPermissions)).put("*", allPermissionCollection);

                c = Class.forName("javax.crypto.JceSecurityManager");
                f = c.getDeclaredField("defaultPolicy");
                f.setAccessible(true);
                Field mf = Field.class.getDeclaredField("modifiers");
                mf.setAccessible(true);
                mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(null, allPermissions);

                newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
            }
        } catch (Exception e) {
            this.error(errorString);
            if (ss.debug) {
                this.error("Reason:");
                this.error(e.getMessage());
            }
            return;
        }
        if (newMaxKeyLength < 256) {
            this.error(errorString);
            if (ss.debug) {
                this.error("Reason:");
                this.error("Unknown");
            }
        }
    }
}
