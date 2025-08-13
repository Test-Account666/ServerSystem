package me.testaccount666.serversystem;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceRegistry {
    private final Map<Class<?>, Object> _services = new ConcurrentHashMap<>();

    <T> T registerService(Class<T> type, T service) {
        if (hasService(type)) throw new IllegalStateException("Service already registered: ${type.getName()}");

        _services.put(type, service);

        return service;
    }

    public <T> T getService(Class<T> type) {
        return type.cast(_services.get(type));
    }

    public <T> Optional<T> getServiceOptional(Class<T> type) {
        return Optional.ofNullable(_services.get(type)).map(type::cast);
    }

    public boolean hasService(Class<?> type) {
        return getServiceOptional(type).isPresent();
    }

    void clearServices() {
        _services.clear();
    }
}
