package me.testaccount666.serversystem.userdata.persistence;

import me.testaccount666.serversystem.utils.FieldAccessor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Manages the persistence of object fields to and from configuration files.
 * This class uses reflection and the SaveableField annotation to automatically
 * save and load fields.
 */
public class PersistenceManager {
    private static final Map<Class<?>, FieldHandler<?>> _TYPE_HANDLERS = new HashMap<>();
    private static final Map<Class<?>, Map<String, FieldInfo>> _CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    static {
        // Register default handlers for common types
        registerHandler(Boolean.class, new PrimitiveFieldHandler<>(Boolean.class));
        registerHandler(boolean.class, new PrimitiveFieldHandler<>(boolean.class));
        registerHandler(Integer.class, new PrimitiveFieldHandler<>(Integer.class));
        registerHandler(int.class, new PrimitiveFieldHandler<>(int.class));
        registerHandler(Long.class, new PrimitiveFieldHandler<>(Long.class));
        registerHandler(long.class, new PrimitiveFieldHandler<>(long.class));
        registerHandler(Double.class, new PrimitiveFieldHandler<>(Double.class));
        registerHandler(double.class, new PrimitiveFieldHandler<>(double.class));
        registerHandler(String.class, new PrimitiveFieldHandler<>(String.class));
        registerHandler(Location.class, new LocationFieldHandler());
    }

    /**
     * Registers a handler for a specific type.
     *
     * @param type    The type to register the handler for
     * @param handler The handler to register
     * @param <T>     The type of data the handler can process
     */
    public static <T> void registerHandler(Class<T> type, FieldHandler<T> handler) {
        _TYPE_HANDLERS.put(type, handler);
    }

    /**
     * Saves all fields marked with SaveableField from an object to a configuration.
     *
     * @param object The object to save fields from
     * @param config The configuration to save to
     */
    public static void saveFields(Object object, FileConfiguration config) {
        var clazz = object.getClass();
        var fieldInfoMap = getFieldInfoMap(clazz);

        for (var fieldInfo : fieldInfoMap.values()) {
            var value = fieldInfo.getter.apply(object);
            fieldInfo.handler.save(config, fieldInfo.path, value);
        }
    }

    /**
     * Loads all fields marked with SaveableField from a configuration to an object.
     *
     * @param object The object to load fields into
     * @param config The configuration to load from
     */
    public static void loadFields(Object object, FileConfiguration config) {
        var clazz = object.getClass();
        var fieldInfoMap = getFieldInfoMap(clazz);

        for (var fieldInfo : fieldInfoMap.values()) {
            var defaultValue = fieldInfo.getter.apply(object);
            var value = fieldInfo.handler.load(config, fieldInfo.path, defaultValue);
            fieldInfo.setter.apply(object, value);
        }
    }

    /**
     * Gets the field info map for a class, caching it for future use.
     *
     * @param clazz The class to get field info for
     * @return A map of field names to field info
     */
    @SuppressWarnings("unchecked")
    private static Map<String, FieldInfo> getFieldInfoMap(Class<?> clazz) {
        return _CLASS_FIELD_CACHE.computeIfAbsent(clazz, finalCurrentClass -> {
            var fieldInfoMap = new HashMap<String, FieldInfo>();

            //TODO: This is kinda stupid

            // Get all fields from the class and its superclasses
            List<Field> fields = new ArrayList<>();
            var currentClass = finalCurrentClass;

            while (currentClass != null) {
                fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
                currentClass = currentClass.getSuperclass();
            }

            for (var field : fields) {
                var annotation = field.getAnnotation(SaveableField.class);
                if (annotation == null) continue;

                var fieldName = field.getName();
                var path = annotation.path().isEmpty()? "User.${fieldName}" : annotation.path();

                @SuppressWarnings("unchecked")
                var getter = (Function<Object, Object>) FieldAccessor.createGetter(finalCurrentClass, fieldName);
                @SuppressWarnings("unchecked")
                var setter = (BiFunction<Object, Object, Object>) FieldAccessor.createSetter(finalCurrentClass, fieldName);

                // Get the appropriate handler for the field
                FieldHandler<Object> handler;
                if (annotation.handler() != DefaultFieldHandler.class) try {
                    handler = (FieldHandler<Object>) annotation.handler().getDeclaredConstructor().newInstance();
                } catch (Exception exception) {
                    throw new RuntimeException("Failed to instantiate handler for field ${fieldName}", exception);
                }
                else {
                    // Use the type handler based on the field type
                    var fieldType = field.getType();
                    handler = (FieldHandler<Object>) _TYPE_HANDLERS.getOrDefault(fieldType, new PrimitiveFieldHandler<>(Object.class));
                }

                fieldInfoMap.put(fieldName, new FieldInfo(path, getter, setter, handler));
            }

            return fieldInfoMap;
        });
    }

    /**
     * Information about a field that can be saved and loaded.
     */
    private record FieldInfo(String path, Function<Object, Object> getter, BiFunction<Object, Object, Object> setter, FieldHandler<Object> handler) {
    }
}
