package me.testaccount666.serversystem.utils;

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
            var field = targetClass.getDeclaredField(fieldName);
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

    public static <T, R> Function<T, R> createGetter(Class<T> targetClass, String fieldName, Class<R> fieldType) {
        try {
            var field = targetClass.getDeclaredField(fieldName);
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
}