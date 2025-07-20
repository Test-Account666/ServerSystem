package me.testaccount666.serversystem.placeholderapi;

import io.github.classgraph.ClassGraph;
import me.testaccount666.serversystem.ServerSystem;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class PlaceholderManager {
    private final Map<String, Placeholder> _registeredPlaceholders = new HashMap<>();

    public void registerPlaceholders() {
        try (var scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("me.testaccount666.serversystem.placeholderapi.executables")
                .scan()) {
            var placeholderClasses = scanResult.getClassesImplementing(Placeholder.class);

            for (var placeholderClass : placeholderClasses)
                try {
                    var placeholder = (Placeholder) placeholderClass.loadClass().getDeclaredConstructor().newInstance();
                    placeholder.getIdentifiers().forEach(identifier -> _registeredPlaceholders.put(identifier.toLowerCase(), placeholder));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    ServerSystem.getLog().log(Level.SEVERE, "Error registering placeholder '${placeholderClass.getName()}'", exception);
                }
        }
    }

    public Optional<Placeholder> getPlaceholder(String identifier) {
        identifier = identifier.toLowerCase();
        return Optional.ofNullable(_registeredPlaceholders.get(identifier));
    }
}
