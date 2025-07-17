package me.testaccount666.serversystem.listener.management;

import io.github.classgraph.ClassGraph;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.commands.management.CommandManager;
import me.testaccount666.serversystem.utils.MethodAccessor;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class ListenerManager {
    private final CommandManager _commandManager;
    private final Set<Listener> _registeredListeners = new HashSet<>();

    public ListenerManager(CommandManager commandManager) {
        _commandManager = commandManager;
    }

    public void registerListeners() {
        try (var scan = new ClassGraph().enableAllInfo().scan()) {
            var listeners = scan.getClassesImplementing(Listener.class);

            listeners.forEach(listenerClass -> {
                try {
                    var loadedClass = listenerClass.loadClass();

                    var listener = (Listener) loadedClass.getDeclaredConstructor().newInstance();

                    if (loadedClass.isAnnotationPresent(RequiredCommands.class)) {
                        var shouldRegister = shouldRegister(listener);

                        if (!shouldRegister) return;
                    }

                    Bukkit.getPluginManager().registerEvents(listener, ServerSystem.Instance);
                    _registeredListeners.add(listener);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new RuntimeException("Failed to register listener '${listenerClass.getName()}'", exception);
                }
            });
        }
    }

    public void unregisterListeners() {
        _registeredListeners.forEach(HandlerList::unregisterAll);
        _registeredListeners.clear();
    }

    private Optional<ServerSystemCommandExecutor> findInstance(Class<? extends ServerSystemCommandExecutor> commandExecutor) {
        for (var instance : _commandManager.getRegisteredCommandInstances())
            if (instance.getClass().isAssignableFrom(commandExecutor)) return Optional.of(instance);

        return Optional.empty();
    }

    private boolean shouldRegister(Listener listener) {
        var requiredCommands = listener.getClass().getAnnotation(RequiredCommands.class);

        var instances = new HashSet<ServerSystemCommandExecutor>();

        for (var command : requiredCommands.requiredCommands()) {
            var instance = findInstance(command);

            if (instance.isEmpty()) continue;

            instances.add(instance.get());
        }

        try {
            var methodAccessor = MethodAccessor.createAccessor(listener.getClass(), "canRegister", Set.class, Boolean.class);

            return ((BiFunction<Listener, Set, Boolean>) methodAccessor).apply(listener, instances);
        } catch (RuntimeException | NoSuchMethodError exception) {
            ServerSystem.getLog().severe("Listener ${listener.getClass().getName()} requires the method 'canRegister(Set<ServerSystemCommandExecutor>, Boolean)' to be implemented! This should not happen!");
            exception.printStackTrace();
            return false;
        }
    }
}
