package me.entity303.serversystem.commands.util;

import me.entity303.serversystem.commands.executable.*;
import me.entity303.serversystem.listener.plotsquared.*;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.*;
import me.entity303.serversystem.config.ConfigReader;
import me.entity303.serversystem.config.DefaultConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class CommandManager {
    private final ServerSystem serverSystem;
    private final List<String> serverSystemCommands = new ArrayList<>();
    private final Map<String, PluginCommand> deactivatedBukkitCommands = new HashMap<>();
    private final Map<String, Command> deactivatedMiscCommands = new HashMap<>();
    private boolean dropActive = true;
    private boolean pickupActive = true;
    private boolean interactActive = true;
    private boolean chatActive = true;

    public CommandManager(ServerSystem serverSystem) {
        this.serverSystem = serverSystem;
    }

    public boolean isDropActive() {
        return this.dropActive;
    }

    public boolean isPickupActive() {
        return this.pickupActive;
    }

    public boolean isInteractActive() {
        return this.interactActive;
    }

    public boolean isChatActive() {
        return this.chatActive;
    }

    public void rtc(String command, TabCompleter completer) {
        if (this.serverSystem.getCommand(command) != null)
            this.serverSystem.getCommand(command).setTabCompleter(completer);
    }

    public Command getCommand(String command) {
        Object result = null;
        try {
            result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        SimpleCommandMap commandMap = (SimpleCommandMap) result;
        return commandMap.getCommand(command.toLowerCase(Locale.ROOT));
    }

    public void rc(String command, CommandExecutor executor, TabCompleter tabCompleter) {
        if (executor == null) System.out.println("Executor!");
        if (command == null) System.out.println("Command?!");

        if (tabCompleter == null)
            tabCompleter = new DefaultTabCompleter(this.serverSystem);


        File commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        ConfigReader commandsConfig = DefaultConfigReader.loadConfiguration(commandsFiles);

        if (commandsConfig.getBoolean(command.toLowerCase())) {
            this.registerCommand(executor, tabCompleter, this.serverSystem, command);

            this.serverSystemCommands.add(command.toLowerCase());

            File aliasFiles = new File("plugins//ServerSystem", "aliases.yml");
            ConfigReader aliasConfig = DefaultConfigReader.loadConfiguration(aliasFiles);

            String aliasString = aliasConfig.getString("Aliases." + command.toLowerCase() + ".aliases");

            if (aliasString != null) {
                if (!aliasString.equalsIgnoreCase("No Aliases")) {
                    String[] aliases = aliasConfig.getString("Aliases." + command.toLowerCase() + ".aliases").replace(" ", "").toLowerCase().split(",");
                    this.addAlias(command, executor, aliases);
                    this.serverSystemCommands.addAll(Arrays.asList(aliases));
                }
            } else this.serverSystem.warn("Null alias for: " + command);
        } else if (command.equalsIgnoreCase("drop")) this.dropActive = false;
        else if (command.equalsIgnoreCase("chat")) this.chatActive = false;
        else if (command.equalsIgnoreCase("pickup")) this.pickupActive = false;
        else if (command.equalsIgnoreCase("interact")) this.interactActive = false;
    }

    private Object getPrivateField(Object object, String field) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();

        String version = this.serverSystem.getVersionManager().getVersion();

        Field objectField = field.equals("commandMap") ? clazz.getDeclaredField(field) : field.equals("knownCommands") ? this.serverSystem.getVersionManager().isV113() ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field) : null;
        objectField.setAccessible(true);
        return objectField.get(object);
    }

    public void registerCommand(CommandExecutor executor, TabCompleter tabCompleter, Plugin plugin, String... aliases) {
        if (this.serverSystem.getServer().getPluginCommand(aliases[0]) != null)
            if (!this.serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName().equalsIgnoreCase("ServerSystem"))
                this.deactivateBukkitCommand(aliases[0], this.serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName());

        Object map = null;
        try {
            map = this.getPrivateField(this.getCommandMap(), "knownCommands");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;

        aliases = Arrays.stream(aliases).map(String::toLowerCase).toArray(String[]::new);
        PluginCommand command = this.getNewCommand(aliases[0], plugin);

        command.setExecutor(executor);
        if (tabCompleter != null)
            command.setTabCompleter(tabCompleter);

        for (String alias : aliases) {
            knownCommands.remove(alias.toLowerCase());
        }

        for (String alias : aliases) {
            alias = alias.toLowerCase();
            knownCommands.put("serversystem:" + alias.toLowerCase(), command);
            knownCommands.put(alias.toLowerCase(), command);
        }

        command.register(this.getCommandMap());
    }

    private PluginCommand getNewCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return command;
    }

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(this.serverSystem.getServer().getPluginManager());
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }

    private void addAlias(String cmd, CommandExecutor executor, String[] aliases) {
        cmd = cmd.toLowerCase();
        try {
            Object result = null;
            try {
                result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;

            for (String alias : aliases) {
                knownCommands.remove(alias.toLowerCase());
                knownCommands.remove("serversystem:" + alias.toLowerCase());
            }

            PluginCommand command = this.getNewCommand(cmd.toLowerCase(), this.serverSystem);

            command.setExecutor(executor);

            command.setAliases(Arrays.asList(aliases.clone()));

            for (String alias : aliases) {
                alias = alias.toLowerCase();
                knownCommands.put(alias.toLowerCase(), command);
                knownCommands.put("serversystem:" + alias.toLowerCase(), command);
            }

            command.register(commandMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deactivateOwnCommand(String cmd) {
        try {
            String plugin = "serversystem";
            Object result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            if (cmd == null) return;
            if (this.serverSystem.getServer().getPluginCommand(plugin + ":" + cmd) == this.serverSystem.getServer().getPluginCommand(cmd)) {
                knownCommands.remove(cmd);
            }
            knownCommands.remove(plugin + ":" + cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deactivateBukkitCommand(String cmd, String plugin) {
        try {
            Object result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            if (cmd == null) return;
            if (plugin == null) return;
            if (this.serverSystem.getServer().getPluginCommand(plugin + ":" + cmd) == this.serverSystem.getServer().getPluginCommand(cmd)) {
                knownCommands.remove(cmd);
            }

            if (!plugin.equalsIgnoreCase("minecraft") && !plugin.equalsIgnoreCase("bukkit") && !plugin.equalsIgnoreCase("spigot")) {
                knownCommands.remove(plugin + ":" + cmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void activateBukkitCommand(PluginCommand cmd) {
        try {
            Object result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            if (cmd == null) return;
            knownCommands.put(cmd.getName().toLowerCase(), cmd);
            knownCommands.put(cmd.getPlugin().getName().toLowerCase() + ":" + cmd.getName().toLowerCase(), cmd);
            if (cmd.getAliases().size() > 0) for (String alias : cmd.getAliases()) {
                knownCommands.put(alias.toLowerCase(), cmd);
                if (Bukkit.getServer().getPluginCommand(alias.toLowerCase().toLowerCase()) == null)
                knownCommands.put(cmd.getPlugin().getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterCommands() {
        if (this.deactivatedBukkitCommands.size() > 0) {
            for (String cmd : this.deactivatedBukkitCommands.keySet()) {
                String plugin = cmd.split(":")[0];
                String command = cmd.split(":")[1];
                this.serverSystem.log("Reactivating command " + command + " from " + plugin + "!");
                this.activateBukkitCommand(this.deactivatedBukkitCommands.get(cmd));
            }

            if (this.serverSystem.getEssentialsCommandListener() != null)
                for (String cmd : this.serverSystem.getEssentialsCommandListener().getNewEssentialsCommands()) {
                    this.serverSystem.log("Reactivating command " + cmd + "!");
                    this.serverSystem.getEssentialsCommandListener().removeCommand(cmd);
                }
        }

        try {
            for (String command : this.serverSystemCommands) {
                command = command.toLowerCase();
                this.deactivateOwnCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerCommands() {
        this.rc("unlimited", new UnlimitedCommand(this.serverSystem), null);
        this.rc("clearchat", new ClearChatCommand(this.serverSystem), null);
        this.rc("back", new BackCommand(this.serverSystem), null);
        this.rc("broadcast", new BroadcastCommand(this.serverSystem), null);
        this.rc("sethome", new SetHomeCommand(this.serverSystem), null);
        this.rc("delhome", new DelHomeCommand(this.serverSystem), new DelHomeTabCompleter(this.serverSystem));
        this.rc("home", new HomeCommand(this.serverSystem), new HomeTabCompleter(this.serverSystem));
        this.rc("workbench", new WorkbenchCommand(this.serverSystem), null);
        this.rc("tpa", new TeleportRequestCommand(this.serverSystem), null);
        this.rc("tpahere", new TeleportRequestHereCommand(this.serverSystem), null);
        this.rc("tpaccept", new TeleportRequestAcceptCommand(this.serverSystem), null);
        this.rc("tpdeny", new TeleportRequestDenyCommand(this.serverSystem), null);
        this.rc("vanish", new VanishCommand(this.serverSystem), null);
        this.rc("chat", new ChatCommand(this.serverSystem), null);
        this.rc("interact", new InteractCommand(this.serverSystem), null);
        this.rc("pickup", new PickUpCommand(this.serverSystem), null);
        this.rc("drop", new DropCommand(this.serverSystem), null);
        this.rc("tptoggle", new TeleportToggleCommand(this.serverSystem), null);
        this.rc("clearenderchest", new ClearEnderChestCommand(this.serverSystem), null);
        this.rc("clearinventory", new ClearInventoryCommand(this.serverSystem), null);
        this.rc("checkgamemode", new CheckgamemodeCommand(this.serverSystem), null);
        this.rc("speed", new SpeedCommand(this.serverSystem), new SpeedTabCompleter(this.serverSystem));
        this.rc("serversystem", new ServerSystemCommand(this.serverSystem), new ServerSystemTabCompleter(this.serverSystem));
        this.rc("gamemode", new GameModeCommand(this.serverSystem), new GameModeTabCompleter());
        this.rc("gmc", new GmcCommand(this.serverSystem), null);
        this.rc("gms", new GmsCommand(this.serverSystem), null);
        this.rc("gma", new GmaCommand(this.serverSystem), null);
        this.rc("gmsp", new GmspCommand(this.serverSystem), null);
        this.rc("feed", new FeedCommand(this.serverSystem), null);
        this.rc("time", new TimeCommand(this.serverSystem), new WorldTabCompleter());
        this.rc("heal", this.serverSystem.getVersionManager().is188() ? new HealCommand(this.serverSystem) : new HealNewerCommand(this.serverSystem), null);
        this.rc("hat", new HatCommand(this.serverSystem), null);
        this.rc("weather", new WeatherCommand(this.serverSystem), new WorldTabCompleter());
        this.rc("day", new DayCommand(this.serverSystem), new WorldTabCompleter());
        this.rc("night", new NightCommand(this.serverSystem), new WorldTabCompleter());
        this.rc("restart", new RestartCommand(this.serverSystem), null);
        this.rc("noon", new NoonCommand(this.serverSystem), new WorldTabCompleter());
        this.rc("ping", new PingCommand(this.serverSystem), null);
        this.rc("burn", new BurnCommand(this.serverSystem), null);
        this.rc("ip", new IpCommand(this.serverSystem), null);
        this.rc("repair", new RepairCommand(this.serverSystem), null);
        this.rc("disenchant", new DisenchantCommand(this.serverSystem), null);
        this.rc("suicide", new SuicideCommand(this.serverSystem), null);
        this.rc("extinguish", new ExtinguishCommand(this.serverSystem), null);
        this.rc("lag", new LagCommand(this.serverSystem), null);
        this.rc("sudo", new SudoCommand(this.serverSystem), null);
        this.rc("smelt", new SmeltCommand(this.serverSystem), null);
        this.rc("stack", new StackCommand(this.serverSystem), null);
        this.rc("sun", new SunCommand(this.serverSystem), new WorldTabCompleter());
        this.rc("rain", new RainCommand(this.serverSystem), new WorldTabCompleter());
        this.rc("disposal", new DisposalCommand(this.serverSystem), null);
        this.rc("editsign", Bukkit.getPluginManager().getPlugin("PlotSquared") != null ? new EditSignPlotSquaredCommand(this.serverSystem) : new EditSignCommand(this.serverSystem), null);
        this.rc("teamchat", new TeamChatCommand(this.serverSystem), null);
        this.rc("sign", new SignCommand(this.serverSystem), null);
        this.rc("unsign", new UnSignCommand(this.serverSystem), null);
        this.rc("tppos", new TeleportPositionCommand(this.serverSystem), null);
        if (this.serverSystem.getConfigReader().getBoolean("bansystem.enabled")) {
            this.rc("ban", new BanCommand(this.serverSystem), new BanTabCompleter(this.serverSystem));
            this.rc("unban", new UnBanCommand(this.serverSystem), new UnBanTabCompleter(this.serverSystem));
            this.rc("mute", new MuteCommand(this.serverSystem), new MuteTabCompleter(this.serverSystem));
            this.rc("unmute", new UnMuteCommand(this.serverSystem), new UnMuteTabCompleter(this.serverSystem));
            this.rc("kick", new KickCommand(this.serverSystem), null);
            this.rc("kickall", new KickAllCommand(this.serverSystem), null);
        }
        if (this.serverSystem.getConfigReader().getBoolean("economy.enabled")) {
            this.rc("money", new MoneyCommand(this.serverSystem), null);
            this.rc("pay", new PayCommand(this.serverSystem), null);
            this.rc("economy", new EconomyCommand(this.serverSystem), new EconomyTabCompleter(this.serverSystem));
            this.rc("baltop", new BaltopCommand(this.serverSystem), null);
        }
        this.rc("fly", new FlyCommand(this.serverSystem), null);
        this.rc("commandspy", new CommandSpyCommand(this.serverSystem), null);
        this.rc("warp", new WarpCommand(this.serverSystem), new WarpTabCompleter(this.serverSystem));
        this.rc("delwarp", new DeleteWarpCommand(this.serverSystem), new WarpTabCompleter(this.serverSystem));
        this.rc("setwarp", new SetWarpCommand(this.serverSystem), null);
        this.rc("setspawn", new SetSpawnCommand(this.serverSystem), null);
        this.rc("spawn", new SpawnCommand(this.serverSystem), null);
        this.rc("createkit", new CreateKitCommand(this.serverSystem), null);
        this.rc("delkit", new DeleteKitCommand(this.serverSystem), new DeleteKitTabCompleter(this.serverSystem));
        this.rc("kit", new KitCommand(this.serverSystem), new KitTabCompleter(this.serverSystem));
        this.rc("invsee", new InvseeCommand(this.serverSystem), null);
        this.rc("enderchest", new EnderChestCommand(this.serverSystem), null);
        this.rc("tp", new TeleportCommand(this.serverSystem), null);
        this.rc("tpo", new TeleportForceCommand(this.serverSystem), null);
        this.rc("tphere", new TeleportHereCommand(this.serverSystem), null);
        this.rc("tpohere", new TeleportForceHereCommand(this.serverSystem), null);
        this.rc("tpall", new TeleportAllCommand(this.serverSystem), null);
        this.rc("msg", new MsgCommand(this.serverSystem), null);
        this.rc("msgtoggle", new MsgToggleCommand(this.serverSystem), null);
        this.rc("socialspy", new SocialSpyCommand(this.serverSystem), null);
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null)
            this.rc("convertfromessentials", new EssentialsConversionCommand(this.serverSystem), null);
        this.rc("god", new GodCommand(this.serverSystem), null);
        this.rc("reply", new ReplyCommand(this.serverSystem), null);
        this.rc("maintenance", new MaintenanceCommand(this.serverSystem), null);
        this.rc("rules", new RulesCommand(this.serverSystem), null);
        this.rc("rename", new RenameCommand(this.serverSystem), null);
        this.rc("recipe", new RecipeCommand(this.serverSystem), new RecipeTabCompleter(this.serverSystem));
        this.rc("warps", new WarpsCommand(this.serverSystem), null);
        this.rc("checkhealth", new CheckhealthCommand(this.serverSystem), null);
        this.rc("lightning", new LightningCommand(this.serverSystem), null);
        this.rc("getpos", new GetPosCommand(this.serverSystem), null);
        this.rc("anvil", new AnvilCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualCartography() != null)
            this.rc("cartographytable", new CartographyCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualGrindstone() != null)
            this.rc("grindstone", new GrindStoneCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualLoom() != null)
            this.rc("loom", new LoomCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualStoneCutter() != null)
            this.rc("stonecutter", new StoneCutterCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualSmithing() != null)
            this.rc("smithingtable", new SmithingTableCommand(this.serverSystem), null);
        this.rc("break", new BreakCommand(this.serverSystem), null);
        this.rc("offlineteleport", new OfflineTeleportCommand(this.serverSystem), null);
        this.rc("offlineteleporthere", new OfflineTeleportHereCommand(this.serverSystem), null);
        this.rc("offlineinvsee", new OfflineInvseeCommand(this.serverSystem), null);
        this.rc("offlineenderchest", new OfflineEnderChestCommand(this.serverSystem), null);

        boolean plotSquaredAlreadyRegistered = false;

        try {
            Class.forName("com.plotsquared.core.PlotAPI");
            new PlotListener4();
            new PlotListener(this.serverSystem);
            plotSquaredAlreadyRegistered = true;
        } catch (Exception ignored) {
        }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.plotsquared.core.events.PlayerEnterPlotEvent");
                new PlotListener3();
                new PlotListener(this.serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener2(this.serverSystem), this.serverSystem);
                new PlotListener(this.serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener1(this.serverSystem), this.serverSystem);
                new PlotListener(this.serverSystem);
            } catch (Exception ignored) {

            }
    }
}
