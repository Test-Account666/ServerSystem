package me.testaccount666.serversystem.commands.management;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.commands.wrappers.CommandExecutorWrapper;
import me.testaccount666.serversystem.commands.wrappers.TabCompleterWrapper;
import me.testaccount666.serversystem.managers.config.ConfigReader;
import me.testaccount666.serversystem.utils.ClassList;
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
    private final Function<SimpleCommandMap, Map<String, Command>> commandMapAccessor = FieldAccessor.createGetter(SimpleCommandMap.class, "knownCommands");
    private final BiFunction<String, Plugin, PluginCommand> pluginCommandConstructor = ConstructorAccessor.createConstructor(PluginCommand.class, String.class, Plugin.class);
    private final Consumer<? extends Server> syncCommandsAccessor = MethodAccessor.createVoidAccessor(Bukkit.getServer().getClass(), "syncCommands");
    private final ConfigReader configReader;
    private final Set<String> registeredCommands = new HashSet<>();

    public CommandManager(ConfigReader configReader) {
        this.configReader = configReader;
    }

    private Map<String, Command> getCommandMap() {
        var commandMap = (SimpleCommandMap) Bukkit.getCommandMap();
        return commandMapAccessor.apply(commandMap);
    }

    private PluginCommand createCommand(String name) {
        return pluginCommandConstructor.apply(name, ServerSystem.Instance);
    }

    private void registerCommand(ServerSystemCommandExecutor command, ServerSystemTabCompleter completer, Map<String, List<String>> variantAliasMap) {
        var commandMap = getCommandMap();

        variantAliasMap.forEach((variant, aliases) -> {
            aliases.forEach(commandMap::remove);

            var bukkitCommand = createCommand(variant);
            bukkitCommand.setExecutor(new CommandExecutorWrapper(command));
            bukkitCommand.setTabCompleter(new TabCompleterWrapper(completer));

            bukkitCommand.setAliases(aliases);

            for (var alias : aliases) {
                commandMap.put(alias, bukkitCommand);
                commandMap.put("serversystem:${alias}", bukkitCommand);

                registeredCommands.add(alias);
                registeredCommands.add("serversystem:${alias}");
            }
        });
    }

    public void registerCommands() {
        var commandExecutors = ClassList.getClassesOfType(ServerSystemCommandExecutor.class);

        for (var clazz : commandExecutors) processCommandExecutor(clazz);

        syncCommands();
    }

    public void unregisterCommands() {
        var commandMap = getCommandMap();

        registeredCommands.forEach(commandMap::remove);
        registeredCommands.clear();
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
        return configReader.getBoolean("Commands.${command}.Enabled", false);
    }

    private boolean isVariantEnabled(String command, String variant) {
        return configReader.getBoolean("Commands.${command}.Variants.${variant}.Enabled", false);
    }

    private Map<String, List<String>> buildVariantAliasMap(String command, String[] variants) {
        var variantAliasMap = new HashMap<String, List<String>>();

        var parentAliases = getAliases("Commands.${command}.Aliases");
        variantAliasMap.put(command, parentAliases);

        for (var variant : variants) {
            if (!isVariantEnabled(command, variant)) continue;

            var variantAliases = getAliases("Commands.${command}.${variant}.Aliases");
            variantAliasMap.put(variant, variantAliases);
        }

        return variantAliasMap;
    }

    private List<String> getAliases(String configPath) {
        return Arrays.stream(configReader.getString(configPath, "").split(","))
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
        ((Consumer) syncCommandsAccessor).accept(Bukkit.getServer());

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}