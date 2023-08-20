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
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
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
import java.lang.reflect.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static me.entity303.serversystem.bansystem.TimeUnit.*;

public final class ServerSystem extends JavaPlugin {
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
    private permissions permissions;
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

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        this.configReader = new NonValidatingConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
    }

    @Override
    public void reloadConfig() {
        if (this.configReader != null)
            this.configReader.reload();
        else
            this.configReader = new NonValidatingConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
    }

    public void reloadConfigValidating() {
        if (this.configReader != null && !(this.configReader instanceof NonValidatingConfigReader))
            this.configReader.reload();
        else
            this.configReader = new DefaultConfigReader(new File("plugins" + File.separator + "ServerSystem", "config.yml"), this);
    }

    @Override
    public void saveConfig() {
        if (this.configReader != null)
            this.configReader.save();
    }

    public WantsTeleport getWantsTeleport() {
        return this.wantsTeleport;
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
            }, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        this.loadConfigs();

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(new File("plugins" + File.separator + "ServerSystem", "config.yml"));

        if (!cfg.isSet("specialSudo"))
            if (cfg.isSet("specialsudo")) {
                File serverSystemFolder = new File("plugins//ServerSystem");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
                LocalDateTime now = LocalDateTime.now();
                String date = dtf.format(now);

                try {
                    FileUtils.copyDirectory(serverSystemFolder, new File("plugins//ServerSystem-Backups//ServerSystem-Backup-" + date));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cfg.set("deactivateEntityCollision", cfg.get("deactivateentitycollision"));

                cfg.set("specialSudo", cfg.get("specialsudo"));

                cfg.set("advancedInvSee", cfg.get("advancedinvsee"));

                cfg.set("messageByItemBreak", cfg.get("messagebyitembreak"));

                cfg.set("fly.stopWhenHit", cfg.get("fly.stopwhenhit"));
                cfg.set("fly.disableWhenHit", cfg.get("fly.disablewhenhit"));

                cfg.set("worldChange.resetGameMode", cfg.get("worldChange.resetgamemode"));
                cfg.set("worldChange.resetFly", cfg.get("worldChange.resetfly"));
                cfg.set("worldChange.resetGod", cfg.get("worldChange.resetgod"));

                cfg.set("kit.giveOnFirstSpawn", cfg.get("kit.giveonfirstspawn"));
                cfg.set("kit.givenKit", cfg.get("kit.givenkit"));

                cfg.set("teleportation.spawn.enableDelay", cfg.get("teleportation.spawn.enabledelay"));
                cfg.set("teleportation.tpa.enableDelay", cfg.get("teleportation.tpa.enabledelay"));
                cfg.set("teleportation.home.enableDelay", cfg.get("teleportation.home.enabledelay"));
                cfg.set("teleportation.warp.enableDelay", cfg.get("teleportation.warp.enabledelay"));

                cfg.set("economy.createAccountOnJoin", cfg.get("economy.createaccountonjoin"));
                cfg.set("economy.hookIntoVault", cfg.get("economy.hookintovault"));
                cfg.set("economy.startingMoney", cfg.get("economy.startingmoney"));
                cfg.set("economy.displayFormat", cfg.get("economy.displayformat"));
                cfg.set("economy.moneyFormat", cfg.get("economy.moneyformat"));

                cfg.set("banSystem.enabled", cfg.get("bansystem.enabled"));
                cfg.set("banSystem.dateFormat", cfg.get("bansystem.dateformat"));

                cfg.set("spawn.firstLoginTp", cfg.get("spawn.firstlogintp"));

                for (String commandName : cfg.getConfigurationSection("swapCommands").getKeys(false)) {
                    if (commandName.toLowerCase().contains("enabled"))
                        continue;

                    for (String key : cfg.getConfigurationSection("swapCommands." + commandName).getKeys(false)) {
                        cfg.set(("swapCommands." + commandName + "." + key)
                                .replace("fromplugin", "fromPlugin")
                                .replace("toplugin", "toPlugin")
                                .replace("tocommand", "toCommand"), cfg.get("swapCommands." + commandName + "." + key));

                        cfg.set("swapCommands." + commandName + "." + key, null);
                    }
                }

                for (String key : cfg.getConfigurationSection("deactivatedcommands").getKeys(false)) {
                    key = "deactivatedcommands." + key;

                    cfg.set(key
                            .replace("deactivatedcommands", "deactivatedCommands"), cfg.get(key));
                }

                cfg.set("mysql.economy.serverName", cfg.get("mysql.economy.servername"));
                cfg.set("mysql.banSystem", cfg.get("mysql.bansystem"));

                cfg.set("sqlite.banSystem", cfg.get("sqlite.bansystem"));

                cfg.set("h2.banSystem", cfg.get("h2.bansystem"));

                cfg.set("specialsudo", null);
                cfg.set("advancedinvsee", null);
                cfg.set("messagebyitembreak", null);
                cfg.set("fly.stopwhenhit", null);
                cfg.set("fly.disablewhenhit", null);
                cfg.set("fly.fly", null);
                cfg.set("worldChange.resetgamemode", null);
                cfg.set("worldChange.resetfly", null);
                cfg.set("worldChange.resetgod", null);
                cfg.set("kit.giveonfirstspawn", null);
                cfg.set("kit.givenkit", null);
                cfg.set("teleportation.spawn.enabledelay", null);
                cfg.set("teleportation.tpa.enabledelay", null);
                cfg.set("teleportation.home.enabledelay", null);
                cfg.set("teleportation.warp.enabledelay", null);
                cfg.set("economy.createaccountonjoin", null);
                cfg.set("economy.hookintovault", null);
                cfg.set("economy.startingmoney", null);
                cfg.set("economy.displayformat", null);
                cfg.set("economy.moneyformat", null);
                cfg.set("bansystem", null);
                cfg.set("spawn.firstlogintp", null);
                cfg.set("mysql.economy.servername", null);
                cfg.set("mysql.bansystem", null);
                cfg.set("sqlite.bansystem", null);
                cfg.set("h2.bansystem", null);
                cfg.set("deactivatedcommands", null);
                cfg.set("deactivateentitycollision", null);

                try {
                    cfg.save(new File("plugins" + File.separator + "ServerSystem", "config.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.loadConfigs();

                this.warn("ServerSystem just fixed your config.yml!");
                this.warn("Please check your config.yml file and make sure everything is fine!");
                this.warn("If not, please contact me: https://discord.gg/dBhfCzdZxq");
                this.warn("A backup was saved at: " + new File("plugins//ServerSystem-Backups//ServerSystem-Backup-" + date).getAbsolutePath());
            }

        ConfigUpdater updater = new ConfigUpdater(this);

        if (updater.configUpdateNeeded(this.getConfigReader().getString("version"))) {
            try {
                updater.updateConfig(this.getConfigReader().getString("version"));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            return;
        } else
            this.reloadConfigValidating();


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

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) this.vault = new Vault();

        ServerSystem.debug = this.getConfigReader().getBoolean("debug");

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

            if (this.versionManager.isV113()) {
                if (this.syncCommandsMethod == null) try {
                    this.syncCommandsMethod = Class.forName("org.bukkit.craftbukkit." + this.versionManager.getNMSVersion() + ".CraftServer").getDeclaredMethod("syncCommands");
                    this.syncCommandsMethod.setAccessible(true);
                } catch (NoSuchMethodException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                try {
                    this.syncCommandsMethod.invoke(Bukkit.getServer());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            if (this.getVersionManager().isV113()) if (Bukkit.getOnlinePlayers().size() >= 1)
                for (Player player : Bukkit.getOnlinePlayers()) player.updateCommands();
        }, 5L);

        this.eventManager = new EventManager(this);

        this.eventManager.registerEvents();

        Bukkit.getScheduler().runTaskLater(this, () -> this.starting = false, 100L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<Player> players = new ArrayList<>();
            for (Map.Entry<Player, TpaData> entry : this.tpaDataMap.entrySet()) {
                Player player = entry.getKey();

                if (entry.getValue().getEnd() <= System.currentTimeMillis())
                    players.add(player);
            }

            if (players.size() >= 1)
                for (Player player : players)
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

        if (this.stopFlightOnHit) this.getEventManager().re(new FlightHitListener(this));

        if (this.getConfigReader().getBoolean("metrics"))
            this.metrics = new MetricsLite(this, 9043);

        Bukkit.getScheduler().runTaskLater(this, () -> this.furnace = new Furnace(this), 10L);

        this.startDeactivatingCommands();

        this.specialSudo = this.getConfigReader().getBoolean("specialSudo", true);

        this.advancedInvsee = this.getConfigReader().getBoolean("advancedInvSee", true);

        this.clientsideOp = this.getConfigReader().getBoolean("clientsideOpSpoof", true);

        File commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFiles);

        if (commandsConfig.getBoolean("baltop"))
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> this.getEconomyManager().fetchTopTen(), 20L, 72000L);

        this.startSwappingCommands();

        this.startUpdateCheck();

        this.check();
    }

    private void startDeactivatingCommands() {
        if (this.getConfigReader().getBoolean("deactivatedCommands.enabled"))
            Bukkit.getScheduler().runTaskLater(this, () -> this.getConfigReader().getConfigurationSection("deactivatedCommands").getKeys(false).forEach(cmd -> {
                if (!cmd.equalsIgnoreCase("enabled")) {
                    this.log("Deactivating command " + cmd + " from plugin " + this.getConfigReader().getString("deactivatedCommands." + cmd) + "!");
                    this.commandManager.deactivateBukkitCommand(cmd.toLowerCase(), this.getConfigReader().getString("deactivatedCommands." + cmd).toLowerCase());
                }
            }), 40L);
    }

    private void startSwappingCommands() {
        if (this.getConfigReader().getBoolean("swapCommands.enabled"))
            Bukkit.getScheduler().runTaskLater(this, () -> this.getConfigReader().getConfigurationSection("swapCommands").getKeys(false).forEach(cmdFrom -> {
                if (!cmdFrom.equalsIgnoreCase("enabled")) {
                    String pluginFrom = this.getConfigReader().getString("swapCommands." + cmdFrom + ".fromPlugin");
                    String cmdTo = this.getConfigReader().getString("swapCommands." + cmdFrom + ".toCommand");
                    String pluginTo = this.getConfigReader().getString("swapCommands." + cmdFrom + ".toPlugin");

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
        if (this.getConfigReader().getBoolean("updates.check"))
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
                boolean autoUpdate = this.getConfigReader().getBoolean("updates.autoUpdate");
                String version = this.getDescription().getVersion();

                Document doc = null;
                try {
                    doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem").referrer("ServerSystem").timeout(30000).get();
                } catch (IOException e) {
                    this.error("An error occurred while trying to connect to the updater!");
                    //e.printStackTrace();
                    this.log("Please ignore this error. The update server is currently down. Please be patient");
                }

                if (doc != null) {
                    for (Element f : doc.getElementsContainingOwnText(".jar")) {
                        String s = f.attr("href");
                        s = s.substring(0, s.lastIndexOf('.'));
                        version = s;
                    }

                    if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                        this.warn("There is a new version available! (" + version + ")");
                        if (autoUpdate) {
                            this.log("Auto-updating!");
                            this.log("(You need to restart the server so the update can take effect)");
                            try {
                                Connection.Response resultImageResponse = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar").referrer("ServerSystem").timeout(30000).ignoreContentType(true).execute();

                                BufferedInputStream in = new BufferedInputStream(new URL("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar").openStream());
                                FileOutputStream fileOutputStream = new FileOutputStream(new File("plugins/update", this.JAR_NAME));
                                byte[] dataBuffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                                    fileOutputStream.write(dataBuffer, 0, bytesRead);

                                in.close();
                                fileOutputStream.close();
                            } catch (Exception e) {
                                this.error("Error while trying downloading the update!");
                                e.printStackTrace();
                            }
                        } else if (this.getConfigReader().getBoolean("updates.notifyOnJoin")) {
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
                                } else if (this.getConfigReader().getBoolean("updates.notifyOnJoin")) {
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
        dateFormat = this.getConfigReader().getString("banSystem.dateFormat");


        String currencySingular = this.getConfigReader().getString("economy.currency.singular");
        String currencyPlural = this.getConfigReader().getString("economy.currency.plural");
        String startingMoney = this.getConfigReader().getString("economy.startingMoney");
        String displayFormat = this.getConfigReader().getString("economy.displayFormat");
        String moneyFormat = this.getConfigReader().getString("economy.moneyFormat");
        String separator = this.getConfigReader().getString("economy.separator");
        String thousands = this.getConfigReader().getString("economy.thousand");

        if (this.getConfigReader().getBoolean("mysql.use")) {
            String hostname = this.getConfigReader().getString("mysql.hostname");
            String port = this.getConfigReader().getString("mysql.port");
            String username = this.getConfigReader().getString("mysql.username");
            String password = this.getConfigReader().getString("mysql.password");
            String database = this.getConfigReader().getString("mysql.database");

            boolean mariadb = this.getConfigReader().getBoolean("mysql.mariadb");

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
                        this.economyManager = new EconomyManager_MySQL(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
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
                        this.economyManager = new EconomyManager_H2(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
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
                        this.economyManager = new EconomyManager_SQLite(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
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
                this.economyManager = new EconomyManager(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, this);
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

        File permFile = new File("plugins//ServerSystem", "permissions.yml");
        File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
        File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
        File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
        File msgTRFile = new File("plugins//ServerSystem", "messages_tr.yml");
        File msgZHCNFile = new File("plugins//ServerSystem", "messages_zhcn.yml");
        File msgITFile = new File("plugins//ServerSystem", "messages_it.yml");
        File msgRUFile = new File("plugins//ServerSystem", "messages_ru.yml");
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

        if (msgITFile.exists()) this.saveResource("messages_it.yml", false);

        if (msgRUFile.exists()) this.saveResource("messages_ru.yml", false);

        if (!rulesFile.exists()) {
            String locale = System.getProperty("user.language");
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
            else if (locale.toLowerCase(Locale.ROOT).contains("it")) try {
                Files.copy(msgITFile, new File("plugins//ServerSystem", "messages.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            else if (locale.toLowerCase(Locale.ROOT).contains("ru")) try {
                Files.copy(msgRUFile, new File("plugins//ServerSystem", "messages.yml"));
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
        File file = new File("plugins//ServerSystem", "vanish.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (UUID uuid : this.getVanish().getVanishList())
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

        if (this.versionManager.isV113()) {
            this.log("Syncing commands...");

            if (this.syncCommandsMethod == null) try {
                this.syncCommandsMethod = Class.forName("org.bukkit.craftbukkit." + this.versionManager.getNMSVersion() + ".CraftServer").getDeclaredMethod("syncCommands");
                this.syncCommandsMethod.setAccessible(true);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            try {
                this.syncCommandsMethod.invoke(Bukkit.getServer());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        this.log("Unregistering Handlers...");

        HandlerList.unregisterAll(this);

        this.log("Shutdown done!");
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
            if (ServerSystem.debug) {
                this.error("Reason:");
                this.error(e.getMessage());
            }
            return;
        }
        if (newMaxKeyLength < 256) {
            this.error(errorString);
            if (ServerSystem.debug) {
                this.error("Reason:");
                this.error("Unknown");
            }
        }
    }

    public boolean isSpecialSudo() {
        return this.specialSudo;
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
    }

    public ConfigReader getConfigReader() {
        return this.configReader;
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

    public Message getMessages() {
        return this.messages;
    }

    public permissions getPermissions() {
        return this.permissions;
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
}
