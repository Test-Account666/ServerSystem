package me.testaccount666.serversystem.listener.management;

import io.github.classgraph.ClassGraph;
import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class ListenerManager {
    private final Set<Listener> _registeredListeners = new HashSet<>();

    public void registerListeners() {
        try (var scan = new ClassGraph().enableAllInfo().scan()) {
            var listeners = scan.getClassesImplementing(Listener.class);

            listeners.forEach(listenerClass -> {
                try {
                    var listener = (Listener) listenerClass.loadClass().getDeclaredConstructor().newInstance();

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
}
