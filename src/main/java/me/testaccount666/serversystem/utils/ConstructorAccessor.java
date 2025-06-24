package me.testaccount666.serversystem.utils;

import java.util.function.BiFunction;
import java.util.function.Function;

/*
 Kinda limited, but is suitable for ServerSystem's current needs.

 Made this for future shenanigans
*/
//TODO: Make this look better
public final class ConstructorAccessor {

    private ConstructorAccessor() {
    }

    public static <T> Function<Void, T> createConstructor(Class<T> targetClass) {
        try {
            var constructor = targetClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            return unused -> {
                try {
                    return constructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to create instance of ${targetClass.getName()}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create constructor accessor for ${targetClass.getName()}", e);
        }
    }

    public static <T, P> Function<P, T> createConstructor(Class<T> targetClass, Class<P> paramType) {
        try {
            var constructor = targetClass.getDeclaredConstructor(paramType);
            constructor.setAccessible(true);

            return param -> {
                try {
                    return constructor.newInstance(param);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to create instance of ${targetClass.getName()}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create constructor accessor for ${targetClass.getName()}", e);
        }
    }

    public static <T, P1, P2> BiFunction<P1, P2, T> createConstructor(
            Class<T> targetClass,
            Class<P1> param1Type,
            Class<P2> param2Type
    ) {
        try {
            var constructor = targetClass.getDeclaredConstructor(param1Type, param2Type);
            constructor.setAccessible(true);

            return (param1, param2) -> {
                try {
                    return constructor.newInstance(param1, param2);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to create instance of ${targetClass.getName()}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to create constructor accessor for ${targetClass.getName()}", e);
        }
    }
}