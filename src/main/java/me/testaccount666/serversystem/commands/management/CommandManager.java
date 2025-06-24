package me.testaccount666.serversystem.commands.management;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.DefaultTabCompleter;
import me.testaccount666.serversystem.commands.executables.gamemode.CommandGameMode;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.commands.wrappers.CommandExecutorWrapper;
import me.testaccount666.serversystem.commands.wrappers.TabCompleterWrapper;
import me.testaccount666.serversystem.utils.ConstructorAccessor;
import me.testaccount666.serversystem.utils.FieldAccessor;
import me.testaccount666.serversystem.utils.MethodAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CommandManager {
    private final Function<SimpleCommandMap, Map<String, Command>> commandMapAccessor = FieldAccessor.createGetter(SimpleCommandMap.class, "knownCommands");
    private final BiFunction<String, Plugin, PluginCommand> pluginCommandConstructor = ConstructorAccessor.createConstructor(PluginCommand.class, String.class, Plugin.class);
    private final Consumer<?> syncCommandsAccessor = MethodAccessor.createVoidAccessor(Bukkit.getServer().getClass(), "syncCommands");

    private Map<String, Command> getCommandMap() {
        var commandMap = (SimpleCommandMap) Bukkit.getCommandMap();
        return commandMapAccessor.apply(commandMap);
    }

    private PluginCommand createCommand(String name) {
        return pluginCommandConstructor.apply(name, ServerSystem.Instance);
    }

    private void registerCommand(ServerSystemCommandExecutor command, ServerSystemTabCompleter completer, String... variants) {
        var commandMap = getCommandMap();

        for (var variant : variants) {
            commandMap.remove(variant);

            var bukkitCommand = createCommand(variant);
            bukkitCommand.setExecutor(new CommandExecutorWrapper(command));
            bukkitCommand.setTabCompleter(new TabCompleterWrapper(completer));

            commandMap.put(variant, bukkitCommand);
            commandMap.put("serversystem:${variant}", bukkitCommand);
        }


    }

    public void registerCommands() {
        //TODO: Remove testing-implementation and replace with actual one
        var gameModeCommand = new CommandGameMode();
        var defaultTabCompleter = new DefaultTabCompleter();

        registerCommand(gameModeCommand, defaultTabCompleter, "gamemode");

        syncCommands();
    }

    private void syncCommands() {
        var server = Bukkit.getServer();
        //noinspection unchecked,rawtypes
        ((Consumer) syncCommandsAccessor).accept(server);

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}