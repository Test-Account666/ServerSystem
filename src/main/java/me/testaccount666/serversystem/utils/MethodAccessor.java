package me.testaccount666.serversystem.utils;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 Made this for future shenanigans
*/
//TODO: Make this look better
public final class MethodAccessor {

    private MethodAccessor() {
    }

    public static <T> Consumer<T> createVoidAccessor(Class<T> targetClass, String methodName) {
        try {
            var method = targetClass.getDeclaredMethod(methodName);
            method.setAccessible(true);

            return instance -> {
                try {
                    method.invoke(instance);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to invoke method ${targetClass.getName()}.${methodName}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create void method accessor for ${targetClass.getName()}.${methodName}", e);
        }
    }

    public static <T, P> BiConsumer<T, P> createVoidAccessor(
            Class<T> targetClass,
            String methodName,
            Class<P> paramType
    ) {
        try {
            var method = targetClass.getDeclaredMethod(methodName, paramType);
            method.setAccessible(true);

            return (instance, param) -> {
                try {
                    method.invoke(instance, param);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to invoke method ${targetClass.getName()}.${methodName}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create void method accessor for ${targetClass.getName()}.${methodName}", e);
        }
    }

    public static <T> BiConsumer<T, Object[]> createVoidAccessor(
            Class<T> targetClass,
            String methodName,
            Class<?>... paramTypes
    ) {
        try {
            var method = targetClass.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);

            return (instance, params) -> {
                try {
                    method.invoke(instance, params);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to invoke method ${targetClass.getName()}.${methodName}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create void method accessor for ${targetClass.getName()}.${methodName}", e);
        }
    }

    public static <T, R> Function<T, R> createAccessor(Class<T> targetClass, String methodName, Class<R> returnType) {
        try {
            var method = targetClass.getDeclaredMethod(methodName);
            method.setAccessible(true);

            if (method.getReturnType() == void.class)
                throw new IllegalArgumentException("Method returns void. Use createVoidAccessor instead.");

            return instance -> {
                try {
                    @SuppressWarnings("unchecked")
                    var result = (R) method.invoke(instance);
                    return result;
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to invoke method ${targetClass.getName()}.${methodName}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create method accessor for ${targetClass.getName()}.${methodName}", e);
        }
    }

    public static <T, P, R> BiFunction<T, P, R> createAccessor(
            Class<T> targetClass,
            String methodName,
            Class<P> paramType,
            Class<R> returnType
    ) {
        try {
            var method = targetClass.getDeclaredMethod(methodName, paramType);
            method.setAccessible(true);

            if (method.getReturnType() == void.class)
                throw new IllegalArgumentException("Method returns void. Use createVoidAccessor instead.");

            return (instance, param) -> {
                try {
                    @SuppressWarnings("unchecked")
                    var result = (R) method.invoke(instance, param);
                    return result;
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to invoke method ${targetClass.getName()}.${methodName}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create method accessor for ${targetClass.getName()}.${methodName}", e);
        }
    }

    public static <T, R> BiFunction<T, Object[], R> createAccessor(
            Class<T> targetClass,
            String methodName,
            Class<R> returnType,
            Class<?>... paramTypes
    ) {
        try {
            var method = targetClass.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);

            if (method.getReturnType() == void.class)
                throw new IllegalArgumentException("Method returns void. Use createVoidAccessor instead.");

            return (instance, params) -> {
                try {
                    @SuppressWarnings("unchecked")
                    var result = (R) method.invoke(instance, params);
                    return result;
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to invoke method ${targetClass.getName()}.${methodName}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create method accessor for ${targetClass.getName()}.${methodName}", e);
        }
    }
}