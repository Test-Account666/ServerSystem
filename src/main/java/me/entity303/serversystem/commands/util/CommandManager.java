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
    private final ServerSystem _serverSystem;
    private final List<String> _serverSystemCommands = new ArrayList<>();
    private final Map<String, PluginCommand> _deactivatedBukkitCommands = new HashMap<>();
    private final Map<String, Command> _deactivatedMiscCommands = new HashMap<>();
    private boolean _dropActive = true;
    private boolean _pickupActive = true;
    private boolean _interactActive = true;
    private boolean _chatActive = true;

    public CommandManager(ServerSystem serverSystem) {
        this._serverSystem = serverSystem;
    }

    public void RegisterTabComplete(String command, TabCompleter completer) {
        if (this._serverSystem.getCommand(command) != null)
            this._serverSystem.getCommand(command).setTabCompleter(completer);
    }

    public Command GetCommand(String command) {
        Object result = this.GetCommandMap();
        var commandMap = (SimpleCommandMap) result;
        return commandMap.getCommand(command.toLowerCase(Locale.ROOT));
    }

    private CommandMap GetCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                var commandMapField = ((SimplePluginManager) Bukkit.getPluginManager()).getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);

                commandMap = (CommandMap) commandMapField.get(this._serverSystem.getServer().getPluginManager());
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            if (exception instanceof NoSuchFieldException)
                try {
                    var commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
                    commandMapField.setAccessible(true);

                    commandMap = (CommandMap) commandMapField.get(this._serverSystem.getServer().getPluginManager());
                    return commandMap;
                } catch (Exception exception1) {
                    exception.addSuppressed(exception1);
                }
            exception.printStackTrace();
        }

        return commandMap;
    }

    public void UnregisterCommands() {
        if (!this._deactivatedBukkitCommands.isEmpty()) {
            for (var entry : this._deactivatedBukkitCommands.entrySet()) {
                var commandWithNamespace = entry.getKey();

                var plugin = commandWithNamespace.split(":")[0];
                var command = commandWithNamespace.split(":")[1];
                this._serverSystem.Info("Reactivating command " + command + " from " + plugin + "!");
                this.ActivateBukkitCommand(this._deactivatedBukkitCommands.get(command));
            }

            if (this._serverSystem.GetEssentialsCommandListener() != null)
                for (var command : this._serverSystem.GetEssentialsCommandListener().GetNewEssentialsCommands()) {
                    this._serverSystem.Info("Reactivating command " + command + "!");
                    this._serverSystem.GetEssentialsCommandListener().RemoveCommand(command);
                }
        }

        try {
            for (var command : this._serverSystemCommands) {
                command = command.toLowerCase();
                this.DeactivateOwnCommand(command);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void ActivateBukkitCommand(PluginCommand command) {
        try {
            Object result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/this.GetCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.GetPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (command == null)
                return;
            knownCommands.put(command.getName().toLowerCase(), command);
            knownCommands.put(command.getPlugin().getName().toLowerCase() + ":" + command.getName().toLowerCase(), command);
            if (!command.getAliases().isEmpty())
                for (var alias : command.getAliases()) {
                    knownCommands.put(alias.toLowerCase(), command);
                    if (Bukkit.getServer().getPluginCommand(alias.toLowerCase().toLowerCase()) == null)
                        knownCommands.put(command.getPlugin().getName().toLowerCase() + ":" + alias.toLowerCase(), command);
                }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void DeactivateOwnCommand(String command) {
        try {
            var plugin = "serversystem";
            Object result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/this.GetCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.GetPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (command == null)
                return;
            if (this._serverSystem.getServer().getPluginCommand(plugin + ":" + command) == this._serverSystem.getServer().getPluginCommand(command))
                knownCommands.remove(command);
            knownCommands.remove(plugin + ":" + command);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Object GetPrivateField(Object object) throws IllegalArgumentException, IllegalAccessException {
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

    public void RegisterCommands() {
        this.RegisterCommand("afk", new AwayFromKeyboardCommand(this._serverSystem), null);
        this.RegisterCommand("unlimited", new UnlimitedCommand(this._serverSystem), null);
        this.RegisterCommand("clearchat", new ClearChatCommand(this._serverSystem), null);
        this.RegisterCommand("back", new BackCommand(this._serverSystem), null);
        this.RegisterCommand("broadcast", new BroadcastCommand(this._serverSystem), null);
        this.RegisterCommand("sethome", new SetHomeCommand(this._serverSystem), null);
        this.RegisterCommand("delhome", new DelHomeCommand(this._serverSystem), new DelHomeTabCompleter());
        this.RegisterCommand("home", new HomeCommand(this._serverSystem), new HomeTabCompleter());
        this.RegisterCommand("workbench", new WorkbenchCommand(this._serverSystem), null);
        this.RegisterCommand("tpa", new TeleportRequestCommand(this._serverSystem), null);
        this.RegisterCommand("tpahere", new TeleportRequestHereCommand(this._serverSystem), null);
        this.RegisterCommand("tpaccept", new TeleportRequestAcceptCommand(this._serverSystem), null);
        this.RegisterCommand("tpdeny", new TeleportRequestDenyCommand(this._serverSystem), null);
        this.RegisterCommand("vanish", new VanishCommand(this._serverSystem), null);
        this.RegisterCommand("chat", new ChatCommand(this._serverSystem), null);
        this.RegisterCommand("interact", new InteractCommand(this._serverSystem), null);
        this.RegisterCommand("pickup", new PickUpCommand(this._serverSystem), null);
        this.RegisterCommand("drop", new DropCommand(this._serverSystem), null);
        this.RegisterCommand("tptoggle", new TeleportToggleCommand(this._serverSystem), null);
        this.RegisterCommand("clearenderchest", new ClearEnderChestCommand(this._serverSystem), null);
        this.RegisterCommand("clearinventory", new ClearInventoryCommand(this._serverSystem), null);
        this.RegisterCommand("checkgamemode", new CheckGameModeCommand(this._serverSystem), null);
        this.RegisterCommand("speed", new SpeedCommand(this._serverSystem), new SpeedTabCompleter(this._serverSystem));
        this.RegisterCommand("serversystem", new ServerSystemCommand(this._serverSystem), new ServerSystemTabCompleter(this._serverSystem));
        this.RegisterCommand("gamemode", new GameModeCommand(this._serverSystem), new GameModeTabCompleter());
        this.RegisterCommand("feed", new FeedCommand(this._serverSystem), null);
        this.RegisterCommand("time", new TimeCommand(this._serverSystem), new WorldTabCompleter());
        this.RegisterCommand("heal", new HealCommand(this._serverSystem), null);
        this.RegisterCommand("hat", new HatCommand(this._serverSystem), null);
        this.RegisterCommand("weather", new WeatherCommand(this._serverSystem), new WorldTabCompleter());
        this.RegisterCommand("restart", new RestartCommand(this._serverSystem), null);
        this.RegisterCommand("ping", new PingCommand(this._serverSystem), null);
        this.RegisterCommand("burn", new BurnCommand(this._serverSystem), null);
        this.RegisterCommand("ip", new IpCommand(this._serverSystem), null);
        this.RegisterCommand("repair", new RepairCommand(this._serverSystem), null);
        this.RegisterCommand("disenchant", new DisenchantCommand(this._serverSystem), null);
        this.RegisterCommand("suicide", new SuicideCommand(this._serverSystem), null);
        this.RegisterCommand("extinguish", new ExtinguishCommand(this._serverSystem), null);
        this.RegisterCommand("lag", new LagCommand(this._serverSystem), null);
        this.RegisterCommand("sudo", new SudoCommand(this._serverSystem), null);
        this.RegisterCommand("smelt", new SmeltCommand(this._serverSystem), null);
        this.RegisterCommand("stack", new StackCommand(this._serverSystem), null);
        this.RegisterCommand("disposal", new DisposalCommand(this._serverSystem), null);
        this.RegisterCommand("teamchat", new TeamChatCommand(this._serverSystem), null);
        this.RegisterCommand("sign", new SignCommand(this._serverSystem), null);
        this.RegisterCommand("unsign", new UnSignCommand(this._serverSystem), null);
        this.RegisterCommand("tppos", new TeleportPositionCommand(this._serverSystem), null);
        if (this._serverSystem.GetConfigReader().GetBoolean("banSystem.enabled")) {
            this.RegisterCommand("ban", new BanCommand(this._serverSystem), new BanTabCompleter(this._serverSystem));
            this.RegisterCommand("unban", new UnBanCommand(this._serverSystem), new UnBanTabCompleter(this._serverSystem));
            this.RegisterCommand("mute", new MuteCommand(this._serverSystem), new MuteTabCompleter(this._serverSystem));
            this.RegisterCommand("unmute", new UnMuteCommand(this._serverSystem), new UnMuteTabCompleter(this._serverSystem));
            this.RegisterCommand("kick", new KickCommand(this._serverSystem), null);
            this.RegisterCommand("kickall", new KickAllCommand(this._serverSystem), null);
        }
        if (this._serverSystem.GetConfigReader().GetBoolean("economy.enabled")) {
            this.RegisterCommand("money", new MoneyCommand(this._serverSystem), null);
            this.RegisterCommand("pay", new PayCommand(this._serverSystem), null);
            this.RegisterCommand("economy", new EconomyCommand(this._serverSystem), new EconomyTabCompleter(this._serverSystem));
            this.RegisterCommand("baltop", new BalanceTopCommand(this._serverSystem), null);
        }
        this.RegisterCommand("fly", new FlyCommand(this._serverSystem), null);
        this.RegisterCommand("commandspy", new CommandSpyCommand(this._serverSystem), null);
        this.RegisterCommand("warp", new WarpCommand(this._serverSystem), new WarpTabCompleter(this._serverSystem));
        this.RegisterCommand("delwarp", new DeleteWarpCommand(this._serverSystem), new WarpTabCompleter(this._serverSystem));
        this.RegisterCommand("setwarp", new SetWarpCommand(this._serverSystem), null);
        this.RegisterCommand("setspawn", new SetSpawnCommand(this._serverSystem), null);
        this.RegisterCommand("spawn", new SpawnCommand(this._serverSystem), null);
        this.RegisterCommand("createkit", new CreateKitCommand(this._serverSystem), null);
        this.RegisterCommand("delkit", new DeleteKitCommand(this._serverSystem), new DeleteKitTabCompleter(this._serverSystem));
        this.RegisterCommand("kit", new KitCommand(this._serverSystem), new KitTabCompleter(this._serverSystem));
        this.RegisterCommand("invsee", new InvseeCommand(this._serverSystem), null);
        this.RegisterCommand("enderchest", new EnderChestCommand(this._serverSystem), null);
        this.RegisterCommand("tp", new TeleportCommand(this._serverSystem), null);
        this.RegisterCommand("tpo", new TeleportForceCommand(this._serverSystem), null);
        this.RegisterCommand("tphere", new TeleportHereCommand(this._serverSystem), null);
        this.RegisterCommand("tpohere", new TeleportForceHereCommand(this._serverSystem), null);
        this.RegisterCommand("tpall", new TeleportAllCommand(this._serverSystem), null);
        this.RegisterCommand("msg", new MsgCommand(this._serverSystem), null);
        this.RegisterCommand("msgtoggle", new MsgToggleCommand(this._serverSystem), null);
        this.RegisterCommand("socialspy", new SocialSpyCommand(this._serverSystem), null);
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            this.RegisterCommand("convertfromessentials", new EssentialsConversionCommand(this._serverSystem), null);
            this.RegisterCommand("converttoessentials", new ServerSystemConversionCommand(this._serverSystem), null);
        }
        this.RegisterCommand("god", new GodCommand(this._serverSystem), null);
        this.RegisterCommand("reply", new ReplyCommand(this._serverSystem), null);
        this.RegisterCommand("maintenance", new MaintenanceCommand(this._serverSystem), null);
        this.RegisterCommand("rules", new RulesCommand(this._serverSystem), null);
        this.RegisterCommand("rename", new RenameCommand(this._serverSystem), null);
        this.RegisterCommand("recipe", new RecipeCommand(this._serverSystem), new RecipeTabCompleter(this._serverSystem));
        this.RegisterCommand("warps", new WarpsCommand(this._serverSystem), null);
        this.RegisterCommand("checkhealth", new CheckHealthCommand(this._serverSystem), null);
        this.RegisterCommand("lightning", new LightningCommand(this._serverSystem), null);
        this.RegisterCommand("getpos", new GetPositionCommand(this._serverSystem), null);
        this.RegisterCommand("anvil", new AnvilCommand(this._serverSystem), null);
        if (this._serverSystem.GetVersionStuff().GetVirtualCartography() != null)
            this.RegisterCommand("cartographytable", new CartographyCommand(this._serverSystem), null);
        if (this._serverSystem.GetVersionStuff().GetVirtualGrindstone() != null)
            this.RegisterCommand("grindstone", new GrindStoneCommand(this._serverSystem), null);
        if (this._serverSystem.GetVersionStuff().GetVirtualLoom() != null)
            this.RegisterCommand("loom", new LoomCommand(this._serverSystem), null);
        if (this._serverSystem.GetVersionStuff().GetVirtualStoneCutter() != null)
            this.RegisterCommand("stonecutter", new StoneCutterCommand(this._serverSystem), null);
        if (this._serverSystem.GetVersionStuff().GetVirtualSmithing() != null)
            this.RegisterCommand("smithingtable", new SmithingTableCommand(this._serverSystem), null);
        this.RegisterCommand("break", new BreakCommand(this._serverSystem), null);
        this.RegisterCommand("offlineteleport", new OfflineTeleportCommand(this._serverSystem), null);
        this.RegisterCommand("offlineteleporthere", new OfflineTeleportHereCommand(this._serverSystem), null);
        this.RegisterCommand("offlineinvsee", new OfflineInvseeCommand(this._serverSystem), null);
        this.RegisterCommand("offlineenderchest", new OfflineEnderChestCommand(this._serverSystem), null);
        this.RegisterCommand("seen", new SeenCommand(this._serverSystem), null);
        this.RegisterCommand("freeze", new FreezeCommand(this._serverSystem), null);


        var plotSquaredAlreadyRegistered = false;

        try {
            Class.forName("com.plotsquared.core.PlotAPI");
            new PlotListener4();
            new PlotListener(this._serverSystem);
            plotSquaredAlreadyRegistered = true;
        } catch (Exception ignored) {
        }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.plotsquared.core.events.PlayerEnterPlotEvent");
                new PlotListener3();
                new PlotListener(this._serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener2(this._serverSystem), this._serverSystem);
                new PlotListener(this._serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener1(this._serverSystem), this._serverSystem);
                new PlotListener(this._serverSystem);
            } catch (Exception ignored) {

            }
    }

    public void RegisterCommand(String command, CommandExecutor executor, TabCompleter tabCompleter) {
        if (executor == null)
            this._serverSystem.Error("CommandExecutor does not exist, please forward this message to the plugin author!");
        if (command == null)
            this._serverSystem.Error("Command does not exist, please forward this message to the plugin author!");

        if (tabCompleter == null)
            if (executor instanceof TabCompleter)
                tabCompleter = (TabCompleter) executor;
            else
                tabCompleter = new DefaultTabCompleter(this._serverSystem);


        var commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        var commandsConfig = DefaultConfigReader.LoadConfiguration(commandsFiles, this._serverSystem);

        if (commandsConfig.GetBoolean(command.toLowerCase())) {
            this.RegisterCommandInternal(executor, tabCompleter, this._serverSystem, command);

            this._serverSystemCommands.add(command.toLowerCase());

            var aliasFiles = new File("plugins//ServerSystem", "aliases.yml");
            var aliasConfig = DefaultConfigReader.LoadConfiguration(aliasFiles, this._serverSystem);

            var aliasString = aliasConfig.GetString("Aliases." + command.toLowerCase() + ".aliases");

            if (aliasString != null) {
                if (!aliasString.equalsIgnoreCase("No Aliases")) {
                    var aliases = aliasConfig.GetString("Aliases." + command.toLowerCase() + ".aliases").replace(" ", "").toLowerCase().split(",");
                    this.AddAlias(command, executor, aliases);
                    this._serverSystemCommands.addAll(Arrays.asList(aliases));
                }
            } else
                this._serverSystem.Warn("Null alias for: " + command);
        } else if (command.equalsIgnoreCase("drop"))
            this._dropActive = false;
        else if (command.equalsIgnoreCase("chat"))
            this._chatActive = false;
        else if (command.equalsIgnoreCase("pickup"))
            this._pickupActive = false;
        else if (command.equalsIgnoreCase("interact"))
            this._interactActive = false;
    }

    private void RegisterCommandInternal(CommandExecutor executor, TabCompleter tabCompleter, Plugin plugin, String... aliases) {
        if (this._serverSystem.getServer().getPluginCommand(aliases[0]) != null)
            if (!this._serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName().equalsIgnoreCase("ServerSystem"))
                this.DeactivateBukkitCommand(aliases[0], this._serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName());

        Object map = null;
        try {
            map = this.GetPrivateField(this.GetCommandMap());
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        var knownCommands = (HashMap<String, Command>) map;

        aliases = Arrays.stream(aliases).map(String::toLowerCase).toArray(String[]::new);
        var command = this.GetNewCommand(aliases[0], plugin);

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

        command.register(this.GetCommandMap());
    }

    private void AddAlias(String commandName, CommandExecutor executor, String... aliases) {
        commandName = commandName.toLowerCase();
        try {
            Object result;
            result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/ this.GetCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.GetPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;

            for (var alias : aliases) {
                knownCommands.remove(alias.toLowerCase());
                knownCommands.remove("serversystem:" + alias.toLowerCase());
            }

            var command = this.GetNewCommand(commandName.toLowerCase(), this._serverSystem);

            command.setExecutor(executor);

            command.setAliases(Arrays.asList(aliases.clone()));

            for (var alias : aliases) {
                alias = alias.toLowerCase();
                knownCommands.put(alias.toLowerCase(), command);
                knownCommands.put("serversystem:" + alias.toLowerCase(), command);
            }

            command.register(commandMap);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void DeactivateBukkitCommand(String command, String plugin) {
        try {
            Object result = /*this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap")*/this.GetCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.GetPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (command == null)
                return;
            if (plugin == null)
                return;
            if (this._serverSystem.getServer().getPluginCommand(plugin + ":" + command) == this._serverSystem.getServer().getPluginCommand(command))
                knownCommands.remove(command);

            if (!plugin.equalsIgnoreCase("minecraft") && !plugin.equalsIgnoreCase("bukkit") && !plugin.equalsIgnoreCase("spigot"))
                knownCommands.remove(plugin + ":" + command);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private PluginCommand GetNewCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            var constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            command = constructor.newInstance(name, plugin);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException exception) {
            exception.printStackTrace();
        }

        return command;
    }

    public boolean IsDropActive() {
        return this._dropActive;
    }

    public boolean IsPickupActive() {
        return this._pickupActive;
    }

    public boolean IsInteractActive() {
        return this._interactActive;
    }

    public boolean IsChatActive() {
        return this._chatActive;
    }
}
