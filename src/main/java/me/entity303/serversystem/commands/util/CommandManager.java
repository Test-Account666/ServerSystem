package me.entity303.serversystem.commands.util;

import me.entity303.serversystem.commands.executable.*;
import me.entity303.serversystem.config.DefaultConfigReader;
import me.entity303.serversystem.listener.plotsquared.*;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.io.File;
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

    public void registerTabComplete(String command, TabCompleter completer) {
        if (this.serverSystem.getCommand(command) != null)
            this.serverSystem.getCommand(command).setTabCompleter(completer);
    }

    public Command getCommand(String command) {
        Object result = this.getCommandMap();
        var commandMap = (SimpleCommandMap) result;
        return commandMap.getCommand(command.toLowerCase(Locale.ROOT));
    }

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                var commandMapField = ((SimplePluginManager) Bukkit.getPluginManager()).getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);

                commandMap = (CommandMap) commandMapField.get(this.serverSystem.getServer().getPluginManager());
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            if (e instanceof NoSuchFieldException)
                try {
                    var commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
                    commandMapField.setAccessible(true);

                    commandMap = (CommandMap) commandMapField.get(this.serverSystem.getServer().getPluginManager());
                    return commandMap;
                } catch (Exception ex) {
                    e.addSuppressed(ex);
                }
            e.printStackTrace();
        }

        return commandMap;
    }

    public void unregisterCommands() {
        if (!this.deactivatedBukkitCommands.isEmpty()) {
            for (var entry : this.deactivatedBukkitCommands.entrySet()) {
                var cmd = entry.getKey();

                var plugin = cmd.split(":")[0];
                var command = cmd.split(":")[1];
                this.serverSystem.log("Reactivating command " + command + " from " + plugin + "!");
                this.activateBukkitCommand(this.deactivatedBukkitCommands.get(cmd));
            }

            if (this.serverSystem.getEssentialsCommandListener() != null)
                for (var cmd : this.serverSystem.getEssentialsCommandListener().getNewEssentialsCommands()) {
                    this.serverSystem.log("Reactivating command " + cmd + "!");
                    this.serverSystem.getEssentialsCommandListener().removeCommand(cmd);
                }
        }

        try {
            for (var command : this.serverSystemCommands) {
                command = command.toLowerCase();
                this.deactivateOwnCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void activateBukkitCommand(PluginCommand cmd) {
        try {
            Object result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/this.getCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.getPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (cmd == null)
                return;
            knownCommands.put(cmd.getName().toLowerCase(), cmd);
            knownCommands.put(cmd.getPlugin().getName().toLowerCase() + ":" + cmd.getName().toLowerCase(), cmd);
            if (!cmd.getAliases().isEmpty())
                for (var alias : cmd.getAliases()) {
                    knownCommands.put(alias.toLowerCase(), cmd);
                    if (Bukkit.getServer().getPluginCommand(alias.toLowerCase().toLowerCase()) == null)
                        knownCommands.put(cmd.getPlugin().getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deactivateOwnCommand(String cmd) {
        try {
            var plugin = "serversystem";
            Object result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/this.getCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.getPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (cmd == null)
                return;
            if (this.serverSystem.getServer().getPluginCommand(plugin + ":" + cmd) == this.serverSystem.getServer().getPluginCommand(cmd))
                knownCommands.remove(cmd);
            knownCommands.remove(plugin + ":" + cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getPrivateField(Object object) throws IllegalArgumentException, IllegalAccessException {
        var clazz = object.getClass();

        try {
            var objectField = clazz.getSuperclass().getDeclaredField("knownCommands");
            objectField.setAccessible(true);
            return objectField.get(object);
        } catch (NoSuchFieldException | NoSuchFieldError ignored) {
            for (var field1 : this.getClass().getDeclaredFields())
                System.out.println(field1.getName() + " -> " + field1.getType().getName());
            return null;
        }
    }

    public void registerCommands() {
        this.registerCommand("afk", new AwayFromKeyboardCommand(this.serverSystem), null);
        this.registerCommand("unlimited", new UnlimitedCommand(this.serverSystem), null);
        this.registerCommand("clearchat", new ClearChatCommand(this.serverSystem), null);
        this.registerCommand("back", new BackCommand(this.serverSystem), null);
        this.registerCommand("broadcast", new BroadcastCommand(this.serverSystem), null);
        this.registerCommand("sethome", new SetHomeCommand(this.serverSystem), null);
        this.registerCommand("delhome", new DelHomeCommand(this.serverSystem), new DelHomeTabCompleter(this.serverSystem));
        this.registerCommand("home", new HomeCommand(this.serverSystem), new HomeTabCompleter(this.serverSystem));
        this.registerCommand("workbench", new WorkbenchCommand(this.serverSystem), null);
        this.registerCommand("tpa", new TeleportRequestCommand(this.serverSystem), null);
        this.registerCommand("tpahere", new TeleportRequestHereCommand(this.serverSystem), null);
        this.registerCommand("tpaccept", new TeleportRequestAcceptCommand(this.serverSystem), null);
        this.registerCommand("tpdeny", new TeleportRequestDenyCommand(this.serverSystem), null);
        this.registerCommand("vanish", new VanishCommand(this.serverSystem), null);
        this.registerCommand("chat", new ChatCommand(this.serverSystem), null);
        this.registerCommand("interact", new InteractCommand(this.serverSystem), null);
        this.registerCommand("pickup", new PickUpCommand(this.serverSystem), null);
        this.registerCommand("drop", new DropCommand(this.serverSystem), null);
        this.registerCommand("tptoggle", new TeleportToggleCommand(this.serverSystem), null);
        this.registerCommand("clearenderchest", new ClearEnderChestCommand(this.serverSystem), null);
        this.registerCommand("clearinventory", new ClearInventoryCommand(this.serverSystem), null);
        this.registerCommand("checkgamemode", new CheckGameModeCommand(this.serverSystem), null);
        this.registerCommand("speed", new SpeedCommand(this.serverSystem), new SpeedTabCompleter(this.serverSystem));
        this.registerCommand("serversystem", new ServerSystemCommand(this.serverSystem), new ServerSystemTabCompleter(this.serverSystem));
        this.registerCommand("gamemode", new GameModeCommand(this.serverSystem), new GameModeTabCompleter());
        this.registerCommand("feed", new FeedCommand(this.serverSystem), null);
        this.registerCommand("time", new TimeCommand(this.serverSystem), new WorldTabCompleter());
        this.registerCommand("heal", new HealCommand(this.serverSystem), null);
        this.registerCommand("hat", new HatCommand(this.serverSystem), null);
        this.registerCommand("weather", new WeatherCommand(this.serverSystem), new WorldTabCompleter());
        this.registerCommand("restart", new RestartCommand(this.serverSystem), null);
        this.registerCommand("ping", new PingCommand(this.serverSystem), null);
        this.registerCommand("burn", new BurnCommand(this.serverSystem), null);
        this.registerCommand("ip", new IpCommand(this.serverSystem), null);
        this.registerCommand("repair", new RepairCommand(this.serverSystem), null);
        this.registerCommand("disenchant", new DisenchantCommand(this.serverSystem), null);
        this.registerCommand("suicide", new SuicideCommand(this.serverSystem), null);
        this.registerCommand("extinguish", new ExtinguishCommand(this.serverSystem), null);
        this.registerCommand("lag", new LagCommand(this.serverSystem), null);
        this.registerCommand("sudo", new SudoCommand(this.serverSystem), null);
        this.registerCommand("smelt", new SmeltCommand(this.serverSystem), null);
        this.registerCommand("stack", new StackCommand(this.serverSystem), null);
        this.registerCommand("disposal", new DisposalCommand(this.serverSystem), null);
        this.registerCommand("teamchat", new TeamChatCommand(this.serverSystem), null);
        this.registerCommand("sign", new SignCommand(this.serverSystem), null);
        this.registerCommand("unsign", new UnSignCommand(this.serverSystem), null);
        this.registerCommand("tppos", new TeleportPositionCommand(this.serverSystem), null);
        if (this.serverSystem.getConfigReader().getBoolean("banSystem.enabled")) {
            this.registerCommand("ban", new BanCommand(this.serverSystem), new BanTabCompleter(this.serverSystem));
            this.registerCommand("unban", new UnBanCommand(this.serverSystem), new UnBanTabCompleter(this.serverSystem));
            this.registerCommand("mute", new MuteCommand(this.serverSystem), new MuteTabCompleter(this.serverSystem));
            this.registerCommand("unmute", new UnMuteCommand(this.serverSystem), new UnMuteTabCompleter(this.serverSystem));
            this.registerCommand("kick", new KickCommand(this.serverSystem), null);
            this.registerCommand("kickall", new KickAllCommand(this.serverSystem), null);
        }
        if (this.serverSystem.getConfigReader().getBoolean("economy.enabled")) {
            this.registerCommand("money", new MoneyCommand(this.serverSystem), null);
            this.registerCommand("pay", new PayCommand(this.serverSystem), null);
            this.registerCommand("economy", new EconomyCommand(this.serverSystem), new EconomyTabCompleter(this.serverSystem));
            this.registerCommand("baltop", new BaltopCommand(this.serverSystem), null);
        }
        this.registerCommand("fly", new FlyCommand(this.serverSystem), null);
        this.registerCommand("commandspy", new CommandSpyCommand(this.serverSystem), null);
        this.registerCommand("warp", new WarpCommand(this.serverSystem), new WarpTabCompleter(this.serverSystem));
        this.registerCommand("delwarp", new DeleteWarpCommand(this.serverSystem), new WarpTabCompleter(this.serverSystem));
        this.registerCommand("setwarp", new SetWarpCommand(this.serverSystem), null);
        this.registerCommand("setspawn", new SetSpawnCommand(this.serverSystem), null);
        this.registerCommand("spawn", new SpawnCommand(this.serverSystem), null);
        this.registerCommand("createkit", new CreateKitCommand(this.serverSystem), null);
        this.registerCommand("delkit", new DeleteKitCommand(this.serverSystem), new DeleteKitTabCompleter(this.serverSystem));
        this.registerCommand("kit", new KitCommand(this.serverSystem), new KitTabCompleter(this.serverSystem));
        this.registerCommand("invsee", new InvseeCommand(this.serverSystem), null);
        this.registerCommand("enderchest", new EnderChestCommand(this.serverSystem), null);
        this.registerCommand("tp", new TeleportCommand(this.serverSystem), null);
        this.registerCommand("tpo", new TeleportForceCommand(this.serverSystem), null);
        this.registerCommand("tphere", new TeleportHereCommand(this.serverSystem), null);
        this.registerCommand("tpohere", new TeleportForceHereCommand(this.serverSystem), null);
        this.registerCommand("tpall", new TeleportAllCommand(this.serverSystem), null);
        this.registerCommand("msg", new MsgCommand(this.serverSystem), null);
        this.registerCommand("msgtoggle", new MsgToggleCommand(this.serverSystem), null);
        this.registerCommand("socialspy", new SocialSpyCommand(this.serverSystem), null);
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            this.registerCommand("convertfromessentials", new EssentialsConversionCommand(this.serverSystem), null);
            this.registerCommand("converttoessentials", new ServerSystemConversionCommand(this.serverSystem), null);
        }
        this.registerCommand("god", new GodCommand(this.serverSystem), null);
        this.registerCommand("reply", new ReplyCommand(this.serverSystem), null);
        this.registerCommand("maintenance", new MaintenanceCommand(this.serverSystem), null);
        this.registerCommand("rules", new RulesCommand(this.serverSystem), null);
        this.registerCommand("rename", new RenameCommand(this.serverSystem), null);
        this.registerCommand("recipe", new RecipeCommand(this.serverSystem), new RecipeTabCompleter(this.serverSystem));
        this.registerCommand("warps", new WarpsCommand(this.serverSystem), null);
        this.registerCommand("checkhealth", new CheckHealthCommand(this.serverSystem), null);
        this.registerCommand("lightning", new LightningCommand(this.serverSystem), null);
        this.registerCommand("getpos", new GetPositionCommand(this.serverSystem), null);
        this.registerCommand("anvil", new AnvilCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualCartography() != null)
            this.registerCommand("cartographytable", new CartographyCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualGrindstone() != null)
            this.registerCommand("grindstone", new GrindStoneCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualLoom() != null)
            this.registerCommand("loom", new LoomCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualStoneCutter() != null)
            this.registerCommand("stonecutter", new StoneCutterCommand(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualSmithing() != null)
            this.registerCommand("smithingtable", new SmithingTableCommand(this.serverSystem), null);
        this.registerCommand("break", new BreakCommand(this.serverSystem), null);
        this.registerCommand("offlineteleport", new OfflineTeleportCommand(this.serverSystem), null);
        this.registerCommand("offlineteleporthere", new OfflineTeleportHereCommand(this.serverSystem), null);
        this.registerCommand("offlineinvsee", new OfflineInvseeCommand(this.serverSystem), null);
        this.registerCommand("offlineenderchest", new OfflineEnderChestCommand(this.serverSystem), null);
        this.registerCommand("seen", new SeenCommand(this.serverSystem), null);
        this.registerCommand("freeze", new FreezeCommand(this.serverSystem), null);


        var plotSquaredAlreadyRegistered = false;

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

    public void registerCommand(String command, CommandExecutor executor, TabCompleter tabCompleter) {
        if (executor == null)
            this.serverSystem.error("CommandExecutor does not exist, please forward this message to the plugin author!");
        if (command == null)
            this.serverSystem.error("Command does not exist, please forward this message to the plugin author!");

        if (tabCompleter == null)
            if (executor instanceof TabCompleter)
                tabCompleter = (TabCompleter) executor;
            else
                tabCompleter = new DefaultTabCompleter(this.serverSystem);


        var commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        var commandsConfig = DefaultConfigReader.loadConfiguration(commandsFiles, this.serverSystem);

        if (commandsConfig.getBoolean(command.toLowerCase())) {
            this.registerCommandInternal(executor, tabCompleter, this.serverSystem, command);

            this.serverSystemCommands.add(command.toLowerCase());

            var aliasFiles = new File("plugins//ServerSystem", "aliases.yml");
            var aliasConfig = DefaultConfigReader.loadConfiguration(aliasFiles, this.serverSystem);

            var aliasString = aliasConfig.getString("Aliases." + command.toLowerCase() + ".aliases");

            if (aliasString != null) {
                if (!aliasString.equalsIgnoreCase("No Aliases")) {
                    var aliases = aliasConfig.getString("Aliases." + command.toLowerCase() + ".aliases").replace(" ", "").toLowerCase().split(",");
                    this.addAlias(command, executor, aliases);
                    this.serverSystemCommands.addAll(Arrays.asList(aliases));
                }
            } else
                this.serverSystem.warn("Null alias for: " + command);
        } else if (command.equalsIgnoreCase("drop"))
            this.dropActive = false;
        else if (command.equalsIgnoreCase("chat"))
            this.chatActive = false;
        else if (command.equalsIgnoreCase("pickup"))
            this.pickupActive = false;
        else if (command.equalsIgnoreCase("interact"))
            this.interactActive = false;
    }

    private void registerCommandInternal(CommandExecutor executor, TabCompleter tabCompleter, Plugin plugin, String... aliases) {
        if (this.serverSystem.getServer().getPluginCommand(aliases[0]) != null)
            if (!this.serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName().equalsIgnoreCase("ServerSystem"))
                this.deactivateBukkitCommand(aliases[0], this.serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName());

        Object map = null;
        try {
            map = this.getPrivateField(this.getCommandMap());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        var knownCommands = (HashMap<String, Command>) map;

        aliases = Arrays.stream(aliases).map(String::toLowerCase).toArray(String[]::new);
        var command = this.getNewCommand(aliases[0], plugin);

        command.setExecutor(executor);
        if (tabCompleter != null)
            command.setTabCompleter(tabCompleter);

        for (var alias : aliases)
            knownCommands.remove(alias.toLowerCase());

        for (var alias : aliases) {
            alias = alias.toLowerCase();
            knownCommands.put("serversystem:" + alias.toLowerCase(), command);
            knownCommands.put(alias.toLowerCase(), command);
        }

        command.register(this.getCommandMap());
    }

    private void addAlias(String cmd, CommandExecutor executor, String... aliases) {
        cmd = cmd.toLowerCase();
        try {
            Object result;
            result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/ this.getCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.getPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;

            for (var alias : aliases) {
                knownCommands.remove(alias.toLowerCase());
                knownCommands.remove("serversystem:" + alias.toLowerCase());
            }

            var command = this.getNewCommand(cmd.toLowerCase(), this.serverSystem);

            command.setExecutor(executor);

            command.setAliases(Arrays.asList(aliases.clone()));

            for (var alias : aliases) {
                alias = alias.toLowerCase();
                knownCommands.put(alias.toLowerCase(), command);
                knownCommands.put("serversystem:" + alias.toLowerCase(), command);
            }

            command.register(commandMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deactivateBukkitCommand(String cmd, String plugin) {
        try {
            Object result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/this.getCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.getPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (cmd == null)
                return;
            if (plugin == null)
                return;
            if (this.serverSystem.getServer().getPluginCommand(plugin + ":" + cmd) == this.serverSystem.getServer().getPluginCommand(cmd))
                knownCommands.remove(cmd);

            if (!plugin.equalsIgnoreCase("minecraft") && !plugin.equalsIgnoreCase("bukkit") && !plugin.equalsIgnoreCase("spigot"))
                knownCommands.remove(plugin + ":" + cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PluginCommand getNewCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            var c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return command;
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
}
