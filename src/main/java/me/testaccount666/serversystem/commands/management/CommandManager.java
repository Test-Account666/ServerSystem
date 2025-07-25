package me.testaccount666.serversystem.commands.management;

import io.github.classgraph.ClassGraph;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.commands.wrappers.CommandExecutorWrapper;
import me.testaccount666.serversystem.commands.wrappers.TabCompleterWrapper;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.utils.ConstructorAccessor;
import me.testaccount666.serversystem.utils.FieldAccessor;
import me.testaccount666.serversystem.utils.MethodAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommandManager {
    private final Function<SimpleCommandMap, Map<String, Command>> _commandMapAccessor = FieldAccessor.createGetter(SimpleCommandMap.class, "knownCommands");
    private final BiFunction<String, Plugin, PluginCommand> _pluginCommandConstructor = ConstructorAccessor.createConstructor(PluginCommand.class, String.class, Plugin.class);
    private final Consumer<? extends Server> _syncCommandsAccessor = MethodAccessor.createVoidAccessor(Bukkit.getServer().getClass(), "syncCommands");
    private final ConfigReader _configReader;
    private final Set<String> _registeredCommands = new HashSet<>();
    private final Set<ServerSystemCommandExecutor> _registeredCommandInstances = new HashSet<>();

    public CommandManager(ConfigReader configReader) {
        _configReader = configReader;
    }

    public Optional<Command> getCommand(String commandName) {
        var commandMap = getCommandMap();
        return Optional.ofNullable(commandMap.get(commandName));
    }

    public Optional<PluginCommand> getServerSystemCommand(String commandName) {
        return getCommandMap().values().stream()
                .filter(PluginCommand.class::isInstance)
                .map(PluginCommand.class::cast)
                .filter(pluginCommand -> pluginCommand.getName().equalsIgnoreCase(commandName))
                .filter(pluginCommand -> pluginCommand.getPlugin() == ServerSystem.Instance)
                .filter(pluginCommand -> pluginCommand.getExecutor() instanceof ServerSystemCommandExecutor)
                .findFirst();
    }

    public Optional<ServerSystemCommandExecutor> getServerSystemCommandExecutor(String commandName) {
        return getServerSystemCommand(commandName)
                .map(PluginCommand::getExecutor)
                .filter(ServerSystemCommandExecutor.class::isInstance)
                .map(ServerSystemCommandExecutor.class::cast);
    }

    private Map<String, Command> getCommandMap() {
        var commandMap = (SimpleCommandMap) Bukkit.getCommandMap();
        return _commandMapAccessor.apply(commandMap);
    }

    private PluginCommand createCommand(String name) {
        return _pluginCommandConstructor.apply(name, ServerSystem.Instance);
    }

    private void registerCommand(ServerSystemCommandExecutor command, ServerSystemTabCompleter completer, Map<String, List<String>> variantAliasMap) {
        var commandMap = getCommandMap();

        variantAliasMap.forEach((variant, aliases) -> {
            aliases.forEach(commandMap::remove);

            var bukkitCommand = createCommand(variant);
            bukkitCommand.setExecutor(new CommandExecutorWrapper(command));
            bukkitCommand.setTabCompleter(new TabCompleterWrapper(completer));

            for (var alias : aliases) {
                commandMap.put(alias, bukkitCommand);
                commandMap.put("serversystem:${alias}", bukkitCommand);

                _registeredCommands.add(alias);
                _registeredCommands.add("serversystem:${alias}");

                _registeredCommandInstances.add(command);
            }

            // Make aliases List modifiable
            aliases = new ArrayList<>(aliases);

            // Paper, for some reason, throws an error if the alias and command name are the same...
            aliases.remove(bukkitCommand.getName());
            bukkitCommand.setAliases(aliases);
        });
    }

    public void registerCommands() {
        try (var scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("me.testaccount666.serversystem.commands.executables")
                .scan()) {
            var serverSystemCommands = scanResult.getClassesWithAnnotation(ServerSystemCommand.class);
            serverSystemCommands.forEach(clazz -> processCommandExecutor(clazz.loadClass()));
        }

        syncCommands();
    }

    public void unregisterCommands() {
        var commandMap = getCommandMap();

        _registeredCommands.forEach(commandMap::remove);
        _registeredCommands.clear();
        _registeredCommandInstances.clear();
    }

    public Set<String> getRegisteredCommands() {
        return Collections.unmodifiableSet(_registeredCommands);
    }

    public Set<ServerSystemCommandExecutor> getRegisteredCommandInstances() {
        return Collections.unmodifiableSet(_registeredCommandInstances);
    }

    private void processCommandExecutor(Class<?> clazz) {
        @SuppressWarnings("unchecked")
        var commandExecutor = (Class<ServerSystemCommandExecutor>) clazz;

        if (!commandExecutor.isAnnotationPresent(ServerSystemCommand.class)) return;

        var commandAnnotation = commandExecutor.getAnnotation(ServerSystemCommand.class);
        assert commandAnnotation != null;
        var command = commandAnnotation.name();

        if (!isCommandEnabled(command)) return;

        var variantAliasMap = buildVariantAliasMap(command, commandAnnotation.variants());
        instantiateAndRegisterCommand(commandExecutor, commandAnnotation.tabCompleter(), variantAliasMap, command);
    }

    private boolean isCommandEnabled(String command) {
        return _configReader.getBoolean("Commands.${command}.Enabled", false);
    }

    private boolean isVariantEnabled(String command, String variant) {
        return _configReader.getBoolean("Commands.${command}.Variants.${variant}.Enabled", false);
    }

    private Map<String, List<String>> buildVariantAliasMap(String command, String[] variants) {
        var variantAliasMap = new HashMap<String, List<String>>();

        var parentAliases = getAliases("Commands.${command}.Aliases");
        variantAliasMap.put(command, parentAliases);

        for (var variant : variants) {
            if (!isVariantEnabled(command, variant)) continue;

            var variantAliases = getAliases("Commands.${command}.Variants.${variant}.Aliases");
            variantAliasMap.put(variant, variantAliases);
        }

        return variantAliasMap;
    }

    private List<String> getAliases(String configPath) {
        return Arrays.stream(_configReader.getString(configPath, "").split(","))
                .map(String::trim)
                .filter(alias -> !alias.isEmpty())
                .toList();
    }

    private void instantiateAndRegisterCommand(Class<? extends ServerSystemCommandExecutor> commandExecutor,
                                               Class<? extends ServerSystemTabCompleter> tabCompleter,
                                               Map<String, List<String>> variantAliasMap,
                                               String command) {
        try {
            registerCommand(
                    commandExecutor.getDeclaredConstructor().newInstance(),
                    tabCompleter.getDeclaredConstructor().newInstance(),
                    variantAliasMap
            );
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new RuntimeException("Error registering command '${command}'!", exception);
        }
    }

    private void syncCommands() {
        //noinspection unchecked,rawtypes
        ((Consumer) _syncCommandsAccessor).accept(Bukkit.getServer());

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}