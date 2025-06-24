package me.testaccount666.serversystem.utils;

import me.testaccount666.serversystem.ServerSystem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

//TODO: Figure out a nicer way to access the list of classes
public final class ClassList {
    private static final List<Class<?>> classes = new ArrayList<>();

    private ClassList() {
    }

    public static List<Class<?>> getClassesOfType(Class<?> type) {
        var classesOfType = new ArrayList<Class<?>>();

        for (var clazz : getAllClasses()) {
            if (clazz == type || !type.isAssignableFrom(clazz)) continue;

            classesOfType.add(clazz);
        }

        return classesOfType;
    }

    public static List<Class<?>> getAllClasses() {
        if (!classes.isEmpty()) return Collections.unmodifiableList(classes);

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

                try {
                    classes.add(Class.forName(entry.getName().replace("/", ".").replace(".class", "")));
                } catch (Throwable ignored) {
                }
            }
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
        }

        return Collections.unmodifiableList(classes);
    }
}
