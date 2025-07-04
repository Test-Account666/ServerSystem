package me.testaccount666.serversystem.utils;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Function;

/*
 Made this for future shenanigans
*/
//TODO: Make this look better
public final class FieldAccessor {

    private FieldAccessor() {
    }

    public static <T, R> Function<T, R> createGetter(Class<T> targetClass, String fieldName) {
        try {
            var field = findField(targetClass, fieldName);
            field.setAccessible(true);

            return instance -> {
                try {
                    @SuppressWarnings("unchecked")
                    var value = (R) field.get(instance);
                    return value;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to get field value for ${targetClass.getName()}.${fieldName}", e);
                }
            };
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create field accessor for ${targetClass.getName()}.${fieldName}", e);
        }
    }

    /**
     * Finds a field in a class or any of its superclasses.
     *
     * @param clazz     The class to search in
     * @param fieldName The name of the field to find
     * @return The field if found
     * @throws NoSuchFieldException If the field is not found in the class hierarchy
     */
    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        var currentClass = clazz;
        while (currentClass != null) try {
            return currentClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // Field not found in this class, try the superclass
            currentClass = currentClass.getSuperclass();
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class " + clazz.getName() + " or any of its superclasses");
    }

    public static <T, R> Function<T, R> createGetter(Class<T> targetClass, String fieldName, Class<R> fieldType) {
        try {
            var field = findField(targetClass, fieldName);
            field.setAccessible(true);

            if (!fieldType.isAssignableFrom(field.getType())) throw new IllegalArgumentException(
                    "Field '${fieldName}' of type ${field.getType().getName()} is not assignable to provided type ${fieldType.getName()}");

            return instance -> {
                try {
                    @SuppressWarnings("unchecked")
                    var value = (R) field.get(instance);
                    return value;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to get field value for ${targetClass.getName()}.${fieldName}", e);
                }
            };
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create field accessor for ${targetClass.getName()}.${fieldName}", e);
        }
    }


    public static <T, R> BiFunction<T, R, R> createSetter(Class<T> targetClass, String fieldName) {
        try {
            var field = findField(targetClass, fieldName);
            field.setAccessible(true);

            return (instance, value) -> {
                try {
                    field.set(instance, value);
                    return value;
                } catch (IllegalAccessException exception) {
                    throw new RuntimeException("Failed to set field value for ${targetClass.getName()}.${fieldName}", exception);
                }
            };
        } catch (NoSuchFieldException exception) {
            throw new RuntimeException("Failed to create field accessor for ${targetClass.getName()}.${fieldName}", exception);
        }
    }

    public static <T, R> BiFunction<T, R, R> createSetter(Class<T> targetClass, String fieldName, Class<R> fieldType) {
        try {
            var field = findField(targetClass, fieldName);
            field.setAccessible(true);

            if (!fieldType.isAssignableFrom(field.getType())) throw new IllegalArgumentException(
                    "Field '${fieldName}' of type ${field.getType().getName()} is not assignable to provided type ${fieldType.getName()}");

            return (instance, value) -> {
                try {
                    field.set(instance, value);
                    return value;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to set field value for " + targetClass.getName() + "." + fieldName, e);
                }
            };
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create field accessor for " + targetClass.getName() + "." + fieldName, e);
        }
    }
}
