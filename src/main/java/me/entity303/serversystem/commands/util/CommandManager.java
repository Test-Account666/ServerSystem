package me.entity303.serversystem.commands.util;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.config.DefaultConfigReader;
import me.entity303.serversystem.listener.plotsquared.*;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.DefaultTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarFile;

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
            if (exception instanceof NoSuchFieldException) {
                try {
                    var commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
                    commandMapField.setAccessible(true);

                    commandMap = (CommandMap) commandMapField.get(this._serverSystem.getServer().getPluginManager());
                    return commandMap;
                } catch (Exception exception1) {
                    exception.addSuppressed(exception1);
                }
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

            if (this._serverSystem.GetEssentialsCommandListener() != null) {
                for (var command : this._serverSystem.GetEssentialsCommandListener().GetNewEssentialsCommands()) {
                    this._serverSystem.Info("Reactivating command " + command + "!");
                    this._serverSystem.GetEssentialsCommandListener().RemoveCommand(command);
                }
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
            Object result = this.GetCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.GetPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (command == null) return;
            knownCommands.put(command.getName().toLowerCase(), command);
            knownCommands.put(command.getPlugin().getName().toLowerCase() + ":" + command.getName().toLowerCase(), command);
            if (!command.getAliases().isEmpty()) {
                for (var alias : command.getAliases()) {
                    knownCommands.put(alias.toLowerCase(), command);
                    if (Bukkit.getServer().getPluginCommand(alias.toLowerCase().toLowerCase()) == null) {
                        knownCommands.put(command.getPlugin().getName().toLowerCase() + ":" + alias.toLowerCase(), command);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void DeactivateOwnCommand(String command) {
        try {
            var plugin = "serversystem";
            Object result = this.GetCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.GetPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (command == null) return;
            if (this._serverSystem.getServer().getPluginCommand(plugin + ":" + command) == this._serverSystem.getServer().getPluginCommand(command)) {
                knownCommands.remove(command);
            }
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
        var commandClasses = FetchCommandExecutors();

        for (var commandClass : commandClasses) {
            if (!commandClass.isAnnotationPresent(ServerSystemCommand.class)) continue;

            try {
                var shouldRegisterMethod = commandClass.getDeclaredMethod("shouldRegister", ServerSystem.class);

                var shouldRegister = (boolean) shouldRegisterMethod.invoke(null, this._serverSystem);

                if (!shouldRegister) continue;
            } catch (NoSuchMethodException ignored) {
            } catch (InvocationTargetException | IllegalAccessException exception) {
                this._serverSystem.Error("Could not register command " + commandClass.getName() + " to serversystem!");
                exception.printStackTrace();
                continue;
            }

            var serverSystemCommand = commandClass.getAnnotation(ServerSystemCommand.class);

            var tabCompleterClass = serverSystemCommand.tabCompleter();

            try {
                var commandExecutor = commandClass.getConstructor(ServerSystem.class).newInstance(this._serverSystem);

                var tabCompleter = tabCompleterClass.getConstructor(ServerSystem.class).newInstance(this._serverSystem);

                this.RegisterCommand(serverSystemCommand.name(), commandExecutor, tabCompleter);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                this._serverSystem.Error("Could not register command " + commandClass.getName() + " to serversystem!");
                exception.printStackTrace();
            }
        }

        //TODO: Figure out why tf this is here
        var plotSquaredAlreadyRegistered = false;

        try {
            Class.forName("com.plotsquared.core.PlotAPI");
            new PlotListener4();
            new PlotListener(this._serverSystem);
            plotSquaredAlreadyRegistered = true;
        } catch (Exception ignored) {
        }

        if (!plotSquaredAlreadyRegistered) {
            try {
                Class.forName("com.plotsquared.core.events.PlayerEnterPlotEvent");
                new PlotListener3();
                new PlotListener(this._serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }
        }

        if (!plotSquaredAlreadyRegistered) {
            try {
                Class.forName("com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener2(this._serverSystem), this._serverSystem);
                new PlotListener(this._serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }
        }

        if (!plotSquaredAlreadyRegistered) {
            try {
                Class.forName("com.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener1(this._serverSystem), this._serverSystem);
                new PlotListener(this._serverSystem);
            } catch (Exception ignored) {

            }
        }
    }

    private static List<Class<? extends ICommandExecutorOverload>> FetchCommandExecutors() {
        var commandExecutorClass = CommandExecutor.class;

        var commandExecutors = new ArrayList<Class<? extends ICommandExecutorOverload>>();

        GetAllClasses().forEach(potentialCommand -> {
            if (potentialCommand == null || potentialCommand == commandExecutorClass || !commandExecutorClass.isAssignableFrom(potentialCommand)) return;

            //noinspection unchecked
            var commandClass = (Class<? extends ICommandExecutorOverload>) potentialCommand;

            commandExecutors.add(commandClass);
        });

        return commandExecutors;
    }

    public void RegisterCommand(String command, CommandExecutor executor, TabCompleter tabCompleter) {
        if (executor == null) this._serverSystem.Error("CommandExecutor does not exist, please forward this message to the plugin author!");
        if (command == null) this._serverSystem.Error("Command does not exist, please forward this message to the plugin author!");

        if (tabCompleter == null) {
            if (executor instanceof TabCompleter) {
                tabCompleter = (TabCompleter) executor;
            } else {
                tabCompleter = new DefaultTabCompleter(this._serverSystem);
            }
        }


        var commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        var commandsConfig = DefaultConfigReader.LoadConfiguration(commandsFiles, this._serverSystem);

        if (commandsConfig.GetBoolean("Commands." + command.toLowerCase() + ".enabled")) {
            var aliasString = commandsConfig.GetString("Commands." + command.toLowerCase() + ".aliases");

            if (aliasString == null) {
                this._serverSystem.Warn("Null alias for: " + command);
                return;
            }

            var aliases = commandsConfig.GetString("Commands." + command.toLowerCase() + ".aliases").replace(" ", "").toLowerCase().split(",");

            this.RegisterCommandInternal(executor, tabCompleter, this._serverSystem, aliases);

            return;
        }

        if (command.equalsIgnoreCase("drop")) {
            this._dropActive = false;
        } else if (command.equalsIgnoreCase("chat")) {
            this._chatActive = false;
        } else if (command.equalsIgnoreCase("pickup")) {
            this._pickupActive = false;
        } else if (command.equalsIgnoreCase("interact")) this._interactActive = false;
    }

    private static List<Class<?>> GetAllClasses() {
        var classes = new ArrayList<Class<?>>();

        try {
            /*
             Friendly reminder that we should never use a try-with-resources block here ^^
             Otherwise, the plugin won't work (for obvious reasons)
             */
            //noinspection resource
            var jarFile = new JarFile(new File(ServerSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            var entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements()) {
                var entry = entryEnumeration.nextElement();

                if (entry.isDirectory()) continue;
                if (!entry.getName().endsWith(".class")) continue;
                if (entry.getName().contains("META-INF")) continue;

                try {
                    classes.add(Class.forName(entry.getName().replace("/", ".").replace(".class", "")));
                } catch (Throwable ignored) {
                }
            }
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
        }

        return classes;
    }

    private void RegisterCommandInternal(CommandExecutor executor, TabCompleter tabCompleter, Plugin plugin, String... aliases) {
        if (this._serverSystem.getServer().getPluginCommand(aliases[0]) != null) {
            if (!this._serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName().equalsIgnoreCase("ServerSystem")) {
                this.DeactivateBukkitCommand(aliases[0], this._serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName());
            }
        }

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
        if (tabCompleter != null) command.setTabCompleter(tabCompleter);

        for (var alias : aliases) knownCommands.remove(alias.toLowerCase());

        for (var alias : aliases) {
            alias = alias.toLowerCase();
            knownCommands.put("serversystem:" + alias.toLowerCase(), command);
            knownCommands.put(alias.toLowerCase(), command);
        }

        command.register(this.GetCommandMap());

        Arrays.stream(aliases).map(String::toLowerCase).forEach(this._serverSystemCommands::add);
    }

    public void DeactivateBukkitCommand(String command, String plugin) {
        try {
            Object result = this.GetCommandMap();
            var commandMap = (SimpleCommandMap) result;
            var map = this.GetPrivateField(commandMap);
            var knownCommands = (HashMap<String, Command>) map;
            if (command == null) return;
            if (plugin == null) return;
            if (this._serverSystem.getServer().getPluginCommand(plugin + ":" + command) == this._serverSystem.getServer().getPluginCommand(command)) {
                knownCommands.remove(command);
            }

            if (!plugin.equalsIgnoreCase("minecraft") && !plugin.equalsIgnoreCase("bukkit") && !plugin.equalsIgnoreCase("spigot")) {
                knownCommands.remove(plugin + ":" + command);
            }
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
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException exception) {
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
